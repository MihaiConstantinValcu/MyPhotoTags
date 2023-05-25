package com.example.myphototags.ui.addphoto

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.BitmapCompat
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.myphototags.R
import com.example.myphototags.database.AppDatabase
import com.example.myphototags.databinding.FragmentAddphotoBinding
import com.example.myphototags.entities.Imagini
import com.example.myphototags.entities.TagImagini
import com.example.myphototags.entities.Tags

@Suppress("DEPRECATION")
class AddphotoFragment : Fragment() {

    private var _binding: FragmentAddphotoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    lateinit var setTag: EditText
    var fileName: String = ""
    lateinit var bitmap: Bitmap
    var array = arrayListOf<String>()
    val REQUEST_IMAGE_CAPTURE = 100

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val pickImage = 100
        _binding = FragmentAddphotoBinding.inflate(inflater, container, false)

        // Connect to DB
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "database"
        ).allowMainThreadQueries().build()

        // --- Generate Check Box ---
        val tags = db.tagsDao().getALL()
        for (content in tags)
            array.add(content.nume.toString())


        if (!array.isEmpty()) {
            array.forEachIndexed { index, element ->
                val checkbox = CheckBox(context)
                checkbox.id = index
                checkbox.text = element
                binding.checkboxContainer.addView(checkbox)
            }
        } else {
            val textView = TextView(context)
            val id = 1
            textView.id = id
            textView.text = "No tags added yet"
            textView.setPadding(40, 30, 0, 20)
            binding.checkboxContainer.addView(textView)
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    imageUri = data?.data
                    binding.photoImageView.setImageURI(imageUri)

                    binding.btnSave.isEnabled = true
                    binding.btnCancel.isEnabled = true

                    bitmap = MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        imageUri
                    )
                    fileName = imageUri?.lastPathSegment.toString()

                }
            }

        binding.addPhotoBtn.setOnClickListener() {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(gallery)
        }

        setTag = binding.setTag
        val count = binding.checkboxContainer.childCount

        // Take Photo button

        binding.takePhotoBtn.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try{
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException){
                Toast.makeText(context, "Error: " + e.localizedMessage, Toast.LENGTH_SHORT).show()
            }


        }

        // Cancel Button
        binding.btnCancel.setOnClickListener {
            binding.photoImageView.setImageResource(R.drawable.placeholder)
            setTag.setText("")

            var i=0
            while(i <= count)
            {
                val box = binding.checkboxContainer.getChildAt(i)
                if (box is CheckBox)
                    if(box.isChecked)
                        box.isChecked = false
                i++
            }
            binding.btnSave.isEnabled = false
            binding.btnCancel.isEnabled = false

            val rotationAnimator = ObjectAnimator.ofFloat(binding.btnCancel, "rotation", 0f, 360f)
            rotationAnimator.duration = 1000
            rotationAnimator.interpolator = LinearInterpolator()
            rotationAnimator.start()
        }


        binding.btnSave.setOnClickListener {
            var i: Int
            var checkOK: Boolean
            var tagOK: Boolean

            if (binding.photoImageView.drawable.constantState == resources.getDrawable(R.drawable.placeholder).constantState || binding.photoImageView.drawable == null)
                Toast.makeText(context, "Please select a photo", Toast.LENGTH_SHORT).show()
            else {
                i = 0
                checkOK = false // checkbox is not checked

                if (array.isNotEmpty()) // if we have tags in database
                {
                    // Verify in OK if a checkbox is checked
                    while (i <= count) {
                        val box = binding.checkboxContainer.getChildAt(i)
                        if (box is CheckBox)
                            if (box.isChecked) {
                                checkOK = true
                                break
                            }
                        i++
                    }
                }

                // Verify in tagOK if a tag is set manually
                tagOK = false //no tag entered
                if(!setTag.text.contentEquals(""))
                    tagOK = true

                if(tagOK || checkOK) // if a tag is entered or selected, add to database
                {
                    // compress bitmap if size >= 2MB
                    val numDigits = BitmapCompat.getAllocationByteCount(bitmap).toString().length
                    if(numDigits >= 8)
                    {
                        val maxHeight: Int
                        val maxWidth: Int
                        if(bitmap.width > bitmap.height)
                        {
                            maxHeight = 1280
                            maxWidth = 1920
                        }
                        else
                        {
                            maxHeight = 1920
                            maxWidth = 1280
                        }

                        val scale = Math.min(maxHeight.toFloat() / bitmap.width,
                            maxWidth.toFloat() / bitmap.height)

                        val matrix = Matrix()
                        matrix.postScale(scale, scale)

                        bitmap = Bitmap.createBitmap(bitmap,
                            0,
                            0,
                            bitmap.width,
                            bitmap.height,
                            matrix,
                            true)
                    }
                    var imagini = db.imaginiDao().getALL()
                    val id_img = imagini.size + 1
                    db.imaginiDao().insertAll(Imagini(id_img, fileName, bitmap))

                    if(tagOK)
                    {
                        var OK = true // putem insera tag-ul in database
                        var tag = db.tagsDao().getALL()
                        var idTag = tag.size + 1

                        for(content in tag)
                            if(content.nume.contentEquals(setTag.text.trim().toString()))
                            {
                                idTag = content.id
                                OK = false // tag-ul exista in database
                            }

                        if(OK)
                        {
                            if(!tags.isEmpty())
                                idTag = tag.last().id + 1
                            db.tagsDao().insertAll(Tags(idTag, setTag.text.trim().toString()))
                        }

                        var tagImg = db.tagImaginiDao().getALL()
                        if(!tagImg.isEmpty())
                            db.tagImaginiDao().insertAll(TagImagini(tagImg.last().id + 1, id_img, idTag))
                        else db.tagImaginiDao().insertAll(TagImagini(tagImg.size + 1, id_img, idTag))
                    }

                    if(checkOK)
                    {
                        i = 0
                        while(i <= count)
                        {
                            val box = binding.checkboxContainer.getChildAt(i)
                            if (box is CheckBox)
                                if(box.isChecked && !box.text.toString().contentEquals(setTag.text.toString()))
                                {
                                    var tagImg = db.tagImaginiDao().getALL()
                                    var idTag = db.tagsDao().findByName(box.text.toString())

                                    if(!tagImg.isEmpty())
                                        db.tagImaginiDao().insertAll(TagImagini(tagImg.last().id + 1, id_img, idTag))
                                    else db.tagImaginiDao().insertAll(TagImagini(tagImg.size + 1, id_img, idTag))
                                }
                            i++
                        }
                    }

                    Toast.makeText(context, "Photo saved!", Toast.LENGTH_SHORT).show()
                }
            }

            val rotationAnimator = ObjectAnimator.ofFloat(binding.btnSave, "rotation", 0f, 360f)
            rotationAnimator.duration = 1000
            rotationAnimator.interpolator = LinearInterpolator()
            rotationAnimator.start()
        }

        binding.btnSave.isEnabled = false
        binding.btnCancel.isEnabled = false

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
         if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
             val imageBitmap = data?.extras?.get("data") as Bitmap
             binding.photoImageView.setImageBitmap(imageBitmap)
         }
        else {
            super.onActivityResult(requestCode, resultCode, data)
         }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}