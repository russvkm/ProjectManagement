package com.russvkm.projectmanagement.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.activity.TaskListActivity
import com.russvkm.projectmanagement.models.Card
import com.russvkm.projectmanagement.models.SelectedMember
import kotlinx.android.synthetic.main.card_list_adapter_layout.view.*

class CardAdapter(private val context: Context, private val arrayList:ArrayList<Card>):RecyclerView.Adapter<CardAdapter.MyViewHolder>(){

    private var onClickListener:OnClickListener?=null

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val cardName:TextView=view.cardName
        val colorCard:TextView=view.colorCard
        val imageHolderRecyclerView:RecyclerView=view.imageHolderListOnCardAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_list_adapter_layout,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model=arrayList[position]
        holder.cardName.text=model.card_name

        if((context as TaskListActivity).mAssignedUserList.size>0){
            val selectedMemberList:ArrayList<SelectedMember> = ArrayList()
            for (i in context.mAssignedUserList.indices){
                for(j in model.assignedTo){
                    if(context.mAssignedUserList[i].id==j){
                        val selectedMember=SelectedMember(
                        context.mAssignedUserList[i].id,
                        context.mAssignedUserList[i].image
                        )
                        selectedMemberList.add(selectedMember)
                    }
                }
            }

            if (selectedMemberList.size>0){
                if(selectedMemberList.size==1&&selectedMemberList[0].id==model.createdBy){
                    holder.imageHolderRecyclerView.visibility=View.GONE
                }else{
                    holder.imageHolderRecyclerView.visibility=View.VISIBLE
                    holder.imageHolderRecyclerView.layoutManager=GridLayoutManager(context,3)
                    val adapter=CardMemberListAdapter(context,selectedMemberList,false)
                    holder.imageHolderRecyclerView.adapter=adapter
                    adapter.setOnClickListener(object : CardMemberListAdapter.OnClickListener{
                        override fun onClick() {
                            if (onClickListener!=null)
                                onClickListener!!.onClick(position)
                        }

                    })
                }
            }else{
                holder.imageHolderRecyclerView.visibility=View.GONE
            }
        }
        if(model.labelColor.isNotEmpty()){
            holder.colorCard.setBackgroundColor(Color.parseColor(model.labelColor))
        }
        holder.itemView.setOnClickListener{
            if (onClickListener!=null){
                onClickListener!!.onClick(position)
            }
        }
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener=onClickListener
    }

    interface OnClickListener{
        fun onClick(cardPosition: Int)
    }

}