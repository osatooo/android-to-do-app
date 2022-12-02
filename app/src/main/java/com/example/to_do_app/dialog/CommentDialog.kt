package com.example.to_do_app.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.to_do_app.adapter.TaskAdapter

class CommentDialog(
    private val taskAdapter: TaskAdapter,
    private val position: Int,
    private val comment: String?
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("CommentDialog", "onCreateDialog run")

        val addMsg = "コメントを入力してください。"
        val editText = EditText(context)

        val commentDialog = activity?.let {
            editText.hint = "50文字まで"
            comment?.let { editText.setText(comment)}
            AlertDialog.Builder(it).setMessage(addMsg)
                .setView(editText)
                .setPositiveButton("保存", null)
                .setNegativeButton("キャンセル", null)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")

        commentDialog.show()

        // ボタンタップ時に自動で消えないよう、show()後にclick処理を実装
        commentDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (editText.text.length > 50){
                commentDialog.setMessage("$addMsg\n※50文字以内で入力してください。")
            } else {
                taskAdapter.addComment(editText.text.toString(), position)
                dialog?.cancel()
            }
        }
        return commentDialog
    }
}