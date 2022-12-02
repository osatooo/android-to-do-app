package com.example.to_do_app.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_app.R
import com.example.to_do_app.databinding.TaskItemBinding
import com.example.to_do_app.dialog.CommentDialog
import com.example.to_do_app.domain.TaskUseCase
import com.example.to_do_app.domain.model.Task

class TaskAdapter(
    private val context: Context,
    private val projectId: Int,
    private val taskClassId: Int,
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    private val TAG = "TaskAdapter"
    private val taskUseCase = TaskUseCase(context)

    // 表示用list
    val taskItems: MutableList<Task>

    init {
        Log.d(TAG, "init")
        taskItems = taskUseCase.getViewData(projectId, taskClassId)
    }

    inner class ViewHolder(binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameLabel: TextView = binding.label
        val commentIcon: ImageView = binding.commentIcon
        val checkIcon: ImageView = binding.checkIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder run")
        return ViewHolder(
            TaskItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder start")
        val item = taskItems[position]
        holder.nameLabel.text = item.name
        holder.nameLabel.setOnClickListener {
            //ファイル保存値更新
            taskUseCase.updateCheckStatus(item)
            // 表示用リスト更新
            item.checkStatus = !item.checkStatus
            notifyItemChanged(position)
        }

        holder.commentIcon.visibility = View.VISIBLE
        holder.commentIcon.setOnClickListener {
            val dialog = CommentDialog(this, position, item.comment)
            val activity = context as AppCompatActivity
            activity.let { dialog.show(it.supportFragmentManager, null) }
        }

        if (!item.comment.isNullOrEmpty()) {
            holder.commentIcon.setColorFilter(Color.argb(255, 57, 153, 170))
        } else {
            holder.commentIcon.setColorFilter(Color.argb(200, 200, 200, 200))
        }

        if (item.checkStatus) {
            holder.checkIcon.visibility = View.VISIBLE
        } else {
            holder.checkIcon.visibility = View.INVISIBLE
        }
        Log.d(TAG, "onBindViewHolder end")
    }

    fun addTask(label: String) {
        if (itemCount >= 50) {
            Toast.makeText(context,
                "タスク件数が50件に達しているため追加できません。",
                Toast.LENGTH_LONG).show()
            return
        }
        val task = Task(label, projectId, taskClassId)
        //ファイル保存値更新
        taskUseCase.addTask(task)
        //表示用リスト更新
        taskItems.add(task)
        notifyItemInserted(itemCount)
    }

    fun addComment(comment: String, position: Int) {
        val task = taskItems[position]
        //ファイル保存値更新
        taskUseCase.addComment(task, comment)

        //表示用リスト更新
        task.comment = comment
        notifyItemRangeChanged(position, itemCount)
    }

    override fun getItemCount(): Int = taskItems.size

    fun getSwipeToDismissTouchHelper(adapter: TaskAdapter) =
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d(TAG, "onSwiped")
                //ファイル保存値更新
                taskUseCase.removeTask(taskItems[viewHolder.adapterPosition])

                //リストからスワイプしたカードを削除
                taskItems.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

            //スワイプした時の背景を設定
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean,
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView // RecyclerViewのitemView
                val background = ColorDrawable(Color.RED) // スワイプ時の背景の定義

                // dXは左に動かすとマイナスになる
                // setBoundsで上下左右の位置を指定
                //itemView.right = 左端から数えたときの右端 ex)1020
                background.setBounds(
                    itemView.right + dX.toInt(), //背景は「+dX」で動的に変化
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                val deleteIcon = AppCompatResources.getDrawable( // スワイプ時に表示されるアイコンを定義
                    recyclerView.context,
                    R.drawable.ic_baseline_delete_24
                )
                // アイコン表示位置を決めるためのマージンを計算（今回は高さを基準とする）
                val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2
                if (-dX.toInt() >= iconMarginVertical + deleteIcon.intrinsicWidth) {
                    deleteIcon.setBounds(
                        itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    deleteIcon.draw(c)
                } else {
                    deleteIcon.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    deleteIcon.draw(c)
                }
            }
        })
}