package com.example.kotlincompiler.ParserAnalysis

import com.example.kotlincompiler.LexicalAnalysis.token.Token
import com.example.kotlincompiler.LexicalAnalysis.tokenType.TokenType

class Parser {

    // Stack لتتبع الكتل: if/while/for/functions
    private val blockStack = mutableListOf<String>()
    private val declaredVariables = mutableSetOf<String>()

    fun parse(tokens: List<Token>): String {
        val errors = mutableListOf<String>()
        var openParens = 0
        var openBraces = 0
        var openBrackets = 0
        var lastToken: Token? = null

        val validKeywords = listOf(
            "if",
            "else",
            "while",
            "for",
            "return",
            "fun",
            "val",
            "var",
            "do",
            "true",
            "false",
            "print"
        )
        val validTypes = listOf("int", "float", "String", "Boolean")
        val operators = listOf("+", "-", "*", "/", "=")

        for ((index, token) in tokens.withIndex()) {

            // --------- الأقواس ---------
            when (token.value) {
                "(" -> openParens++
                ")" -> {
                    openParens--
                    if (openParens < 0) errors.add("Unmatched closing parenthesis at token ${index + 1}")
                }

                "{" -> {
                    openBraces++
                    // بداية بلوك
                    if (lastToken != null && lastToken.value in listOf(
                            "if",
                            "while",
                            "for",
                            "fun"
                        )
                    ) {
                        blockStack.add(lastToken.value)
                    }
                }

                "}" -> {
                    openBraces--
                    if (openBraces < 0) errors.add("Unmatched closing brace at token ${index + 1}")
                    // نهاية بلوك
                    if (blockStack.isNotEmpty()) blockStack.removeAt(blockStack.lastIndex)
                }

                "[" -> openBrackets++
                "]" -> {
                    openBrackets--
                    if (openBrackets < 0) errors.add("Unmatched closing bracket at token ${index + 1}")
                }
            }

            // --------- الكلمات المفتاحية ---------
            if (token.type == TokenType.KEYWORD) {
                if (token.value !in validKeywords && token.value !in validTypes) {
                    errors.add("Unknown keyword or type '${token.value}' at token ${index + 1}")
                }
            }

            // --------- تعريف المتغيرات ---------
            if (token.value in validTypes) {
                if (index + 1 < tokens.size) {
                    val next = tokens[index + 1]
                    if (next.type == TokenType.IDENTIFIER) {
                        declaredVariables.add(next.value)
                    } else {
                        errors.add("Expected variable name after type '${token.value}' at token ${index + 1}")
                    }
                }
            }

            // --------- استخدام المتغيرات ---------
            if (token.type == TokenType.IDENTIFIER) {
                if (lastToken?.value !in validTypes && token.value !in declaredVariables && token.value !in operators) {
                    errors.add("Undeclared variable '${token.value}' used at token ${index + 1}")
                }
            }

            // --------- التحقق من التعبيرات ---------
            if (token.value in operators) {
                if (lastToken == null || (lastToken.type != TokenType.IDENTIFIER && lastToken.type != TokenType.NUMBER)) {
                    errors.add("Invalid operator usage '${token.value}' at token ${index + 1}")
                }
            }

            // --------- if-else sequence ---------
            if (token.value == "else") {
                if (blockStack.lastOrNull() != "if") {
                    errors.add("else must follow if block at token ${index + 1}")
                }
            }

            lastToken = token
        }

        // --------- تحقق نهائي من الأقواس ---------
        if (openParens > 0) errors.add("Unmatched opening parenthesis")
        if (openBraces > 0) errors.add("Unmatched opening brace")
        if (openBrackets > 0) errors.add("Unmatched opening bracket")

        return if (errors.isEmpty()) "No Syntax Error  " else errors.joinToString("\n")
    }
}