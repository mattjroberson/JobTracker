package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthew.jobtracker.databinding.FragmentJobSettingsMenuBinding
import com.matthew.jobtracker.recyclerviews.RvAdapter
import com.matthew.jobtracker.recyclerviews.RvItem

class JobSettingsMenuFragment : Fragment() {

    private var _binding: FragmentJobSettingsMenuBinding? = null
    private val binding get() = _binding!!

    private var possibleJobs = mutableListOf<RvItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        possibleJobs.add(RvItem("Job 1", "") { navToTaskSettings() })
        possibleJobs.add(RvItem("Job 2", "") { })
        possibleJobs.add(RvItem("Job 3", "") { })

        connectRecyclerAdapter(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJobSettingsMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun connectRecyclerAdapter(view: View){
        val graphListAdapter = RvAdapter(possibleJobs, view)

        binding.rvPossibleJobs.apply{
            adapter = graphListAdapter
            layoutManager = LinearLayoutManager(view.context)
        }
    }

    private fun navToTaskSettings(){
        val action = JobSettingsMenuFragmentDirections.actionSettingsMenuFragmentToTaskSettingsMenuFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}