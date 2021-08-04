package com.matthew.jobtracker.navdestinations

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.matthew.jobtracker.data.rv_items.CurrentJobItemData
import com.matthew.jobtracker.databinding.FragmentCurrentJobsBinding
import com.matthew.jobtracker.popups.NewTaskFragment
import com.matthew.jobtracker.recyclerviews.RvAdapter

class CurrentJobsFragment : Fragment(), RvAdapter.OnItemListener {
    private var _binding: FragmentCurrentJobsBinding? = null
    private val binding get() = _binding!!

    private var backBtnPressed = false

    private lateinit var db : DatabaseHelper
    private lateinit var itemList : MutableList<CurrentJobItemData>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCurrentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        itemList = db.getCurrentJobs().map{CurrentJobItemData(it)} as MutableList<CurrentJobItemData>

        connectRecyclerAdapter()

        binding.fab.setOnClickListener {
            val newFragment = NewTaskFragment()
            newFragment.show(parentFragmentManager, "new_task")
        }

        requireActivity().apply{
            title = "Job Tracker"

            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                if(backBtnPressed) navigateOutOfApp()
                else notifyBackPressed()
            }
        }
    }

    override fun onItemClick(position: Int) {
        val name = itemList[position].job.name
        val action = CurrentJobsFragmentDirections.actionActiveJobsFragmentToActiveTasksFragment(name)
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
        Toast.makeText(requireContext(), resources.getString(R.string.back_message), Toast.LENGTH_SHORT).show()
    }

    private fun connectRecyclerAdapter(){
        val graphListAdapter = RvAdapter(itemList, this)

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {return true}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteJob(viewHolder.adapterPosition)
                graphListAdapter.notifyDataSetChanged()
            }
        }

        binding.rvActiveJobs.apply{
            adapter = graphListAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun deleteJob(position : Int){
        val oldItem = itemList[position]
        db.deleteCurrentJob(oldItem.job.name)
        itemList.remove(oldItem)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}