package com.example.to_do_app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_app.*
import com.example.to_do_app.databinding.ProjectItemBinding
import com.example.to_do_app.dialog.ClearDialog
import com.example.to_do_app.domain.model.Project
import com.example.to_do_app.domain.ProjectUseCase

/**
 * プロジェクト一覧画面を表示するためのアダプター
 */
class ProjectAdapter(private val context: Context): RecyclerView.Adapter<ProjectAdapter.ViewHolder>(){
    private val TAG = "ProjectAdapter"
    private val projectItems: MutableList<Project>

    private val projectUseCase = ProjectUseCase(context)
    private var clearStatus: Boolean = true

    init {
        Log.d(TAG, "init")
        projectItems = projectUseCase.getViewData()
    }

    inner class ViewHolder(binding: ProjectItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val clearIcon: ImageView = binding.clear
        val nameLabel: TextView = binding.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder run")
        return ViewHolder(
            ProjectItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder start")
        val item = projectItems[position]
        if (clearStatus){
            holder.clearIcon.visibility = View.GONE
            holder.nameLabel.isClickable = true
        } else {
            holder.clearIcon.visibility = View.VISIBLE
            holder.nameLabel.isClickable = false
            holder.clearIcon.setOnClickListener{
                //×ボタンを押したときの処理
                val dialog = ClearDialog(this, projectItems[position].name, position)
                val activity = it.context as AppCompatActivity
                activity.let { dialog.show(it.supportFragmentManager, null) }
            }
        }

        holder.nameLabel.text = item.name
        holder.nameLabel.setOnClickListener {
            Log.d(TAG, "project tap")
            val activity = it.context as AppCompatActivity
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, MainFragment.newInstance(PageType.TASKCLASS, item.name, item.projectId))
            transaction.addToBackStack(null)
            transaction.commit()
        }
        Log.d(TAG, "onBindViewHolder end")
    }
    /**
     * プロジェクト追加メソッド
     * @param name プロジェクト名
     */
    fun addProject(name: String){
        if (itemCount >= 20){
            Toast.makeText(context,
                "プロジェクト件数が20件に達しているため追加できません。",
                Toast.LENGTH_LONG).show()
            return
        }

        val project = Project(projectUseCase.getProjectId(), name)
        //ファイル値更新
        projectUseCase.addProject(project)
        //表示用リスト更新
        projectItems.add(project)
        notifyItemInserted(itemCount)
    }

    /**
     * プロジェクト削除画面のレイアウト切り替えメソッド
     */
    fun editProject(){
        clearStatus = !clearStatus
        notifyItemRangeChanged(0, itemCount)
    }

    /**
     * プロジェクト削除メソッド
     * @param position 削除したい要素のリスト位置
     */
    fun removeProject(position: Int){
        //ファイル値更新
        projectUseCase.removeProject(projectItems[position])
        //表示用リスト更新
        projectItems.removeAt(position)
        notifyItemRemoved(position) //構造変更イベント
        notifyItemChanged(position) //アイテム変更イベントで再bind
    }

    override fun getItemCount(): Int = projectItems.size
}