package com.russvkm.projectmanagement.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.russvkm.projectmanagement.R
import kotlinx.android.synthetic.main.color_adapter_layout.view.*

class ColorContainerAdapter(private val context: Context,
                            private val arrayList:ArrayList<String>,
                            private val mSelectedColor:String):
                            RecyclerView.Adapter<ColorContainerAdapter.MyViewHolder>() {

    private var onClickListener:OnClickListener?=null

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view) {

        val mainColorView:View=view.mainColorView
        val selectColorIndicator: ImageView =view.selectColorIndicator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.color_adapter_layout,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item=arrayList[position]
        holder.mainColorView.setBackgroundColor(Color.parseColor(item))
        if(item==mSelectedColor){
            holder.selectColorIndicator.visibility=View.VISIBLE
        }else{
            holder.selectColorIndicator.visibility=View.GONE
        }
        holder.itemView.setOnClickListener {
            if(onClickListener!=null) {
                onClickListener!!.onClick(position,item)
            }
        }
    }
    interface OnClickListener{
        fun onClick(position: Int,color:String)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }
}