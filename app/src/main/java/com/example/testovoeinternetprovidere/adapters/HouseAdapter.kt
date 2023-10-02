package com.example.testovoeinternetprovidere.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.example.testovoeinternetprovidere.R

class HouseAdapter(context: Context, private val dataList: List<String>) :
    ArrayAdapter<String>(context, R.layout.street_item, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.street_item, parent, false)

        val textView = view.findViewById<TextView>(R.id.street_name)
        textView.text = dataList[position]
        return view
    }
}