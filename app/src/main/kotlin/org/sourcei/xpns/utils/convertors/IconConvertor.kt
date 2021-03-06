package org.sourcei.xpns.utils.convertors

import androidx.room.TypeConverter
import com.google.gson.Gson
import org.sourcei.xpns.database.realtime.objects.IconPojo


/**
 * @info -
 *
 * @author - Saksham
 * @tnote Last Branch Update - master
 *
 * @tnote Created on 2018-08-18 by Saksham
 * @tnote Updates :
 */
class IconConvertor {
    @TypeConverter
    fun fromString(value: String?): IconPojo? {
        return if (value == null) null else Gson().fromJson(value, IconPojo::class.java)
    }

    @TypeConverter
    fun stringToIcon(icon: IconPojo?): String? {
        return Gson().toJson(icon!!)
    }
}