package com.example.to_do_app.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_app.MainFragment
import com.example.to_do_app.PageType
import com.example.to_do_app.R
import com.example.to_do_app.databinding.TaskItemBinding
import com.example.to_do_app.dialog.ClearDialog
import com.example.to_do_app.domain.TaskClassUseCase
import com.example.to_do_app.domain.model.TaskClass

class TaskClassAdapter(val context: Context, private val projectId: Int) :
    RecyclerView.Adapter<TaskClassAdapter.ViewHolder>() {
    private val TAG = "TaskClassAdapter"
    private val taskClassItems: MutableList<TaskClass>

    private val taskClassUseCase = TaskClassUseCase(context)

    init {
        Log.d(TAG, "init")
        taskClassItems = taskClassUseCase.getViewData(projectId)
    }

    inner class ViewHolder(binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameLabel = binding.label
        val checkIcon = binding.checkIcon
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
        val item = taskClassItems[position]
        holder.nameLabel.text = item.name
        holder.nameLabel.setOnClickListener {
            Log.d(TAG, "taskClass tap")
            val activity = it.context as AppCompatActivity
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container,
                MainFragment.newInstance(PageType.TASK,
                    item.name,
                    item.projectId,
                    item.taskClassId))
            transaction.addToBackStack(null)
            transaction.commit()
        }
        holder.checkIcon.isVisible =
            taskClassUseCase.isTaskClassStatus(item.projectId, item.taskClassId)
        Log.d(TAG, "onBindViewHolder end")
    }

    fun addTaskClass(label: String) {
        if (itemCount >= 20) {
            Toast.makeText(context,
                "タスク区分件数が20件に達しているため追加できません。",
                Toast.LENGTH_LONG).show()
            return
        }
        val taskClass = TaskClass(label, projectId, taskClassUseCase.getTaskClassId(projectId))
        //ファイル保存値を更新
        taskClassUseCase.addTaskClass(taskClass)
        //表示用リスト更新
        taskClassItems.add(taskClass)
        notifyItemInserted(itemCount)
    }

    fun removeTaskClass(position: Int) {
        //ファイル値更新
        taskClassUseCase.removeTaskClass(taskClassItems[position])
        //表示用リスト更新
        taskClassItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeCancel(position: Int?){
        position?.let{
            notifyItemChanged(it)
        }
    }

    override fun getItemCount(): Int = taskClassItems.size

    fun getSwipeToDismissTouchHelper() =
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
                // 確認ダイアログ表示
                val dialog = ClearDialog(this@TaskClassAdapter,
                    taskClassItems[viewHolder.adapterPosition].name,
                    viewHolder.adapterPosition)
                val activity = context as AppCompatActivity
                activity.let { dialog.show(it.supportFragmentManager, null) }
            }

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
                val itemView = viewHolder.itemView

                val background = ColorDrawable(Color.RED)
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                val deleteIcon = AppCompatResources.getDrawable(
                    recyclerView.context,
                    R.drawable.ic_baseline_delete_24
                )
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
