package com.example.receptikstlv.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "recipes.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_RECIPES = "recipes"
        const val TABLE_INGREDIENTS = "ingredients"
        const val TABLE_STEPS = "steps"

        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_TIME = "time"
        const val COLUMN_COMPLEXITY = "complexity"
        const val COLUMN_PHOTO = "photo"
        const val COLUMN_FAVORITE = "is_favorite"
        const val COLUMN_RECIPE_ID = "recipe_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_STEP_NUMBER = "step_number"
        const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_RECIPES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_TIME INTEGER NOT NULL,
                $COLUMN_COMPLEXITY TEXT NOT NULL,
                $COLUMN_PHOTO TEXT,
                $COLUMN_FAVORITE INTEGER DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_INGREDIENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RECIPE_ID INTEGER NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_AMOUNT TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_RECIPE_ID) REFERENCES $TABLE_RECIPES($COLUMN_ID) ON DELETE CASCADE
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_STEPS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RECIPE_ID INTEGER NOT NULL,
                $COLUMN_STEP_NUMBER INTEGER NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_RECIPE_ID) REFERENCES $TABLE_RECIPES($COLUMN_ID) ON DELETE CASCADE
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STEPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INGREDIENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPES")
        onCreate(db)
    }
}