package com.example.tugasmokom

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import java.math.RoundingMode
import java.text.DecimalFormat

class CalculatorViewModel : ViewModel() {

    private val _equationText = MutableStateFlow("")
    val equationText: StateFlow<String> = _equationText

    private val _resultText = MutableStateFlow("0")
    val resultText: StateFlow<String> = _resultText

    private var lastResult: String = "0"

    // NOTES STATE UNTUK BERPINDAH TOMBOL SC-NOR
    private val _isScientific = MutableStateFlow(false)
    val isScientific: StateFlow<Boolean> = _isScientific

    fun toggleMode() {
        _isScientific.value = !_isScientific.value
    }

    private fun factorial(n: Int): Long {
        if (n < 0) return 0
        return if (n <= 1) 1 else n.toLong() * factorial(n - 1)
    }

    // BUTTON FUNGSI
    fun onButtonClick(btn: String) {

        // NOTES
        // LOGIKA TOMBOL
        if (btn == "SC" || btn == "NOR") {
            toggleMode()
            return
        }

        Log.i("Clicked Button", btn)
        val currentEquation = _equationText.value

        when (btn) {
            "AC" -> {
                _equationText.value = ""
                _resultText.value = "0"
                return
            }

            // ICON DELETE
            "⌫" -> {
                if (currentEquation.isNotEmpty()) {
                    _equationText.value = currentEquation.dropLast(1)
                }
            }
            "=" -> {
                lastResult = _resultText.value
                _equationText.value = _resultText.value
                return
            }
            "π" -> {
                _equationText.value = currentEquation + "π"
            }

            "1/x" -> _equationText.value = "1/(" + currentEquation.takeIf { it.isNotEmpty() } + ")"

            "x^y" -> _equationText.value = currentEquation + "**"

            "x!" -> {
                val numberRegex = Regex("(\\d+\\.?\\d*)$")
                val match = numberRegex.find(currentEquation)

                if (match != null) {
                    val number = match.value.toIntOrNull()
                    if (number != null && number >= 0) {
                        val factResult = factorial(number).toString()
                        _resultText.value = factResult
                        _equationText.value = currentEquation.replace(numberRegex, factResult)
                        return
                    }
                }
                return
            }

            "X" -> _equationText.value = currentEquation + "*"
            "%" -> _equationText.value = currentEquation + "/100"

            "SIN", "COS", "TAN", "ASIN", "ACOS", "ATAN", "LN", "LOG", "SQRT" -> {
                val uiFunction = btn.toLowerCase()
                _equationText.value = currentEquation + uiFunction + "("
            }

            else -> _equationText.value = currentEquation + btn
        }

        if (btn != "=" && btn != "AC" && btn != "x!" && btn != "SC" && btn != "NOR") {
            updateResult()
        }
    }

    private fun updateResult() {
        viewModelScope.launch {
            try {
                _resultText.value = calculateResult(_equationText.value)
            } catch (e: Exception) {
            }
        }
    }

    private fun calculateResult(equation: String): String {
        var cleanEquation = equation.trim().replace(Regex("[*+/\\-]{1,}$"), "")
        if (cleanEquation.isEmpty()) return "0"

        val functionMap = mapOf(
            "sin" to "Math.sin", "cos" to "Math.cos", "tan" to "Math.tan",
            "asin" to "Math.asin", "acos" to "Math.acos", "atan" to "Math.atan",
            "ln" to "Math.log", "log" to "Math.log10", "sqrt" to "Math.sqrt"
        )

        functionMap.forEach { (ui, math) ->
            cleanEquation = cleanEquation.replace(ui, math, ignoreCase = true)
        }

        cleanEquation = cleanEquation.replace("π", "Math.PI")


        cleanEquation = cleanEquation.replace(Regex("([\\d.]+)\\*\\*([\\d.]+)|(\\))\\*\\*([\\d.]+)")) { matchResult ->
            val base = matchResult.groupValues[1].takeIf { it.isNotEmpty() } ?: matchResult.groupValues[3]
            val exponent = matchResult.groupValues[2].takeIf { it.isNotEmpty() } ?: matchResult.groupValues[4]
            "Math.pow($base, $exponent)"
        }

        val context: Context = Context.enter()
        context.optimizationLevel = -1
        val scriptable: Scriptable = context.initStandardObjects()

        val resultString = try {
            context.evaluateString(scriptable, cleanEquation, "Javascript", 1, null).toString()
        } catch (e: Exception) {
            Context.exit()
            return _resultText.value
        }

        Context.exit()

        val resultDouble = resultString.toDoubleOrNull() ?: return "Error"

        val df = DecimalFormat("#.##########")
        df.roundingMode = RoundingMode.HALF_UP

        var finalResult = df.format(resultDouble).replace(',', '.')

        if (finalResult.endsWith(".0")) {
            finalResult = finalResult.replace(".0", "")
        }
        return finalResult
    }
}