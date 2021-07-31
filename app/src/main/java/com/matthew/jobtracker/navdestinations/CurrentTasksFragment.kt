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
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.DialogCallback
import com.matthew.jobtracker.data.Job
import com.matthew.jobtracker.data.JobTemplate
import com.matthew.jobtracker.data.Task
import com.matthew.jobtracker.databinding.FragmentCurrentTasksBinding
import com.matthew.jobtracker.popups.EditTaskFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter
import com.matthew.jobtracker.recyclerviews.RvItem

class CurrentTasksFragment : Fragment(), DialogCallback {
    private lateinit var db : DatabaseHelper

    private var _binding: FragmentCurrentTasksBinding? = null
    private val binding get() = _binding!!

    private val args : CurrentTasksFragmentArgs by navArgs()

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
        val job = db.getCurrentJobs()[args.jobPosition]
        connectRecyclerAdapter(job)

        requireActivity().apply{
            title = "${job.name} Tasks"
            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                navigateBackToCurrentJobs()
            }
        }
    }

    private fun showEditPopup(job : Job, taskName : String){
        val newFragment = EditTaskFragment(job, taskName)
        newFragment.setTargetFragment(this, 0)
        newFragment.show(parentFragmentManager,"edit task")
    }

    override fun onDialogDismiss(response : String?){
        //Ignore if no response given
        if(response == null) return

        //TODO Add back in the add option here
//        val job = db.getCurrentJobs()[args.job]

        binding.rvActiveTasks.adapter?.notifyDataSetChanged()
    }

    private fun connectRecyclerAdapter(job : Job){
        val graphListAdapter = RvAdapter(
            job.taskList.map{it.taskName},
            job.taskList.map{it.prettyTime})

        binding.rvActiveTasks.apply{
            adapter = graphListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
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