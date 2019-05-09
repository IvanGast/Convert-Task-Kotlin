package app.page.bankconvert

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
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
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var myHistoryItems: ArrayList<MyHistoryItem>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
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

        val myHistoryListView = findViewById<ListView>(R.id.myHistoryListView)

        var mSharedPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        if (mSharedPreference.getString("history", null) != null) {
            val gson = Gson()
            val dataType = object : TypeToken<ArrayList<MyHistoryItem>>() {}.type
            myHistoryItems = gson.fromJson<ArrayList<MyHistoryItem>>(mSharedPreference.getString("history", null), dataType)
            val adapter = MyAdapter(this, myHistoryItems)
            myHistoryListView.adapter = adapter


        } else {
            myHistoryItems = arrayListOf()
        }

        val myTotalCount = mSharedPreference.getInt("count", 0)
        val quantityTextView = findViewById<TextView>(R.id.quantityTextView)
        val overallTextView = findViewById<TextView>(R.id.overallTextView)
        val totalCommissionTextView = findViewById<TextView>(R.id.totalCommissionTextView)
        totalCommissionTextView.text = "Čia nenurodoma"
        overallTextView.text = "Čia nenurodoma"
        quantityTextView.text = "Konvertavimu kiekis : $myTotalCount"

        val df = DecimalFormat("#.##")
        val allButton = findViewById<Button>(R.id.allButton)
        val eurButton = findViewById<Button>(R.id.eurButton)
        val usdButton = findViewById<Button>(R.id.usdButton)
        val jpyButton = findViewById<Button>(R.id.jpyButton)

        allButton.setOnClickListener {
            val myAllAdapter = MyAdapter(this, myHistoryItems)
            myHistoryListView.adapter = myAllAdapter
            quantityTextView.text = "Konvertavimu kiekis : $myTotalCount"
            overallTextView.text = "Čia nenurodoma"
            totalCommissionTextView.text = "Čia nenurodoma"
        }
        eurButton.setOnClickListener {
            val myEurItems = myHistoryItems.stream()
                .filter { n -> n.fromCurr == "EUR" }
                .collect(Collectors.toList()) as ArrayList
            val myEurAdapter = MyAdapter(this, myEurItems)
            myHistoryListView.adapter = myEurAdapter
            val myOverall = df.format(myEurItems.sumByDouble { it.myAmount.toDouble() })
            val myTotal = df.format(myEurItems.sumByDouble { it.addition.toDouble() })
            totalCommissionTextView.text = "Iš viso komisiniu mokėsčiu : $myTotal EUR"
            quantityTextView.text = "Konvertavimu kiekis : " + myEurItems.size
            overallTextView.text = "Iš viso konvertuota : $myOverall EUR"
        }
        usdButton.setOnClickListener {
            val myUsdItems = myHistoryItems.stream()
                .filter { n -> n.fromCurr == "USD" }
                .collect(Collectors.toList()) as ArrayList
            val myUsdAdapter = MyAdapter(this, myUsdItems)
            myHistoryListView.adapter = myUsdAdapter
            val myOverall = df.format(myUsdItems.sumByDouble { it.myAmount.toDouble() })
            val myTotal = df.format(myUsdItems.sumByDouble { it.addition.toDouble() })
            totalCommissionTextView.text = "Iš viso komisiniu mokėsčiu : $myTotal USD"
            quantityTextView.text = "Konvertavimu kiekis : " + myUsdItems.size
            overallTextView.text = "Iš viso konvertuota : $myOverall USD"
        }
        jpyButton.setOnClickListener {
            val myJpyItems = myHistoryItems.stream()
                .filter { n -> n.fromCurr == "JPY" }
                .collect(Collectors.toList()) as ArrayList
            val myJpyAdapter = MyAdapter(this, myJpyItems)
            myHistoryListView.adapter = myJpyAdapter
            val myOverall = df.format(myJpyItems.sumByDouble { it.myAmount.toDouble() })
            val myTotal = df.format(myJpyItems.sumByDouble { it.addition.toDouble() })
            totalCommissionTextView.text = "Iš viso komisiniu mokėsčiu : $myTotal JPY"
            quantityTextView.text = "Konvertavimu kiekis : " + myJpyItems.size
            overallTextView.text = "Iš viso konvertuota : $myOverall JPY"
        }
    }

    override fun onResume() {
        val myHistoryListView = findViewById<ListView>(R.id.myHistoryListView)

        var mSharedPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (mSharedPreference.getString("history", null) != null) {
            val gson = Gson()
            val dataType = object : TypeToken<ArrayList<MyHistoryItem>>() {}.type
            myHistoryItems = gson.fromJson<ArrayList<MyHistoryItem>>(mSharedPreference.getString("history", null), dataType)
            println(mSharedPreference.getString("history", null))
            val adapter = MyAdapter(this, myHistoryItems)
            myHistoryListView.adapter = adapter

            val quantityTextView = findViewById<TextView>(R.id.quantityTextView)
            val overallTextView = findViewById<TextView>(R.id.overallTextView)
            val totalCommissionTextView = findViewById<TextView>(R.id.totalCommissionTextView)
            quantityTextView.text = "Konvertavimu kiekis : " + myHistoryItems.size
            overallTextView.text = "Čia nenurodoma"
            totalCommissionTextView.text = "Čia nenurodoma"
        }
        super.onResume()
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
        menuInflater.inflate(R.menu.history, menu)
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
}
