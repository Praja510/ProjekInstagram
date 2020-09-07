package com.mprajadinata.projekinstagram.fragments

import android.icu.text.CaseMap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mprajadinata.projekinstagram.R
import com.mprajadinata.projekinstagram.adapter.UserAdapter
import com.mprajadinata.projekinstagram.model.User
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {

    private var recycler: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var myUser: MutableList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recycler = view.findViewById(R.id.recycler_search)
        recycler?.setHasFixedSize(true)
        recycler?.layoutManager = GridLayoutManager(context, 2)

        myUser = ArrayList()
        userAdapter = context?.let {
            it.let { it1 -> UserAdapter(it1, myUser as ArrayList<User>) }
        }

        recycler?.adapter = userAdapter

        view.edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.edt_search.toString() == "") {

                } else {
                    recycler?.visibility = View.VISIBLE
                    getUser()
                    searchUser(p0.toString().toLowerCase())
                }
            }
        })

        return view
    }

    private fun searchUser(toLower: String) {
        val query = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullname")
            .startAt(toLower).endAt(toLower + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                myUser?.clear()

                for (s in snapshot.children) {
                    val user = s.getValue(User::class.java)
                    if (user != null) {
                        myUser?.add(user)
                    }
                }

                userAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun getUser() {

        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (view?.edt_search?.text.toString() == "")
                    myUser?.clear()

                for (s in snapshot.children) {
                    val user = s.getValue(User::class.java)
                    if (user != null) {
                        myUser?.add(user)
                    }
                }

                userAdapter?.notifyDataSetChanged()
            }
        })
    }
}