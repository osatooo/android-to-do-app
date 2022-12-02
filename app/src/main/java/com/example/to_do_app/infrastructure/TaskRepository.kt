package com.example.to_do_app.infrastructure

import android.content.Context
import android.util.Log
import com.example.to_do_app.domain.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

class TaskRepository(private val context: Context) {
    private val TASK_FILE = "task.json"

    fun getTaskList(): MutableList<Task> {
        val readFile = File(context.filesDir, TASK_FILE)
        if (readFile.exists()) {
            // ファイルが存在する場合のみ読み込む
            try {
                val contents = readFile.bufferedReader().use(BufferedReader::readText)
                Log.d("task contents", contents)
                val type: Type = object : TypeToken<MutableList<Task>>() {}.type
                return Gson().fromJson(contents, type)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return mutableListOf()
    }

    fun updateTask(taskList: List<Task>) {
        context.openFileOutput(TASK_FILE, Context.MODE_PRIVATE).use {
            val str = Gson().toJson(taskList)
            it.write(str.toByteArray())
        }
    }
}