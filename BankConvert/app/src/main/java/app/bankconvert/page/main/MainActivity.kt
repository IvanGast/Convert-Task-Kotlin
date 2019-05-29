package app.bankconvert.page.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import app.bankconvert.page.convert.ConvertActivity
import app.bankconvert.dagger.module.ContextModule
import app.bankconvert.dagger.DaggerRequestInterfaceComponent
import app.bankconvert.page.history.HistoryActivity
import app.page.bankconvert.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainAccountView {
    private val daggerRequestInterface = DaggerRequestInterfaceComponent.builder()
                                                                        .contextModule(ContextModule(this))
                                                                        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.fab_message), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        daggerRequestInterface.getMainPresenter().getData()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun setDataError() {
        convertCountTextView.text = getString(R.string.not_available)
    }

    override fun setData(updatedCount: Int) {
        convertCountTextView.text = getString(R.string.overall_convert) + updatedCount
    }

    override fun onResume() {
        daggerRequestInterface.getMainPresenter().getData()
        super.onResume()
    }
}