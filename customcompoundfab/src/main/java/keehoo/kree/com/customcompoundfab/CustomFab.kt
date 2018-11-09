package keehoo.kree.com.customcompoundfab

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView


class CustomFab @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : ConstraintLayout(context, attrs, defStyle) {

    var buttons: MutableList<Button> = mutableListOf()

    var isCollapsed: Boolean = true
        private set(value) {
            field = value
            animateFab()
            animateButtons()

        }

    private fun animateButtons() {
        buttons.forEach {
            onAnimation(it, true)
        }
    }

    private val lottieAnimationView: LottieAnimationView
        get() {
            return findViewById(R.id.animation_view)
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.lottie_stuff, this, true)
        val lottie: LottieAnimationView = lottieAnimationView

        lottie.setOnClickListener {
            animateFab()
            fabFlipped()
        }
    }

    private fun fabFlipped() {
        isCollapsed = !isCollapsed
    }

    private fun animateFab() {
        val animator: ValueAnimator = if (isCollapsed) {
            ValueAnimator.ofFloat(0.1F, 0.5F).setDuration(300)
        } else {
            ValueAnimator.ofFloat(0.6F, 1.0F).setDuration(300)
        }
        animator.addUpdateListener {
            lottieAnimationView.progress = it.animatedValue as Float
        }
        animator.start()
    }

    fun onAnimation(view: View, shouldDisappear: Boolean) {

        val measuredWidth = view.rootView.measuredWidth
        val trueExtend = (-50F.div(100)).times(measuredWidth)

        if (isCollapsed) {
            val animation = ObjectAnimator.ofFloat(view, "translationX", trueExtend)
            animation.duration = 300
            animation.addUpdateListener {
                when (it.currentPlayTime) {
                    in 250..300 -> {
                        view.translationZ = 100f
                    }
                    in 0..300 -> {
                        if (view.visibility == View.INVISIBLE && shouldDisappear) view.visibility = View.VISIBLE
                    }
                }
            }
//            if (powerManagerService.isPowerSaveMode) view.translationZ = 100f //FIXME: Add instance of powerManagerService
            view.visibility = View.VISIBLE
            animation.start()
        } else {
            val animation = ObjectAnimator.ofFloat(view, "translationX", 0.0f)
            view.translationZ = 0f
            animation.duration = 300
            animation.addUpdateListener {
                when (it.currentPlayTime) {
                    0L -> {
                        view.translationZ = 0f
                    }
                    in 220..300 -> {
                        if (view.visibility == View.VISIBLE && shouldDisappear) view.visibility = View.INVISIBLE
                    }
                }
            }
            animation.start()
        }
    }

    fun addButtons(args: Map<Int, () -> Unit>) {

        val layout = findViewById<ConstraintLayout>(R.id.main_fab_layout)
        val set = ConstraintSet()

        val view = Button(context)
        view.id = View.generateViewId()
        view.setCompoundDrawablesWithIntrinsicBounds(null, context.getDrawable(args.keys.first()), null, null)
        view.setPadding(8,8,8,8)
        view.setOnClickListener {
            args.values.first()
        }
        layout.addView(view, 0)
        set.clone(layout)
        set.connect(view.id, ConstraintSet.END, layout.id, ConstraintSet.END, 8)
        set.connect(view.id, ConstraintSet.BOTTOM, layout.id, ConstraintSet.BOTTOM, 28)
        set.applyTo(layout)

        buttons.add(view)

    }
}