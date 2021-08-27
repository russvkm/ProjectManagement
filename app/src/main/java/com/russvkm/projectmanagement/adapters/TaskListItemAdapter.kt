package com.russvkm.projectmanagement.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.activity.TaskListActivity
import com.russvkm.projectmanagement.models.Task
import kotlinx.android.synthetic.main.task_list.view.*

class TaskListItemAdapter(private val context:Context,
                          private val list:ArrayList<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.task_list,parent,false)
        val layoutParams=LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp()).toPx(),0,(40.toDp()).toPx(),0)
        view.layoutParams=layoutParams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]

        if(holder is MyViewHolder){
            if(position==list.size-1){
                holder.itemView.tv_add_task_list.visibility=View.VISIBLE
                holder.itemView.ll_task_item.visibility=View.GONE
            }else{
                holder.itemView.tv_add_task_list.visibility=View.GONE
                holder.itemView.ll_task_item.visibility=View.VISIBLE
            }
            holder.itemView.tv_task_list_title.text=model.title
            holder.itemView.tv_add_task_list.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility=View.GONE
                holder.itemView.cv_add_task_list_name.visibility=View.VISIBLE
            }
            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility=View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility=View.GONE
            }
            holder.itemView.ib_done_list_name.setOnClickListener {
               val taskName= holder.itemView.et_task_list_name.text.toString()
                if(taskName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(taskName)
                    }
                }else{
                    Toast.makeText(context, "List Name can not be empty :(",Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.ib_edit_list_name.setOnClickListener {
                holder.itemView.et_edit_task_list_name.setText(model.title)
                holder.itemView.ll_title_view.visibility=View.GONE
                holder.itemView.cv_edit_task_list_name.visibility=View.VISIBLE
            }
            holder.itemView.ib_close_editable_view.setOnClickListener{
                holder.itemView.ll_title_view.visibility=View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility=View.GONE
            }
            holder.itemView.ib_done_edit_list_name.setOnClickListener {
                val listName=holder.itemView.et_edit_task_list_name.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }else{
                    Toast.makeText(context, "List Name can not be empty :(",Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.ib_delete_list.setOnClickListener {
                createAlertDialog(position,model.title)
            }
            holder.itemView.tv_add_card.setOnClickListener {
                holder.itemView.tv_add_card.visibility=View.GONE
                holder.itemView.cv_add_card.visibility=View.VISIBLE
            }
            holder.itemView.ib_close_card_name.setOnClickListener {
                holder.itemView.tv_add_card.visibility=View.VISIBLE
                holder.itemView.cv_add_card.visibility=View.GONE
            }
            holder.itemView.ib_done_card_name.setOnClickListener {
                val cardName=holder.itemView.et_card_name.text.toString()
                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createCard(position,cardName)
                    }else{
                        Toast.makeText(context, "Card Name can not be empty :(",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            holder.itemView.rv_card_list.layoutManager=LinearLayoutManager(context)
            holder.itemView.rv_card_list.setHasFixedSize(true)
            val adapter=CardAdapter(context,model.cards)
            holder.itemView.rv_card_list.adapter=adapter
            adapter.setOnClickListener(object : CardAdapter.OnClickListener{
                override fun onClick(cardPosition: Int) {
                    if(context is TaskListActivity){
                        context.intendingToCardDetailActivity(position,cardPosition)
                    }
                }
            })
        }
    }

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)


    private fun Int.toDp():Int= (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx():Int= (this*Resources.getSystem().displayMetrics.density).toInt()

    private fun createAlertDialog(position:Int,title:String){
        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete $title")
            .setTitle("Alert")
            .setIcon(R.drawable.ic_warning)
            .setCancelable(false)
            .setPositiveButton("YES"){
                    _, _ ->
                if(context is TaskListActivity){
                    context.deleteTaskList(position)
                }
            }
            .setNegativeButton("NO"){
                    dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}