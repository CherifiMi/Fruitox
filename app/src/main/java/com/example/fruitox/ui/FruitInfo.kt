package com.example.fruitox.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fruitox.R
import com.example.fruitox.databinding.FragmentFruitInfoBinding
import com.example.fruitox.util.NetworkResult
import com.example.fruitox.viewModel.MainViewModel

class FruitInfo : Fragment() {

    //............values
    private lateinit var mainViewModel : MainViewModel

    private var _binding: FragmentFruitInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFruitInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        readApi()

        return view
    }

    private fun readApi() {
        mainViewModel.getFruit("tomato")
        mainViewModel.myResponse.observe(viewLifecycleOwner, Observer {
            Log.d("datatest", it.name)
            Log.d("datatest", it.family)
            Log.d("datatest", it.genus)
            Log.d("datatest", it.nutritions.toString())
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}