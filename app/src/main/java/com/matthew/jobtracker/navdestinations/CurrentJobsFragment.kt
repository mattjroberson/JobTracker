package com.matthew.jobtracker.navdestinations

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.R
import com.matthew.jobtracker.data.Job
import com.matthew.jobtracker.data.Task
import com.matthew.jobtracker.databinding.FragmentCurrentJobsBinding
import com.matthew.jobtracker.popups.NewTaskFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter
import com.matthew.jobtracker.recyclerviews.RvItem


class CurrentJobsFragment : Fragment() {
    private var _binding: FragmentCurrentJobsBinding? = null
    private val binding get() = _binding!!

    private var curJobItems = mutableListOf<RvItem>()
    private var backBtnPressed = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCurrentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = "Job Tracker"

        val db = DatabaseHelper(requireContext())
        val jobs = db.getCurrentJobs()

        jobs.values.forEach{ job ->
            curJobItems.add(RvItem(job.name, job.prettyTotalTime) { navigateToActiveTasks(job.name) })
        }

        binding.fab.setOnClickListener {
            val newFragment = NewTaskFragment()
            newFragment.show(parentFragmentManager, "new_task")
        }

        connectRecyclerAdapter(view)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true

            if(backBtnPressed) navigateOutOfApp()
            else notifyBackPressed()
        }
    }

    private fun navigateToActiveTasks(jobName : String){
        val action = CurrentJobsFragmentDirections.actionActiveJobsFragmentToActiveTasksFragment(jobName)
        findNavController().navigate(action)
    }

    private fun navigateOutOfApp(){
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun notifyBackPressed(){
        backBtnPressed = true
        Handler().postDelayed({ backBtnPressed = false }, 2000)
        Toast.makeText(requireContext(), resources.getString(R.string.back_message), Toast.LENGTH_SHORT).show();
    }

    private fun connectRecyclerAdapter(view: View){
        val graphListAdapter = RvAdapter(curJobItems, view)

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {return true}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                curJobItems.removeAt(viewHolder.adapterPosition)
                graphListAdapter.notifyDataSetChanged()
            }

        }

        binding.rvActiveJobs.apply{
            adapter = graphListAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(view.context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}