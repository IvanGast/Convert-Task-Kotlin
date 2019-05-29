package app.bankconvert.page.convert

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.*
import app.bankconvert.dagger.module.ContextModule
import app.bankconvert.dagger.DaggerRequestInterfaceComponent
import app.bankconvert.page.history.HistoryActivity
import app.bankconvert.page.main.MainActivity
import app.page.bankconvert.R
import kotlinx.android.synthetic.main.activity_convert.*
import kotlinx.android.synthetic.main.app_bar_convert.*
import kotlinx.android.synthetic.main.content_convert.*
import java.text.DecimalFormat

class ConvertActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ConvertAccountView {
    private val daggerRequestInterface = DaggerRequestInterfaceComponent.builder()
                                                                        .contextModule(ContextModule(this))
                                                                        .build()
    private lateinit var url: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert)
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
        daggerRequestInterface.getConvertPresenter().getData()
        submitButton.setOnClickListener {
            if (amountEditText.text.toString() != "") {
                val df = DecimalFormat("#.##")
                var someConvert = Convert(fromSpinner.selectedItem as String,
                                          toSpinner.selectedItem as String,
                                          df.format(amountEditText.text.toString().toDouble()))
                amountEditText.setText(someConvert.myAmount)
                amountEditText.setSelection(someConvert.myAmount.length)
                daggerRequestInterface.getConvertPresenter().checkConvert(someConvert)
            } else {
                displayError(getString(R.string.value_error))
            }
        }
    }

    fun setUrl(url: String) {
        this.url = url
    }

    fun getUrl():String {
        return url
    }

    override fun displayError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun handleResponse(addition: Double, amount: String, someConvert: Convert) {
        val df = DecimalFormat("#.##")
        if (addition > 0) {
            Toast.makeText(
                applicationContext,
                getString(R.string.converted) + df.format(someConvert.myAmount.toDouble()) + " "  + someConvert.fromCurr + " į " + amount + " " + someConvert.toCurr + ". Komisinis mokestis - " + df.format(addition) + " " + someConvert.fromCurr +" . (0.7% komisinis mokestis)",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.converted) + df.format(someConvert.myAmount.toDouble()) + " "  + someConvert.fromCurr + " į " + amount + " " + someConvert.toCurr + ". Komisinis mokestis - 0.00 EUR. (nemokamai)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun handleError(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.convert, menu)
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
}