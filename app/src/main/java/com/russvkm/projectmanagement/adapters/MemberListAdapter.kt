package com.russvkm.projectmanagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.models.User
import com.russvkm.projectmanagement.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.member_list.view.*

class MemberListAdapter(private val context: Context, private val list:ArrayList<User>):
    RecyclerView.Adapter<MemberListAdapter.MyViewHolder>(){

    private var onClickListener:OnClickListener?=null
    class MyViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val memberImage:CircleImageView=view.membersPhotograph
        val titleTextView: AppCompatTextView =view.titleTextView
        val createdByTextView:AppCompatTextView=view.createdByTextView
        val selectMemberIndicator: ImageView =view.selectMemberIndicator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.member_list,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model=list[position]
        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_update_user_image)
            .into(holder.memberImage)
        holder.titleTextView.text=model.name
        holder.createdByTextView.text=model.email
        if (model.selected){
            holder.selectMemberIndicator.visibility=View.VISIBLE
        }else{
            holder.selectMemberIndicator.visibility=View.GONE
        }
        holder.itemView.setOnClickListener {
            if (onClickListener!=null){
                if (model.selected){
                    onClickListener!!.onClick(position,model,Constants.UN_SELECT)
                }else{
                    onClickListener!!.onClick(position,model,Constants.SELECT)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position:Int,user:User,select:String)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

}
