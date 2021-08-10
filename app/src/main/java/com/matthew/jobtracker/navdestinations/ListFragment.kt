package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matthew.jobtracker.helpers.DatabaseHelper
import com.matthew.jobtracker.data.rv_items.ItemData
import com.matthew.jobtracker.databinding.FragmentCurrentJobsBinding
import com.matthew.jobtracker.helpers.RvAdapter

abstract class ListFragment<ItemDataType : ItemData>(
    private val name : String) : Fragment(), RvAdapter.OnItemListener {

    private var _binding: FragmentCurrentJobsBinding? = null
    private val binding get() = _binding!!

    private lateinit var db : DatabaseHelper
    private lateinit var itemList : MutableList<ItemDataType>

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
        loadItemData()

        connectRecyclerAdapter()

        binding.fab.setOnClickListener { onFabPressed() }

        requireActivity().apply{
            title = name

            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                onBackButtonPressed()
            }
        }
    }

    abstract fun loadItemData()

    abstract fun onFabPressed()

    abstract fun onBackButtonPressed()

    abstract fun onItemSwipe(viewHolder : RecyclerView.ViewHolder)

    private fun connectRecyclerAdapter(){
        val graphListAdapter = RvAdapter(itemList, this)

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {return true}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onItemSwipe(viewHolder)
            }
        }

        binding.rvActiveJobs.apply{
            adapter = graphListAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}