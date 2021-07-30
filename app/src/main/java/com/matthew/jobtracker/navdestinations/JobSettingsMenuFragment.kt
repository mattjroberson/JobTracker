package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.DialogCallback
import com.matthew.jobtracker.data.JobTemplate
import com.matthew.jobtracker.databinding.FragmentJobSettingsMenuBinding
import com.matthew.jobtracker.popups.NewSettingFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter
import com.matthew.jobtracker.recyclerviews.RvItem

class JobSettingsMenuFragment : Fragment(), DialogCallback {

    private lateinit var db : DatabaseHelper
    private lateinit var templates : MutableMap<String, MutableList<String>>

    private var _binding: FragmentJobSettingsMenuBinding? = null
    private val binding get() = _binding!!

    private var possibleJobs = mutableListOf<RvItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = "Settings: Jobs"

        db = DatabaseHelper(requireContext())
        templates = db.getTemplates()

        templates.keys.forEach{ job ->
            possibleJobs.add(RvItem(job, "") {navToTaskSettings(job)})
        }

        binding.fab.setOnClickListener {
            val newFragment = NewSettingFragment()
            newFragment.setTargetFragment(this, 0)
            newFragment.show(parentFragmentManager, "new_job_setting")
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            navBackToActiveJobs()
        }

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

    override fun onDialogDismiss(response : String?){
        //Ignore if no new name given or name already exists
        if(response == null || possibleJobs.contains(response)) return

        val newTemplate = JobTemplate(response)

        db.addJobTemplate(newTemplate)
        templates[response] = mutableListOf()

        possibleJobs.add(RvItem(response, "") {navToTaskSettings(response)})
        binding.rvPossibleJobs.adapter?.notifyDataSetChanged()
    }

    private fun connectRecyclerAdapter(view: View){
        val graphListAdapter = RvAdapter(possibleJobs, view)

        binding.rvPossibleJobs.apply{
            adapter = graphListAdapter
            layoutManager = LinearLayoutManager(view.context)
        }
    }

    private fun navToTaskSettings(job : String){
        val taskList = templates[job]?.toTypedArray() ?: return

        val action = JobSettingsMenuFragmentDirections.actionSettingsMenuFragmentToTaskSettingsMenuFragment(taskList, job)
        findNavController().navigate(action)
    }

    private fun navBackToActiveJobs(){
        val action = JobSettingsMenuFragmentDirections.actionJobSettingsMenuFragmentToActiveJobsFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

