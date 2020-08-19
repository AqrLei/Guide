package com.aqrlei.guide.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.*
import android.view.animation.Animation
import android.widget.FrameLayout
import kotlin.math.abs

/**
 * Created by AqrLei on 2019-08-20
 */
@SuppressLint("ViewConstructor")
internal class GuideLayout(
    context: Context,
    private val guidePage: GuidePage,
    private val controller: Controller
) : FrameLayout(context) {

    // 0xb2000000
    val defaultColor = -0x4e000000

    private var onGuideLayoutDismissListener: OnGuideLayoutDismissListener? = null

    private var touchSlop = 0
    private var downX: Float = 0F
    private var downY: Float = 0F
    private var mPaint: Paint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        maskFilter = BlurMaskFilter(10F, BlurMaskFilter.Blur.INNER)

    }

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        setWillNotDraw(false)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setGuidePage(guidePage)
        addCustomViewToLayout()
        guidePage.overlay?.enterAnimation?.let {
            startAnimation(it)
        }
    }

    private fun setGuidePage(guidePage: GuidePage) {
        setOnClickListener {
            if (guidePage.everywhereCancelable) {
                remove()
            }
        }
    }

    fun remove() {
        guidePage.overlay?.exitAnimation?.let {
            it.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    dismiss()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
            startAnimation(it)
        } ?: dismiss()
    }

    private fun addCustomViewToLayout() {
        removeAllViews()
        guidePage.overlay?.let {
            if (it.layoutResId != 0) {
                val view = LayoutInflater.from(context).inflate(it.layoutResId, this, false)
                guidePage.notifyOverlayWithHoleDrawListener(
                    controller, view,
                    guidePage.hole?.holeCanvas, guidePage.hole?.holeRectF,
                    highlightDrew = false,
                    overlayInflated = true)
                addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            }
        }
    }

    fun setOnGuideLayoutDismissListener(block: (GuideLayout) -> Unit) {
        onGuideLayoutDismissListener = object :
            OnGuideLayoutDismissListener {
            override fun onGuideLayoutDismiss(guideLayout: GuideLayout) {
                block(guideLayout)
            }
        }
    }


    private fun dismiss() {
        (parent as? ViewGroup)?.let {
            it.removeView(this)
            onGuideLayoutDismissListener?.onGuideLayoutDismiss(this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val upX = event.x
                val upY = event.y
                if (abs(upX - downX) < touchSlop && abs(upY - downY) < touchSlop) {
                    val rectF = guidePage.hole?.getRectF(parent as? ViewGroup)
                    if (rectF?.contains(upX, upY) == true) {
                        guidePage.notifyHoleClickListener(controller)
                        return true
                    }
                    performClick()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val backgroundColor = guidePage.overlay?.backgroundColor ?: 0
        canvas?.drawColor(if (backgroundColor == 0) defaultColor else backgroundColor)
        canvas?.run { drawHighlightHole(this) }
    }

    private fun drawHighlightHole(canvas: Canvas) {
        guidePage.hole?.let {
            it.getRectF(parent as? ViewGroup)?.let { rectF ->

                when (it.shape) {
                    Hole.Shape.RECTANGLE -> {
                        val radius = it.radius.toFloat()
                        if (radius > 0) {
                            canvas.drawRoundRect(rectF, radius, radius, mPaint)
                        } else {
                            canvas.drawRect(rectF, mPaint)
                        }
                    }
                    Hole.Shape.CIRCLE -> {
                        val radius = (rectF.width().coerceAtLeast(rectF.height()))/2
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(), radius, mPaint)
                    }
                    Hole.Shape.OVAL -> {
                        canvas.drawOval(rectF, mPaint)
                    }
                }
                guidePage.notifyOverlayWithHoleDrawListener(
                    guidePage.overlay?.guideController, guidePage.overlay?.overlayView,
                    canvas, rectF, true)
            }
        }
    }

    interface OnGuideLayoutDismissListener {
        fun onGuideLayoutDismiss(guideLayout: GuideLayout)
    }
}