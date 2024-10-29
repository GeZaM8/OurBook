package com.example.ourbooktm

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.ourbooktm.database.DatabaseHelperOurBook
import com.example.ourbooktm.databinding.ActivityUpdatePersonBinding
import com.example.ourbooktm.model.Person
import com.squareup.picasso.Picasso
import java.util.Calendar

class UpdatePerson : AppCompatActivity() {
    private lateinit var binding: ActivityUpdatePersonBinding
    private lateinit var db: DatabaseHelperOurBook
    private var personId: Int = -1

    val CAMERA_REQUEST = 100
    val STORAGE_PERMISSION = 101

    val cameraPermissions: Array<String> = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val storagePermissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri: Uri? = result.uriContent
            Picasso.get().load(uri).into(binding.foto)
        } else {
            val error = result.error
            error?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelperOurBook(this)

        personId = intent.getIntExtra("person_id", -1)
        if (personId == -1) {
            finish()
            return
        }

        val person = db.getUserById(personId)

        val img = person.photo
        val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)

        binding.etName.setText(person.name)
        binding.etNickname.setText(person.nickname)
        binding.etTelp.setText(person.number)
        binding.etEmail.setText(person.email)
        binding.etAlamat.setText(person.address)
        binding.etTglLahir.setText(person.birth)
        binding.foto.setImageBitmap(bitmap)

        binding.foto.setOnClickListener {
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
                binding.etAlamat,
                binding.etTglLahir,
                binding.etTelp
            )

            val empty: Array<Boolean> = Array(fields.size + 1) {true}
            for (i in 0 until (empty.size - 1)) { empty[i] = isEmptyEt(fields[i]) }
            empty[empty.size - 1] = isEmptyIv(binding.foto)

            if (empty.any { it }) return@setOnClickListener

            val about = AlertDialog.Builder(this)
            about.setTitle("Update Person")
            about.setMessage("Are You Sure to Submit?")
            about.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            about.setPositiveButton("Yes") { dialog, _ ->
                val name = binding.etName.text.toString()
                val nickname = binding.etNickname.text.toString()
                val email = binding.etEmail.text.toString()
                val address = binding.etAlamat.text.toString()
                val birth = binding.etTglLahir.text.toString()
                val number = binding.etTelp.text.toString()

                val updatePerson = Person(personId, name, nickname, email, address, birth, number, db.ImageViewToByte(binding.foto))

                db.updateUser(updatePerson)
                finish()
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
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
}