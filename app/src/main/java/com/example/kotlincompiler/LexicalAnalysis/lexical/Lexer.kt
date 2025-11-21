package com.example.kotlincompiler.LexicalAnalysis.lexical

import com.example.kotlincompiler.LexicalAnalysis.token.Token
import com.example.kotlincompiler.LexicalAnalysis.tokenType.TokenType

class Lexer {

    private val tokenPatterns = listOf(
        // Comments
        TokenType.COMMENT to Regex("""//.*|/\*[\s\S]*?\*/"""),

        // Strings
        TokenType.STRING to Regex("\"(\\\\.|[^\"\\\\])*\""),

        // Keywords
        TokenType.KEYWORD to Regex("\\b(int|float|if|else|while|for|return|void|char|double|true|false|boolean|break|continue)\\b"),


        TokenType.BOOLEAN to Regex("\\b(true|false)\\b"),


        TokenType.IDENTIFIER to Regex("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"),

        TokenType.NUMBER to Regex("\\b\\d+(\\.\\d+)?\\b"),

        TokenType.OPERATOR to Regex("(==|!=|<=|>=|&&|\\|\\||[=+\\-*/<>!%])"),

        TokenType.DELIMITER to Regex("[;,(){}\\[\\]:]"),

        TokenType.WHITESPACE to Regex("\\s+")
    )

    fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var remaining = input

        while (remaining.isNotEmpty()) {
            var matched = false

            for ((type, pattern) in tokenPatterns) {
                val match = pattern.find(remaining)
                if (match != null && match.range.first == 0) {
                    if (type != TokenType.WHITESPACE) {
                        tokens.add(Token(type, match.value))
                    }
                    remaining = remaining.substring(match.range.last + 1)
                    matched = true
                    break
                }
            }

            if (!matched) {
                tokens.add(Token(TokenType.UNKNOWN, remaining[0].toString()))
                remaining = remaining.substring(1)
            }
        }

        return tokens
    }
}
