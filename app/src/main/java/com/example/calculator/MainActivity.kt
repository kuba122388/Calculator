package com.example.calculator

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val simpleCalc: Button = findViewById(R.id.button3)
        val advancedCalc: Button = findViewById(R.id.button4)

        simpleCalc.setOnClickListener {
            val intent = Intent(this, ActivitySimpleCalc::class.java)
            startActivity(intent)
        }

        advancedCalc.setOnClickListener {
            val intent = Intent(this, ActivityAdvancedCalc::class.java)
            startActivity(intent)
        }

        updateGuideline()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateGuideline()
    }


    private fun updateGuideline() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.main)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            constraintSet.setGuidelinePercent(R.id.TopGuideline, 0.0f)
        }
        else {
            constraintSet.setGuidelinePercent(R.id.TopGuideline, 0.2f)
        }
        constraintSet.applyTo(constraintLayout)

    }


}