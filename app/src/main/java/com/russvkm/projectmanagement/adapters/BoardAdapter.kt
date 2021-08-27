package com.russvkm.projectmanagement.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.models.Board
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.recycler_view_handler.view.*

class BoardAdapter(val context: Context, private val boardArrayList:ArrayList<Board>):
    RecyclerView.Adapter<BoardAdapter.ViewHolder>() {
    private var onClickListener:OnClickListener?=null

    class ViewHolder (view: View):RecyclerView.ViewHolder(view){
        val titleTextView:TextView=view.boardNameAdapterTextView
        val createdByTextView:TextView=view.createdByTextView
        val boardImageView:CircleImageView=view.adapterImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recycler_view_handler,parent,false)
        )
    }

    override fun getItemCount(): Int {
       return boardArrayList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model=boardArrayList[position]
        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.user_image)
            .into(holder.boardImageView)
        holder.titleTextView.text=model.card_name
        holder.createdByTextView.text="Created By: ${model.cratedBy}"
        holder.itemView.setOnClickListener{
            if(onClickListener!=null){
                onClickListener!!.onClick(position,model)
            }
        }
    }
    interface OnClickListener {
        fun onClick(position: Int,model:Board)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }
}
