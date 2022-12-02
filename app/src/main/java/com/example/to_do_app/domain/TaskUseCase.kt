package com.example.to_do_app.domain

import android.content.Context
import android.util.Log
import com.example.to_do_app.domain.model.Task
import com.example.to_do_app.infrastructure.TaskRepository

class TaskUseCase(context: Context) {
    private val TAG = "TaskUseCase"
    private val taskRepository = TaskRepository(context)

    /**
     * 表示用データ取得メソッド
     * @return task保存値のうち、対象のtaskClassに属するもの
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
     * task追加メソッド
     */
    fun addTask(task: Task) {
        val taskList = taskRepository.getTaskList()
        taskList.add(task)
        taskRepository.updateTask(taskList)
        Log.d(TAG, "add task: $task")
    }

    /**
     * task削除メソッド
     */
    fun removeTask(task: Task) {
        val taskList = taskRepository.getTaskList()
        taskList.remove(task)
        taskRepository.updateTask(taskList)
        Log.d(TAG, "remove task: $task")
    }

    /**
     * コメント追加メソッド
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
     * task削除メソッド
     * project, taskClass削除時に紐づくタスクを削除する場合に利用
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
     * @return 当該ステータスが全てtrueか否か
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