package com.example.fruitox.ui.processing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fruitox.R
import processing.android.CompatUtils
import processing.android.PFragment


class Canvas : Fragment() {

    //..................values
    private lateinit var sketch : OnesZeros

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_canvas, container, false)

        view.id = CompatUtils.getUniqueViewId()


        sketch = OnesZeros()
        val fragment = PFragment(sketch)
        fragment.setView(view, requireActivity())

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        sketch.pause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        sketch.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
    }
}