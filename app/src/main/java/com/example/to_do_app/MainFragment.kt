package com.example.to_do_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_do_app.adapter.ProjectAdapter
import com.example.to_do_app.adapter.TaskAdapter
import com.example.to_do_app.adapter.TaskClassAdapter
import com.example.to_do_app.databinding.FragmentLayoutBinding
import com.example.to_do_app.dialog.AddDialog

class MainFragment : Fragment() {
    private val TAG = "MainFragment"
    private var pageType: PageType = PageType.PROJECT
    private var pageName: String = ""
    private var projectId: Int? = null
    private var taskClassId: Int? = null
    private var _binding: FragmentLayoutBinding? = null
    private val binding get() = _binding!!

    private var projectAdapter: ProjectAdapter? = null
    private var taskClassAdapter: TaskClassAdapter? = null
    private var taskAdapter: TaskAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(TAG, "onCreateView run")

        _binding = FragmentLayoutBinding.inflate(inflater, container, false)
        binding.let {

            val recyclerView = it.recycle
            when (pageType) {
                PageType.PROJECT -> {
                    with(recyclerView) {
                        layoutManager = GridLayoutManager(context, 2)
                        if (projectAdapter == null) {
                            // fragmentで管理されているインスタンスがない場合のみインスタンスを生成する
                            projectAdapter = ProjectAdapter(context)
                        }
                        adapter = projectAdapter
                    }
                    it.toolbar.title = getString(PageType.PROJECT.title)
                    it.addButton.tag = PageType.PROJECT
                    it.editButton.visibility = View.VISIBLE
                }
                PageType.TASKCLASS -> {
                    with(recyclerView) {
                        layoutManager = LinearLayoutManager(context)
                        if (taskClassAdapter == null){
                            taskClassAdapter = TaskClassAdapter(context, projectId!!)
                        }
                        adapter = taskClassAdapter
                        taskClassAdapter?.getSwipeToDismissTouchHelper()
                            ?.attachToRecyclerView(recyclerView)
                        addItemDecoration(
                            DividerItemDecoration(context, LinearLayoutManager(context).orientation)
                        )
                    }
                    it.toolbar.title = pageName + " > " + getString(PageType.TASKCLASS.title)
                    it.addButton.tag = PageType.TASKCLASS
                    it.editButton.visibility = View.GONE
                }
                PageType.TASK -> {
                    with(recyclerView) {
                        layoutManager = LinearLayoutManager(context)
                        if (taskAdapter == null) {
                            taskAdapter = TaskAdapter(context, projectId!!, taskClassId!!)
                        }
                        adapter = taskAdapter
                        taskAdapter?.let {
                            it.getSwipeToDismissTouchHelper(it).attachToRecyclerView(recyclerView)
                        }
                        addItemDecoration(
                            DividerItemDecoration(context, LinearLayoutManager(context).orientation)
                        )
                    }
                    it.toolbar.title = pageName + " > " + getString(PageType.TASK.title)
                    it.addButton.tag = PageType.TASK
                    it.editButton.visibility = View.GONE
                }
            }
        }

        binding.addButton.setOnClickListener {
            val dialog = AddDialog(this, it, pageName)
            activity?.let {
                dialog.show(it.supportFragmentManager, null)
            }
        }

        binding.editButton.setOnClickListener {
            projectAdapter?.editProject()
            binding.addButton.isClickable = !binding.addButton.isClickable
            if (!binding.addButton.isClickable) {
                binding.addButton.visibility = View.GONE
                binding.toolbar.title = "${binding.toolbar.title}[編集]"
            } else {
                binding.addButton.visibility = View.VISIBLE
                binding.toolbar.title = binding.toolbar.title.removeSuffix("[編集]")
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addValues(view: View, name: String) {
        Log.d(TAG, "addValues (${view.tag})")
        when (view.tag) {
            PageType.PROJECT -> projectAdapter?.addProject(name)
            PageType.TASKCLASS -> taskClassAdapter?.addTaskClass(name)
            PageType.TASK -> taskAdapter?.addTask(name)
            //インスタンスは保持されているものを使用する（新しく作成すると別物の新adaoterに追加されてしまう）
        }
    }

    companion object {
        fun newInstance(
            pageType: PageType, pageName: String = "",
            projectId: Int? = null, taskClassId: Int? = null,
        ) = MainFragment().apply {
                this.pageType = pageType
                this.pageName = pageName
                this.projectId = projectId
                this.taskClassId = taskClassId
            }
    }
}