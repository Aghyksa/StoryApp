package com.aghyksa.storyapp.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghyksa.storyapp.R
import com.aghyksa.storyapp.adapter.StoryAdapter
import com.aghyksa.storyapp.add.AddActivity
import com.aghyksa.storyapp.api.RetrofitClient
import com.aghyksa.storyapp.api.StoryRepository
import com.aghyksa.storyapp.databinding.ActivityMainBinding
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.detail.DetailActivity
import com.aghyksa.storyapp.login.LoginActivity
import com.aghyksa.storyapp.map.MapsActivity
import com.aghyksa.storyapp.model.Story
import com.aghyksa.storyapp.utils.GenericViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var token: String
    private val storyViewModel: StoryPagerViewModel by viewModels {
        GenericViewModelFactory.create(
            StoryPagerViewModel(StoryRepository(RetrofitClient.apiInstance, "Bearer $token"))
        )
    }
    private val viewModel: MainViewModel by viewModels {
        GenericViewModelFactory.create(
            MainViewModel(UserPreference.getInstance(dataStore))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setupUserRecyclerView()
        setupViewModel()
        setupListener()
    }



    private fun setupListener() {
        bind.btnAdd.setOnClickListener{
            startActivity(Intent(this,AddActivity::class.java))
        }
    }

    private fun setupUserRecyclerView() {
        adapter = StoryAdapter(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                Intent(this@MainActivity, DetailActivity::class.java).also {
                    it.putExtra(DetailActivity.EXTRA_ID, data.id)
                    startActivity(it)
                }
            }
        })
        with(bind) {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.adapter = adapter
        }
    }

    override fun onStart() {
        super.onStart()
        if(this::token.isInitialized){
            fetchStories()
        }
    }

    private fun fetchStories() {
        storyViewModel.refresh()
        storyViewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun setupViewModel() {
        with(viewModel) {
            getUser().observe(this@MainActivity) {
                if (!it.isLogin) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }else{
                    token=it.token
                    fetchStories()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.logout()
                true
            }
            R.id.menu_map -> {
                startActivity(Intent(this,MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}