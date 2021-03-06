package org.sourcei.xpns.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_category.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.sourcei.xpns.R
import org.sourcei.xpns.utils.handlers.ColorHandler
import org.sourcei.xpns.utils.handlers.ImageHandler
import org.sourcei.xpns.utils.interfaces.Callback
import org.sourcei.xpns.database.room.models.CategoryModel
import org.sourcei.xpns.database.room.objects.CategoryObject
import org.sourcei.xpns.database.realtime.objects.IconPojo
import org.sourcei.xpns.ui.sheets.ModalSheetCatName
import org.sourcei.xpns.ui.sheets.ModalSheetType
import org.sourcei.xpns.utils.*
import org.sourcei.xpns.utils.extensions.*
import org.sourcei.xpns.utils.others.Colors
import java.util.*

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2018-09-04 by Saksham
 * @note Updates :
 */
class AddCategoryActivity : AppCompatActivity(), View.OnClickListener, Callback {
    private lateinit var nameSheet: ModalSheetCatName
    private lateinit var typeSheet: ModalSheetType
    private lateinit var model: CategoryModel
    private var color = 0
    private var type: String? = null
    private var icon: IconPojo? = null
    private val SELECT_PARENT = 1
    private var parent: CategoryObject? = null

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        nameSheet = ModalSheetCatName()
        typeSheet = ModalSheetType()
        model = CategoryModel(lifecycle, this)
        addCImage.setOnClickListener(this)
        addCName.setOnClickListener(this)
        addCType.setOnClickListener(this)
        addCParentL.setOnClickListener(this)
        addCClose.setOnClickListener(this)
    }

    // on click
    override fun onClick(v: View) {
        when (v.id) {
            addCImage.id ->
                startActivityForResult(Intent(this, IconsActivity::class.java), C.ICON_REQUEST_CODE)
            addCName.id -> {
                if (addCName.text.toString() != "NAME")
                    nameSheet.arguments = bundleOf(Pair(C.NAME, addCName.text.toString()))
                nameSheet.show(supportFragmentManager, nameSheet.tag)
            }
            addCDone.id -> {
                if (icon != null) {
                    if (addCName.text.toString() != "NAME") {
                        if (type != null) {
                            if (parent != null) { // a child category , also update parent one
                                val uuid = UUID.randomUUID().toString()
                                if (parent!!.cchildren != null)
                                    parent!!.cchildren!!.add(uuid)
                                else
                                    parent!!.cchildren = org.sourcei.xpns.utils.extensions.arrayListOf(uuid)
                                model.editItem(parent!!)
                                model.insert(
                                    addCName.text.toString().trim(),
                                    icon!!,
                                    type!!,
                                    color.toColor(),
                                    parent!!.cid,
                                    false,
                                    true,
                                    uuid
                                )
                            } else { // a parent category
                                model.insert(
                                    addCName.text.toString().trim(),
                                    icon!!,
                                    type!!,
                                    color.toColor()
                                )
                            }

                            toast("category inserted")
                            EventBus.getDefault().post(
                                Event(
                                    jsonOf(
                                        Pair(C.TYPE, C.NEW_CATEGORY),
                                        Pair(C.CATEGORY_TYPE, type!!)
                                    )
                                )
                            )
                            finish()
                        } else
                            toast("kindly select category type")
                    } else
                        toast("kindly provide a name")
                } else
                    toast("kindly select an icon")
            }
            addCType.id -> {
                typeSheet.arguments = bundleOf(Pair(C.NAME, addCName.text.toString()))
                typeSheet.show(supportFragmentManager, typeSheet.tag)
            }
            addCParentL.id -> {
                openActivityForResult(CategoryActivity::class.java, SELECT_PARENT) {
                    putBoolean(C.SELECT, true)
                    putBoolean(C.SHOW_CHILD, false)
                    putBoolean(C.FAB, false)
                }
            }
            addCClose.id -> finish()
        }
    }

    // activity result from icons activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == C.ICON_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                icon = Gson().fromJson(data!!.getStringExtra(C.ICON), IconPojo::class.java)
                ImageHandler.getImageAsBitmap(lifecycle, this, icon!!.iurls!!.url64) {
                    addCImage.setImageBitmap(it)
                    color = ColorHandler.getNonDarkColor(
                        androidx.palette.graphics.Palette.from(it).generate(),
                        this
                    )
                    setColor()
                }
                enableDone()
            }
        }
        if (requestCode == SELECT_PARENT) {
            if (resultCode == Activity.RESULT_OK) {
                parent =
                    Gson().fromJson(data!!.getStringExtra(C.CATEGORY), CategoryObject::class.java)
                addCParentT.text = parent!!.cname
                ImageHandler.inView(lifecycle, addCParentI, parent!!.cicon.iurls!!.url64)
            }
        }
    }

    // custom callback for cname sheet
    override fun call(any: Any) {
        any as JSONObject
        when (any.get(C.TYPE)) {
            C.NAME -> {
                if (any.getString(C.NAME).isEmpty())
                    addCName.text = "NAME"
                else
                    addCName.text = any.getString(C.NAME)
            }
            C.TYPE -> {
                type = any.getString(C.NAME)
                addCType.text = type!!.toUpperCase()
                if (type == C.EXPENSE)
                    addCType.setTextColor(Colors(this).EXPENSE)
                else
                    addCType.setTextColor(Colors(this).SAVING)
            }
        }
        enableDone()
    }

    // set ccolor
    private fun setColor() {
        //val circle = addCCircle.background.current as GradientDrawable
        //var done = addCDone.background.current as GradientDrawable

        //circle.setColor(color)
        //done.setCcolor(ccolor)
        addCCView.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        addCName.setTextColor(color)
        setStatusBarColor(color)
    }

    // enable done button
    private fun enableDone() {
        fun disable() {
            addCDone.alpha = 0.3.toFloat()
            addCDone.setOnClickListener(null)
        }

        if (icon != null)
            if (addCName.text.toString() != "NAME")
                if (type != null) {
                    addCDone.alpha = 1.toFloat()
                    addCDone.setOnClickListener(this)
                } else
                    disable()
            else
                disable()
        else
            disable()
    }
}
