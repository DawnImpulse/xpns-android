package org.sourcei.xpns.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import org.sourcei.xpns.database.room.models.CategoryModel
import org.sourcei.xpns.ui.viewholders.ChildCategoryViewHolder

/**
 * @info -
 *
 * @author - Saksham
 * @tnote Last Branch Update - master
 *
 * @tnote Created on 2019-01-18 by Saksham
 * @tnote Updates :
 */
class ChildCategoryAdapter(private val lifecycle: Lifecycle,
                           private val ids: List<String>,
                           private val select: Boolean) : RecyclerView.Adapter<ChildCategoryViewHolder>() {
    private val NAME = "ChildCategoryAdapter"
    private lateinit var model: CategoryModel
    private lateinit var context: Context
    private lateinit var activity: Activity

    // no of items
    override fun getItemCount(): Int {
        return ids.size
    }

    // on create view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildCategoryViewHolder {
        model = CategoryModel(lifecycle, parent.context)
        context = parent.context
        activity = context as Activity
        return ChildCategoryViewHolder(parent, lifecycle, select)
    }

    // bind view
    override fun onBindViewHolder(holder: ChildCategoryViewHolder, position: Int) {
        model.getItem(ids[position]) {
            activity.runOnUiThread {
                holder.bindTo(it)
            }

        }
    }

}