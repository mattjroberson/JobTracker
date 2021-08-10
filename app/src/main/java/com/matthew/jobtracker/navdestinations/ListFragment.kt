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
import androidx.viewbinding.ViewBinding
import com.matthew.jobtracker.helpers.DatabaseHelper
import com.matthew.jobtracker.data.rv_items.ItemData
import com.matthew.jobtracker.helpers.RvAdapter

abstract class ListFragment<ItemDataType : ItemData, VbType : ViewBinding>(
    private val name : String = "") : Fragment(), RvAdapter.OnItemListener {

    private var _binding: VbType? = null
    protected val binding get() = _binding!!

    protected var _recyclerView: RecyclerView? = null
    protected val recyclerView get() = _recyclerView!!

    protected lateinit var db : DatabaseHelper
    protected var itemList : MutableList<ItemDataType> = mutableListOf()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        loadListData()

        setupViews()
        connectRecyclerAdapter()

        requireActivity().apply{
            title = name

            onBackPressedDispatcher.addCallback(this) {
                this.isEnabled = true
                onBackButtonPressed()
            }
        }
    }

    //TODO Maybe make this return the data
    abstract fun loadListData()

    abstract fun getViewBinding() : VbType

    abstract fun setupViews()

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
                adapter.notifyDataSetChanged()
            }
        }

        recyclerView.apply{
            this.adapter = adapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _recyclerView = null
    }


}