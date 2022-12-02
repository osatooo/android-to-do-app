package com.example.to_do_app.domain

import android.content.Context
import android.util.Log
import com.example.to_do_app.domain.model.TaskClass
import com.example.to_do_app.infrastructure.TaskClassRepository

class TaskClassUseCase(context: Context) {
    private val TAG = "TaskClassUseCase"
    private val taskClassRepository = TaskClassRepository(context)
    private val taskUseCase = TaskUseCase(context)

    /**
     * 表示用データ取得メソッド
     * @return taskClass保存値のうち、対象のprojectに属するもの
     */
    fun getViewData(projectId: Int): MutableList<TaskClass> {
        val viewData = mutableListOf<TaskClass>()
        taskClassRepository.getTaskClassList().forEach {
            if (it.projectId == projectId)
                viewData.add(it)
        }
        return viewData
    }

    /**
     * taskClass追加メソッド
     */
    fun addTaskClass(taskClass: TaskClass) {
        val taskClassList = taskClassRepository.getTaskClassList()
        taskClassList.add(taskClass)
        taskClassRepository.updateTaskClass(taskClassList)
        Log.d(TAG, "add taskClass: $taskClass")
    }

    /**
     * taskClass削除メソッド
     */
    fun removeTaskClass(taskClass: TaskClass) {
        //taskClassに紐づいたtaskを削除
        taskUseCase.removeTask(taskClass.projectId, taskClass.taskClassId)

        val taskClassList = taskClassRepository.getTaskClassList()
        taskClassList.remove(taskClass)
        taskClassRepository.updateTaskClass(taskClassList)
        Log.d(TAG, "remove taskClass: $taskClass")
    }

    /**
     * taskClass削除メソッド
     * project削除時に紐づくtaskClassを削除する場合に利用
     */
    fun removeTaskClass(projectId: Int) {
        //taskClassに紐づいたtaskを削除
        taskUseCase.removeTask(projectId, null)

        val taskClassList = taskClassRepository.getTaskClassList()
        taskClassList.removeIf { it.projectId == projectId }
        taskClassRepository.updateTaskClass(taskClassList)
        Log.d(TAG, "remove taskClass in pj($projectId)")
    }

    /**
     * taskClassStatus確認メソッド
     * @return 対象のtaskClassに含まれるtaskのcheckStatusが全てtrueかどうか
     */
    fun isTaskClassStatus(projectId: Int, taskClassId: Int): Boolean {
        return taskUseCase.isTaskClassStatus(projectId, taskClassId)
    }

    /**
     * taskClassId設定メソッド
     * @return 使用可能なtaskClassId (3桁まで)
     */
    fun getTaskClassId(projectId: Int): Int {
        var id = 0
        val idList = mutableListOf<Int>()
        taskClassRepository.getTaskClassList().forEach {
            if (it.projectId == projectId){
                idList.add(it.taskClassId)
            }
        }
        for (i in 0..1000){
            if (!idList.contains(i)){
                id = i
                break
            }
        }
        return id
    }
}