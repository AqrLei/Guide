package com.aqrlei.guide.lifecycle

import androidx.fragment.app.Fragment
import com.aqrlei.guide.lifecycle.ActivityFragmentLifecycle

/**
 * Created by AqrLei on 2019-08-23
 */
class LifecycleListenerFragment : Fragment {
    constructor() : this(ActivityFragmentLifecycle())

    var fragmentLifecycle: ActivityFragmentLifecycle? = null

    private constructor(fragmentLifecycle: ActivityFragmentLifecycle) {
        this.fragmentLifecycle = fragmentLifecycle
    }

    override fun onStart() {
        super.onStart()
        fragmentLifecycle?.onStart()
    }

    override fun onStop() {
        super.onStop()
        fragmentLifecycle?.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentLifecycle?.onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentLifecycle?.onDestroy()
    }
}