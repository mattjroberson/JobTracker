package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.DialogCallback
import com.matthew.jobtracker.data.rv_items.ItemData
import com.matthew.jobtracker.data.JobTemplate
import com.matthew.jobtracker.data.rv_items.TaskSettingItemData
import com.matthew.jobtracker.databinding.FragmentTaskSettingsMenuBinding
import com.matthew.jobtracker.popups.NewSettingFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter

class TaskSettingsMenuFragment : Fragment(), DialogCallback {

    private lateinit var db : DatabaseHelper
    private var _binding: FragmentTaskSettingsMenuBinding? = null
    private val binding get() = _binding!!
    private val args : TaskSettingsMenuFragmentArgs by navArgs()

    private lateinit var taskItems : MutableList<TaskSettingItemData>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        val possibleTasks = args.taskList.toMutableList()
        taskItems = possibleTasks.map{ TaskSettingItemData(it) } as MutableList<TaskSettingItemData>

        binding.fab.setOnClickListener {
            val newFragment = NewSettingFragment()
            newFragment.setTargetFragment(this, 0)
            newFragment.show(parentFragmentManager, "new_task_setting")
        }

        connectRecyclerAdapter()

        requireActivity().apply{
            title = "${args.job}: Task Settings"

            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                navigateBackToJobSettings()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTaskSettingsMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun connectRecyclerAdapter(){
        val graphListAdapter = RvAdapter(taskItems, null)

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {return true}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                removeTaskAtPos(viewHolder.adapterPosition)
                graphListAdapter.notifyDataSetChanged()
            }
        }

        binding.rvPossibleTasks.apply{
            adapter = graphListAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun navigateBackToJobSettings(){
        val action = TaskSettingsMenuFragmentDirections.actionTaskSettingsMenuFragmentToJobSettingsMenuFragment()
        findNavController().navigate(action)
    }

    private fun removeTaskAtPos(position : Int){
        taskItems.removeAt(position)

        val template = JobTemplate(args.job, taskItems.map{it.text} as MutableList<String>)
        db.addJobTemplate(template)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDialogDismiss(response : String?){
        if(response == null || taskItems.map{it.text}.contains(response)) return

        taskItems.add(TaskSettingItemData(response))

        val template = JobTemplate(args.job, taskItems.map{it.text} as MutableList<String>)
        db.addJobTemplate(template)

        binding.rvPossibleTasks.adapter?.notifyDataSetChanged()
    }
}