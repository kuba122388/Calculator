package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.*

class ActivityAdvancedCalc : AppCompatActivity() {
    private var previousValue: String = ""
    private var displayedValue: String = "0"
    private var operation: String = ""
    private val arithmeticOperationsText =
        listOf('+', '-', '*', '/', '^')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_advanced_calc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeUI()
    }

    private fun initializeUI() {
        val resultView = findViewById<TextView>(R.id.Result)
        val backspace = findViewById<Button>(R.id.Backspace)
        val clear = findViewById<Button>(R.id.Clear)
        val sign = findViewById<Button>(R.id.PlusMinus)
        val equal = findViewById<Button>(R.id.Equal)
        val dot = findViewById<Button>(R.id.Dot)

        val arithmeticOperations = listOf(
            R.id.Divide, R.id.Multiply, R.id.Subtract, R.id.Add
        ).map { id -> findViewById<Button>(id) }

        val advancedOperations = listOf(
            R.id.Sinus,
            R.id.Cosinus,
            R.id.Tangens,
            R.id.SquareRoot,
            R.id.Square,
            R.id.Power,
            R.id.Logarithm,
            R.id.NaturalLog
        ).map { id -> findViewById<Button>(id) }

        val numbers = listOf(
            R.id.No0, R.id.No1, R.id.No2, R.id.No3, R.id.No4,
            R.id.No5, R.id.No6, R.id.No7, R.id.No8, R.id.No9
        ).map { id -> findViewById<Button>(id) }

        // Focuses right side while typing
        val hsv = findViewById<HorizontalScrollView>(R.id.HSView)
        fun updateScreen() {
            hsv.post {
                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
            }
        }

        numbers.forEach { button ->
            button.setOnClickListener {
                onNumberClick(
                    resultView,
                    button.text.toString()
                )
                updateScreen()
            }
        }

        arithmeticOperations.forEach { button ->
            button.setOnClickListener {
                onOperationClick(
                    resultView,
                    button.text.toString()
                )
                updateScreen()
            }
        }

        advancedOperations.forEach { button ->
            button.setOnClickListener {
                onAdvancedOperationClick(
                    resultView,
                    button.text.toString()
                )
            }
        }

        backspace.setOnClickListener { onBackspaceClick(resultView); updateScreen() }
        clear.setOnClickListener { onClearClick(resultView); updateScreen() }
        sign.setOnClickListener { onSignClick(resultView); updateScreen() }
        equal.setOnClickListener { onEqualClick(resultView); updateScreen() }
        dot.setOnClickListener { onDotClick(resultView); updateScreen() }
    }

    // If decimal fraction is .0 drop last 2 chars
    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0 && !value.toString().contains("E")) value.toString()
            .trimEnd('0').trimEnd('.')
        else {
            value.toString()
        }
    }

    private fun calculate(equalButton: Boolean) {
        // If it's equal sign, delete last operation if needed (e.g. 25*74- and then = )
        if (equalButton && displayedValue.last() in arithmeticOperationsText) {
            displayedValue = displayedValue.dropLast(1)
        }

        // - If first number was already entered and operation is chosen and it's not equal button:
        //    1. Calculate result
        //    2. Remember value by replacing [previousValue] with the result
        //    3. Prepare for the next number to be entered
        //
        // - If it is equal button:
        //    1. Calculate result
        //    2. Remember value by replacing [previousValue] with the result
        //
        // - If it's first operation and [previousValue] with arithmetic operation is not initialized
        //    1. Initialize arithmetic operation
        //    2. Remember value by replacing [previousValue] with the result

        if (previousValue.isNotEmpty() && operation.isNotEmpty() && !equalButton) {
            val result = when (operation) {
                "+" -> previousValue.toDouble() + displayedValue.dropLast(1).toDouble()
                "-" -> previousValue.toDouble() - displayedValue.dropLast(1).toDouble()
                "/" -> previousValue.toDouble() / displayedValue.dropLast(1).toDouble()
                "*" -> previousValue.toDouble() * displayedValue.dropLast(1).toDouble()
                "^" -> previousValue.toDouble().pow(displayedValue.dropLast(1).toDouble())
                else -> 0.0

            }
            previousValue = formatResult(result)
            displayedValue = "0"
        } else if (equalButton) {
            val result = when (operation) {
                "+" -> previousValue.toDouble() + displayedValue.toDouble()
                "-" -> previousValue.toDouble() - displayedValue.toDouble()
                "/" -> previousValue.toDouble() / displayedValue.toDouble()
                "*" -> previousValue.toDouble() * displayedValue.toDouble()
                "^" -> previousValue.toDouble().pow(displayedValue.toDouble())
                else -> 0.0
            }
            previousValue = formatResult(result)
            operation = ""
        } else {
            operation = displayedValue.last().toString()
            previousValue = displayedValue.dropLast(1)
        }

        if (previousValue == "-0") previousValue = "0"
    }

    private fun onNumberClick(resultView: TextView, number: String) {
        // Numbers listeners
        //   - If displayed number is only plain 0, replace it with the new number
        //   - If user chosen arithmetic operation, invoke a calculate function
        //     and replace displayed number with the new number
        //   - If it's big number, add another number to the displayed number
        //   - Update result view

        if (displayedValue == "0") displayedValue = number
        else if (displayedValue.last() in arithmeticOperationsText) {
            calculate(false)
            displayedValue = number
        } else displayedValue += number
        resultView.text = displayedValue
    }

    private fun onOperationClick(resultView: TextView, operation: String) {
        // Arithmetic operations listeners
        //   - If User chosen different arithmetic operation, replace it
        //   - If User chosen arithmetic operation for the first time, display it
        //   - Update result view

        if (displayedValue.last() in arithmeticOperationsText) {
            displayedValue = displayedValue.dropLast(1)
            displayedValue += operation
        } else displayedValue += operation
        resultView.text = displayedValue
    }

    private fun onAdvancedOperationClick(resultView: TextView, operation: String) {
        if (resultView.text.last() in arithmeticOperationsText) {
            displayedValue = displayedValue.dropLast(1)
        }

        if (operation == "x^y") {
            displayedValue += "^"
            resultView.text = displayedValue
            return
        }

        val result = when (operation) {
            "sin" -> {
                sin(displayedValue.toDouble())
            }

            "cos" -> {
                cos(displayedValue.toDouble())
            }

            "tan" -> {
                tan(displayedValue.toDouble())
            }

            "ln" -> {
                ln(displayedValue.toDouble())
            }

            "log" -> {
                log10(displayedValue.toDouble())
            }

            "sqrt" -> {
                sqrt(displayedValue.toDouble())
            }

            "x^2" -> {
                (displayedValue.toDouble() * displayedValue.toDouble())
            }

            else -> return
        }
        displayedValue = formatResult(result)
        resultView.text = displayedValue
        if (result.isInfinite() || result.isNaN()) {
            Toast.makeText(this, "Illegal operation", Toast.LENGTH_SHORT).show()
            displayedValue = "0"
            this.operation = ""
            resultView.text = displayedValue
        }
    }

    private fun onBackspaceClick(resultView: TextView) {
        if (displayedValue.length > 2 || (displayedValue.length == 2 && displayedValue[0] != '-')) {
            displayedValue = displayedValue.dropLast(1)
            if (displayedValue.last() == 'E') displayedValue = displayedValue.dropLast(1)
        } else displayedValue = "0"
        resultView.text = displayedValue
    }

    private fun onClearClick(resultView: TextView) {
        previousValue = ""
        operation = ""
        displayedValue = "0"
        resultView.text = displayedValue
    }

    private fun onSignClick(resultView: TextView) {
        displayedValue = if (displayedValue[0] == '-') displayedValue.drop(1)
        else if (displayedValue != "0") "-$displayedValue" else displayedValue
        resultView.text = displayedValue
    }

    private fun onEqualClick(resultView: TextView) {
        if (operation.isNotEmpty()) {
            calculate(true)
            displayedValue = previousValue
            resultView.text = previousValue
        }
        if (displayedValue.last() in arithmeticOperationsText) {
            return
        }
        if (displayedValue.toDouble().isNaN() || displayedValue.toDouble().isInfinite()) {
            Toast.makeText(this, "Illegal operation", Toast.LENGTH_SHORT).show()
            previousValue = ""
            operation = ""
            displayedValue = "0"
            resultView.text = displayedValue
        }
    }

    private fun onDotClick(resultView: TextView) {
        if (!displayedValue.contains('.') && displayedValue.last() !in arithmeticOperationsText
        ) displayedValue += '.'
        resultView.text = displayedValue
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("previousValue", previousValue)
        outState.putString("displayedValue", displayedValue)
        outState.putString("operation", operation)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        previousValue = savedInstanceState.getString("previousValue").toString()
        displayedValue = savedInstanceState.getString("displayedValue").toString()
        operation = savedInstanceState.getString("operation").toString()

        val result = findViewById<TextView>(R.id.Result)
        result.text = displayedValue

    }
}