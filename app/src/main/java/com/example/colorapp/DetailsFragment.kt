package com.example.colorapp

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private lateinit var viewModel: ColorViewModel
    private var colorId: Int = 0
    private var currentColor: ColorItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // بيظهر السهم
            title = "Color Details"
        }

        viewModel = ViewModelProvider(this)[ColorViewModel::class.java]

        val colorView = view.findViewById<View>(R.id.detailColorView)
        val tvHex = view.findViewById<TextView>(R.id.tvDetailHex)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val tvName = view.findViewById<TextView>(R.id.tvColorName)

        colorId = arguments?.getInt("id") ?: 0
        val hex = arguments?.getString("hex") ?: "#000000"
        val name = arguments?.getString("name") ?: "Unnamed"

        colorView.setBackgroundColor(Color.parseColor(hex))
        tvHex.text = hex
        tvName.text = name

        view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            val colorToDelete = ColorItem(id = colorId, hexCode = hex, colorName = name, R = 0, G = 0, B = 0)
            viewModel.deleteColor(colorToDelete)
            findNavController().popBackStack()
        }
    }
}