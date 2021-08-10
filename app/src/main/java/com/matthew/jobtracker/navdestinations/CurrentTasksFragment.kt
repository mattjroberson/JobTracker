package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import android.util.Log
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
import com.matthew.jobtracker.helpers.DatabaseHelper
import com.matthew.jobtracker.helpers.DialogCallback
import com.matthew.jobtracker.data.rv_items.CurrentTaskItemData
import com.matthew.jobtracker.databinding.FragmentCurrentTasksBinding
import com.matthew.jobtracker.popups.EditTaskFragment
import com.matthew.jobtracker.helpers.RvAdapter

class CurrentTasksFragment : Fragment(), DialogCallback, RvAdapter.OnItemListener{
    private lateinit var db : DatabaseHelper
    private lateinit var itemList : MutableList<CurrentTaskItemData>
    private lateinit var jobName : String

    private var _binding: FragmentCurrentTasksBinding? = null
    private val binding get() = _binding!!

    private val args : CurrentTasksFragmentArgs by navArgs()
    private var editingPosition : Int = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCurrentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        jobName = args.jobName

        val job = db.getCurrentJob(jobName)

        itemList = job?.taskList?.map{CurrentTaskItemData(it)} as MutableList<CurrentTaskItemData>

        connectRecyclerAdapter()

        requireActivity().apply{
            title = "${job.name} Tasks"

            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                navigateBackToCurrentJobs()
            }
        }
    }

    override fun onItemClick(position: Int) {
        val task = itemList[position].task
        editingPosition = position

        val newFragment = EditTaskFragment.newInstance(task.name, task.prettyTime)
        newFragment.setTargetFragment(this, 0)
        newFragment.show(parentFragmentManager,"edit task")
    }

    override fun onDialogDismiss(response : String?){
        //Ignore if no response given
        if(response == null) return

        editTaskTimeAtPos(response, editingPosition)
        binding.rvActiveTasks.adapter?.notifyDataSetChanged()
    }

    private fun connectRecyclerAdapter(){
        val graphListAdapter = RvAdapter(itemList, this)

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {return true}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                removeTaskAtPos(viewHolder.adapterPosition)
                graphListAdapter.notifyDataSetChanged()
            }
        }

        binding.rvActiveTasks.apply{
            adapter = graphListAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun removeTaskAtPos(position: Int){
        val job = db.getCurrentJob(jobName) ?: return
        job.taskList.removeAt(position)
        db.addCurrentJob(job)
        itemList.removeAt(position)
    }

    private fun editTaskTimeAtPos(newTime : String, position: Int){
        val job = db.getCurrentJob(jobName) ?: return
        job.taskList[position].loggedTime = newTime.toLong()
        itemList[position] = CurrentTaskItemData(job.taskList[position])
        db.addCurrentJob(job)
    }
    private fun navigateBackToCurrentJobs(){
        val action = CurrentTasksFragmentDirections.actionActiveTasksFragmentToActiveJobsFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}