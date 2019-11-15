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

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log

/**
 * Created by HoiNx on 15,November,2019
 */
class AvatarDrawable(val context: Context, val color: Int) : Drawable() {
    private val stringBuilder by lazy {
        StringBuilder(5)
    }
    private val namePaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG);

    }
    private val avatarBackgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private var textLayout: StaticLayout? = null
    private var textWidth = 0f
    private var textHeight = 0f
    private var textLeft = 0f

    init {
        namePaint.textSize = AndroidUtilities.dp(18f).toFloat();
    }

    fun setInfo(firstName: String, lastName: String) {
        var firstName = firstName
        var lastName = lastName
        if (firstName.isEmpty()) {
            firstName = lastName
            lastName = ""
        }
        stringBuilder.setLength(0)

        if (firstName.isNotEmpty()) {
            stringBuilder.appendCodePoint(firstName.codePointAt(0))
        }
        if (lastName.isNotEmpty()) {
            var lastch = 0
            for (a in lastName.length - 1 downTo 0) {
                if (lastch != null && lastName[a] == ' ') {
                    break
                }
                lastch = lastName.codePointAt(a)
            }
            if (Build.VERSION.SDK_INT >= 17) {
                stringBuilder.append("\u200C")
            }
            stringBuilder.appendCodePoint(lastch)
        } else if (firstName.isNotEmpty()) {
            for (a in firstName.length - 1 downTo 0) {
                if (firstName[a] == ' ') {
                    if (a != firstName.length - 1 && firstName[a + 1] != ' ') {
                        if (Build.VERSION.SDK_INT >= 17) {
                            stringBuilder.append("\u200C")
                        }
                        stringBuilder.appendCodePoint(firstName.codePointAt(a + 1))
                        break
                    }
                }
            }
        }

        if (stringBuilder.isNotEmpty()) {
            val text = stringBuilder.toString().toUpperCase()
            Log.d("Hoi_Nguyen", text)
            try {
                textLayout = StaticLayout(
                    text,
                    namePaint,
                    AndroidUtilities.dp(100f),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
                )
                textLayout?.let {
                    if (it.lineCount > 0) {
                        textLeft = it.getLineLeft(0)
                        textWidth = it.getLineWidth(0)
                        textHeight = it.getLineBottom(0).toFloat()
                    }
                }

            } catch (e: Exception) {
            }
        } else {
            textLayout = null
        }
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val size = bounds.width()
        namePaint.color = context.resources.getColor(R.color.color_non_check)
        avatarBackgroundPaint.color = color
        canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
        canvas.drawCircle(size / 2.0f, size / 2.0f, size / 2.0f, avatarBackgroundPaint)
        textLayout?.let {
            canvas.translate((size - textWidth) / 2 - textLeft, (size - textHeight) / 2)
            it.draw(canvas)
        }
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun getOpacity() = PixelFormat.TRANSPARENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }


}