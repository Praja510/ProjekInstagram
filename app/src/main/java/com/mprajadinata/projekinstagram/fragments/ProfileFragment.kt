package com.mprajadinata.projekinstagram.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mprajadinata.projekinstagram.EditProfilActivity
import com.mprajadinata.projekinstagram.R
import com.mprajadinata.projekinstagram.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    private lateinit var profileid: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewProfile = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileid = pref.getString("profileid", "none")!!
        }

        if (profileid == firebaseUser.uid) {
            view?.btn_edit_profile?.text = "Edit Profile"

        } else if (profileid != firebaseUser.uid) {

            cekFollowAndFollowingButtonStatus()
        }

        viewProfile.btn_edit_profile.setOnClickListener {
            startActivity(Intent(context, EditProfilActivity::class.java))

        }
        getFollowers()
        getFollowings()
        userInfo()
        return viewProfile
    }


    private fun cekFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {

                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileid).exists()) {
                        view?.btn_edit_profile?.text = "Following"
                    } else {
                        view?.btn_edit_profile?.text = "Follow"
                    }
                }
            })
        }
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileid)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.txt_total_followers?.text = snapshot.childrenCount.toString()
                }
            }
        })
    }

    private fun getFollowings() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileid)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.txt_total_followers?.text = snapshot.childrenCount.toString()

                }
            }
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(profileid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile)
                        .into(view?.profile_pic)
                    view?.profile_username?.text = user?.getUsername()
                    view?.txt_profile_fullname?.text = user?.getFullname()
                    view?.txt_profile_bio?.text = user?.getBio()
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid", firebaseUser.uid)
        pref?.apply()
    }
}