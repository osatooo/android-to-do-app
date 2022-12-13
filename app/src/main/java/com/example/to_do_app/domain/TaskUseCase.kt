package com.example.to_do_app.domain

import android.content.Context
import android.util.Log
import com.example.to_do_app.domain.model.Task
import com.example.to_do_app.infrastructure.TaskRepository

/**
 * タスクデータを利用するクラス
 */
class TaskUseCase(context: Context) {
    private val TAG = "TaskUseCase"
    private val taskRepository = TaskRepository(context)

    /**
     * 表示用データ取得メソッド
     * @param projectId タスクが属するプロジェクトのID
     * @param taskClassId タスクが属するタスク区分のID
     * @return タスク保存値のうち、対象のタスク区分に属するもの
     */
    fun getViewData(projectId: Int, taskClassId: Int): MutableList<Task> {
        val viewData = mutableListOf<Task>()
        taskRepository.getTaskList().forEach {
            if (it.projectId == projectId && it.taskClassId == taskClassId) {
                viewData.add(it)
            }
        }
        return viewData
    }

    /**
     *  チェックステータス更新用メソッド
     *  @param task チェックステータスが更新されるタスク
     */
    fun updateCheckStatus(task: Task) {
        val taskList = taskRepository.getTaskList()
        taskList.forEach {
            if (it == task) {
                it.checkStatus = !task.checkStatus
            }
        }
        taskRepository.updateTask(taskList)
    }

    /**
     * タスク追加メソッド
     * @param task 追加するタスク
     */
    fun addTask(task: Task) {
        val taskList = taskRepository.getTaskList()
        taskList.add(task)
        taskRepository.updateTask(taskList)
        Log.d(TAG, "add task: $task")
    }

    /**
     * タスク削除メソッド
     * @param task 削除するタスク
     */
    fun removeTask(task: Task) {
        val taskList = taskRepository.getTaskList()
        taskList.remove(task)
        taskRepository.updateTask(taskList)
        Log.d(TAG, "remove task: $task")
    }

    /**
     * コメント追加メソッド
     * @param task コメント追加対象のタスク
     * @param comment 追加するコメント
     */
    fun addComment(task: Task, comment: String) {
        val taskList = taskRepository.getTaskList()
        taskList.forEach {
            if (it == task) {
                it.comment = comment
            }
        }
        taskRepository.updateTask(taskList)
        Log.d(TAG, "add comment to ${task.name}")
    }

    /**
     * タスク削除メソッド
     * （プロジェクト,タスク区分削除時に紐づくタスクを削除するために利用）
     * @param projectId タスクが属するプロジェクトのID
     * @param taskClassId タスクが属するタスク区分のID
     */
    fun removeTask(projectId: Int, taskClassId: Int?) {
        val taskList = taskRepository.getTaskList()
        if (taskClassId == null) {
            taskList.removeIf { it.projectId == projectId }
        } else {
            taskList.removeIf { it.projectId == projectId && it.taskClassId == taskClassId }
        }
        taskRepository.updateTask(taskList)
        Log.d(TAG, "remove task in pj($projectId)-tc($taskClassId)")
    }

    /**
     * タスク区分ごとのチェックステータス確認メソッド
     * @param projectId プロジェクトID
     * @param taskClassId タスク区分ID
     * @return タスク区分ごとのタスクのステータスが全てtrueか否か
     */
    fun isTaskClassStatus(projectId: Int, taskClassId: Int): Boolean {
        var result = false
        taskRepository.getTaskList().forEach {
            if (it.projectId == projectId && it.taskClassId == taskClassId) {
                result = if (!it.checkStatus) {
                    return false
                } else {
                    true
                }
            }
        }
        return result
    }
}