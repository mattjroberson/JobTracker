package com.matthew.jobtracker.navdestinations

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.matthew.jobtracker.helpers.DialogCallback
import com.matthew.jobtracker.data.rv_items.CurrentTaskItemData
import com.matthew.jobtracker.databinding.FragmentCurrentTasksBinding
import com.matthew.jobtracker.popups.EditTaskFragment

class CurrentTasksFragment :
    ListFragment<CurrentTaskItemData, FragmentCurrentTasksBinding>(),
    DialogCallback {

    //title = "${job.name} Tasks"
    private lateinit var jobName : String

    private val args : CurrentTasksFragmentArgs by navArgs()
    private var editingPosition : Int = 0

    override fun getViewBinding(): FragmentCurrentTasksBinding {
        return FragmentCurrentTasksBinding.inflate(layoutInflater)
    }

    override fun setupViews() {
        _recyclerView = binding.rvActiveTasks
    }

    override fun loadListData() {
        jobName = args.jobName

        val job = db.getCurrentJob(jobName)

        if(job != null){
            itemList = job.taskList.map{CurrentTaskItemData(it)}
                    as MutableList<CurrentTaskItemData>
        }
    }

    override fun onItemClick(position: Int) {
        val task = itemList[position].task
        editingPosition = position

        val newFragment = EditTaskFragment.newInstance(task.name, task.prettyTime)
        newFragment.setTargetFragment(this, 0)
        newFragment.show(parentFragmentManager,"edit task")
    }

    override fun onItemSwipe(position: Int) {
        removeTaskAtPos(position)
    }

    override fun onBackButtonPressed() {
        navigateBackToCurrentJobs()
    }

    override fun onDialogDismiss(response : String?){
        //Ignore if no response given
        if(response == null) return

        editTaskTimeAtPos(response, editingPosition)
        recyclerView.adapter?.notifyDataSetChanged()
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
}