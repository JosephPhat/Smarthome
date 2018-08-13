package com.example.josephpham.smarhome.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import com.example.josephpham.smarhome.database.ConstantTableFieldsName.COL_TOKEN
import com.example.josephpham.smarhome.database.ConstantTableFieldsName.DATABASE_NAME
import com.example.josephpham.smarhome.database.ConstantTableFieldsName.TABLE_NAME
import org.jetbrains.anko.db.*

class DatabaseHandler(var context: Context): ManagedSQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    companion object {
        private var instance: DatabaseHandler? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseHandler {
            if (instance == null) {
                instance = DatabaseHandler(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(TABLE_NAME, true,
                COL_TOKEN to TEXT )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("Token", true)
    }
    fun insertTable(token: String){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_TOKEN, token)
        var result = db.insert(TABLE_NAME, null, cv)
        if(result == -1.toLong()){
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
        }else{
            Log.d("BBBB", result.toString())

        }
    }
    fun readData(): String{
        var s: String = ""
        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_NAME
        val result = db.rawQuery(query, null)
        while(result.moveToNext()){
            s = result.getString(result.getColumnIndex(COL_TOKEN))
        }
        result.close()
        db.close()
        return s
    }
    fun deletes(): String{
        var s: String = ""
        val db = this.readableDatabase
        db.execSQL("DELETE FROM " + TABLE_NAME)
        db.close()
        return s
    }

}

// Access property for Context
val Context.database: DatabaseHandler
    get() = DatabaseHandler.getInstance(getApplicationContext())
