package com.example.ourbooktm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ourbooktm.database.DatabaseHelperOurBook
import com.example.ourbooktm.databinding.PersonItemBinding
import com.example.ourbooktm.model.Person

class PersonsAdapter(private var person: List<Person>): RecyclerView.Adapter<PersonsAdapter.ViewHolder>() {

    class ViewHolder(val binding: PersonItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PersonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return person.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = person[position]

        val img = person.photo
        val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)

        holder.binding.textNama.text = person.name
        holder.binding.textPanggilan.text = person.nickname
        holder.binding.texttelp.text = person.number
        holder.binding.textEmail.text = person.email
        holder.binding.textAlamat.text = person.address
        holder.binding.textTglLahir.text = person.birth
        holder.binding.foto.setImageBitmap(bitmap)

        holder.binding.btnEdit.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdatePerson::class.java).apply {
                putExtra("person_id", person.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.binding.btnDelete.setOnClickListener {
            val about = AlertDialog.Builder(holder.itemView.context)
            about.setTitle("Delete Person")
            about.setMessage("Are You Sure to Delete It? (Data Can't be Return)")
            about.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            about.setPositiveButton("Yes") { dialog, _ ->
                val db = DatabaseHelperOurBook(holder.itemView.context)
                db.deleteUser(person.id)
                refreshData(db.getAllUser())
                Toast.makeText(holder.itemView.context, "Person Deleted", Toast.LENGTH_SHORT).show()
            }
            about.show()
        }

        holder.binding.call.setOnClickListener {
            val about = AlertDialog.Builder(holder.itemView.context)
            about.setTitle("Call Person")
            about.setMessage("Are You Sure Want To Call This Person?")
            about.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            about.setPositiveButton("Yes") { dialog, _ ->
                val phoneNumber = holder.binding.texttelp.text.toString()
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")

                if (ActivityCompat.checkSelfPermission(holder.itemView.context, android.Manifest.permission.CALL_PHONE) == (PackageManager.PERMISSION_GRANTED)) {
                        holder.itemView.context.startActivity(intent)
                } else {
                    Toast.makeText(holder.itemView.context, "Permission untuk panggilan telepon diperlukan", Toast.LENGTH_SHORT).show()
                    ActivityCompat.requestPermissions(holder.itemView.context as Activity, arrayOf(android.Manifest.permission.CALL_PHONE), 1)
                }
            }
            about.show()
        }
    }

    fun refreshData(newPerson: List<Person>) {
        person = newPerson
        notifyDataSetChanged()
    }
}