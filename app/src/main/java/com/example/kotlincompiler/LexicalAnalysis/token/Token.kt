package com.example.kotlincompiler.LexicalAnalysis.token

import com.example.kotlincompiler.LexicalAnalysis.tokenType.TokenType

data class Token(val type: TokenType, val value: String)
