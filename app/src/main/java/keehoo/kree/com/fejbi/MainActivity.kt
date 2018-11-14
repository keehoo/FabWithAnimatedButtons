package keehoo.kree.com.fejbi

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import keehoo.kree.com.customcompoundfab.FabParams
import kotlinx.android.synthetic.main.activity_main.fab

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawableIdToLambda: Map<Int, () -> Unit> = mapOf(
                R.drawable.ic_person_black_24dp to { logstuff("A") },
                R.drawable.ic_filter to { logstuff("B") },
                R.drawable.ic_location_24dp to { logstuff("C") }
//                R.drawable.ic_whatshot_black_24dp to { logstuff("D") },
//                R.drawable.ic_cake_black_24dp to { logstuff("E") },
//                R.drawable.ic_child_care_black_24dp to { logstuff("F") }
        )

        fab.setup(
                drawableIdToLambda,
                FabParams(
                        buttonSize = 50,
                        buttonaAvatarTintColor = R.color.error_color_material_light,
                        buttonBackgroundColor = R.color.abc_color_highlight_material,
                        buttonBottomOffset = 12,
                        buttonEndOffset = 2,
                        collapseAfterButtonClick = true,
                        extractionOffset = 24
                )
        )
        fab.isPowerSaverMode = false
    }

    @SuppressLint("LogNotTimber")
    private fun logstuff(s: String) {
        Log.wtf("tag", s)
    }
}
