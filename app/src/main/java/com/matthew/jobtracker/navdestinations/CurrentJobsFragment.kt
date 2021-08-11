package com.matthew.jobtracker.navdestinations

import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.matthew.jobtracker.R
import com.matthew.jobtracker.data.rv_items.CurrentJobItemData
import com.matthew.jobtracker.popups.NewTaskFragment

class CurrentJobsFragment : ListFragment<CurrentJobItemData>("Job Tracker") {
    private var backBtnPressed = false

    override fun getListData() : MutableList<CurrentJobItemData> {
        return db.getCurrentJobs().map{CurrentJobItemData(it)} as MutableList<CurrentJobItemData>
    }

    override fun onItemClick(position: Int) {
        val name = itemList[position].job.name
        navigateToCurrentTasks(name)
    }

    override fun onFabPressed() {
        val newFragment = NewTaskFragment()
        newFragment.show(parentFragmentManager, "new_task")
    }

    override fun onBackButtonPressed() {
        if(backBtnPressed) navigateOutOfApp()
        else notifyBackPressed()
    }

    override fun onItemSwipe(position: Int) {
        deleteJob(position)
    }

    private fun navigateOutOfApp(){
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun navigateToCurrentTasks(jobName : String){
        val action = CurrentJobsFragmentDirections.actionActiveJobsFragmentToActiveTasksFragment(jobName)
        findNavController().navigate(action)
    }

    private fun notifyBackPressed(){
        backBtnPressed = true
        Handler().postDelayed({ backBtnPressed = false }, 2000)
        Toast.makeText(requireContext(), resources.getString(R.string.back_message), Toast.LENGTH_SHORT).show()
    }

    private fun deleteJob(position : Int){
        val oldItem = itemList[position]
        db.deleteCurrentJob(oldItem.job.name)
        itemList.remove(oldItem)
    }
}