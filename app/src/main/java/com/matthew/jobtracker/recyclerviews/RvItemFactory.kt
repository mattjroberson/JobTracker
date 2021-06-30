package com.matthew.jobtracker.recyclerviews

import android.view.View
import com.matthew.jobtracker.R

class RvItemFactory {
    companion object {
        fun getItemViewType(item : RvItem): Int {
            when(item) {
//                is RvClickableItem -> return 0;
                is RvActiveJobItem -> return 1;
//                is RvTextFieldItem -> return 2;
//                is RvBooleanItem -> return 3;
//                is RvNumberFieldItem -> return 4;
//                is RvDropdownItem -> return 5;
//                is RvCurveEntryItem -> return 6;
            }

            return -1;
        }

        fun getItemLayout(viewType : Int): Int {
            when(viewType){
//                0 -> return R.layout.item_clickable
                1 -> return R.layout.item_active_job
//                2 -> return R.layout.item_text_field
//                3 -> return R.layout.item_boolean
//                4 -> return R.layout.item_number_field
//                5 -> return R.layout.item_dropdown
//                6 -> return R.layout.item_curve_entry
            }

            return -1;
        }

        fun bindViewHolder(itemView : View, item : RvItem) {
            item.attach(itemView);
        }
    }
}

