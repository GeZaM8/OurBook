package com.example.ourbooktm.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.example.ourbooktm.model.Person
import java.io.ByteArrayOutputStream

class DatabaseHelperOurBook(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "OurBook.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "nama"
        private const val COLUMN_NICKNAME = "nickname"
        private const val COLUMN_PHOTO = "photo"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_ADDRESS = "alamat"
        private const val COLUMN_BIRTH = "tglLahir"
        private const val COLUMN_NUMBER = "telp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME VARCHAR(50), " +
                "$COLUMN_NICKNAME VARCHAR(10), " +
                "$COLUMN_PHOTO BLOB, " +
                "$COLUMN_EMAIL VARCHAR(100), " +
                "$COLUMN_ADDRESS TEXT, " +
                "$COLUMN_BIRTH DATE, " +
                "$COLUMN_NUMBER VARCHAR(14)" +
                ")"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun getAllUser():List<Person> {
        val personsList = mutableListOf<Person>()
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
            val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
            val birth = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH))
            val number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMBER))

            val person = Person(id, name, nickname, email, address, birth, number, photo)
            personsList.add(person)
        }
        cursor.close()
        db.close()
        return personsList
    }

    fun getUserById(id: Int): Person {
        val db = writableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $id"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val nickname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NICKNAME))
        val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
        val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
        val birth = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH))
        val number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMBER))

        cursor.close()
        db.close()
        return Person(id, name, nickname, email, address, birth, number, photo)
    }

    fun insertUser(person: Person) {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_NAME, person.name)
            put(COLUMN_NICKNAME, person.nickname)
            put(COLUMN_PHOTO, person.photo)
            put(COLUMN_EMAIL, person.email)
            put(COLUMN_ADDRESS, person.address)
            put(COLUMN_BIRTH, person.birth)
            put(COLUMN_NUMBER, person.number)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateUser(person: Person) {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_NAME, person.name)
            put(COLUMN_NICKNAME, person.nickname)
            put(COLUMN_PHOTO, person.photo)
            put(COLUMN_EMAIL, person.email)
            put(COLUMN_ADDRESS, person.address)
            put(COLUMN_BIRTH, person.birth)
            put(COLUMN_NUMBER, person.number)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(person.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun deleteUser(id: Int) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(id.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun ImageViewToByte(img: ImageView): ByteArray {
        val bitmap: Bitmap = (img.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val bytes: ByteArray = stream.toByteArray()
        return bytes
    }
}