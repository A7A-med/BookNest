package com.example.colorapp

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

class AddColorFragment : Fragment(R.layout.fragment_add_color) {

    private var r = 0; private var g = 0; private var b = 0
    private lateinit var viewModel: ColorViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ColorViewModel::class.java]

        val colorPreview = view.findViewById<View>(R.id.colorPreview)
        val tvHex = view.findViewById<TextView>(R.id.tvHexDisplay)
        val etName = view.findViewById<EditText>(R.id.etColorName)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        fun updateUI() {
            val hex = String.format("#%02X%02X%02X", r, g, b)
            colorPreview.setBackgroundColor(Color.rgb(r, g, b))
            tvHex.text = hex
        }

        val sbRed = view.findViewById<SeekBar>(R.id.sbRed)
        val sbGreen = view.findViewById<SeekBar>(R.id.sbGreen)
        val sbBlue = view.findViewById<SeekBar>(R.id.sbBlue)

        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                when(s?.id) {
                    R.id.sbRed -> r = p
                    R.id.sbGreen -> g = p
                    R.id.sbBlue -> b = p
                }
                updateUI()
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        }

        sbRed.setOnSeekBarChangeListener(listener)
        sbGreen.setOnSeekBarChangeListener(listener)
        sbBlue.setOnSeekBarChangeListener(listener)

        btnSave.setOnClickListener {
            val hex = String.format("#%02X%02X%02X", r, g, b)
            val name = etName.text.toString()
            val finalName = if (name.isEmpty()) "" else name
            viewModel.addColor(ColorItem(hexCode = hex, colorName = finalName, R = r, G = g, B = b))
            findNavController().popBackStack()
        }
    }
}