package com.example.myphototags.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.myphototags.Adapter
import com.example.myphototags.database.AppDatabase
import com.example.myphototags.databinding.FragmentHomeBinding
import com.example.myphototags.entities.Imagini

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var recyclerView: RecyclerView
    lateinit var photos: List<Imagini>
    lateinit var adapter: Adapter

    private val listener: Adapter.onItemClickListener = object : Adapter.onItemClickListener {
        override fun onItemClick(position: Int) {
            // Handle item click event
            // This method will be called when an item in the RecyclerView is clicked
            // You can perform any actions you want here
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        // Afisare poze din baza de date in RecyclerView
        recyclerView = binding.recyclerView
        var gridLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = gridLayoutManager

        photos = db.imaginiDao().getALL()

        // Daca nu a fost aleasa nicio poza, sa se afiseze un mesaj
        val noPhotoTextView = binding.noPhoto
        if(photos.isEmpty())
            noPhotoTextView.isVisible = true

        adapter = Adapter(photos, listener)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}