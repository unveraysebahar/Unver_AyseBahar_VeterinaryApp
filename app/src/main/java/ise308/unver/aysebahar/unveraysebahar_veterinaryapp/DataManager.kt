package ise308.unver.aysebahar.unveraysebahar_veterinaryapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val TAG = "DataManager"

class DataManager(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int ):
    SQLiteOpenHelper(context, DB_NAME, factory, DB_VERSION){

    val TABLE_ANIMAL = "Animal"
    var ID = "animalID"
    var ANIMALNAME = "animalName"
    var ANIMALTYPE = "animalType"
    var ANIMALAGE = "animalAge"
    var ANIMALOWNERNUMBER = "animalOwnerNumber"
    var VACCINE = "vaccine"
    var IMAGE = "image"

    companion object{
        private const val DB_NAME = "Veterinary.db"
        private val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_ANIMAL(" +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "$ANIMALNAME TEXT," +
                "$ANIMALTYPE TEXT,"  +
                "$ANIMALAGE INT DEFAULT 0," +
                "$ANIMALOWNERNUMBER TEXT," +
                "$VACCINE flag INT DEFAULT 0," +
                "$IMAGE TEXT)"

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun getAnimal(context: Context): ArrayList<Animal>{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ANIMAL",null)
        val animalList = ArrayList<Animal>()

        if(cursor.count != 0)
        {
            cursor.moveToFirst()
            while(!cursor.isAfterLast()){
                val animal = Animal()
                animal.animalID = cursor.getInt(cursor.getColumnIndex(ID))
                animal.animalName = cursor.getString(cursor.getColumnIndex(ANIMALNAME))
                animal.animalType = cursor.getString(cursor.getColumnIndex(ANIMALTYPE))
                animal.animalAge = cursor.getInt(cursor.getColumnIndex(ANIMALAGE))
                animal.animalOwnerNumber = cursor.getString(cursor.getColumnIndex(
                    ANIMALOWNERNUMBER))
                animal.vaccine = (cursor.getInt(cursor.getColumnIndex(VACCINE)) ==1)
                animal.image = cursor.getString(cursor.getColumnIndex(IMAGE))

                animalList.add(animal)
                cursor.moveToNext()
            }
        }

        cursor.close()
        db.close()

        return animalList
    }

    fun addAnimal(context: Context, animal: Animal){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ANIMALNAME,animal.animalName)
        values.put(ANIMALTYPE,animal.animalType)
        values.put(ANIMALAGE,animal.animalAge)
        values.put(ANIMALOWNERNUMBER,animal.animalOwnerNumber)
        values.put(VACCINE,animal.vaccine)
        values.put(IMAGE,animal.image)

        db.insert(TABLE_ANIMAL,null,values)
        db.close()
    }

    fun deleteAnimal(animalID: Int) : Boolean{
        val db = this.writableDatabase

        db.execSQL("DELETE FROM $TABLE_ANIMAL WHERE $ID = $animalID")
        db.close()

        return true
    }

    fun editAnimal(animalID: String, animalName: String, animalType: String, animalAge: String, animalOwnerNumber: String, vaccine: String) : Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ANIMALNAME,animalName)
        contentValues.put(ANIMALTYPE,animalType)
        contentValues.put(ANIMALAGE,animalAge)
        contentValues.put(ANIMALOWNERNUMBER,animalOwnerNumber)
        contentValues.put(VACCINE,vaccine)

        db.update(TABLE_ANIMAL,contentValues,"$ID = ?", arrayOf(animalID))
        db.close()

        return true
    }
}