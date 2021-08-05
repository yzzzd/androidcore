package com.crocodic.core.helper

import android.view.View

interface ClickPrevention : View.OnClickListener {
    override fun onClick(v: View?) {
        preventTwoClick(v)
    }

    fun preventTwoClick(view: View?){
        view?.isEnabled = false
        view?.postDelayed({ view.isEnabled = true }, 1500)
    }

}