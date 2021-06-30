package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.matthew.jobtracker.databinding.FragmentFirstBinding
import com.matthew.jobtracker.recyclerviews.RvActiveJobItem
import com.matthew.jobtracker.recyclerviews.RvAdapter

class ActiveJobFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private var curJobItems = mutableListOf<RvActiveJobItem>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        curJobItems.add(RvActiveJobItem("TEST") { _: RvActiveJobItem, _: RvActiveJobItem.Companion.ActionType -> })
        connectRecyclerAdapter(view)
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