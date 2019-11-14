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

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import java.util.*
import kotlin.math.*

/**
 * Created by HoiNx on 14,November,2019
 */
object AndroidUtilities {
    private var density = 1f
    private var displaySize = Point()
    private var roundMessageSize = 0
    private var displayMetrics = DisplayMetrics()
    private var leftBaseline = 0
    private var usingHardwareInput = false

    private var isTablet: Boolean = MyApplication.applicationContext.resources.getBoolean(R.bool.isTablet)

    fun checkFullScreen(activity: Activity): Boolean {
        try {
            val mForcedWindowFlags = Window::class.java.getDeclaredField("mForcedWindowFlags")
            mForcedWindowFlags.isAccessible = true
            val value = mForcedWindowFlags[activity.window] as Int
            if (value or 1024 == value) return true
        } catch (e: NoSuchFieldException) {
            e.printStackTrace();
        } catch (e: IllegalAccessException) {
            e.printStackTrace();
        }
        val flag = activity.window.attributes.flags
        return flag or 1024 == flag
    }


    fun dp(value: Float): Int {
        return if (value == 0f) {
            0
        } else ceil(density * value.toDouble()).toInt()
    }

    fun dp2(value: Float): Int {
        return if (value == 0f) {
            0
        } else floor(density * value.toDouble()).toInt()
    }

    fun dpf2(value: Float): Float {
        return if (value == 0f) {
            0f
        } else density * value
    }

    fun checkDisplaySize(context: Context, newConfiguration: Configuration?) {
        try {
            density = context.resources.displayMetrics.density
            var configuration = newConfiguration
            if (configuration == null) {
                configuration = context.resources.configuration
            }
            usingHardwareInput = configuration!!.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = manager.defaultDisplay
            if (display != null) {
                display.getMetrics(displayMetrics)
                display.getSize(displaySize)
            }

            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                val newSize = ceil(configuration.screenWidthDp * density.toDouble()).toInt()
                if (abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                val newSize = ceil(configuration.screenHeightDp * density.toDouble()).toInt()
                if (abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize
                }
            }
            if (roundMessageSize == 0) {
                roundMessageSize = if (isTablet) {
                    (minTabletSide * 0.6f).toInt()
                } else {
                    (min(displaySize.x, displaySize.y) * 0.6f).toInt()
                }
            }
        } catch (e: Exception) {
        }
    }

    fun getPixelsInCM(cm: Float, isX: Boolean): Float {
        return cm / 2.54f * if (isX) displayMetrics.xdpi else displayMetrics.ydpi
    }

    private val isSmallTablet: Boolean
        get() {
            val minSide = min(
                    displaySize.x,
                    displaySize.y
            ) / density
            return minSide <= 700
        }

    private val minTabletSide: Int
        get() = if (!isSmallTablet) {
            val smallSide = min(displaySize.x, displaySize.y)
            var leftSide = smallSide * 35 / 100
            if (leftSide < dp(320f)) {
                leftSide = dp(320f)
            }
            smallSide - leftSide
        } else {
            val smallSide = min(displaySize.x, displaySize.y)
            val maxSide = max(displaySize.x, displaySize.y)
            var leftSide = maxSide * 35 / 100
            if (leftSide < dp(320f)) {
                leftSide = dp(320f)
            }
            min(smallSide, maxSide - leftSide)
        }

    init {
        leftBaseline = if (isTablet) 87 else 79
        checkDisplaySize(MyApplication.applicationContext, null)
    }
}