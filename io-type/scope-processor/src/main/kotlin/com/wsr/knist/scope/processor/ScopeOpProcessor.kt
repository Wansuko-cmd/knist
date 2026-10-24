package com.wsr.knist.scope.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.validate

class ScopeOpProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {

    private val collectedFunctions = mutableListOf<KSFunctionDeclaration>()
    private var generated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val (valid, deferred) = resolver
            .getSymbolsWithAnnotation("com.wsr.knist.scope.ScopeOp")
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()
            .partition { it.validate() }

        collectedFunctions.addAll(valid)
        if (deferred.isNotEmpty()) return deferred

        if (!generated && collectedFunctions.isNotEmpty()) {
            generated = true
            val file = codeGenerator.createNewFile(
                dependencies = Dependencies(
                    aggregating = true,
                    *collectedFunctions.mapNotNull { it.containingFile }.toTypedArray(),
                ),
                packageName = "com.wsr.knist.core",
                fileName = "IOScope",
            )
            file.bufferedWriter().use { writer ->
                writer.write(buildIOScopeContent(collectedFunctions))
            }
        }

        return emptyList()
    }

    private fun buildIOScopeContent(functions: List<KSFunctionDeclaration>): String {
        val indexed = functions.mapIndexedNotNull { i, func ->
            generateMember(func, i)?.let { i to it }
        }

        val sb = StringBuilder()
        sb.appendLine("@file:OptIn(kotlin.experimental.ExperimentalTypeInference::class)")
        sb.appendLine()
        sb.appendLine("package com.wsr.knist.core")
        sb.appendLine()
        sb.appendLine("import com.wsr.knist.BufferScope")
        sb.appendLine("import com.wsr.knist.batch.toLocal")
        // 名前衝突回避の為(FQNは使えない)
        indexed.forEach { (i, data) ->
            sb.appendLine("import ${data.fqn} as _impl$i")
        }
        sb.appendLine()
        sb.appendLine("@Suppress(\"NOTHING_TO_INLINE\")")
        sb.appendLine("class IOScope(val bufferScope: BufferScope) {")
        sb.appendLine()

        sb.appendLine("    companion object {")
        sb.appendLine("        // 二重ラップ防止用(context parameterで優先順位を整える)")
        sb.appendLine("        context(scope: IOScope)")
        sb.appendLine("        inline fun <T> launch(block: IOScope.() -> T): T = scope.block()")
        sb.appendLine("    }")

        indexed.forEach { (_, data) ->
            sb.appendLine(data.member)
        }

        sb.appendLine("}")

        sb.appendLine("inline fun <T> IOScope.Companion.launch(block: IOScope.() -> T): T {")
        sb.appendLine("    val bufferScope = BufferScope()")
        sb.appendLine("    val ioScope = IOScope(bufferScope)")
        sb.appendLine("    return bufferScope.use {")
        sb.appendLine("        val result = ioScope.block()")
        sb.appendLine("        bufferScope.remove(result)")
        sb.appendLine("        result")
        sb.appendLine("    }")
        sb.appendLine("}")

        sb.appendLine("@PublishedApi")
        sb.appendLine("internal fun BufferScope.remove(target: Any?) {")
        sb.appendLine("    when(target) {")
        sb.appendLine("        is IOType -> remove(target.value)")
        sb.appendLine("        is com.wsr.knist.batch.Batch<*> -> remove(target.value)")
        sb.appendLine("        is Pair<*, *> -> {")
        sb.appendLine("            remove(target.first)")
        sb.appendLine("            remove(target.second)")
        sb.appendLine("        }")
        sb.appendLine("        is Triple<*, *, *> -> {")
        sb.appendLine("            remove(target.first)")
        sb.appendLine("            remove(target.second)")
        sb.appendLine("            remove(target.third)")
        sb.appendLine("        }")
        sb.appendLine("        is Iterable<*> -> target.forEach { remove(it) }")
        sb.appendLine("    }")
        sb.appendLine("}")

        return sb.toString()
    }

    private data class MemberData(val fqn: String, val member: String)

    private fun generateMember(func: KSFunctionDeclaration, index: Int): MemberData? {
        val receiverTypeName = func.extensionReceiver?.resolve()?.toFqnString()
        val returnType = func.returnType?.resolve() ?: return null
        val returnTypeName = returnType.toFqnString()

        val returnDeclFqn = returnType.declaration.qualifiedName?.asString() ?: return null
        if (!returnDeclFqn.startsWith("com.wsr.knist.core.IOType") &&
            !returnDeclFqn.startsWith("com.wsr.knist.batch.Batch")
        ) {
            logger.warn("@ScopeOp on ${func.qualifiedName?.asString()} returns $returnDeclFqn, skipping")
            return null
        }

        val funcName = func.simpleName.asString()
        val fqn = func.qualifiedName?.asString() ?: return null
        val modifiers = buildModifiers(func)
        val params = func.parameters

        val paramsDecl = params.joinToString(", ") { p ->
            val name = p.name!!.asString()
            val type = p.type.resolve().toFqnString()
            val default = p.annotations
                .find { it.shortName.asString() == "ScopeOpDefault" }
                ?.arguments?.firstOrNull()?.value as? String
            if (default != null) "$name: $type = $default" else "$name: $type"
        }

        val paramCall = params.joinToString(", ") { p ->
            p.name!!.asString()
        }

        val batchTypeArgFqn = if (returnDeclFqn == "com.wsr.knist.batch.Batch") {
            returnType.arguments.firstOrNull()
                ?.type?.resolve()?.declaration?.qualifiedName?.asString()
        } else {
            null
        }

        val isGlobalReturn =
            (returnDeclFqn.startsWith("com.wsr.knist.core.IOType") && returnDeclFqn.endsWith(".Global")) ||
                (batchTypeArgFqn?.endsWith(".Global") == true)
        val outputReturnTypeName = if (isGlobalReturn) {
            returnTypeName.replace(".Global", ".Local")
        } else {
            returnTypeName
        }

        val member = buildString {
            appendLine("    @kotlin.jvm.JvmName(\"_ioScopeImpl$index\")")
            if (receiverTypeName != null) {
                appendLine("    $modifiers fun $receiverTypeName.$funcName($paramsDecl): $outputReturnTypeName {")
                appendLine("        val result = this._impl$index($paramCall)")
            } else {
                appendLine("    $modifiers fun $funcName($paramsDecl): $outputReturnTypeName {")
                appendLine("        val result = _impl$index($paramCall)")
            }
            if (isGlobalReturn) {
                appendLine("        return result.toLocal()")
            } else {
                appendLine("        bufferScope.register(result.value)")
                appendLine("        return result")
            }
            append("    }")
        }
        return MemberData(fqn = fqn, member = member)
    }

    private fun buildModifiers(func: KSFunctionDeclaration): String {
        val mods = mutableListOf("inline")
        if (Modifier.OPERATOR in func.modifiers) mods.add("operator")
        if (Modifier.INFIX in func.modifiers) mods.add("infix")
        if (Modifier.SUSPEND in func.modifiers) mods.add("suspend")
        return mods.joinToString(" ")
    }

    private fun KSType.toFqnString(): String {
        val fqn = declaration.qualifiedName?.asString()
            ?: declaration.simpleName.asString()
        return if (arguments.isEmpty()) {
            fqn + if (isMarkedNullable) "?" else ""
        } else {
            val argStr = arguments.joinToString(", ") { arg ->
                when (arg.variance) {
                    Variance.STAR -> "*"
                    else -> arg.type?.resolve()?.toFqnString() ?: "*"
                }
            }
            "$fqn<$argStr>" + if (isMarkedNullable) "?" else ""
        }
    }
}
