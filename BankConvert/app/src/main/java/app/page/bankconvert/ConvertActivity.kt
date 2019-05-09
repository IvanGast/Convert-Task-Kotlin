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
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ConvertActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var myAccount: MyAccount
    private var myConvertsCount:Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert)
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

        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (mSharedPreference.getString("account", null) != null) {
            val myAccountString = mSharedPreference.getString("account", null)
            val gson = Gson()
            val dataType = object : TypeToken<MyAccount>() {}.type
            myAccount = gson.fromJson<MyAccount>(myAccountString, dataType)
            myConvertsCount = mSharedPreference.getInt("count", 0)
        } else {
            myAccount = MyAccount(1000.00, 0.00, 0.00)
            myConvertsCount = 0
        }

        setText()

        val fromSpinner = findViewById<Spinner>(R.id.fromSpinner)
        val toSpinner = findViewById<Spinner>(R.id.toSpinner)
        val amountEditTextView = findViewById<EditText>(R.id.amountEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val convertCountTextView = findViewById<TextView>(R.id.convertCountTextView)

        convertCountTextView.text = "Iš viso konvertavimu : $myConvertsCount"

        submitButton.setOnClickListener {
            if (amountEditTextView.text.toString() != "") {
                var myConvert = MyConvert()
                myConvert.setFromCurr(fromSpinner.selectedItem as String)
                myConvert.setToCurr(toSpinner.selectedItem as String)
                myConvert.setMyAmount(amountEditTextView.text.toString())

                checkConvert(myConvert)
            } else {
                displayError("Nurodykit konvertavimo sumą")
            }
        }

    }

    private fun checkConvert(someConvert: MyConvert) {
        if (someConvert.getFromCurr() == someConvert.getToCurr() ) {
            displayError("Valiutos turi skirtis")
        } else {
            if (someConvert.getMyAmount().toDouble() < getAmount(someConvert.getFromCurr()) ) {
                checkAddition(someConvert)
            } else {
                displayError("Nepakanka pinigų")
            }
        }
    }

    private fun checkAddition(someConvert: MyConvert){
        if(myConvertsCount < 5 ||
            myConvertsCount % 5 == 0 ||
            (someConvert.getFromCurr() != "JPY" && someConvert.getMyAmount().toDouble() <= 200) ||
            (someConvert.getFromCurr() == "JPY" && someConvert.getMyAmount().toDouble() <= 15000)
        ) {
            validateConvert(someConvert, 0.0)
        } else {
            if (getAmount(someConvert.getFromCurr()) - someConvert.getMyAmount().toDouble() * 1.007 > 0) {
                validateConvert(someConvert, someConvert.getMyAmount().toDouble() * 0.007)
            } else {
                displayError("Nepakanka pinigų užmokėti komisinį mokėstį")
            }
        }

    }

    private fun validateConvert(someConvert: MyConvert, addition: Double) {
        var myData = GetApiData().execute(someConvert.getMyAmount(), someConvert.getFromCurr(), someConvert.getToCurr()).get()

        if (someConvert.getFromCurr() == "EUR") {
            myAccount.eurAmmount = myAccount.eurAmmount - someConvert.getMyAmount().toDouble() - addition
        } else {
            if (someConvert.getFromCurr() == "USD"){
                myAccount.usdAmmount = myAccount.usdAmmount - someConvert.getMyAmount().toDouble() - addition
            } else {
                myAccount.jpyAmmount = myAccount.jpyAmmount - someConvert.getMyAmount().toDouble() - addition
            }
        }

        if ( myData.getCurrency() == "EUR") {
            myAccount.eurAmmount = myAccount.eurAmmount + myData.getAmmount()
        } else {
            if (myData.getCurrency() == "USD") {
                myAccount.usdAmmount = myAccount.usdAmmount + myData.getAmmount()
            } else {
                myAccount.jpyAmmount = myAccount.jpyAmmount + myData.getAmmount()
            }
        }

        setText()

        var myConvertItems: ArrayList<MyHistoryItem>

        var mSharedPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (mSharedPreference.getString("history", null) != null) {
            var myHistoryString = mSharedPreference.getString("history", null)
            val gson = Gson()
            val dataType = object : TypeToken<ArrayList<MyHistoryItem>>() {}.type
            myConvertItems = gson.fromJson<ArrayList<MyHistoryItem>>(myHistoryString, dataType)
            myConvertsCount = mSharedPreference.getInt("count", 0)
            myConvertsCount++
        } else {
            myConvertItems = arrayListOf()
            myConvertsCount = 1
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val date = Calendar.getInstance().time

        myConvertItems.add(MyHistoryItem(someConvert.getFromCurr(), someConvert.getToCurr(), someConvert.getMyAmount(), myData.getAmmount().toString(), addition.toString(), sdf.format(date)))

        var mEditor = mSharedPreference.edit()
        val gson = Gson()

        mEditor.putString("account", gson.toJson(myAccount)).apply()
        mEditor.putInt("count", myConvertsCount).apply()
        mEditor.putString("history", gson.toJson(myConvertItems)).apply()

        val df = DecimalFormat("#.##")
        if (addition > 0) {
            Toast.makeText(
                applicationContext,
                "Jūs konvertavote " + df.format(someConvert.getMyAmount().toDouble()) + " "  + someConvert.getFromCurr() + " į " + df.format(myData.getAmmount()) + " " + someConvert.getToCurr() + ". Komisinis mokestis - " + df.format(addition) + " " + someConvert.getFromCurr() +" . (0.7% komisinis mokestis)",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                applicationContext,
                "Jūs konvertavote " + df.format(someConvert.getMyAmount().toDouble()) + " "  + someConvert.getFromCurr() + " į " + df.format(myData.getAmmount()) + " " + someConvert.getToCurr() + ". Komisinis mokestis - 0.00 EUR. (nemokamai)",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun displayError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun getAmount(curr: String): Double{
        if ( curr == "EUR") {
            return myAccount.eurAmmount
        } else {
            if ( curr == "USD" ) {
                return myAccount.usdAmmount
            } else {
                return myAccount.jpyAmmount
            }
        }
    }

    private fun setText(){
        val eurTextView = findViewById<TextView>(R.id.eurAmount)
        val usdTextView = findViewById<TextView>(R.id.usdAmount)
        val jpyTextView = findViewById<TextView>(R.id.jpyAmount)

        val df = DecimalFormat("#.##")

        eurTextView.text = df.format(myAccount.eurAmmount).toString() + " "
        usdTextView.text = df.format(myAccount.usdAmmount).toString() + " "
        jpyTextView.text = df.format(myAccount.jpyAmmount).toString() + " "
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
        menuInflater.inflate(R.menu.convert, menu)
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
        myConvertsCount = mSharedPreference.getInt("count", 0)
        myAccount = gson.fromJson(mSharedPreference.getString("account", gson.toJson(myAccount)), dataType)

        val convertCountTextView = findViewById<TextView>(R.id.convertCountTextView)
        convertCountTextView.text = "Iš viso konvertavimu : $myConvertsCount"

        super.onResume()
    }

    override fun onPause() {
        var mSharedPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var mEditor = mSharedPreference.edit()
        val gson = Gson()
        mEditor.putString("account", gson.toJson(myAccount)).apply()
        mEditor.putInt("count", myConvertsCount).apply()
        super.onPause()
    }
}
