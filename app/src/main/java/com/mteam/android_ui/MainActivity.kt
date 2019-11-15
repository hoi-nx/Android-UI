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

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val checkBox = CheckBox(this, null, R.drawable.ic_baseline_check_24)
        checkBox.setColor(resources.getColor(R.color.color_check),resources.getColor(R.color.color_non_check))
        checkBox.setSize(24)
        checkBox.setCheckOffset(AndroidUtilities.dp(1f))
        checkBox.visibility = View.VISIBLE
        val layoutP = LinearLayout.LayoutParams(AndroidUtilities.dp(24f),AndroidUtilities.dp(24f))
        parentLayout.addView(checkBox,layoutP)
        val avatarDrawable = AvatarDrawable(this,resources.getColor(R.color.color_check))
        avatarDrawable.setInfo("Hoi","Nguyen Xuan")
        imageView.setImageDrawable(avatarDrawable)
        textView.setOnClickListener {
            checkBox.setChecked(checked =!checkBox.isChecked() , animated = true)
        }

        edtFirstName.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                avatarDrawable.setInfo(
                    edtFirstName.text.toString(),
                    ""
                )
                imageView.invalidate()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


    }
}
