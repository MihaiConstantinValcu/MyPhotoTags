package com.example.myphototags

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myphototags.R
import com.example.myphototags.entities.Imagini


@Suppress("DEPRECATION")
class Adapter(private val dataList: List<Imagini>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        holder.bindItems(dataList[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //the class is hodling the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(content: Imagini) {
            val imageView = itemView.findViewById(R.id.list_item_image_view) as ImageView
            imageView.setImageBitmap(content.bitmap)

        }

    }


}