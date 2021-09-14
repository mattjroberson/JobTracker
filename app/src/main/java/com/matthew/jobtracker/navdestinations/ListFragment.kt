package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matthew.jobtracker.helpers.DatabaseHelper
import com.matthew.jobtracker.data.rv_items.ItemData
import com.matthew.jobtracker.databinding.FragmentListBinding
import com.matthew.jobtracker.helpers.RvAdapter

abstract class ListFragment<ItemDataType : ItemData>(private val name : String = "") :
    Fragment(), RvAdapter.OnItemListener {

    private var _binding: FragmentListBinding? = null
    protected val binding get() = _binding!!

    private var _db: DatabaseHelper? = null
    protected val db get() = _db!!

    private lateinit var _itemList : MutableList<ItemDataType>
    protected val itemList get() = _itemList

    protected val lastItemIndex get() = itemList.size-1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _db = DatabaseHelper(requireContext())
        _itemList = getListData()

        binding.fab.setOnClickListener { onFabPressed() }
        connectRecyclerAdapter()

        requireActivity().apply{
            title = name

            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                onBackButtonPressed()
            }
        }
    }

    protected fun setFabEnabled(enabled : Boolean){
        binding.fab.isEnabled = enabled
        binding.fab.isVisible = enabled
    }

    protected fun setTitle(title : String){
        requireActivity().title = title
    }

    abstract fun getListData() : MutableList<ItemDataType>

    abstract fun onBackButtonPressed()

    abstract fun onItemSwipe(position : Int)

    //TODO This should be its own interface
    open fun onFabPressed(){ }

    private fun connectRecyclerAdapter(){
        val adapter = RvAdapter(itemList, this)

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {return true}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onItemSwipe(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }
        }

        binding.recyclerView.apply{
            this.adapter = adapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _db = null
    }


}