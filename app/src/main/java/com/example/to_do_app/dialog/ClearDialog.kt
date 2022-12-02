package com.example.to_do_app.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.to_do_app.adapter.ProjectAdapter
import com.example.to_do_app.adapter.TaskClassAdapter

class ClearDialog() : DialogFragment() {
    private var projectAdapter: ProjectAdapter? = null
    private var taskClassAdapter: TaskClassAdapter? = null
    private lateinit var itemName: String
    private var position: Int? = null

    // project用
    constructor(
        projectAdapter: ProjectAdapter,
        itemName: String,
        position: Int,
    ) : this() {
        this.projectAdapter = projectAdapter
        this.itemName = itemName
        this.position = position
    }

    // taskClass用
    constructor(
        taskClassAdapter: TaskClassAdapter,
        itemName: String,
        position: Int,
    ) : this() {
        this.taskClassAdapter = taskClassAdapter
        this.itemName = itemName
        this.position = position
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("ClearDialog", "onCreateDialog run")
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("一度削除したものは元に戻せません。\n本当に${itemName}を削除しますか。")
                .setPositiveButton("削除",
                    DialogInterface.OnClickListener { dialog, id ->
                        position?.let {
                            if (projectAdapter !== null) {
                                projectAdapter?.removeProject(it)
                            } else if (taskClassAdapter != null) {
                                taskClassAdapter?.removeTaskClass(it)
                            }
                        }
                    })
                .setNegativeButton("キャンセル",
                    DialogInterface.OnClickListener { dialog, id ->
                        if (taskClassAdapter != null) {
                            taskClassAdapter?.removeCancel(position)
                        }
                    })
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}