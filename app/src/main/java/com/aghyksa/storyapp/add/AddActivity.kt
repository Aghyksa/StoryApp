package com.aghyksa.storyapp.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aghyksa.storyapp.R
import com.aghyksa.storyapp.camera.CameraActivity
import com.aghyksa.storyapp.databinding.ActivityAddBinding
import com.aghyksa.storyapp.datastore.UserPreference
import com.aghyksa.storyapp.login.LoginActivity
import com.aghyksa.storyapp.utils.GenericViewModelFactory
import com.aghyksa.storyapp.utils.rotateBitmap
import com.aghyksa.storyapp.utils.uriToFile
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class AddActivity : AppCompatActivity() {
    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE = 100
    }

    private val viewModel: AddViewModel by viewModels {
        GenericViewModelFactory.create(
            AddViewModel(UserPreference.getInstance(dataStore))
        )
    }

    private lateinit var token: String
    private lateinit var bind: ActivityAddBinding
    private var file: File? = null

    private val launcherCameraXIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val theFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            file = theFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(theFile.path),
                isBackCamera
            )
            bind.ivAdd.setImageBitmap(result)
        }
    }

    private val launcherGalleryIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val theFile = uriToFile(selectedImg, this@AddActivity)
            file = theFile
            bind.ivAdd.setImageURI(selectedImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAddBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setupViewModel()
        setupListener()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUEST_PERMISSIONS,
                REQUEST_CODE
            )
        }
    }

    private fun setupViewModel() {
        with(viewModel) {
            getUser().observe(this@AddActivity) {
                if (!it.isLogin) {
                    startActivity(Intent(this@AddActivity, LoginActivity::class.java))
                    finish()
                }else{
                    token=it.token
                }
            }
            getMessage().observe(this@AddActivity) {

                Toast.makeText(this@AddActivity, it, Toast.LENGTH_LONG).show()
                showLoading(false)
            }
            getUploadResponse().observe(this@AddActivity) {
                if(!it.error){
                    finish()
                }
                showLoading(false)
            }
        }
    }


    private fun setupListener() {
        bind.btnCamera.setOnClickListener{
            openCamera()
        }
        bind.btnGallery.setOnClickListener{
            openGallery()
        }
        bind.btnUpload.setOnClickListener{
            if(this::token.isInitialized){
                if(bind.etDescription.text.toString().isEmpty()){
                    bind.tilDescription.error="Description must be filled"
                    return@setOnClickListener
                }else{
                    bind.tilDescription.isErrorEnabled=false
                }
                if(file==null){
                    Toast.makeText(this,"Upload image first",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                showLoading(true)
                viewModel.upload(token,file!!,bind.etDescription.text.toString())
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherCameraXIntent.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose Your Picture")
        launcherGalleryIntent.launch(chooser)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.notallowed), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUEST_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            bind.cvProgress.visibility = View.VISIBLE
        } else {
            bind.cvProgress.visibility = View.GONE
        }
    }
}