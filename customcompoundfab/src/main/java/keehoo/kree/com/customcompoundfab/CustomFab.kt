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
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView

class CustomFab @JvmOverloads constructor(
        context: Context,
        val attrs: AttributeSet? = null,
        defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val permanentMenuKeys = ViewConfiguration.get(context).hasPermanentMenuKey()
    private val lottieAnimationView: LottieAnimationView get() = findViewById(R.id.animation_view)
    private var buttons: MutableList<Button> = mutableListOf()
    var isPowerSaverMode = false
    private lateinit var params: FabParams
    private var endMargin: Int? = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.lottie_stuff, this, true)
        lottieAnimationView.setOnClickListener {
            isExpanded = !isExpanded
            animateFab()
        }
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomFab,
                0, 0).apply {

            try {
                endMargin = getDimensionPixelSize(R.styleable.CustomFab_endMargin, 0)
            } finally {
                recycle()
            }
        }


//        (this.layoutParams as MarginLayoutParams).setMargins(0, 0, 50, 0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        var result: String = ""
        (lottieAnimationView.layoutParams as ConstraintLayout.LayoutParams).marginEnd = endMargin ?: 0

    }


    var isExpanded: Boolean = false
        set(value) {
            field = value
            animateFab()
            animateButtons()
        }

    private fun animateButtons() = buttons.forEach { animate(it) }

    private fun animateFab() {
        val animator: ValueAnimator = if (isExpanded) {
            ValueAnimator.ofFloat(0.1F, 0.5F).setDuration(params.animationDuration)
        } else {
            ValueAnimator.ofFloat(0.6F, 1.0F).setDuration(params.animationDuration)
        }
//        animator.removeAllUpdateListeners()
//        animator.addUpdateListener {
//            lottieAnimationView.progress = it.animatedValue as Float
//        }
        animator.start()
    }

    private fun animate(view: View) {

        val display = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = Point()
        display.defaultDisplay.getRealSize(p)
        val isHorizontal = p.x > p.y
        val viewPosition = Point(view.width.toPx, view.height.toPx)
        val fabPosition = Point(lottieAnimationView.width, lottieAnimationView.height)
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val h = resources.getDimensionPixelSize(resourceId)

        val totalButtons = buttons.size.toFloat()
        val currentButton = buttons.indexOf(view as Button) + 1.toFloat()

        val trueExtend = (p.x
                .minus(viewPosition.x)
                .removeBarHeight(permanentMenuKeys, isHorizontal, h)
                .minus(params.buttonSize.toPx)
                .times(currentButton.div(totalButtons))
                .times(-1)
                )

        if (isExpanded) {
            val animation = ObjectAnimator.ofFloat(view, "translationX", trueExtend)
            animation.duration = params.animationDuration
            animation.removeAllListeners()
            animation.addUpdateListener {
                when (it.currentPlayTime) {

                    in 0..100 -> lottieAnimationView.isClickable = false
                    in params.animationDuration.times(0.9).toLong()..params.animationDuration -> {
                        view.translationZ = 1000f
                        lottieAnimationView.isClickable = true
                    }
                    in params.animationDuration.times(0.2).toLong()..params.animationDuration -> {
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
            animation.duration = params.animationDuration
            animation.removeAllUpdateListeners()
            animation.addUpdateListener {
                when (it.currentPlayTime) {
                    0L -> view.translationZ = 0f
                    in params.animationDuration.times(0.9).toLong()..params.animationDuration -> {
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
                clipToPadding = true

                setBackgroundResource(R.drawable.round_button)
                tint(this, ContextCompat.getColor(context, params.buttonaAvatarTintColor))

                val sd = background.mutate() as GradientDrawable
                sd.setColor(ContextCompat.getColor(context, params.buttonBackgroundColor))
                sd.invalidateSelf()

                layout.addView(this, i++)
                with(set) {
                    clone(layout)
                    setMargin(lottieAnimationView.id, 2, endMargin ?: 0)
                    connect(id, ConstraintSet.END, layout.id, ConstraintSet.END, params.buttonEndOffset.toPx.plus(endMargin?: 0))
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

private fun Int.removeBarHeight(permanentKeys: Boolean, horizontal: Boolean, h: Int): Int {
    return if (horizontal && !permanentKeys) minus(h) else plus(h)
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
//        val extractionOffset: Int = buttonEndOffset.times(4),
        val animationDuration: Long = 300
)
