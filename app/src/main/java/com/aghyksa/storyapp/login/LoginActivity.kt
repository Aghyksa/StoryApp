package com.aghyksa.storyapp.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aghyksa.storyapp.databinding.ActivityLoginBinding
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.main.MainActivity
import com.aghyksa.storyapp.register.RegisterActivity
import com.aghyksa.storyapp.utils.GenericViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class LoginActivity : AppCompatActivity() {
    private lateinit var bind : ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels{
        GenericViewModelFactory.create(
            LoginViewModel(UserPreference.getInstance(dataStore))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setupView()
        setupViewModel()
        setupListener()
        playAnimation()
    }

    private fun setupViewModel() {
        with(viewModel){
            getMessage().observe(this@LoginActivity) {
                Toast.makeText(this@LoginActivity,it, Toast.LENGTH_LONG).show()
                showLoading(false)
            }
            getUser().observe(this@LoginActivity){
                if(it.isLogin){
                    Log.d("TAG", it.token)
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setupView() {
        bind.etEmail.setTextInputLayout(bind.tilEmail)
        bind.etPassword.setTextInputLayout(bind.tilPassword)
    }

    private fun setupListener() {
        bind.btnRegister.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }
        bind.btnLogin.setOnClickListener{
            if(bind.tilEmail.error==null&&bind.tilPassword.error==null){
                viewModel.login(bind.etEmail.text.toString(),bind.etPassword.text.toString())
                showLoading(true)
            }else{
                Toast.makeText(this,"Complete the data first",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun playAnimation() {
        val email =
            ObjectAnimator.ofFloat(bind.tilEmail, View.ALPHA, 1f)
                .setDuration(250)
        val password =
            ObjectAnimator.ofFloat(bind.tilPassword, View.ALPHA, 1f)
                .setDuration(250)
        val register =
            ObjectAnimator.ofFloat(bind.btnRegister, View.ALPHA, 1f)
                .setDuration(250)
        val login =
            ObjectAnimator.ofFloat(bind.btnLogin, View.ALPHA, 1f)
                .setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                email,
                password,
                login,
                register
            )
            startDelay = 250
        }.start()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            bind.cvProgress.visibility = View.VISIBLE
        } else {
            bind.cvProgress.visibility = View.GONE
        }
    }
}