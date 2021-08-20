package com.crocodic.core.helper.util

import android.view.View

/**
 * Created by @yzzzd on 4/22/18.
 */

interface ClickPrevention : View.OnClickListener {
    override fun onClick(v: View?) {
        preventTwoClick(v)
    }

    fun preventTwoClick(view: View?){
        view?.isEnabled = false
        view?.postDelayed({ view.isEnabled = true }, 1500)
    }

}