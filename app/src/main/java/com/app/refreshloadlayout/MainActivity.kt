package com.app.refreshloadlayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qbw.refreshloadlayout.RefreshLoadLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),RefreshLoadLayout.OnLoadFailedListener,RefreshLoadLayout.OnLoadListener {


    lateinit var rll:RefreshLoadLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        rll = findViewById<RefreshLoadLayout>(R.id.rll)
        val rcv = findViewById<RecyclerView>(R.id.rcv)
        rcv.layoutManager = LinearLayoutManager(this)
        rcv.adapter = TestAdapter(this)
        rll.setOnLoadListener(this)
        rll.setOnLoadFailedListener(this)
        rll.setStatusFailed(true)
    }

    override fun onLoad() {

    }

    override fun onRetryLoad() {
        onLoad()
    }


}