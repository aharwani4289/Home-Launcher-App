package com.ajayh.homelauncherapp.app.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajayh.homelauncherapp.R
import com.ajayh.homelauncherapp.app.gone
import com.ajayh.homelauncherapp.app.recyclerview.AppAdapter
import com.ajayh.homelauncherapp.app.viewmodel.AppViewModel
import com.ajayh.homelauncherapp.app.visible
import com.ajayh.homelauncherapp.databinding.ActivityMainBinding
import com.ajayh.homelauncherapp.sdk.app.model.ContentItem.Application

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val appViewModel by lazy { getViewModel(AppViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserver()
        initSearch()
    }

    private fun initSearch() {
        //This will help to give padding end to et_search based on tv_clear size
//        binding.tvClear.measure(0, 0)
//        binding.etSearch.setPadding(
//            0,
//            0,
//            (binding.tvClear.measuredWidth + resources.getDimension(R.dimen.dimen_size_10dp).toInt()),
//            0
//        )
        binding.tvClear.setOnClickListener {
            binding.etSearch.text?.clear()
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    if (editable.toString().trim().isNotEmpty()) binding.tvClear.visible()
                    else binding.tvClear.gone()
                    appViewModel.getApps(editable.toString().trim())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun initObserver() {
        appViewModel.appList.observe(this, ::loadApps)
        binding.progressBarLayout.progressBar.visible()
        appViewModel.getApps(null)
    }

    private fun loadApps(apps: List<Application>) {
        binding.progressBarLayout.progressBar.gone()
        viewManager = GridLayoutManager(this, resources.getInteger(R.integer.rail_cards_span_count))
        viewAdapter = AppAdapter(apps) { app ->
            val context: Context = this@MainActivity
            try {
                val i: Intent? =
                    context.getPackageManager().getLaunchIntentForPackage(app.packageName)
                context.startActivity(i)
            } catch (e: PackageManager.NameNotFoundException) {

            }
        }
        binding.appsList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}