/*
 * Copyright (c) 2020.
 */

package kudos26.bounty.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kudos26.bounty.R
import kudos26.bounty.databinding.ItemLoadingBinding

/**
 * Created by kunal on 25-01-2020.
 */

class LoadingAdapter : RecyclerView.Adapter<LoadingAdapter.LoadingItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingItemHolder {
        return LoadingItemHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_loading,
                        parent,
                        false
                )
        )
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(holder: LoadingItemHolder, position: Int) {
        holder.itemLoadingBinding.apply {
            executePendingBindings()
        }
    }

    inner class LoadingItemHolder(val itemLoadingBinding: ItemLoadingBinding) :
            RecyclerView.ViewHolder(itemLoadingBinding.root)

}