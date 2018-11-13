package com.example.kazu.medvol.Models

import com.example.kazu.medvol.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_list.view.*

class ListRow(val item_oL: Item_of_List): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.item_list
    }
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.item_list_name.text = item_oL.name
        viewHolder.itemView.item_list_ID_info.text = item_oL.id
        viewHolder.itemView.item_list_device_info.text = item_oL.device
    }
}