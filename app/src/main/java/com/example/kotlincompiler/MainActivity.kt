package com.example.kotlincompiler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.kotlincompiler.LexicalAnalysis.lexical.Lexer
import com.example.kotlincompiler.ParserAnalysis.Parser
import com.example.kotlincompiler.SemanticAnalysis.SemanticAnalyzer
import com.example.kotlincompiler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            lexerBtn.setOnClickListener { runLexer(); showResult() }
            parserBtn.setOnClickListener { runParser(); showParserResult() }
            semanticBtn.setOnClickListener { runSemanticAnalysis(); showSemanticResult() }

            back.setOnClickListener {
                clearInput()
                goBack()
            }
        }
    }

    private fun runLexer() {
        val lexer = Lexer()
        val code = binding.addEditText.text.toString()
        val tokens = lexer.tokenize(code)

        val result = buildString {
            for (token in tokens) append("${token.type}: '${token.value}'\n")
        }

        binding.textView.text = result
    }

    private fun runParser() {
        val lexer = Lexer()
        val parser = Parser()
        val code = binding.addEditText.text.toString()
        val tokens = lexer.tokenize(code)

        val result = parser.parse(tokens)
        binding.textView.text = result
    }

    private fun runSemanticAnalysis() {
        val lexer = Lexer()
        val analyzer = SemanticAnalyzer()
        val code = binding.addEditText.text.toString()
        val tokens = lexer.tokenize(code)

        val errors = analyzer.analyze(tokens)
        val result = if (errors.isEmpty()) "No Semantic Error " else errors.joinToString("\n")
        binding.textView.text = result
    }

    private fun showResult() = binding.apply {
        lexerBtn.isVisible = false
        parserBtn.isVisible = false
        semanticBtn.isVisible = false
        addEditText.isVisible = false
        textView.isVisible = true
        back.isVisible = true
    }

    private fun showParserResult() = showResult()
    private fun showSemanticResult() = showResult()

    private fun goBack() = binding.apply {
        lexerBtn.isVisible = true
        parserBtn.isVisible = true
        semanticBtn.isVisible = true
        addEditText.isVisible = true
        textView.isVisible = false
        back.isVisible = false
    }

    private fun clearInput() {
        binding.addEditText.text = null
    }
}
