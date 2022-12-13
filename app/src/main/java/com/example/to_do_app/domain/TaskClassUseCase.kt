package com.example.to_do_app.domain

import android.content.Context
import android.util.Log
import com.example.to_do_app.domain.model.TaskClass
import com.example.to_do_app.infrastructure.TaskClassRepository

/**
 * タスク区分データを利用するクラス
 */
class TaskClassUseCase(context: Context) {
    private val TAG = "TaskClassUseCase"
    private val taskClassRepository = TaskClassRepository(context)
    private val taskUseCase = TaskUseCase(context)

    /**
     * 表示用データ取得メソッド
     * @param projectId タスク区分が属するプロジェクトのID
     * @return タスク区分保存値のうち、対象のプロジェクトに属するもの
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
     * タスク区分追加メソッド
     * @param taskClass 追加するタスク区分
     */
    fun addTaskClass(taskClass: TaskClass) {
        val taskClassList = taskClassRepository.getTaskClassList()
        taskClassList.add(taskClass)
        taskClassRepository.updateTaskClass(taskClassList)
        Log.d(TAG, "add taskClass: $taskClass")
    }

    /**
     * タスク区分以下削除メソッド
     * @param taskClass 削除するタスク区分
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
     * タスク区分以下削除メソッド
     * （プロジェクト削除時に紐づくタスク区分とタスクを削除するために利用）
     * @param projectId タスク区分、タスクが属するプロジェクトのID
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
     * タスク区分ステータス確認メソッド
     * @param projectId タスク区分が属するプロジェクトのID
     * @param taskClassId タスク区分ID
     * @return 対象のタスク区分に含まれるタスクのcheckStatusが全てtrueか否か
     */
    fun isTaskClassStatus(projectId: Int, taskClassId: Int): Boolean {
        return taskUseCase.isTaskClassStatus(projectId, taskClassId)
    }

    /**
     * タスク区分ID設定メソッド
     * @param projectId タスク区分が属するプロジェクトのID
     * @return 使用可能なタスク区分ID (3桁まで)
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