package com.example.android.pets.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.content.UriMatcher
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import com.example.android.pets.data.PetContract.PetEntry



/**
 * [ContentProvider] for Pets app.
 */
class PetProvider : ContentProvider() {

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private val PETS = 100
    private val PET_ID = 101

    /**
     * Initialize the provider and the database helper object.
     */
    var petDbHelper : PetDbHelper? = null
    override fun onCreate(): Boolean {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

        petDbHelper = PetDbHelper(context)

        sUriMatcher.addURI(PetContract.PetEntry.CONTENT_AUTHORITY, PetContract.PetEntry.PATH_PETS, PETS)
        sUriMatcher.addURI(PetContract.PetEntry.CONTENT_AUTHORITY, PetContract.PetEntry.PATH_PETS + "/#", PET_ID)
        return true
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {
        val database = petDbHelper?.readableDatabase
        var cursor : Cursor? = null

        val match = sUriMatcher.match(uri)

        when (match) {
            PETS -> {
                cursor = database?.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            PET_ID -> {
                var s = PetContract.PetEntry._ID + "=?"
                val sArgs = arrayOf(ContentUris.parseId(uri).toString())
                cursor = database?.query(PetContract.PetEntry.TABLE_NAME, projection, s, sArgs, null, null, sortOrder);
            }
            else -> throw IllegalArgumentException("Cannot query unknown $uri")
        }

        cursor?.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val match = sUriMatcher.match(uri)
        when (match) {
            PETS -> return insertPet(uri, contentValues!!)
            else -> throw IllegalArgumentException("Insertion is not supported for " + uri)
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private fun insertPet(uri: Uri, values: ContentValues): Uri {

        val name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        val weight = values.getAsDouble(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        val breed = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        val gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);

        if(name == null || TextUtils.isEmpty(name)) throw IllegalArgumentException("Invalid name")
        if(weight == null || weight < 1) throw IllegalArgumentException("Invalid weight")
        if(breed == null || TextUtils.isEmpty(breed)) throw IllegalArgumentException("Invalid breed")
        if(gender == null || gender < 1) throw IllegalArgumentException("Invalid gender")

        val db = petDbHelper?.writableDatabase
        val id = db!!.insert(PetContract.PetEntry.TABLE_NAME, null, values)
        if(id == -1L) {
            Log.d(LOG_TAG, "Failed to insert row for $uri")
            return null!!
        }

        context.contentResolver.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        val match = sUriMatcher.match(uri)
        when (match) {
            PETS -> return updatePet(uri, contentValues!!, selection!!, selectionArgs!!)
            PET_ID -> {
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return updatePet(uri, contentValues!!, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Update is not supported for " + uri)
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private fun updatePet(uri: Uri, values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        val db = petDbHelper?.writableDatabase;
        val s = PetEntry._ID + "=?"
        val sArgs = arrayOf(ContentUris.parseId(uri).toString());
        val rowsUpdated = db?.update(PetEntry.TABLE_NAME, values, s, sArgs)

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated!!
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get writeable database
        val database = petDbHelper?.writableDatabase
        var rowsDeleted : Int

        val match = sUriMatcher.match(uri)
        when (match) {
            PETS ->
                // Delete all rows that match the selection and selection args
                rowsDeleted = database!!.delete(PetEntry.TABLE_NAME, selection, selectionArgs)
            PET_ID -> {
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                context.contentResolver.notifyChange(uri, null)
                rowsDeleted = database!!.delete(PetEntry.TABLE_NAME, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for " + uri)
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)
        when (match) {
            PETS -> return PetEntry.CONTENT_LIST_TYPE
            PET_ID -> return PetEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }

    companion object {

        /** Tag for the log messages  */
        val LOG_TAG = PetProvider::class.java.simpleName
    }
}

