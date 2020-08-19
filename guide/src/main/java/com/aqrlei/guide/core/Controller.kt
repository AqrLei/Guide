package com.aqrlei.guide.core

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.aqrlei.guide.lifecycle.LifecycleListenerFragment
import com.aqrlei.guide.lifecycle.SimpleLifecycleListener

/**
 * Created by AqrLei on 2019-08-20
 */
class Controller(private val builder: Builder) {
    companion object {
        private const val LIFECYCLE_LISTENER_FRAGMENT = "lifecycle_listener_fragment"
    }

    private var mParentView: FrameLayout? = null

    private var guideLayout: GuideLayout? = null

    init {
        mParentView = builder.activity.findViewById(android.R.id.content)
    }

    fun isGuidePageShow() = builder.guidePage?.isDisplayOnWindow ?: false
    fun show() {
        builder.guidePage?.isDisplayOnWindow = true
        val fragment = builder.fragment
        if (fragment != null && !fragment.userVisibleHint) {
            return
        }
        mParentView?.post {
            builder.guidePage?.let {
                showGuidePage(it)
                builder.onGuideChangedListener?.onShowed(this)
            }
            addLifecycleListenerFragment()
        }
    }

    fun dismiss() {
        builder.guidePage?.isDisplayOnWindow = false
        remove()
    }

    private fun remove() {
        guideLayout?.remove()
    }

    private fun showGuidePage(guidePage: GuidePage) {
        guideLayout = GuideLayout(
            builder.activity,
            guidePage,
            this
        ).apply {
            setOnGuideLayoutDismissListener {
                builder.onGuideChangedListener?.onRemoved(this@Controller)
            }
        }
        mParentView?.addView(
            guideLayout,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ))
    }

    private fun addLifecycleListenerFragment() {
        (builder.activity as? AppCompatActivity)?.run {
            addLifecycleListenerFragment(this.supportFragmentManager)
        } ?: builder.fragment?.run {
            if (isAdded) {
                addLifecycleListenerFragment(childFragmentManager)
            }
        }
    }

    private fun addLifecycleListenerFragment(fm: FragmentManager) {
        var fragment =
            fm.findFragmentByTag(LIFECYCLE_LISTENER_FRAGMENT) as? LifecycleListenerFragment
        if (fragment == null) {
            fragment =
                LifecycleListenerFragment()
            fm.beginTransaction().add(
                fragment,
                LIFECYCLE_LISTENER_FRAGMENT
            ).commitAllowingStateLoss()
        }
        fragment.fragmentLifecycle?.addLifecycleListener(object :
            SimpleLifecycleListener() {
            override fun onDestroy() {
                this@Controller.remove()
            }
        })
    }

    interface OnGuideChangedListener {
        fun onShowed(controller: Controller)
        fun onRemoved(controller: Controller)
    }
}