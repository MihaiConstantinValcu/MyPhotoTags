package com.example.myphototags.ui.video

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myphototags.R

import com.example.myphototags.databinding.FragmentVideoBinding
import java.util.*


class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var myVideoView: VideoView? = null
    private var myMediaController: MediaController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)

        myVideoView = binding.myVideoView

        setUpVideoPlayer()

        return binding.root
    }

    private fun setUpVideoPlayer(){
        if(myMediaController == null){
            myMediaController = MediaController(context)
            myMediaController!!.setAnchorView(this.myVideoView)
        }

        myVideoView!!.setMediaController(myMediaController)

        myVideoView!!.setVideoURI(
            Uri.parse("android.resource://"
            + requireContext().packageName +"/" + R.raw.myvideo)
        )

        myVideoView!!.requestFocus()

        myVideoView!!.pause()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}