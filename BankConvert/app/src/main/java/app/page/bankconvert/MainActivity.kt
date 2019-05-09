package app.page.bankconvert

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var myAccount: MyAccount? = null
    private var myConvertsCount:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Rašyk e-mail ivan.gastilovic@hotmail.com", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


        var mSharedPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (mSharedPreference.getString("account", null) != null) {
            var myAccountString = mSharedPreference.getString("account", null)
            val gson = Gson()
            val dataType = object : TypeToken<MyAccount>() {}.type
            myAccount = gson.fromJson<MyAccount>(myAccountString, dataType)
            myConvertsCount = mSharedPreference.getInt("count", 0)
        } else {
            myAccount = MyAccount(1000.00, 0.00, 0.00)
            myConvertsCount = 0
        }

        setText()
    }

    private fun setText(){
        val eurTextView = findViewById<TextView>(R.id.eurAmount)
        val usdTextView = findViewById<TextView>(R.id.usdAmount)
        val jpyTextView = findViewById<TextView>(R.id.jpyAmount)
        val convertCountTextView = findViewById<TextView>(R.id.convertCountTextView)

        val df = DecimalFormat("#.##")

        convertCountTextView.text = "Iš viso konvertavimu : $myConvertsCount"
        eurTextView.text = df.format(myAccount!!.eurAmmount).toString() + " "
        usdTextView.text = df.format(myAccount!!.usdAmmount).toString() + " "
        jpyTextView.text = df.format(myAccount!!.jpyAmmount).toString() + " "
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_convert -> {
                val intent = Intent(this, ConvertActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        var mSharedPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val gson = Gson()
        val dataType = object : TypeToken<MyAccount>() {}.type
        myAccount = gson.fromJson(mSharedPreference.getString("account", gson.toJson(myAccount)), dataType)
        myConvertsCount = mSharedPreference.getInt("count", 0)
        setText()
        super.onResume()
    }
}
