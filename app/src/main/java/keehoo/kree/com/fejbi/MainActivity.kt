package keehoo.kree.com.fejbi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.fab

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map: HashMap<Int, () -> Unit> = HashMap()

        map[R.drawable.ic_person_black_24dp] = { Toast.makeText(this, "asd", Toast.LENGTH_LONG).show() }

        fab.addButtons(map)
    }
}
