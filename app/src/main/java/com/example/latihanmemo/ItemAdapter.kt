package com.example.latihanmemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_row.view.*

class ItemAdapter (val context: Context, val items : ArrayList<modelclass>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val llMain = view.llMain
        val tvCalendar = view.tvCalendar
        val tvKeterangan = view.tvKeterangan
        val ivDelete  = view.ivDelete


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
      return ViewHolder(
          LayoutInflater.from(context).inflate(
              R.layout.item_row,parent,false
          )
      )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        val item = items.get(position)
        holder.tvCalendar.text = item.calendar
        holder.tvKeterangan.text = item.keterangan
        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.white
                    )
            )
        } else {
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
//

        holder.ivDelete.setOnClickListener {
            if(context is MainActivity){
                context.deleteRecordAlertDialog(item)
            }
        }
    }


}