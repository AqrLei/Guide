package com.aqrlei.guide.core

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * Created by AqrLei on 2019-08-20
 */
class Builder {
    internal val activity: Activity
    internal var fragment: Fragment? = null
    internal var guidePage: GuidePage? = null
    internal var onGuideChangedListener: Controller.OnGuideChangedListener? = null

    constructor(activity: Activity) {
        this.activity = activity
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
        this.activity = fragment.activity
            ?: throw IllegalStateException("Fragment $fragment not attached to an activity.")
    }

    fun onGuideChanged(onShowed: (Controller) -> Unit, onRemoved: (Controller) -> Unit) {
        onGuideChangedListener = object :
            Controller.OnGuideChangedListener {
            override fun onShowed(controller: Controller) {
                onShowed(controller)
            }

            override fun onRemoved(controller: Controller) {
                onRemoved(controller)
            }
        }

    }

    fun guidePage(block: GuidePage.() -> Unit) {
        guidePage = GuidePage().apply(block)
    }

    fun build(): Controller =
        Controller(this)
    fun show(): Controller {
        return Controller(this).apply {
            show()
        }
    }
}