package com.aghyksa.storyapp.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aghyksa.storyapp.databinding.ActivityRegisterBinding
import com.aghyksa.storyapp.login.LoginActivity
import com.aghyksa.storyapp.utils.GenericViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var bind : ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels{
        GenericViewModelFactory.create(
            RegisterViewModel()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setupView()
        setupViewModel()
        setupListener()
        playAnimation()
    }

    private fun setupViewModel() {
        viewModel.getMessage().observe(this) {
            Toast.makeText(this,it,Toast.LENGTH_LONG).show()
            showLoading(false)
        }
    }

    private fun setupView() {
        bind.etEmail.setTextInputLayout(bind.tilEmail)
        bind.etPassword.setTextInputLayout(bind.tilPassword)
        bind.etName.setTextInputLayout(bind.tilName)
    }

    private fun setupListener() {
        bind.btnLogin.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        bind.btnRegister.setOnClickListener{
            if(bind.tilName.error==null&&bind.tilEmail.error==null&&bind.tilPassword.error==null){
                viewModel.register(bind.etName.text.toString(),bind.etEmail.text.toString(),bind.etPassword.text.toString())
                showLoading(true)
            }else{
                Toast.makeText(this,"Complete the data first",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun playAnimation() {
        val name =
            ObjectAnimator.ofFloat(bind.tilName, View.ALPHA, 1f)
                .setDuration(250)
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
                name,
                email,
                password,
                register,
                login
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