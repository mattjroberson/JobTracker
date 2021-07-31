package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.DialogCallback
import com.matthew.jobtracker.data.JobTemplate
import com.matthew.jobtracker.databinding.FragmentTaskSettingsMenuBinding
import com.matthew.jobtracker.popups.NewSettingFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter
import com.matthew.jobtracker.recyclerviews.RvItem
import javax.xml.transform.Templates

class TaskSettingsMenuFragment : Fragment(), DialogCallback {

    private lateinit var db : DatabaseHelper
    private var _binding: FragmentTaskSettingsMenuBinding? = null
    private val binding get() = _binding!!
    private val args : TaskSettingsMenuFragmentArgs by navArgs()

    private var possibleTasks = mutableListOf<RvItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())


        binding.fab.setOnClickListener {
            val newFragment = NewSettingFragment()
            newFragment.setTargetFragment(this, 0)
            newFragment.show(parentFragmentManager, "new_task_setting")
        }

        connectRecyclerAdapter(args.taskList.toMutableList())

        requireActivity().apply{
            title = "Settings: ${args.job} Tasks"

            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                navigateBackToJobSettings()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTaskSettingsMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun connectRecyclerAdapter(tasks : MutableList<String>){
        val graphListAdapter = RvAdapter(tasks, tasks)

        binding.rvPossibleTasks.apply{
            adapter = graphListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun navigateBackToJobSettings(){
        val action = TaskSettingsMenuFragmentDirections.actionTaskSettingsMenuFragmentToJobSettingsMenuFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDialogDismiss(response : String?){
        if(response == null || possibleTasks.contains(response)) return


        val taskList = possibleTasks.map { it -> it.title}.toMutableList()
        taskList.add(response)

        val template = JobTemplate(args.job,taskList)
        db.addJobTemplate(template)

        possibleTasks.add(RvItem(response, "") {})
        binding.rvPossibleTasks.adapter?.notifyDataSetChanged()
    }
}