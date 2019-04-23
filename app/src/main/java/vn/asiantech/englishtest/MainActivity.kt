package vn.asiantech.englishtest

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import vn.asiantech.englishtest.listtest.TestListActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        window.statusBarColor = resources.getColor(R.color.colorBlue)
        screenDelay()
    }

    private fun screenDelay() = Handler().postDelayed({
        startActivity(Intent(this, TestListActivity::class.java))
        finish()
    }, 3000)
}
