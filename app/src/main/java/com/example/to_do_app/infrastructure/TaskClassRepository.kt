package com.example.to_do_app.infrastructure

import android.content.Context
import android.util.Log
import com.example.to_do_app.domain.model.TaskClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

class TaskClassRepository(private val context: Context) {
    private val TASK_CLASS_FILE = "taskClass.json"

    fun getTaskClassList(): MutableList<TaskClass> {
        val readFile = File(context.filesDir, TASK_CLASS_FILE)
        if (readFile.exists()) {
            // ファイルが存在する場合のみ読み込む
            try {
                val contents = readFile.bufferedReader().use(BufferedReader::readText)
                Log.d("taskClass contents", contents)

                val type: Type = object : TypeToken<MutableList<TaskClass>>() {}.type
                return Gson().fromJson(contents, type)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return mutableListOf()
    }

    fun updateTaskClass(list: List<TaskClass>) {
        context.openFileOutput(TASK_CLASS_FILE, Context.MODE_PRIVATE).use {
            val str = Gson().toJson(list)
            it.write(str.toByteArray())
        }
    }
}