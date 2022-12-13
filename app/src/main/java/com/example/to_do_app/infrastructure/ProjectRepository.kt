package com.example.to_do_app.infrastructure

import android.content.Context
import android.util.Log
import com.example.to_do_app.domain.model.Project
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.lang.reflect.Type

/**
 * プロジェクトデータを管理するクラス
 */
class ProjectRepository(private val context: Context) {
    private val PROJECT_FILE = "project.json"

    /**
     * プロジェクトデータ取得メソッド
     * @return ファイルに保存されているプロジェクトのリスト
     */
    fun getProjectList(): MutableList<Project> {
        val readFile = File(context.filesDir, PROJECT_FILE)
        if (readFile.exists()) {
            // ファイルが存在する場合のみ読み込む
            try {
                val contents = readFile.bufferedReader().use(BufferedReader::readText)
                Log.d("project contents", contents)

                val type: Type = object : TypeToken<MutableList<Project>>() {}.type
                return Gson().fromJson(contents, type)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return mutableListOf()
    }

    /**
     * プロジェクトデータ更新メソッド
     * @param projectList ファイルを上書きする値
     */
    fun updateProject(projectList: List<Project>) {
        context.openFileOutput(PROJECT_FILE, Context.MODE_PRIVATE).use {
            val str = Gson().toJson(projectList)
            it.write(str.toByteArray())
        }
    }
}