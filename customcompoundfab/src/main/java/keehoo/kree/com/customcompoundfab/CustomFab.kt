package keehoo.kree.com.customcompoundfab

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
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
import android.view.WindowManager


class CustomFab @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : ConstraintLayout(context, attrs, defStyle) {

    private var buttons: MutableList<Button> = mutableListOf()
    private lateinit var params: FabParams

    private var isExpanded: Boolean = false
        private set(value) {
            field = value
            animateFab()
            animateButtons()
        }

    var isPowerSaverMode = false

    private fun animateButtons() = buttons.forEach { onAnimation(it) }


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

    fun onAnimation(view: View) {

        val display = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        display.defaultDisplay.getRealSize(p)
        val isHorizontal = p.x > p.y
        val viewPosition = Point(view.width, view.height)
        val viewPadding = view.paddingEnd.toPx + view.paddingStart.toPx

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val h = resources.getDimensionPixelSize(resourceId)


        val totalButtons = buttons.size.toFloat()
        val currentButton = buttons.indexOf(view as Button) + 1.toFloat()
//        val measuredWidth = view.rootView.measuredWidth
//        val modified = currentButton.div(totalButtons).div(75f).times(-1)
//        val trueExtend = modified.times(measuredWidth).times(60f)

        val trueExtend = (p.x.minus(viewPosition.x )

                .removeBarHeight(isHorizontal, h)
                .minus(params.buttonSize.toPx)
                .times(currentButton.div(totalButtons)).times(-1))
        if (isExpanded) {
            val animation = ObjectAnimator.ofFloat(view, "translationX", trueExtend)
            animation.duration = 300
            animation.addUpdateListener {
                when (it.currentPlayTime) {
                    in 250..300 -> {
                        view.translationZ = 100f
                    }
                    in 0..300 -> {
                        if (view.visibility == View.INVISIBLE) view.visibility = View.VISIBLE
                    }
                }
            }
            if (isPowerSaverMode) view.translationZ = 100f
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
                        if (view.visibility == View.VISIBLE) view.visibility = View.INVISIBLE
                    }
                }
            }
            animation.start()
        }
    }

    fun setup(args: Map<Int, () -> Unit>, params: FabParams = FabParams()) {

        this.params = params
        val layout = findViewById<ConstraintLayout>(R.id.main_fab_layout)
        var i = 0
        buttons.clear()
        args.forEach { entry ->

            val set = ConstraintSet()
            with(Button(context)) {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(
                        params.buttonSize.toPx,
                        params.buttonSize.toPx
                )
                setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        context.getDrawable(entry.key),
                        null,
                        null)
                compoundDrawablePadding = 8
                setOnClickListener {
                    if (params.collapseAfterButtonClick) isExpanded = false
                    entry.value.invoke()
                }
                elevation = 0.0f
                visibility = View.INVISIBLE

                setBackgroundResource(R.drawable.round_button)
                tint(this, ContextCompat.getColor(context, params.buttonaAvatarTintColor))

                val sd = background.mutate() as GradientDrawable
                sd.setColor(ContextCompat.getColor(context, params.buttonBackgroundColor))
                sd.invalidateSelf()

                layout.addView(this, i++)
                with(set) {
                    clone(layout)
                    connect(id, ConstraintSet.END, layout.id, ConstraintSet.END, params.buttonEndOffset.toPx)
                    connect(id, ConstraintSet.BOTTOM, layout.id, ConstraintSet.BOTTOM, params.buttonBottomOffset.toPx)
                    applyTo(layout)
                }
                buttons.add(this)
            }
        }
    }

    private fun tint(button: Button, @ColorInt color: Int) {
        val drawables = button.compoundDrawables
        for (drawable in drawables) {
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}

private fun Int.removeBarHeight(horizontal: Boolean, h: Int): Int {
    return if (horizontal) minus(h) else plus(h)
}

private val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

data class FabParams(
        val buttonSize: Int = 50,
        @ColorRes val buttonaAvatarTintColor: Int = R.color.accent_material_light,
        @ColorRes val buttonBackgroundColor: Int = R.color.primary_dark_material_light,
        val buttonBottomOffset: Int = 16,
        val buttonEndOffset: Int = 16,
        val collapseAfterButtonClick: Boolean = true,
        val extractionOffset: Int = buttonEndOffset.times(4)
)
