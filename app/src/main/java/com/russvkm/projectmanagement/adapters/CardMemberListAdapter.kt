package com.russvkm.projectmanagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.models.SelectedMember
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.selected_member_adapter_inflater.view.*

open class CardMemberListAdapter (
    private val context: Context,
    private val arrayList: ArrayList<SelectedMember>,
    private val assigned:Boolean
    ):RecyclerView.Adapter<CardMemberListAdapter.MyViewHolder>(){

    private var onClickListener:OnClickListener?=null

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val memberImage:CircleImageView=view.memberImage
        val addMember:CircleImageView=view.addMember
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.selected_member_adapter_inflater,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val model=arrayList[position]
        if(position==arrayList.size-1&& assigned){
            holder.addMember.visibility=View.VISIBLE
            holder.memberImage.visibility=View.GONE
        }else{
            holder.addMember.visibility=View.GONE
            holder.memberImage.visibility=View.VISIBLE
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_update_user_image)
                .into(holder.memberImage)
        }
        holder.itemView.setOnClickListener {
            if (onClickListener!=null){
                onClickListener!!.onClick()
            }
        }
    }

    interface OnClickListener{
        fun onClick()
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

}