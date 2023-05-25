package com.example.myphototags.ui.search

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.myphototags.database.AppDatabase
import com.example.myphototags.databinding.FragmentSearchBinding
import com.example.myphototags.entities.Imagini
import com.example.myphototags.Adapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var btnSearch: Button
    var array = arrayListOf<String>()
    private lateinit var checkbox_container: GridLayout
    lateinit var recyclerView: RecyclerView
    lateinit var photos: List<Imagini>
    lateinit var adapter: Adapter

    private val listener: Adapter.onItemClickListener = object : Adapter.onItemClickListener {
        override fun onItemClick(position: Int) {
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        btnSearch = binding.btnSearch as Button

        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().build()

        val tags = db.tagsDao().getALL()
        for(content in tags) // add content of checkbox in array (the tags from database)
            array.add(content.nume.toString())

        checkbox_container = binding.checkboxContainer

        if(array.isNotEmpty())
        {
            array.forEachIndexed { index, element ->
                val checkbox = CheckBox(context)
                checkbox.id = index
                checkbox.text = element
                checkbox_container.addView(checkbox)
            }
        }
        else
        {
            val textView = TextView(context)
            val id = 1
            textView.id = id
            textView.text = "No tags added yet"
            textView.setPadding(40,30,0,20)
            checkbox_container.addView(textView)
        }

        // --- Search button ---

        if(array.isEmpty()) // if we don't have tags in database
            btnSearch.isEnabled = false

        val tagIdsArray = arrayListOf<Int>()
        btnSearch.setOnClickListener {

            // Adaugam id-urile tag-urilor in lista tagIdsArray
            var i = 0
            val count = checkbox_container.childCount
            var checkOK = false

            while (i <= count) {
                val box = checkbox_container.getChildAt(i)
                if (box is CheckBox)
                    if (box.isChecked) {
                        checkOK = true
                        val idTag = db.tagsDao().findByName(box.text.toString())
                        tagIdsArray.add(idTag)
                    }
                i++
            }
            if(!checkOK)
                Toast.makeText(context,"Please select a tag",
                    Toast.LENGTH_SHORT).show()

            // Luam din database imaginile care au tag-urile din tagIdsArray
            photos = db.tagsDao().findImagesByTagIds(tagIdsArray)

            // Afisare poze cu tag-urile alese in RecyclerView
            recyclerView = binding.recyclerView
            val gridLayoutManager = GridLayoutManager(context, 2)
            recyclerView.layoutManager = gridLayoutManager

            adapter = Adapter(photos, listener)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()


            tagIdsArray.clear()

            val rotationAnimator = ObjectAnimator.ofFloat(btnSearch, "rotation", 0f, 360f)
            rotationAnimator.duration = 1000
            rotationAnimator.interpolator = LinearInterpolator()
            rotationAnimator.start()

        }

        binding.shareBtn?.setOnClickListener {
            val checkedTags = StringBuilder()

            val count = checkbox_container.childCount
            for (i in 0 until count) {
                val view = checkbox_container.getChildAt(i)
                if (view is CheckBox && view.isChecked) {
                    checkedTags.append(view.text)
                    checkedTags.append(", ") // Add a comma and space between each tag
                }
            }

            if (checkedTags.isNotEmpty()) {
                checkedTags.delete(checkedTags.length - 2, checkedTags.length) // Remove the last comma and space
                val tagsText = checkedTags.toString()

                // Use the 'tagsText' variable as needed, such as for sharing

                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,
                        "Hey! I take interest in the following categories: $tagsText. What about you?"
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            } else {
                Toast.makeText(context, "Please select at least one tag", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}