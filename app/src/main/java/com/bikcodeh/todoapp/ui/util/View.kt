package com.bikcodeh.todoapp.ui.util

import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.IdRes
import com.google.android.material.snackbar.Snackbar

fun View.snack(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setAnchorView(this).show()
}

fun View.initAnimation(@IdRes animationRes: Int) {
    this.startAnimation(AnimationUtils.loadAnimation(context, animationRes))
}