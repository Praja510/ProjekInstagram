package com.mprajadinata.projekinstagram.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mprajadinata.projekinstagram.EditProfilActivity
import com.mprajadinata.projekinstagram.R
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewProfile = inflater.inflate(R.layout.fragment_profile, container, false)

        viewProfile.btn_edit_profile.setOnClickListener {
            startActivity(Intent(context, EditProfilActivity::class.java))

        }
        return viewProfile
    }
}