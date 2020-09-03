package com.mprajadinata.projekinstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mprajadinata.projekinstagram.fragments.HomeFragment
import com.mprajadinata.projekinstagram.fragments.LikeFragment
import com.mprajadinata.projekinstagram.fragments.ProfileFragment
import com.mprajadinata.projekinstagram.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener(onBottomNavListener)

        val frag = supportFragmentManager.beginTransaction()
        frag.add(R.id.fragContainer, HomeFragment())
        frag.commit()
    }

    private val onBottomNavListener = BottomNavigationView.OnNavigationItemSelectedListener { i ->
        var selectedFragment: Fragment = HomeFragment()

        when(i.itemId) {
            R.id.bott_home -> {
                selectedFragment = HomeFragment()
            }
            R.id.bott_search -> {
                selectedFragment = SearchFragment()
            }
            R.id.bott_addpost -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.bott_like -> {
                selectedFragment = LikeFragment()
            }
            R.id.bott_Profile -> {
                selectedFragment = ProfileFragment()
            }
        }

        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fragContainer, selectedFragment)
        frag.commit()

        true

    }
}
