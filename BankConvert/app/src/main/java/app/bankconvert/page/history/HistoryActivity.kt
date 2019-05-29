package app.bankconvert.page.history

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
import app.bankconvert.page.convert.ConvertActivity
import app.bankconvert.realm.entities.HistoryItem
import app.bankconvert.page.main.MainActivity
import app.page.bankconvert.R
import com.vicpin.krealmextensions.queryAll
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.app_bar_history.*
import kotlinx.android.synthetic.main.content_history.*
import java.text.DecimalFormat
import java.util.stream.Collectors

class HistoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var listHistoryItems: List<HistoryItem>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
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

        listHistoryItems = HistoryItem().queryAll()
        val accountConvertsCount = listHistoryItems.size
        quantityTextView.text = "Konvertavimu kiekis : $accountConvertsCount"
        val df = DecimalFormat("#.##")

        allButton.setOnClickListener {
            val allAdapter = HistoryListAdapter(this, listHistoryItems)
            historyListView.adapter = allAdapter
            quantityTextView.text = "Konvertavimu kiekis : $accountConvertsCount"
            overallTextView.text = getString(R.string.not_available_history)
            totalCommissionTextView.text = getString(R.string.not_available_history)
        }
        eurButton.setOnClickListener {
            val eurItems = listHistoryItems.stream()
                .filter { n -> n.fromCurr == "EUR" }
                .collect(Collectors.toList())
            val eurAdapter = HistoryListAdapter(this, eurItems)
            historyListView.adapter = eurAdapter
            val overall = df.format(eurItems.sumByDouble { it.amount.toDouble() })
            val total = df.format(eurItems.sumByDouble { it.addition.toDouble() })
            totalCommissionTextView.text = "Iš viso komisiniu mokėsčiu : $total EUR"
            quantityTextView.text = "Konvertavimu kiekis : " + eurItems.size
            overallTextView.text = "Iš viso konvertuota : $overall EUR"
        }
        usdButton.setOnClickListener {
            val usdItems = listHistoryItems.stream()
                .filter { n -> n.fromCurr == "USD" }
                .collect(Collectors.toList())
            val usdAdapter = HistoryListAdapter(this, usdItems)
            historyListView.adapter = usdAdapter
            val overall = df.format(usdItems.sumByDouble { it.amount.toDouble() })
            val total = df.format(usdItems.sumByDouble { it.addition.toDouble() })
            totalCommissionTextView.text = "Iš viso komisiniu mokėsčiu : $total USD"
            quantityTextView.text = "Konvertavimu kiekis : " + usdItems.size
            overallTextView.text = "Iš viso konvertuota : $overall USD"
        }
        jpyButton.setOnClickListener {
            val jpyItems = listHistoryItems.stream()
                .filter { n -> n.fromCurr == "JPY" }
                .collect(Collectors.toList())
            val jpyAdapter = HistoryListAdapter(this, jpyItems)
            historyListView.adapter = jpyAdapter
            val overall = df.format(jpyItems.sumByDouble { it.amount.toDouble() })
            val total = df.format(jpyItems.sumByDouble { it.addition.toDouble() })
            totalCommissionTextView.text = "Iš viso komisiniu mokėsčiu : $total JPY"
            quantityTextView.text = "Konvertavimu kiekis : " + jpyItems.size
            overallTextView.text = "Iš viso konvertuota : $overall JPY"
        }
    }

    override fun onResume() {
        listHistoryItems = HistoryItem().queryAll()
        val adapter = HistoryListAdapter(this, listHistoryItems)
        historyListView.adapter = adapter
        quantityTextView.text = "Konvertavimu kiekis : " + listHistoryItems.size
        overallTextView.text = getString(R.string.not_available_history)
        totalCommissionTextView.text = getString(R.string.not_available_history)
        super.onResume()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history, menu)
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
}
