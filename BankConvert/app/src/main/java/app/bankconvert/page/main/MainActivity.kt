package app.bankconvert.page.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import app.bankconvert.page.convert.ConvertActivity
import app.bankconvert.dagger.module.ContextModule
import app.bankconvert.dagger.DaggerRequestInterfaceComponent
import app.bankconvert.page.history.HistoryActivity
import app.bankconvert.realm.entities.Account
import app.page.bankconvert.R
import com.vicpin.krealmextensions.create
import com.vicpin.krealmextensions.queryFirst
import kotlinx.android.synthetic.main.content_main.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    MainAccountView {

    private val daggerRequestInterface = DaggerRequestInterfaceComponent.builder()
                                                                        .contextModule(ContextModule(this))
                                                                        .build()

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
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        if (Account().queryFirst() == null) {
            var acc = Account()
            acc.eurAmmount = 1000.00
            acc.create()
        }
        daggerRequestInterface.getMainPresenter().getData()
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

    override fun setDataError() {
        convertCountTextView.text = "Iš viso konvertavimu : Failed "
        eurAmountTextView.text =  "Failed "
        usdAmountTextView.text = "Failed "
        jpyAmountTextView.text = "Failed "
    }

    override fun setData(strUpdates: ArrayList<String>) {
        val df = DecimalFormat("#.##")

        convertCountTextView.text = "Iš viso konvertavimu : " + strUpdates[0]
        eurAmountTextView.text = df.format(strUpdates[1].toDouble()) + " "
        usdAmountTextView.text = df.format(strUpdates[2].toDouble()) + " "
        jpyAmountTextView.text = df.format(strUpdates[3].toDouble()) + " "
    }

    override fun onResume() {
        daggerRequestInterface.getMainPresenter().getData()
        super.onResume()
    }
}

interface MainAccountView {
    fun setData(strUpdates: ArrayList<String>)
    fun setDataError()
}