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

        val map: Map<Int, () -> Unit> = mapOf(
                R.drawable.ic_person_black_24dp to { logstuff("A") },
                R.drawable.ic_filter to { logstuff("B") },
                R.drawable.ic_location_24dp to { logstuff("C") }
        )

        fab.addButtons(map
                , FabParams(
                40,
                R.color.error_color_material_light,
                16,
                16
        )
        )
    }

    @SuppressLint("LogNotTimber")
    private fun logstuff(s: String): Unit {
        Log.wtf("tag", s)
    }
}
