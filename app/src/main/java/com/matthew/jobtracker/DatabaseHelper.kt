package com.matthew.jobtracker

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.matthew.jobtracker.data.Job
import com.matthew.jobtracker.data.JobTemplate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class DatabaseHelper(context: Context):
                     SQLiteOpenHelper(context, DB_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        createTable(db, TABLE_TEMPLATE_JOBS)
        createTable(db, TABLE_ACTIVE_JOBS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop Table if exists $TABLE_TEMPLATE_JOBS")
        db.execSQL("drop Table if exists $TABLE_ACTIVE_JOBS")
    }

    //region Add Data

    fun addCurrentJob(job: Job) {
        val serializedJob = Json.encodeToString(job)
        addSerializedDataToTable(job.name, TABLE_ACTIVE_JOBS, serializedJob)
    }

    fun addJobTemplate(template: JobTemplate){
        val serializedTemplate = Json.encodeToString(template)
        addSerializedDataToTable(template.name, TABLE_TEMPLATE_JOBS, serializedTemplate)
    }

    private fun addSerializedDataToTable(name: String, table: String, serializedData: String) {
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_SERIALIZED_DATA, serializedData)

        val db = this.writableDatabase
        db.insert("'$table'", null, values)
    }

    //endregion

    //region Get Data

    fun getCurrentJob(jobName: String) : Job?{
        val jobs = getCurrentJobs()

        jobs.forEach{
            if(it.name == jobName) return it
        }

        return null
    }

    fun getCurrentJobs(): MutableList<Job> {
        val serializedList = getSerializedListFromTable(TABLE_ACTIVE_JOBS)
        val jobList = mutableListOf<Job>()

        serializedList.forEach { data ->
            val job : Job = Json.decodeFromString(data)
            jobList.add(job)
        }

        return jobList
    }

    fun getTemplates(): MutableList<JobTemplate> {
        val serializedList = getSerializedListFromTable(TABLE_TEMPLATE_JOBS)
        val templateList = mutableListOf<JobTemplate>()

        serializedList.forEach { data ->
            val jobTemplate : JobTemplate = Json.decodeFromString(data)
            templateList.add(jobTemplate)
        }

        return templateList
    }

    private fun getSerializedListFromTable(table: String): MutableList<String> {
        val query = "SELECT * FROM '$table'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var serializedList = mutableListOf<String>()

        while(cursor.moveToNext()){
           serializedList.add(cursor.getString(2))
        }

        cursor.close()
        return serializedList
    }

    fun deleteCurrentJob(name : String){ delete(TABLE_ACTIVE_JOBS, name)}

    fun deleteJobTemplate(name : String){ delete(TABLE_TEMPLATE_JOBS, name)}

    private fun delete(table : String, name : String){
        val db = this.writableDatabase
        val whereClause = "name=?"
        val whereArgs =  arrayOf(name)
        db.delete(table, whereClause, whereArgs)
    }

    //endregion

    private fun createTable(db: SQLiteDatabase, tableName: String){
        val createTable = ("CREATE TABLE '" + tableName + "'(" +
                COLUMN_ID + " INT PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_SERIALIZED_DATA + " TEXT," +
                "UNIQUE(" + COLUMN_NAME + ") ON CONFLICT REPLACE" + ")")

        db.execSQL(createTable)
    }

    companion object{
        private const val VERSION = 1
        const val DB_NAME = "JobTracker.db"
        const val TABLE_TEMPLATE_JOBS = "templates"
        const val TABLE_ACTIVE_JOBS = "active_jobs"

        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_SERIALIZED_DATA = "serialized_list"
    }
}