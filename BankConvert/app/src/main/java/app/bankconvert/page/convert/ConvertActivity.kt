package app.bankconvert.page.convert

import android.annotation.SuppressLint
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
import android.widget.*
import app.bankconvert.dagger.module.ContextModule
import app.bankconvert.dagger.DaggerRequestInterfaceComponent
import app.bankconvert.page.history.HistoryActivity
import app.bankconvert.realm.entities.Account
import app.bankconvert.page.main.MainActivity
import app.bankconvert.retrofit.model.DataEntity
import app.page.bankconvert.R
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_convert.*
import java.text.DecimalFormat
import java.util.*

class ConvertActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ConvertAccountView {

    private val VALUE_ERROR = "Nurodykit konvertavimo sumą"
    private val CURRENCY_ERROR = "Valiutos turi skirtis"
    private val BALANCE_ERROR = "Nepakanka pinigų"
    private val COMMISSION_ERROR = "Nepakanka pinigų užmokėti komisinį mokėstį"
    private val EUR = "EUR"
    private val USD = "USD"
    private val JPY = "JPY"
    private val NOT_AVAILABLE = "Failed "

    private val daggerRequestInterface = DaggerRequestInterfaceComponent.builder()
                                                                        .contextModule(ContextModule(this))
                                                                        .build()
    private lateinit var globalUrl: String
    private lateinit var account: Account
    private lateinit var someConvert: Convert
    private var addition:Double = 0.0

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
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        account = Account().queryFirst() as Account

        daggerRequestInterface.getConvertPresenter().getData()

        val fromSpinner = findViewById<Spinner>(R.id.fromSpinner)
        val toSpinner = findViewById<Spinner>(R.id.toSpinner)
        val amountEditTextView = findViewById<EditText>(R.id.amountEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            if (amountEditTextView.text.toString() != "") {
                someConvert = Convert()
                someConvert.setFromCurr(fromSpinner.selectedItem as String)
                someConvert.setToCurr(toSpinner.selectedItem as String)
                val df = DecimalFormat("#.##")
                someConvert.setMyAmount(df.format(amountEditTextView.text.toString().toDouble()))
                amountEditTextView.setText(someConvert.getMyAmount())
                amountEditTextView.setSelection(someConvert.getMyAmount().length)
                checkConvert()
            } else {
                displayError(VALUE_ERROR)
            }
        }
    }

    private fun checkConvert() {
        if (someConvert.getFromCurr() == someConvert.getToCurr() ) {
            displayError(CURRENCY_ERROR)
        } else {
            if (someConvert.getMyAmount().toDouble() <= getAmount(someConvert.getFromCurr()) ) {
                checkAddition()
            } else {
                displayError(BALANCE_ERROR)
            }
        }
    }

    private fun checkAddition(){
        if(account.convertCount < 5 ||
            account.convertCount % 5 == 0 ||
            (someConvert.getFromCurr() != JPY && someConvert.getMyAmount().toDouble() <= 200) ||
            (someConvert.getFromCurr() == JPY && someConvert.getMyAmount().toDouble() <= 15000)
        ) {
            addition = 0.0
            loadJSON("http://api.evp.lt/currency/commercial/exchange/" + someConvert.getMyAmount() + "-" + someConvert.getFromCurr() + "/" + someConvert.getToCurr() + "/latest/")
        } else {
            if (getAmount(someConvert.getFromCurr()) - someConvert.getMyAmount().toDouble() * 1.007 > 0) {
                addition = someConvert.getMyAmount().toDouble() * 0.007
                loadJSON("http://api.evp.lt/currency/commercial/exchange/" + someConvert.getMyAmount() + "-" + someConvert.getFromCurr() + "/" + someConvert.getToCurr() + "/latest/")
            } else {
                displayError(COMMISSION_ERROR)
            }
        }
    }


    private fun displayError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun getGlobalUrl():String {
        return globalUrl
    }

    private fun getAmount(curr: String): Double{
        if ( curr == EUR) {
            return account.eurAmmount
        } else {
            if ( curr == USD ) {
                return account.usdAmmount
            } else {
                return account.jpyAmmount
            }
        }
    }

    private fun loadJSON(url: String){
        globalUrl = url
        val requestInterface = daggerRequestInterface.getRequestService()

        val mCompositeDisposable = CompositeDisposable()
        mCompositeDisposable.add(requestInterface.getData(url)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(this::handleResponse, this::handleError))
    }


    private fun handleResponse(data: DataEntity) {
        daggerRequestInterface.getConvertPresenter().onResultUpdate(account, someConvert, data, addition)

        val df = DecimalFormat("#.##")

        if (addition > 0) {
            Toast.makeText(
                applicationContext,
                "Jūs konvertavote " + df.format(someConvert.getMyAmount().toDouble()) + " "  + someConvert.getFromCurr() + " į " + data.amount + " " + someConvert.getToCurr() + ". Komisinis mokestis - " + df.format(addition) + " " + someConvert.getFromCurr() +" . (0.7% komisinis mokestis)",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                applicationContext,
                "Jūs konvertavote " + df.format(someConvert.getMyAmount().toDouble()) + " "  + someConvert.getFromCurr() + " į " + data.amount + " " + someConvert.getToCurr() + ". Komisinis mokestis - 0.00 EUR. (nemokamai)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(this, error.message + " Contact support.", Toast.LENGTH_SHORT).show()
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

    override fun setDataError() {
        convertCountTextView.text = "Iš viso konvertavimu : Failed "
        eurAmountTextView.text =  NOT_AVAILABLE
        usdAmountTextView.text = NOT_AVAILABLE
        jpyAmountTextView.text = NOT_AVAILABLE
    }

    override fun setData(strUpdates: ArrayList<String>) {
        val df = DecimalFormat("#.##")
        convertCountTextView.text = "Iš viso konvertavimu : " + strUpdates[0]
        eurAmountTextView.text = df.format(strUpdates[1].toDouble()) + " "
        usdAmountTextView.text = df.format(strUpdates[2].toDouble()) + " "
        jpyAmountTextView.text = df.format(strUpdates[3].toDouble()) + " "
    }
}

interface ConvertAccountView {
    fun setData(strUpdates: ArrayList<String>)
    fun setDataError()
}