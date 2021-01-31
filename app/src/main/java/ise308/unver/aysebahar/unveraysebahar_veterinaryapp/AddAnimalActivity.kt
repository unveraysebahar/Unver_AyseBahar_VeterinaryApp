package ise308.unver.aysebahar.unveraysebahar_veterinaryapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_animal.*
import java.lang.Exception

class AddAnimalActivity : AppCompatActivity() {

    // PERMISSION CONSTANTS

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101

    // IMAGE PICK CONSTANTS

    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103

    // ARRAY OF PERMISSIONS

    private lateinit var cameraPermission: Array<String> // CAMERA AND STORAGE
    private lateinit var storagePermissions: Array<String> // ONLY STORAGE

    // VARIABLES THAT WILL CONTAIN DATA TO SAVE IN DATABASE

    private var imageUri:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_animal)

        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        animalImageView.setOnClickListener{
            imagePickDialog();
        }

        add_btn.setOnClickListener {
            if(editAnimalNameText.text.isEmpty() || editAnimalTypeText.text.isEmpty() || editAnimalAgeText.text.isEmpty() || editAnimalOwnerNumberText.text.isEmpty()){
                Toast.makeText(this,"FILL IN ALL THE INFORMATION", Toast.LENGTH_SHORT).show()
            }
            else{
                val animal = Animal()
                animal.animalName = editAnimalNameText.text.toString()
                animal.animalType = editAnimalTypeText.text.toString()
                animal.animalAge = editAnimalAgeText.text.toString().toIntOrNull()
                animal.animalOwnerNumber = editAnimalOwnerNumberText.text.toString()
                animal.vaccine = vaccineCheckBox.isChecked
                animal.image = imageUri.toString()

                MainActivity.database.addAnimal(this,animal)

                Toast.makeText(this,"ADDING SUCCESSFUL", Toast.LENGTH_SHORT).show()

                clear()
                finish()
            }
        }

        cancel_btn.setOnClickListener{
            clear()
            finish()
        }

    }

    private fun imagePickDialog() {
        val options = arrayOf("CAMERA", "GALLERY")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("PICK IMAGE FROM")
        builder.setItems(options){_, which ->
            val selected = options[which]
            if(which==0){
                if(!checkCameraPermissions()){
                    requestCameraPermission()
                }
                else{
                    pickFromCamera()
                }
            }
            else{
                if(!checkStoragePermission()){
                    requestStoragePermission()
                }
                else{
                    pickFromGallery()
                }
            }
        }
        val dialog=builder.create()
        dialog.show()
    }

    private fun pickFromGallery() {

        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*" // ONLY IMAGE TO BE PICKED
        startActivityForResult(
            galleryIntent,
            IMAGE_PICK_GALLERY_CODE
        )
    }

    private fun requestStoragePermission() {
        try {
            ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE)
        }catch (e:Exception){
            print(e)
        }
    }

    private fun checkStoragePermission(): Boolean {
        return  ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "IMAGE TITLE")
        values.put(MediaStore.Images.Media.DESCRIPTION, "IMAGE DESCRIPTION")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
            cameraIntent,
            IMAGE_PICK_CAMERA_CODE
        )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermissions(): Boolean {
        val results = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val results1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return results && results1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty()){
                    // IF ALLOWED RETURNS TRUE OTHERWISE FALSE
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera()
                    }
                    else{
                        Toast.makeText(this, "CAMERA AND STORAGE PERMISSIONS ARE REQUIRED", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if(grantResults.isEmpty()){
                    // IF ALLOWED RETURNS TRUE OTHERWISE FALSE
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if(storageAccepted){
                        pickFromGallery()
                    }
                    else{
                        Toast.makeText(this, "STORAGE PERMISSION IS REQUIRED", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // IMAGE PICKED FROM CAMERA OR GALLERY WILL BE RECEIVED HERE
        if(resultCode == Activity.RESULT_OK){
            // IMAGE IS PICKED
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                // PICKED FROM GALLERY
                // CROP IMAGE
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                // PICKED FROM CAMERA
                // CROP IMAGE
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }
            else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                // CROPPED IMAGES RECEIVED
                val result = CropImage.getActivityResult(data)
                if(resultCode == Activity.RESULT_OK){
                    val resultUri = result.uri
                    imageUri = resultUri
                    animalImageView.setImageURI(resultUri)
                }
                else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    // ERROR
                    val error = result.error
                    Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun clear(){
        editAnimalNameText.text.clear()
        editAnimalTypeText.text.clear()
        editAnimalAgeText.text.clear()
        editAnimalOwnerNumberText.text.clear()
    }
}