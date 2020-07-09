/*
 * Copyright (c) 2020.
 */

package kudos26.bounty.adapter

import android.view.Gravity
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by kunal on 25-01-2020.
 */

class EmptyAdapter(
        private val message: String
) : RecyclerView.Adapter<EmptyAdapter.EmptyViewHolder>() {

    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyViewHolder {
        AppCompatTextView(parent.context).apply {
            textSize = 18f
            gravity = Gravity.CENTER
            textAlignment = TEXT_ALIGNMENT_CENTER
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            )
            return EmptyViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: EmptyViewHolder, position: Int) {
        holder.textView.text = message
    }

    inner class EmptyViewHolder(val textView: AppCompatTextView) : RecyclerView.ViewHolder(textView)

}