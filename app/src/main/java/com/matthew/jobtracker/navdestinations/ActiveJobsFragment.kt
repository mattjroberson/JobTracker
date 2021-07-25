package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.databinding.FragmentActiveJobsBinding
import com.matthew.jobtracker.popups.NewTaskFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter
import com.matthew.jobtracker.recyclerviews.RvItem

class ActiveJobsFragment : Fragment() {
    private var _binding: FragmentActiveJobsBinding? = null
    private val binding get() = _binding!!

    private var curJobItems = mutableListOf<RvItem>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActiveJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = DatabaseHelper(requireContext())

        db.getJobs().forEach(){
            curJobItems.add(RvItem(it.name, it.getTotalTime().toString()) { navigateToActiveTasks() })
        }

        binding.fab.setOnClickListener {
            val newFragment = NewTaskFragment()
            newFragment.show(parentFragmentManager, "new_task")
        }

        connectRecyclerAdapter(view)

    }

    private fun navigateToActiveTasks(){
        val action = ActiveJobsFragmentDirections.actionActiveJobsFragmentToActiveTasksFragment()
        findNavController().navigate(action)
    }

    private fun connectRecyclerAdapter(view: View){
        val graphListAdapter = RvAdapter(curJobItems, view)

        binding.rvActiveJobs.apply{
            adapter = graphListAdapter
            layoutManager = LinearLayoutManager(view.context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}