package com.example.tipcalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tipcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge drawing (make sure to have androidx.activity:activity-ktx dependency)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets padding to root view
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                systemBarsInsets.bottom
            )
            insets
        }

        setupSpinner()
        setupListeners()
    }

    private fun setupSpinner() {
        val partySizes = (1..10).map { it.toString() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, partySizes)
        binding.partySizeSpinner.adapter = adapter
    }

    private fun setupListeners() {
        binding.tipSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.tipPercentLabel.text = "$progress%"
                updateTipDescription(progress)
                calculateTipAndTotal()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.baseAmountInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateTipAndTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.partySizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                calculateTipAndTotal()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateTipDescription(percent: Int) {
        val description = when {
            percent <= 9 -> "Poor"
            percent <= 14 -> "Acceptable"
            percent <= 20 -> "Good"
            percent <= 25 -> "Great"
            else -> "Amazing"
        }
        binding.tipDescription.text = description
    }

    private fun calculateTipAndTotal() {
        val baseAmountStr = binding.baseAmountInput.text.toString()
        if (baseAmountStr.isEmpty()) return

        val base = baseAmountStr.toDoubleOrNull() ?: return
        val tipPercent = binding.tipSeekBar.progress
        val partySize = binding.partySizeSpinner.selectedItem.toString().toIntOrNull() ?: 1

        val tip = base * tipPercent / 100
        val total = base + tip

        // Divide tip and total by party size
        binding.tipAmount.text = String.format("Tip: $%.2f", tip / partySize)
        binding.totalAmount.text = String.format("Total: $%.2f", total / partySize)
    }
}
