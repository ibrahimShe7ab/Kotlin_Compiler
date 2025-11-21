package com.example.kotlincompiler.SemanticAnalysis


import com.example.kotlincompiler.LexicalAnalysis.token.Token
import com.example.kotlincompiler.LexicalAnalysis.tokenType.TokenType

// ---------------- Variable Info ----------------


// ---------------- Semantic Analyzer ----------------
class SemanticAnalyzer {

    // Stack لكل scope
    private val variableStack = mutableListOf<MutableList<VariableInfo>>()
    private val errors = mutableListOf<String>()

    private val validTypes = listOf("int", "float", "String", "Boolean")

    fun analyze(tokens: List<Token>): List<String> {
        variableStack.clear()
        variableStack.add(mutableListOf()) // Global scope
        var currentScope = 0
        var lastType: String? = null

        for ((index, token) in tokens.withIndex()) {

            when (token.value) {
                "{" -> { // دخول Scope جديد
                    currentScope++
                    variableStack.add(mutableListOf())
                }

                "}" -> { // خروج Scope
                    if (currentScope > 0) {
                        variableStack.removeAt(variableStack.lastIndex)
                        currentScope--
                    } else {
                        errors.add("Unexpected closing brace '}' at token ${index + 1}")
                    }
                }
            }

            // تعريف المتغيرات
            if (token.type == TokenType.KEYWORD && token.value in validTypes) {
                lastType = token.value
                if (index + 1 < tokens.size) {
                    val next = tokens[index + 1]
                    if (next.type == TokenType.IDENTIFIER) {
                        if (isDeclaredInScope(next.value, currentScope)) {
                            errors.add("Variable '${next.value}' already declared in this scope at token ${index + 2}")
                        } else {
                            variableStack[currentScope].add(
                                VariableInfo(
                                    next.value,
                                    lastType,
                                    currentScope
                                )
                            )
                        }
                    } else {
                        errors.add("Expected variable name after type '${token.value}' at token ${index + 1}")
                    }
                } else {
                    errors.add("Type '${token.value}' at token ${index + 1} must be followed by a variable name")
                }
            }

            // التحقق من استخدام المتغيرات
            if (token.type == TokenType.IDENTIFIER) {
                val varInfo = findVariable(token.value)
                if (varInfo == null) {
                    errors.add("Undeclared variable '${token.value}' used at token ${index + 1}")
                }
            }

            // يمكن إضافة قواعد دلالية أخرى لاحقاً
            // مثل التحقق من عمليات الجمع بين أنواع مختلفة
        }

        // تحقق من أي Scope مفتوح
        if (currentScope > 0) {
            errors.add("Unclosed scope: $currentScope block(s) not closed")
        }

        return errors.ifEmpty { listOf("Semantic Analysis: No errors found") }
    }

    // تحقق من تعريف المتغير في الـ scope الحالي فقط
    private fun isDeclaredInScope(name: String, scope: Int): Boolean {
        return variableStack[scope].any { it.name == name }
    }

    // البحث عن المتغير في جميع الـ Scopes (من الأحدث إلى الأقدم)
    private fun findVariable(name: String): VariableInfo? {
        for (scope in variableStack.reversed()) {
            val found = scope.find { it.name == name }
            if (found != null) return found
        }
        return null
    }
}
