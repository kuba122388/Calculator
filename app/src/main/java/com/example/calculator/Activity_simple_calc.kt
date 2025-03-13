package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity_simple_calc : AppCompatActivity() {

    private var previousValue: String = "";
    private var displayedValue: String = "0";
    private var operation: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_simple_calc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val resultView = findViewById<TextView>(R.id.Result)
        val backspace = findViewById<Button>(R.id.backspace)
        val clear = findViewById<Button>(R.id.Clear)
        val sign = findViewById<Button>(R.id.PlusMinus)

        val dot = findViewById<Button>(R.id.Dot)

        val arithmeticOperations = listOf(
            R.id.Divide, R.id.Multiply, R.id.Subtract, R.id.Add, R.id.Equal
        ).map{id->findViewById<Button>(id)}

        val arithmeticOperationsText = arithmeticOperations.map { item -> item.text.toString() }

        val numbers = listOf(
            R.id.No0, R.id.No1, R.id.No2, R.id.No3, R.id.No4,
            R.id.No5, R.id.No6, R.id.No7, R.id.No8, R.id.No9
        ).map { id -> findViewById<Button>(id) }


        for (number in numbers) {
            number.setOnClickListener {
                if (displayedValue == "0") displayedValue = number.text.toString()
                else if(displayedValue.last().toString() in arithmeticOperationsText){
                    previousValue = displayedValue
                    operation = displayedValue.last().toString()
                    displayedValue = number.text.toString()
                }
                else displayedValue += number.text.toString()
                resultView.text = displayedValue
            }
        }

        for (operation in arithmeticOperations) {
            operation.setOnClickListener {
                if (displayedValue.last() in listOf('+', '-', '*', '/')){
                    displayedValue = displayedValue.dropLast(1)
                    displayedValue += operation.text.toString()
                }
                else displayedValue += operation.text.toString()
                resultView.text = displayedValue
            }

        }

        dot.setOnClickListener {
            if (!displayedValue.contains('.') && displayedValue.last().toString() !in arithmeticOperationsText) displayedValue += '.'
            resultView.text = displayedValue
        }

        backspace.setOnClickListener {
            if (displayedValue.length > 2 || (displayedValue.length == 2 && displayedValue[0] != '-')) displayedValue =
                displayedValue.dropLast(1)
            else displayedValue = "0"
            resultView.text = displayedValue
        }

        clear.setOnClickListener {
            displayedValue = "0"
            resultView.text = displayedValue
        }

        sign.setOnClickListener {
            if (displayedValue[0] == '-') displayedValue = displayedValue.drop(1)
            else displayedValue = if (displayedValue != "0") "-$displayedValue" else displayedValue
            resultView.text = displayedValue
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("displayedValue", displayedValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val temp = savedInstanceState.getString("displayedValue")
        if (temp != null) {
            displayedValue = temp
        }

        val Result = findViewById<TextView>(R.id.Result)
        Result.text = displayedValue

    }

}