package com.russvkm.projectmanagement.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.adapters.ColorContainerAdapter
import kotlinx.android.synthetic.main.color_dialog.view.*

abstract class LabelColorListDialog(
    context: Context,
    var arrayList:ArrayList<String>,
    val mSelectedColor:String="",
    val title:String=""
): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view=LayoutInflater.from(context).inflate(R.layout.color_dialog,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View){
        view.selectColorHintTextView.text=title
        view.selectColorContainerRecyclerView.layoutManager=LinearLayoutManager(context)
        val adapter= ColorContainerAdapter(context,arrayList,mSelectedColor)
        view.selectColorContainerRecyclerView.adapter=adapter
        adapter.setOnClickListener(object :ColorContainerAdapter.OnClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        })
    }

    protected abstract fun onItemSelected(color:String)
}