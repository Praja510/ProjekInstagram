package com.mprajadinata.projekinstagram

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.mprajadinata.projekinstagram.model.User
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profil.*

class EditProfilActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var cekInfoProfile = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicture: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicture = FirebaseStorage.getInstance().reference.child("Profil Picture")

        btn_logout_profil.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@EditProfilActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        txt_change_poto.setOnClickListener {
            cekInfoProfile = "clicked"
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@EditProfilActivity)

        }

        btn_save_profil.setOnClickListener {
            if (cekInfoProfile == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            img_set_profil.setImageURI(imageUri)
        }
    }


    private fun updateUserInfoOnly() {

        when {
            TextUtils.isEmpty(edt_fullname_profil.text.toString()) -> {
                Toast.makeText(this, "Tolong Diisi...", Toast.LENGTH_LONG).show()
            }

            edt_username_profil.text.toString() == "" -> {
                Toast.makeText(this, "Tolong Diisi...", Toast.LENGTH_LONG).show()
            }

            edt_bio_profil.text.toString() == "" -> {
                Toast.makeText(this, "Tolong Diisi...", Toast.LENGTH_LONG).show()
            }

            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference
                    .child("Users")

                val userMap = HashMap<String, Any>()

                userMap["fullname"] = edt_fullname_profil.text.toString().toLowerCase()
                userMap["username"] = edt_username_profil.text.toString().toLowerCase()
                userMap["bio"] = edt_bio_profil.text.toString().toLowerCase()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)
                Toast.makeText(this, "Info Profil Sudah di Update", Toast.LENGTH_LONG).show()

                val intent = Intent(this@EditProfilActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }

        }
    }

    private fun userInfo() {

        val usersRef = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    edt_username_profil.setText(user?.getUsername())
                    edt_fullname_profil.setText(user?.getFullname())
                    edt_bio_profil.setText(user?.getBio())
                }
            }
        })
    }

    private fun uploadImageAndUpdateInfo() {

        when {
            imageUri == null -> Toast.makeText(this, "Please Select Image", Toast.LENGTH_LONG)
                .show()
            TextUtils.isEmpty(edt_fullname_profil.text.toString()) -> {
                Toast.makeText(this, "Tolong Jangan Kosong...", Toast.LENGTH_LONG).show()
            }

            edt_username_profil.text.toString() == "" -> {
                Toast.makeText(this, " =Tolong Jangan Kosong...", Toast.LENGTH_LONG).show()
            }

            edt_bio_profil.text.toString() == "" -> {
                Toast.makeText(this, "Tolong Jangan Kosong...", Toast.LENGTH_LONG).show()
            }

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Update Gan..")
                progressDialog.setMessage("Please Wait a Minute...")
                progressDialog.show()

                val fileRef = storageProfilePicture!!.child(firebaseUser.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception.let {
//                            throw it!!
                        }
                    }

                    return@Continuation fileRef.downloadUrl

                }).addOnCompleteListener(
                    OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                            val userMap = HashMap<String, Any>()
                            userMap["fullname"] = edt_fullname_profil.text.toString().toLowerCase()
                            userMap["username"] = edt_username_profil.text.toString().toLowerCase()
                            userMap["bio"] = edt_bio_profil.text.toString().toLowerCase()
                            userMap["image"] = myUrl

                            usersRef.child(firebaseUser.uid).updateChildren(userMap)

                            Toast.makeText(this, "Info Profil Sudah di Update", Toast.LENGTH_LONG)
                                .show()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            progressDialog.dismiss()
                        } else {
                            progressDialog.dismiss()
                        }
                    })
            }
        }
    }
}

