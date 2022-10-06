package com.qbw.refreshloadlayout

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.qbw.refreshloadlayout.RefreshLoadLayout.Load
import com.qbw.refreshloadlayout.RefreshLoadLayout.Load.Companion.getLoadText
import com.qbw.refreshloadlayout.RefreshLoadLayout.Load.Companion.isLoadFailed
import com.qbw.refreshloadlayout.RefreshLoadLayout.Load.Companion.setLoadLoading
import com.qbw.refreshloadlayout.RefreshLoadLayout.OnLoadFailedListener
import com.qinbaowei.refreshloadlayout.R
import com.qinbaowei.refreshloadlayout.databinding.LoadHolderBinding


class LoadHolder(
    private val context: Context?, val binding: LoadHolderBinding,
    private val onLoadFailedListener: OnLoadFailedListener?
) : RecyclerView.ViewHolder(binding.root) {
    private var data: Load? = null

    constructor(
        context: Context?,
        viewParent: ViewGroup?,
        onLoadFailedListener: OnLoadFailedListener?
    ) : this(
        context, DataBindingUtil.inflate<LoadHolderBinding>(
            LayoutInflater.from(context), R.layout.load_holder, viewParent, false
        ), onLoadFailedListener
    ) {
    }

    fun onBindData(position: Int, data: Load) {
        this.data = data
        binding!!.txtLoad.text = getLoadText(context!!, data.getStatus())
        binding.load = data
        binding.executePendingBindings()
        itemView.setOnClickListener {
            if (isLoadFailed(data.getStatus())) {
                if (onLoadFailedListener != null) {
                    setLoadLoading(data)
                    onLoadFailedListener.onRetryLoad()
                }
            }
        }
    }
}