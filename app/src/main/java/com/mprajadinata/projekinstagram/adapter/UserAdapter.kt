package com.mprajadinata.projekinstagram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mprajadinata.projekinstagram.R
import com.mprajadinata.projekinstagram.fragments.ProfileFragment
import com.mprajadinata.projekinstagram.model.User
import com.squareup.picasso.Picasso

class UserAdapter (private var mContext: Context, private val mUser: List<User>):
    RecyclerView.Adapter<UserAdpterViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdpterViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.list_search_layout, parent, false)
        return UserAdpterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdpterViewHolder, position: Int) {

        val user = mUser[position]
        holder.usernameTxtView.text = user.getUsername()
        holder.fullnameTxtView.text = user.getFullname()
        Picasso.get()
            .load(user.getImage())
            .into(holder.userProfilImage)

        cekFollowStatus(user.getUID(), holder.followButton)

        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileid", user.getUID())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, ProfileFragment()).commit()
        }

        holder.followButton.setOnClickListener {

            if (holder.followButton.text.toString() == "Follow") {
                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            } else {

                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun cekFollowStatus(uid: String, followButton: Button) {

        val followingRef = firebaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()) {
                    followButton.text = "Following"
                } else {
                    followButton.text = "Follow"
                }
            }
        })
    }
}

class UserAdpterViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    val usernameTxtView: TextView = itemView.findViewById(R.id.txt_username_search)
    val fullnameTxtView: TextView = itemView.findViewById(R.id.txt_fullname_search)
    val userProfilImage: ImageView = itemView.findViewById(R.id.img_profil_search)
    val followButton: Button = itemView.findViewById(R.id.btn_search_profile)

}
