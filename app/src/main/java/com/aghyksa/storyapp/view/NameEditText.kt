package com.aghyksa.storyapp.view

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class NameEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
    private var textInputLayout: TextInputLayout? = null

    fun setTextInputLayout(textInputLayout: TextInputLayout) {
        this.textInputLayout = textInputLayout
        this.textInputLayout?.error ="Name must be filled"
    }

    private fun init(){
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textInputLayout?.error = if (s.toString().isEmpty()){
                    "Name must be filled"
                } else {
                    null
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}