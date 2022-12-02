package com.example.to_do_app.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.to_do_app.MainFragment
import com.example.to_do_app.PageType

class AddDialog(
    private val mainFragment: MainFragment,
    private val clickView: View,
    var pageName: String,
) : DialogFragment() {

    private val page = when(clickView.tag) {
        PageType.PROJECT -> "プロジェクト名"
        PageType.TASKCLASS -> "タスク区分名"
        PageType.TASK -> "タスク名"
        else -> ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("AddDialog", "onCreateDialog run")

        if (pageName.isNotEmpty()) pageName += "に"
        val addMsg = pageName + "追加する" + page + "を入力してください。"
        val editText = EditText(context)

        val alertDialog = activity?.let {
            editText.hint = "15文字まで"
            AlertDialog.Builder(it).setMessage(addMsg)
                .setView(editText)
                .setPositiveButton("追加",null )
                .setNegativeButton("キャンセル", null)
                .create()

        } ?: throw IllegalStateException("Activity cannot be null")

        alertDialog.show()

        // ボタンタップ時に自動で消えないよう、show()後にclick処理を実装
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            when {
                editText.text.isEmpty() -> {
                    alertDialog.setMessage("$addMsg\n※${page}が未記入です。")
                }
                editText.text.length > 15 -> {
                    alertDialog.setMessage("$addMsg\n※15文字以内で入力してください。")
                }
                else -> {
                    mainFragment.addValues(clickView, editText.text.toString())
                    dialog?.cancel()
                }
            }
        }
        return alertDialog
    }
}