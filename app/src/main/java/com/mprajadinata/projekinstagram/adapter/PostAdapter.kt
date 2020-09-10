package com.mprajadinata.projekinstagram.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mprajadinata.projekinstagram.MainActivity
import com.mprajadinata.projekinstagram.R
import com.mprajadinata.projekinstagram.model.Post
import com.mprajadinata.projekinstagram.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext: Context, private val mPost: List<Post>) :
    RecyclerView.Adapter<PostAdapterViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapterViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return PostAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: PostAdapterViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getPostImage()).into(holder.postImage)

        if (post.getDescription().equals("")) {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = post.getDescription()
        }

        publisherInfo(holder.profilImage, holder.userName, holder.publisher, post.getPublisher())

        isLikes(post.getPostId(), holder.likeButton)

        numberOfLike(holder.like, post.getPostId())

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like") {

                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(post.getPostId()).child(firebaseUser!!.uid)
                    .setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(post.getPostId()).child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }

    }

    private fun publisherInfo(
        profilImage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        publisherID: String
    ) {

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile)
                        .into(profilImage)

                    userName.text = user?.getUsername()
                    publisher.text = user?.getFullname()
                }
            }
        })
    }

    private fun isLikes(postid: String, likeButton: ImageView) {

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        likesRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.child(firebaseUser!!.uid).exists()) {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Liked"
                } else {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "Like"
                }
            }
        })
    }

    private fun numberOfLike(likes: TextView, postid: String) {

        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        likesRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    likes.text = snapshot.childrenCount.toString() + " likes"
                }
            }

        })
    }
}

class PostAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var profilImage: CircleImageView = itemView.findViewById(R.id.img_user_post)
    var postImage: ImageView = itemView.findViewById(R.id.img_main_post)
    var likeButton: ImageView = itemView.findViewById(R.id.btn_like_post)
    var commentButton: ImageView = itemView.findViewById(R.id.btn_comment_post)
    var saveButton: ImageView = itemView.findViewById(R.id.btn_save_post)
    var userName: TextView = itemView.findViewById(R.id.txt_username_post)
    var like: TextView = itemView.findViewById(R.id.txt_like_post)
    var publisher: TextView = itemView.findViewById(R.id.txt_publisher_post)
    var description: TextView = itemView.findViewById(R.id.txt_description_post)
    var comment: TextView = itemView.findViewById(R.id.txt_comment_post)


}
