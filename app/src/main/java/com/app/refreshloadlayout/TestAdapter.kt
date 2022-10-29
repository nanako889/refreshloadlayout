package com.app.refreshloadlayout

import android.content.Context
import com.qbw.refreshloadlayout.RefreshLoadLayout

/**
 * barry 2022/10/29
 */
class TestAdapter(context: Context):RefreshLoadLayout.Adapter(context) {
    override fun onGetLoadViewType(): Int {
        return 1
    }
}