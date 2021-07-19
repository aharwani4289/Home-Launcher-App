package com.ajayh.homelauncherapp.app.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajayh.homelauncherapp.R
import com.ajayh.homelauncherapp.sdk.app.model.ContentItem.Application

class AppAdapter(private val data: List<Application>) :
    RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.content_card, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        if (data.size > position) {
                Log.d("Ajay", "" + data[position])
            (holder.view.findViewById(R.id.iv_app_name) as TextView).text = data[position].appName
            (holder.view.findViewById(R.id.iv_logo) as ImageView).setImageDrawable(data[position].appBanner)
        }
    }

    override fun getItemCount() = data.size

    class AppViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

