package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthew.jobtracker.databinding.FragmentTaskSettingsMenuBinding
import com.matthew.jobtracker.recyclerviews.RvAdapter
import com.matthew.jobtracker.recyclerviews.RvItem

class TaskSettingsMenuFragment : Fragment() {

    private var _binding: FragmentTaskSettingsMenuBinding? = null
    private val binding get() = _binding!!

    private var possibleTasks = mutableListOf<RvItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        possibleTasks.add(RvItem("Task 1", "") { })
        possibleTasks.add(RvItem("Task 2", "") { })

        connectRecyclerAdapter(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTaskSettingsMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun connectRecyclerAdapter(view: View){
        val graphListAdapter = RvAdapter(possibleTasks, view)

        binding.rvPossibleTasks.apply{
            adapter = graphListAdapter
            layoutManager = LinearLayoutManager(view.context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}