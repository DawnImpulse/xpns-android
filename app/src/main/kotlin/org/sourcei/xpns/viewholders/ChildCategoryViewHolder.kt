package org.sourcei.xpns.viewholders

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.inflator_child_category.view.*
import org.sourcei.xpns.R
import org.sourcei.xpns.handlers.ImageHandler
import org.sourcei.xpns.pojo.CategoryPojo
import org.sourcei.xpns.utils.C

/**
 * @info -
 *
 * @author - Saksham
 * @tnote Last Branch Update - master
 *
 * @tnote Created on 2018-09-04 by Saksham
 * @tnote Updates :
 */
class ChildCategoryViewHolder(parent: ViewGroup,
                              private val lifecycle: Lifecycle,
                              private val select: Boolean) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.inflator_child_category, parent, false)) {

    private val image = itemView.childCategoryImage
    private val name = itemView.childCategoryName
    private val color = itemView.childCategoryColor
    private val layout = itemView.childCategoryL
    private val context = parent.context

    // binding data to layout
    fun bindTo(category: CategoryPojo?) {
        category?.let {
            val colorL = color.background.current as GradientDrawable

            ImageHandler.setImageInView(lifecycle, image, category.cicon.iurls!!.url64)
            colorL.setColor(Color.parseColor(category.ccolor))
            name.text = category.cname
            layout.setOnClickListener {
                if (select) {
                    val intent = Intent()
                    intent.putExtra(C.CATEGORY, Gson().toJson(category))
                    (context as AppCompatActivity).setResult(Activity.RESULT_OK, intent)
                    context.finish()
                }
            }
        }
    }
}