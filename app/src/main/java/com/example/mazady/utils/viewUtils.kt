package com.example.mazady.utils

import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.showAndEnable() {
    show()
    isEnabled = true
}