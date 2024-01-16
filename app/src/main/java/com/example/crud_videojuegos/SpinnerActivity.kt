package com.example.crud_videojuegos

import android.app.Activity
import android.view.View
import android.widget.AdapterView

class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }
}