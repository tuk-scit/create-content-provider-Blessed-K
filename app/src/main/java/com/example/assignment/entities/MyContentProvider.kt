package com.example.assignment.entities

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class MyContentProvider : ContentProvider() {

    private lateinit var dbHelper: SQLiteOpenHelper

    companion object {
        const val AUTHORITY = "com.example.assignment"
        const val PATH = "data"
        const val CONTENT_URI_STRING = "content://$AUTHORITY/$PATH"
        val CONTENT_URI: Uri = Uri.parse(CONTENT_URI_STRING)
        const val DATA_TABLE_NAME = "data_table"
        const val URI_CODE_DATA = 1
        const val MIME_TYPE_DIR = "vnd.android.cursor.dir/$AUTHORITY.$PATH"
        const val MIME_TYPE_ITEM = "vnd.android.cursor.item/$AUTHORITY.$PATH"

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH, URI_CODE_DATA)
        }
    }

    override fun onCreate(): Boolean {
        dbHelper = MyDbHelper(context!!)
        return true
    }

    private fun MyDbHelper(context: Context): SQLiteOpenHelper {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor?
        when (uriMatcher.match(uri)) {
            URI_CODE_DATA -> {
                cursor = db.query(
                    DATA_TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            URI_CODE_DATA -> MIME_TYPE_DIR
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = db.insert(DATA_TABLE_NAME, null, values)
        if (id > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            return ContentUris.withAppendedId(uri, id)
        }
        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val count = db.delete(DATA_TABLE_NAME, selection, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val count = db.update(DATA_TABLE_NAME, values, selection, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }
}
