package com.fabs.beem


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.findNavController

import com.fabs.beem.atv.model.TreeNode


/**
 * Created by admin on 31-03-2016.
 */
class MyHolder(context: Context, var toggle: Boolean, var child: Int) :
    TreeNode.BaseNodeViewHolder<MyHolder.IconTreeItem>(context) {

    lateinit var img: ImageView
    lateinit var tvValue: TextView
    internal var leftMargin: Int = 0

    override fun createNodeView(node: TreeNode, value: IconTreeItem): View {
        val inflater = LayoutInflater.from(context)
        val view: View
        if (child == DEFAULT) {
            view = inflater.inflate(R.layout.parent, null, false)
        } else {
            view = inflater.inflate(child, null, false)
        }
        Log.d("MyHolder", "node level: " + node.level)
        leftMargin = getDimens(R.dimen.treeview_left_padding) * node.level

        view.setPadding(
            leftMargin,
            getDimens(R.dimen.treeview_top_padding),
            getDimens(R.dimen.treeview_right_padding),
            getDimens(R.dimen.treeview_bottom_padding)
        )
        if (value.uri != "") {

            view.setOnClickListener {

                val i = Intent(Intent.ACTION_VIEW, Uri.parse(value.uri))
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.setPackage("com.android.chrome")
                try {
                    context.startActivity(i)
                } catch (ex: ActivityNotFoundException) {
                    i.setPackage(null)
                    context.startActivity(i)
                }
            }
        }
        img = view.findViewById<View>(R.id.image) as ImageView
        tvValue = view.findViewById<View>(R.id.text) as TextView
        img.setImageResource(value.icon)
        tvValue.text = value.text
        return view
    }

    override fun toggle(active: Boolean) {
        if (toggle)
            img.setImageResource(
                if (active)
                    R.drawable.ic_folder_open
                else
                    R.drawable.ic_folder
            )
    }

    class IconTreeItem(var text: String, var uri: String) {
        var icon: Int = 0

        init {
            if (uri == "")
                this.icon = R.drawable.ic_folder
            else
                this.icon = R.drawable.ic_link
        }
    }

    private fun getDimens(resId: Int): Int {
        return (context.resources.getDimension(resId) / context.resources.displayMetrics.density).toInt()
    }

    companion object {
        val DEFAULT = 0
    }
}
