package com.example.colorapp

import android.os.Bundle
import android.provider.Settings.Global.putString
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var viewModel: ColorViewModel
    private lateinit var adapter: ColorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ColorViewModel::class.java]

        adapter = ColorAdapter(
            onDeleteClick = { colorItem -> viewModel.deleteColor(colorItem) },
            onItemClick = { colorItem ->
                val bundle = Bundle().apply {
                    putInt("id", colorItem.id)
                    putString("hex", colorItem.hexCode)
                    putString("name", colorItem.colorName)
                }
                findNavController().navigate(R.id.action_mainFragment_to_detailsFragment, bundle)
            }
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allColors.observe(viewLifecycleOwner) { colors ->
            adapter.submitList(colors)
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.addColorBtn)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addColorFragment)
        }
    }
}