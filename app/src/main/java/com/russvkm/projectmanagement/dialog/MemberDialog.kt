package com.russvkm.projectmanagement.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.adapters.MemberListAdapter
import com.russvkm.projectmanagement.models.User
import kotlinx.android.synthetic.main.color_dialog.view.*

abstract class MemberDialog(context:Context,
        private val arrayList:ArrayList<User>,
        private val title:String):Dialog(context) {
    var adapter:MemberListAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view=LayoutInflater.from(context).inflate(R.layout.color_dialog,null)
        setContentView(view)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        configureRecyclerView(view)
    }

    private fun configureRecyclerView(view:View){
        view.selectColorHintTextView.text=title
        if(arrayList.size>0){
            view.selectColorContainerRecyclerView.layoutManager=LinearLayoutManager(context)
            adapter= MemberListAdapter(context,arrayList)
            view.selectColorContainerRecyclerView.adapter=adapter
            adapter!!.setOnClickListener(object :MemberListAdapter.OnClickListener{
                override fun onClick(position: Int, user: User, select: String) {
                    dismiss()
                    onItemSelected(user,select)
                }

            })
        }

    }
    protected abstract fun onItemSelected(user:User,action:String)
}