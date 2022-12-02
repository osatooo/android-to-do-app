package com.example.to_do_app.domain

import android.content.Context
import android.util.Log
import com.example.to_do_app.domain.model.Project
import com.example.to_do_app.infrastructure.ProjectRepository

class ProjectUseCase(context: Context) {
    private val TAG = "ProjectUseCase"
    private val projectRepository = ProjectRepository(context)
    private val taskClassUseCase = TaskClassUseCase(context)

    /**
     * 表示用データ取得メソッド
     * @return project保存値
     */
    fun getViewData(): MutableList<Project> {
        return projectRepository.getProjectList()
    }

    /**
     * project追加メソッド
     */
    fun addProject(project: Project){
        val projectList = projectRepository.getProjectList()
        projectList.add(project)
        projectRepository.updateProject(projectList)
        Log.d(TAG, "add project: $project")
    }

    /**
     * projectClass削除メソッド
     */
    fun removeProject(project: Project){
        //projectに紐づいたtaskClass以下を削除
        taskClassUseCase.removeTaskClass(project.projectId)

        val projectList = projectRepository.getProjectList()
        projectList.remove(project)
        projectRepository.updateProject(projectList)
        Log.d(TAG, "remove project: $project")
    }

    /**
     * projectId設定メソッド
     * @return 使用可能なprojectId (2桁まで)
     */
    fun getProjectId(): Int {
        var id = 0
        val idList = mutableListOf<Int>()
        projectRepository.getProjectList().forEach {
            idList.add(it.projectId)
        }

        for (i in 0..100){
            if (!idList.contains(i)){
                id = i
                break
            }
        }
        return id
    }
}