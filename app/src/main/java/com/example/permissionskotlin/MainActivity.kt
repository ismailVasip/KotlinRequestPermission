package com.example.permissionskotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.permissionskotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var activityResultLauncher :ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher :ActivityResultLauncher<String>
    private var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        registerLauncher()

    }

    fun getPermission(view: View){
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission Needed!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        // request permission
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                } else {
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGalery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }

        } else{
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission Needed!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        // request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                } else {
                    // request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGalery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)
            }
        }


    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    val imageUri = intentFromResult.data
                    //binding.imageView.setImageURI(imageUri)
                    if(imageUri != null){
                        try {
                            if(Build.VERSION.SDK_INT >= 28){
                                val source = ImageDecoder.createSource(this@MainActivity.contentResolver,imageUri)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(this@MainActivity.contentResolver,imageUri)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }

                        } catch (e:Exception){
                            e.printStackTrace()
                        }
                    }

                }
            }

        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                val intentToGalery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGalery)

            } else {
                Toast.makeText(this@MainActivity,"Permission Needed!",Toast.LENGTH_LONG).show()
            }
        }
    }
}