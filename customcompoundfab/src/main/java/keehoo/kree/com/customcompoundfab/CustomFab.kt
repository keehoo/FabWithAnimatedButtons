package keehoo.kree.com.customcompoundfab

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.content.ContextCompat
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

    var isExpanded: Boolean = false
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
            fabFlipped()
            animateFab()
        }
    }

    private fun fabFlipped() {
        isExpanded = !isExpanded
    }

    private fun animateFab() {
        val animator: ValueAnimator = if (isExpanded) {
            ValueAnimator.ofFloat(0.1F, 0.5F).setDuration(300)
        } else {
            ValueAnimator.ofFloat(0.6F, 1.0F).setDuration(300)
        }
        animator.removeAllUpdateListeners()
        animator.addUpdateListener {
            lottieAnimationView.progress = it.animatedValue as Float
        }
        animator.start()
    }

    fun onAnimation(view: View, shouldDisappear: Boolean) {

        val totalButtons = buttons.size.toFloat()
        val currentButton = buttons.indexOf(view as Button) + 1.toFloat()
        val measuredWidth = view.rootView.measuredWidth
        val modified = currentButton.div(totalButtons).div(75f).times(-1)
        val trueExtend = modified.times(measuredWidth).times(60f)

        if (isExpanded) {
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
                    0L -> view.translationZ = 0f
                    in 220..300 -> {
                        if (view.visibility == View.VISIBLE && shouldDisappear) view.visibility = View.INVISIBLE
                    }
                }
            }
            animation.start()
        }
    }

    fun addButtons(args: Map<Int, () -> Unit>, params: FabParams = FabParams()) {

        var i = 0
        args.forEach { entry ->

            val layout = findViewById<ConstraintLayout>(R.id.main_fab_layout)
            val set = ConstraintSet()

            val view = Button(context)
            view.id = View.generateViewId()
            view.layoutParams = ConstraintLayout.LayoutParams(
                    params.buttonSize.toPx,
                    params.buttonSize.toPx
            )
            view.setCompoundDrawablesWithIntrinsicBounds(null, context.getDrawable(entry.key), null, null)
            view.compoundDrawablePadding = 8
            view.setOnClickListener { entry.value.invoke() }
            view.setBackgroundResource(R.drawable.round_button)
            view.clipToOutline
            view.elevation = 0.0f
            view.visibility = View.INVISIBLE
            layout.addView(view, i++)
            tint(view, ContextCompat.getColor(context, params.buttonsColor))

            set.clone(layout)
            set.connect(view.id, ConstraintSet.END, layout.id, ConstraintSet.END, params.buttonEndOffset.toPx
            )
            set.connect(view.id, ConstraintSet.BOTTOM, layout.id, ConstraintSet.BOTTOM, params.buttonBottomOffset.toPx
            )
            set.applyTo(layout)

            buttons.add(view)
        }
    }

    fun tint(button: Button, @ColorInt color: Int) {
        val drawables = button.compoundDrawables
        for (drawable in drawables) {
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

data class FabParams(
        val buttonSize: Int = 50,
        @ColorRes val buttonsColor: Int = R.color.accent_material_light,
        val buttonBottomOffset: Int = 16,
        val buttonEndOffset: Int = 16

)

