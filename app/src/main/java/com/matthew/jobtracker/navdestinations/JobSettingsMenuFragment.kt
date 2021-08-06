package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.DialogCallback
import com.matthew.jobtracker.data.rv_items.JobSettingItemData
import com.matthew.jobtracker.data.JobTemplate
import com.matthew.jobtracker.databinding.FragmentJobSettingsMenuBinding
import com.matthew.jobtracker.popups.NewSettingFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter

class JobSettingsMenuFragment : Fragment(), DialogCallback, RvAdapter.OnItemListener {

    private lateinit var db : DatabaseHelper
    private lateinit var jobItems : MutableList<JobSettingItemData>

    private var _binding: FragmentJobSettingsMenuBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        val templates = db.getTemplates()

        jobItems = templates.map{JobSettingItemData(it)} as MutableList<JobSettingItemData>

        binding.fab.setOnClickListener {
            val newFragment = NewSettingFragment()
            newFragment.setTargetFragment(this, 0)
            newFragment.show(parentFragmentManager, "new_job_setting")
        }

        requireActivity().apply{
            title = "Jobs Settings"
            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                navBackToActiveJobs()
            }
        }

        connectRecyclerAdapter()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentJobSettingsMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDialogDismiss(response : String?){
        //Ignore if no new name given or name already exists
        if(response == null || jobItems.map{it.text}.contains(response)) return

        val newTemplate = JobTemplate(response)
        db.addJobTemplate(newTemplate)

        jobItems.add(JobSettingItemData(newTemplate))

        binding.rvPossibleJobs.adapter?.notifyDataSetChanged()
    }

    private fun connectRecyclerAdapter(){

        val graphListAdapter = RvAdapter(jobItems,this)

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {return true}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                removeJobAtPos(viewHolder.adapterPosition)
                graphListAdapter.notifyDataSetChanged()
            }
        }

        binding.rvPossibleJobs.apply{
            adapter = graphListAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    override fun onItemClick(position : Int){
        val jobItem = jobItems[position]
        val taskList = jobItem.template.taskTemplates.toTypedArray()

        val action = JobSettingsMenuFragmentDirections.actionSettingsMenuFragmentToTaskSettingsMenuFragment(taskList, jobItem.template.name)
        findNavController().navigate(action)
    }

    private fun navBackToActiveJobs(){
        val action = JobSettingsMenuFragmentDirections.actionJobSettingsMenuFragmentToActiveJobsFragment()
        findNavController().navigate(action)
    }

    private fun removeJobAtPos(position: Int){
        val name = jobItems[position].template.name
        db.deleteJobTemplate(name)
        jobItems.removeAt(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

