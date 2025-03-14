package com.example.calculator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.round

class Activity_simple_calc : AppCompatActivity() {

    private var previousValue: String = ""
    private var displayedValue: String = "0"
    private var operation: String = ""
    private val arithmeticOperationsText = listOf("+", "-", "*", "/")

    private fun calculate(equalButton: Boolean) {

        // If decimal fraction is .0 drop last 2 chars
        fun formatResult(value: Double): String{
            return if (value % 1 == 0.0 && !value.toString().contains("E")) value.toString().trimEnd('0').trimEnd('.')
            else {
                value.toString()
            }
        }



        // If it's equal sign, delete last operation if needed (e.g. 25*74- and then = )
        if(equalButton && displayedValue.last().toString() in arithmeticOperationsText){
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
                else -> 0.0

            }
            previousValue = formatResult(result)
            displayedValue = "0"
        }
        else if (equalButton){
            val result = when (operation) {
                "+" -> previousValue.toDouble() + displayedValue.toDouble()
                "-" -> previousValue.toDouble() - displayedValue.toDouble()
                "/" -> previousValue.toDouble() / displayedValue.toDouble()
                "*" -> previousValue.toDouble() * displayedValue.toDouble()
                else -> 0.0
            }
            previousValue = formatResult(result)
            operation = ""
        }
        else {
            operation = displayedValue.last().toString()
            previousValue = displayedValue.dropLast(1)
        }

        if(previousValue == "-0") previousValue = "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_simple_calc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // DONE - Initialize variables
        val resultView = findViewById<TextView>(R.id.Result)
        val backspace = findViewById<Button>(R.id.Backspace)
        val clear = findViewById<Button>(R.id.Clear)
        val sign = findViewById<Button>(R.id.PlusMinus)
        val equal = findViewById<Button>(R.id.Equal)
        val dot = findViewById<Button>(R.id.Dot)

        // DONE Arithmetic Operations
        val arithmeticOperations = listOf(
            R.id.Divide, R.id.Multiply, R.id.Subtract, R.id.Add
        ).map{id->findViewById<Button>(id)}

        // DONE - Numbers
        val numbers = listOf(
            R.id.No0, R.id.No1, R.id.No2, R.id.No3, R.id.No4,
            R.id.No5, R.id.No6, R.id.No7, R.id.No8, R.id.No9
        ).map { id -> findViewById<Button>(id) }

        // Numbers listeners
        //   - If displayed number is only plain 0, replace it with the new number
        //   - If user chosen arithmetic operation, invoke a calculate function
        //     and replace displayed number with the new number
        //   - If it's big number, add another number to the displayed number
        //   - Update result view
        for (number in numbers) {
            number.setOnClickListener {
                if (displayedValue == "0") displayedValue = number.text.toString()
                else if(displayedValue.last().toString() in arithmeticOperationsText){
                    calculate(false)
                    displayedValue = number.text.toString()
                }
                else displayedValue += number.text.toString()
                resultView.text = displayedValue
            }
        }

        // Arithmetic operations listeners
        //   - If User chosen different arithmetic operation, replace it
        //   - If User chosen arithmetic operation for the first time, display it
        //   - Update result view
        for (operation in arithmeticOperations) {
            operation.setOnClickListener {
                if (displayedValue.last().toString() in arithmeticOperationsText){
                    displayedValue = displayedValue.dropLast(1)
                    displayedValue += operation.text.toString()
                }
                else displayedValue += operation.text.toString()
                resultView.text = displayedValue
            }

        }

        // DONE - Decimal fraction button
        dot.setOnClickListener {
            if (!displayedValue.contains('.') && displayedValue.last().toString() !in arithmeticOperationsText) displayedValue += '.'
            resultView.text = displayedValue
        }

        // DONE - Clear one digit button
        backspace.setOnClickListener {
            if (displayedValue.length > 2 || (displayedValue.length == 2 && displayedValue[0] != '-')) {
                displayedValue = displayedValue.dropLast(1)
                if (displayedValue.last() == 'E') displayedValue = displayedValue.dropLast(1)
            }
            else displayedValue = "0"
            resultView.text = displayedValue
        }

        // DONE - Clear button
        clear.setOnClickListener {
            previousValue = ""
            operation = ""
            displayedValue = "0"
            resultView.text = displayedValue
        }

        // DONE - Opposite sign button
        sign.setOnClickListener {
            if (displayedValue[0] == '-') displayedValue = displayedValue.drop(1)
            else displayedValue = if (displayedValue != "0") "-$displayedValue" else displayedValue
            resultView.text = displayedValue
        }

        // Equal button
        equal.setOnClickListener{
            if (operation.isNotEmpty()) {
                calculate(true)
                displayedValue = previousValue
                resultView.text = previousValue
            }
            if(displayedValue.last().toString() in arithmeticOperationsText){
                return@setOnClickListener
            }
            if(displayedValue.toDouble().isNaN() || displayedValue.toDouble().isInfinite()){
                previousValue = ""
                operation = ""
                displayedValue = "0"
            }
        }

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

        val Result = findViewById<TextView>(R.id.Result)
        Result.text = displayedValue

    }

}