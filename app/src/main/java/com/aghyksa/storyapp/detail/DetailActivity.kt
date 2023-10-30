package com.aghyksa.storyapp.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aghyksa.storyapp.databinding.ActivityDetailBinding
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.login.LoginActivity
import com.aghyksa.storyapp.main.MainViewModel
import com.aghyksa.storyapp.utils.GenericViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class DetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_ID = "ID"
    }
    private lateinit var bind: ActivityDetailBinding
    private var id: String? = null
    private val viewModel: DetailViewModel by viewModels {
        GenericViewModelFactory.create(
            DetailViewModel(UserPreference.getInstance(dataStore),id!!)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)
        id = intent.getStringExtra(EXTRA_ID)
        setupViewModel()
    }

    private fun setupViewModel() {
        with(viewModel) {
            getUser().observe(this@DetailActivity) {
                fetchStory(it.token)
            }
            getMessage().observe(this@DetailActivity) {
                Toast.makeText(this@DetailActivity, it, Toast.LENGTH_LONG).show()
                showLoading(false)
            }
            getStory().observe(this@DetailActivity) {
                with(bind){
                    Glide.with(this@DetailActivity)
                        .load(it.photoUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .into(ivStory)
                    tvName.text = it.name
                    tvDesc.text = it.description
                }
                showLoading(false)
            }
        }
    }

    private fun fetchStory(token: String) {
        viewModel.setStory(token)
        showLoading(true)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            bind.cvProgress.visibility = View.VISIBLE
        } else {
            bind.cvProgress.visibility = View.GONE
        }
    }
}