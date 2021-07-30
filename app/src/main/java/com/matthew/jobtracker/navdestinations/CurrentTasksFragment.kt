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

    private var curTaskItems = mutableListOf<RvItem>()

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

        requireActivity().title = "${args.job} Tasks"

        db = DatabaseHelper(requireContext())
        val job = db.getCurrentJobs()[args.job]

        job?.taskList?.values?.forEach{ task ->
            curTaskItems.add(RvItem(task.taskName, task.prettyTime) { showEditPopup(job, task.taskName) })
        }
        connectRecyclerAdapter(view)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            navigateBackToCurrentJobs()
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

        curTaskItems.clear()
        //TODO Refactor this with code in onCreate

        val job = db.getCurrentJobs()[args.job]

        job?.taskList?.values?.forEach{ task ->
            curTaskItems.add(RvItem(task.taskName, task.prettyTime) { showEditPopup(job, task.taskName) })
        }

        binding.rvActiveTasks.adapter?.notifyDataSetChanged()
    }

    private fun connectRecyclerAdapter(view: View){
        val graphListAdapter = RvAdapter(curTaskItems, view)

        binding.rvActiveTasks.apply{
            adapter = graphListAdapter
            layoutManager = LinearLayoutManager(view.context)
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