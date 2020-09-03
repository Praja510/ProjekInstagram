package com.mprajadinata.projekinstagram

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_signin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btn_register.setOnClickListener {
            createAkun()
        }
    }

    private fun createAkun() {
        val fullname = edt_fullname_register.text.toString()
        val userName = edt_username_register.text.toString()
        val email = edt_email_register.text.toString()
        val password = edt_password_register.text.toString()

        when {
            TextUtils.isEmpty(fullname) -> Toast.makeText(
                this,
                "Fullname is Required",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(
                this,
                "Username is Required",
                Toast.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is Required", Toast.LENGTH_LONG)
                .show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "Password is Required",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Register")
                progressDialog.setMessage("Please Wait...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            saveUserInfo(fullname, userName, email, progressDialog)
                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, progressDialog: ProgressDialog) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()

        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Hy I am Student"
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/projekinstagram-8faf6.appspot.com/o/DefaultImage%2F80BEB5A9-3026-4077-AB6F-F7C59683CC3C.jpg?alt=media&token=8bfc7176-db0c-485e-87e0-dc2a28f9f090"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(this, "Akun Nya Udh Jadi Nih", Toast.LENGTH_LONG).show()
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                }else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
        }
    }
}