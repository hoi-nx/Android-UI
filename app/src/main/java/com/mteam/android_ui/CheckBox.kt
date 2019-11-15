/*
 * Copyright (C) 2019 Hoi-Nx
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.mteam.android_ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.ceil

/**
 * Created by HoiNx on 14,November,2019
 */
class CheckBox(context: Context, attrs: AttributeSet? = null, resId: Int) :
    View(context, attrs, resId) {
    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private val eraser by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private val eraser2 by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private val backgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private val textPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG)
    }
    private val checkDrawable: Drawable
    private val drawBitmap by lazy {
        Bitmap.createBitmap(
            AndroidUtilities.dp(size),
            AndroidUtilities.dp(size),
            Bitmap.Config.ARGB_4444
        )
    }
    private var size = 22f
    private val checkBitmap by lazy {
        Bitmap.createBitmap(
            AndroidUtilities.dp(size),
            AndroidUtilities.dp(size),
            Bitmap.Config.ARGB_4444
        )
    }
    private var bitmapCanvas: Canvas? = null
    private var checkCanvas: Canvas? = null
    private var progress = 0f

    private var drawBackground = false
    private var hasBorder = false

    private var checkAnimator: ObjectAnimator? = null
    private var isCheckAnimation = true

    private var attachedToWindow = false
    private var isChecked = false

    private var checkOffset = 0
    private var color = 0
    private var checkedText: String? = null
    private val progressBounceDiff = 0.2f

    init {
        eraser.color = 0
        eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        eraser2.color = 0
        eraser2.style = Paint.Style.STROKE
        eraser2.strokeWidth = AndroidUtilities.dp(28f).toFloat()
        eraser2.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        backgroundPaint.color = context.resources.getColor(R.color.color_non_check)
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = AndroidUtilities.dp(2f).toFloat()

        textPaint.textSize = AndroidUtilities.dp(18f).toFloat()

        checkDrawable = context.resources.getDrawable(resId).mutate()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            bitmapCanvas = Canvas(drawBitmap)
            checkCanvas = Canvas(checkBitmap)
        }
    }

    fun setProgress(value: Float) {
        if (progress == value) {
            return
        }
        progress = value
        invalidate()
    }

    fun setDrawBackground(value: Boolean) {
        drawBackground = value
    }

    fun setHasBorder(value: Boolean) {
        hasBorder = value
    }

    fun setCheckOffset(value: Int) {
        checkOffset = value
    }

    fun setSize(size: Int) {
        this.size = size.toFloat()
    }

    fun getProgress(): Float {
        return progress
    }

    fun setColor(backgroundColor: Int, checkColor: Int) {
        color = backgroundColor
        checkDrawable.colorFilter = PorterDuffColorFilter(checkColor, PorterDuff.Mode.MULTIPLY)
        textPaint.color = checkColor
        invalidate()
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        color = backgroundColor
        invalidate()
    }

    fun setCheckColor(checkColor: Int) {
        //checkDrawable.colorFilter = PorterDuffColorFilter(checkColor, PorterDuff.Mode.MULTIPLY)
        textPaint.color = checkColor
        invalidate()
    }

    private fun cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator!!.cancel()
            checkAnimator = null
        }
    }

    private fun animateToCheckedState(newCheckedState: Boolean) {
        isCheckAnimation = newCheckedState
        checkAnimator = ObjectAnimator.ofFloat(this, "progress", if (newCheckedState) 1f else 0f)
        checkAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (animation == checkAnimator) {
                    checkAnimator = null
                }
                if (!isChecked) {
                    checkedText = null
                }
            }
        })
        checkAnimator!!.duration = 300
        checkAnimator!!.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    fun setChecked(checked: Boolean, animated: Boolean) {
        setChecked(-1, checked, animated)
    }

    fun setNum(num: Int) {
        if (num >= 0) {
            checkedText = "" + (num + 1)
        } else if (checkAnimator == null) {
            checkedText = null
        }
        invalidate()
    }

    private fun setChecked(num: Int, checked: Boolean, animated: Boolean) {
        if (num >= 0) {
            checkedText = "" + (num + 1)
        }
        if (checked == isChecked) {
            return
        }
        isChecked = checked
        if (attachedToWindow && animated) {
            animateToCheckedState(checked)
        } else {
            cancelCheckAnimator()
            setProgress(if (checked) 1.0f else 0.0f)
        }
    }

    fun isChecked(): Boolean {
        return isChecked
    }

    override fun onDraw(canvas: Canvas) {
        if (visibility != VISIBLE) {
            return
        }
        if (drawBackground || progress != 0f) {
            eraser2.strokeWidth = AndroidUtilities.dp(size + 6).toFloat()
            drawBitmap!!.eraseColor(0)
            var rad = measuredWidth / 2.toFloat()
            val roundProgress = if (progress >= 0.5f) 1.0f else progress / 0.5f
            val checkProgress = if (progress < 0.5f) 0.0f else (progress - 0.5f) / 0.5f
            val roundProgressCheckState =
                if (isCheckAnimation) progress else 1.0f - progress
            if (roundProgressCheckState < progressBounceDiff) {
                rad -= AndroidUtilities.dp(2f) * roundProgressCheckState / progressBounceDiff
            } else if (roundProgressCheckState < progressBounceDiff * 2) {
                rad -= AndroidUtilities.dp(2f) - AndroidUtilities.dp(2f) * (roundProgressCheckState - progressBounceDiff) / progressBounceDiff
            }
            if (drawBackground) {
                paint.color = 0x44000000
                canvas.drawCircle(
                    measuredWidth / 2.toFloat(),
                    measuredHeight / 2.toFloat(),
                    rad - AndroidUtilities.dp(1f),
                    paint
                )
                canvas.drawCircle(
                    measuredWidth / 2.toFloat(),
                    measuredHeight / 2.toFloat(),
                    rad - AndroidUtilities.dp(1f),
                    backgroundPaint
                )
            }
            paint.color = color
            if (hasBorder) {
                rad -= AndroidUtilities.dp(2f)
            }
            bitmapCanvas?.let {
                it.drawCircle(measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat(), rad, paint)
                it.drawCircle(measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat(), rad * (1 - roundProgress), eraser)
            }

            canvas.drawBitmap(drawBitmap!!, 0f, 0f, null)
            checkBitmap!!.eraseColor(0)
            if (checkedText != null) {
                val w =
                    ceil(textPaint.measureText(checkedText).toDouble()).toInt()
                checkCanvas!!.drawText(
                    checkedText!!,
                    (measuredWidth - w) / 2.toFloat(),
                    AndroidUtilities.dp(21f).toFloat(),
                    textPaint
                )
            } else {
                val w = checkDrawable.intrinsicWidth
                val h = checkDrawable.intrinsicHeight
                val x = (measuredWidth - w) / 2
                val y = (measuredHeight - h) / 2
                checkDrawable.setBounds(x, y + checkOffset, x + w, y + h + checkOffset)
                checkCanvas?.let {
                    checkDrawable.draw(it)
                }

            }
            checkCanvas!!.drawCircle((measuredWidth / 2 - AndroidUtilities.dp(2.5f)).toFloat(), (measuredHeight / 2 + AndroidUtilities.dp(4f)).toFloat(), (measuredWidth + AndroidUtilities.dp(6f)) / 2 * (1 - checkProgress), eraser2)
            canvas.drawBitmap(checkBitmap!!, 0f, 0f, null)
        }
    }
}