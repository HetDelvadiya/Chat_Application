package com.awcindia.chatapplication.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.ActivityMainBinding
import com.awcindia.chatapplication.ui.fragment.CallFragment
import com.awcindia.chatapplication.ui.fragment.ChatFragment
import com.awcindia.chatapplication.ui.fragment.StatusFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChatFragment()).commit()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.chats -> {
                    loadFragment(ChatFragment())
                    true
                }

                R.id.status -> {
                    loadFragment(StatusFragment())
                    true
                }

                R.id.call -> {
                    loadFragment(CallFragment())
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_option_menu, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.queryHint = "Search"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                query?.let {

                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                newText?.let {

                }

                return false
            }
        })

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.setting -> {

                true
            }

            R.id.newChat -> {

                true
            }

            else -> super.onOptionsItemSelected(item)

        }
    }
}