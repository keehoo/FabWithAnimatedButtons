package keehoo.kree.com.customcompoundfab

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import keehoo.kree.com.customcompoundfab.R.id.animation_view

class CustomFab @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    init {
        LayoutInflater.from(context).inflate(R.layout.lottie_stuff, this, true)
        val lottie: LottieAnimationView = findViewById(R.id.animation_view)

        lottie.setOnClickListener {
            onMenuOperation(it as LottieAnimationView, true)
        }
    }

    fun onMenuOperation(lottieAnimationView: LottieAnimationView, menuExpanded: Boolean) {
        val animator: ValueAnimator = if (menuExpanded) {
            ValueAnimator.ofFloat(0.1F, 0.5F).setDuration(300)
        } else {
            ValueAnimator.ofFloat(0.6F, 1.0F).setDuration(300)
        }
        animator.addUpdateListener {
            lottieAnimationView.progress = it.animatedValue as Float
        }
        animator.start()
    }
}