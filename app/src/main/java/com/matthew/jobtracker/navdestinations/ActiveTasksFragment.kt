package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthew.jobtracker.databinding.FragmentActiveTasksBinding
import com.matthew.jobtracker.popups.EditTaskFragment
import com.matthew.jobtracker.popups.NewSettingFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter
import com.matthew.jobtracker.recyclerviews.RvItem

class ActiveTasksFragment : Fragment() {
    private var _binding: FragmentActiveTasksBinding? = null
    private val binding get() = _binding!!

    private var curTaskItems = mutableListOf<RvItem>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentActiveTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        curTaskItems.add(RvItem("TEST", "4:31") { showEditPopup() })
        connectRecyclerAdapter(view)
    }

    private fun showEditPopup(){
        val newFragment = EditTaskFragment()
        newFragment.show(parentFragmentManager,"new_job_setting")
    }

    private fun connectRecyclerAdapter(view: View){
        val graphListAdapter = RvAdapter(curTaskItems, view)

        binding.rvActiveTasks.apply{
            adapter = graphListAdapter
            layoutManager = LinearLayoutManager(view.context)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}