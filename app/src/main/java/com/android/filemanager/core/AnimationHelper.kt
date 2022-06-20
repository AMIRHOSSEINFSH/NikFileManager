package com.android.filemanager.core

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup

fun View.setHeightResizeAnimator(
    duration: Long = 1000,
    startHeight: Int,
    endHeight: Int,
    onStart: (() -> Unit)? = null,
    onEnd: (() -> Unit)? = null
): ValueAnimator {

    val anim = ValueAnimator.ofInt(startHeight, endHeight)
    anim.addUpdateListener { valueAnimator ->
        val va = valueAnimator.animatedValue as Int
        val layoutParams: ViewGroup.LayoutParams = this.layoutParams
        layoutParams.height = va
        this.layoutParams = layoutParams
    }
    anim.duration = duration
    anim.addListener(object : AnimatorListenerAdapter() {

        override fun onAnimationStart(animation: Animator?) {
            super.onAnimationStart(animation)
            onStart?.invoke()
        }

        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            onEnd?.invoke()
        }
    })
    anim.start()

    return anim
}