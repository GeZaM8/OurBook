package com.example.ourbooktm

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.ourbooktm.database.DatabaseHelperOurBook
import com.example.ourbooktm.databinding.ActivityAddPersonBinding
import com.example.ourbooktm.model.Person
import com.squareup.picasso.Picasso
import java.util.Calendar

class AddPerson : AppCompatActivity() {
    private lateinit var binding: ActivityAddPersonBinding
    private lateinit var db: DatabaseHelperOurBook

    val CAMERA_REQUEST = 100
    val STORAGE_PERMISSION = 101

    val cameraPermissions: Array<String> = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val storagePermissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri: Uri? = result.uriContent
            Picasso.get().load(uri).into(binding.ivPhoto)
        } else {
            val error = result.error
            error?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelperOurBook(this)

        binding.ivPhoto.setOnClickListener {
            var avatar = 0
            if (avatar == 0) {
                if (!checkCameraPermission()) {
                    requstCameraPersmission()
                } else {
                    pickFromGallery()
                }
            } else if (avatar == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }

        binding.etTglLahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.etTglLahir.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        binding.btnSave.setOnClickListener {
            val fields = listOf(
                binding.etName,
                binding.etNickname,
                binding.etEmail,
                binding.etTglLahir,
                binding.etTelp
            )

            val empty: Array<Boolean> = Array(fields.size + 1) {true}
            for (i in 0 until (empty.size - 1)) { empty[i] = isEmptyEt(fields[i]) }
            empty[empty.size - 1] = isEmptyIv(binding.ivPhoto)

            if (empty.any { it }) return@setOnClickListener

            val email = binding.etEmail.text.toString().trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return@setOnClickListener binding.etEmail.setError("Wrong Email Format")

            val about = AlertDialog.Builder(this)
            about.setTitle("Add Person")
            about.setMessage("Are You Sure to Submit? (Data Can be Change)")
            about.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            about.setPositiveButton("Yes") { dialog, _ ->
                val name = binding.etName.text.toString().trim()
                val nickname = binding.etNickname.text.toString().trim()
                val address = binding.etAlamat.text.toString().trim()
                val birth = binding.etTglLahir.text.toString().trim()
                val number = binding.etTelp.text.toString().trim()

                val person = Person(0, name, nickname, email, address, birth, number, db.ImageViewToByte(binding.ivPhoto))
                db.insertUser(person)

                finish()
                Toast.makeText(this, "User Saved", Toast.LENGTH_SHORT).show()
            }
            about.show()
        }
    }

    private fun isEmptyEt(et: EditText): Boolean {
        if (et.text.isEmpty()) {
            et.setError("Input can't be empty")
            return true
        }
        return false
    }

    private fun isEmptyIv(iv: ImageView): Boolean {
        if (iv.drawable == null) {
            iv.setBackgroundResource(R.drawable.baseline_error_border)
            return true
        }
        return false
    }

    private fun requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_PERMISSION)
    }

    private fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun pickFromGallery() {
        val cropImageOptions = CropImageOptions().apply {
            aspectRatioX = 1
            aspectRatioY = 1
            fixAspectRatio = true
        }
        cropImageLauncher.launch(CropImageContractOptions(null, cropImageOptions))
    }

    private fun requstCameraPersmission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
        return result && result2
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Enable Camera and Storage Permissions", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_PERMISSION -> {
                if (grantResults.size > 0) {
                    val storegaAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storegaAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Enable Storage Permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

//    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                val resultUri = CropImage.getPickImageResultUriContent(this, data)
////                resultUri.let {
////                    Picasso.get().load(it).into(binding.ivPhoto)
////                }
//                binding.ivPhoto.setImageURI(resultUri)
//            }
//        }
//    }
}