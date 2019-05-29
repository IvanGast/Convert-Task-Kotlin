package app.bankconvert.presenter.presenter

import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import app.bankconvert.dagger.DaggerRequestInterfaceComponent
import app.bankconvert.dagger.module.ContextModule
import app.bankconvert.page.convert.Convert
import app.bankconvert.page.convert.ConvertAccountView
import app.bankconvert.page.convert.ConvertActivity
import app.bankconvert.presenter.adapter.AccountListAdapter
import app.bankconvert.presenter.interactor.ConvertAccountInteractor
import app.bankconvert.realm.entities.HistoryItem
import app.bankconvert.realm.entities.Account
import app.bankconvert.retrofit.model.DataEntity
import app.page.bankconvert.R
import com.vicpin.krealmextensions.createOrUpdate
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_convert.*
import java.text.SimpleDateFormat
import java.util.*

class ConvertAccountPresenter(private var convertView: ConvertAccountView,
                              private val interactorConvert: ConvertAccountInteractor)
                              : ConvertAccountInteractor.OnFinishedListener {
    private lateinit var account: Account
    private lateinit var presenterSomeConvert: Convert
    private lateinit var presenterAddition: String

    fun getData() {
        interactorConvert.requestServerData(this)
    }

    fun checkConvert(someConvert: Convert){
        account = Account().queryFirst() as Account
        if (someConvert.fromCurr == someConvert.toCurr ) {
            convertView.displayError((convertView as ConvertActivity).getString(R.string.currency_error))
        } else {
            if (someConvert.myAmount.toDouble() <= getAmount(account, someConvert.fromCurr) ) {
                checkAddition(someConvert)
            } else {
                convertView.displayError((convertView as ConvertActivity).getString(R.string.balance_error))
            }
        }
    }

    private fun checkAddition(someConvert: Convert){
        presenterSomeConvert = someConvert
        if(account.convertCount < 5 ||
            account.convertCount % 5 == 0 ||
            (someConvert.fromCurr != "JPY" && someConvert.myAmount.toDouble() <= 200) ||
            (someConvert.fromCurr == "JPY" && someConvert.myAmount.toDouble() <= 15000)
        ) {
            presenterAddition = "0.0"
            loadJSON("http://api.evp.lt/currency/commercial/exchange/" + someConvert.myAmount + "-" + someConvert.fromCurr + "/" + someConvert.toCurr + "/latest/")
        } else {
            if (getAmount(account, someConvert.fromCurr) - someConvert.myAmount.toDouble() * 1.007 > 0) {
                presenterAddition = (someConvert.myAmount.toDouble() * 0.007).toString()
                loadJSON("http://api.evp.lt/currency/commercial/exchange/" + someConvert.myAmount + "-" + someConvert.fromCurr + "/" + someConvert.toCurr + "/latest/")
            } else {
                convertView.displayError((convertView as ConvertActivity).getString(R.string.commission_error))
            }
        }
    }

    private fun loadJSON(url: String){
        (convertView as ConvertActivity).setUrl(url)
        val daggerRequestInterface = DaggerRequestInterfaceComponent.builder()
                                                                    .contextModule(ContextModule(convertView as ConvertActivity))
                                                                    .build()
        val requestInterface = daggerRequestInterface.getRequestService()
        requestInterface.getData(url)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(this::handleResponse, this::handleError)
    }

    private fun handleResponse(data: DataEntity){
        onUpdate(account, presenterSomeConvert, data, presenterAddition)
        convertView.handleResponse(presenterAddition.toDouble(), data.amount, presenterSomeConvert)
    }

    private fun handleError(error: Throwable){
        convertView.handleError(error)
    }

    private fun onUpdate(account: Account, someConvert: Convert, data: DataEntity, addition: String) {
        for ( balance in account.list) {
            if (balance.currency == someConvert.fromCurr) {
                balance.amount = (balance.amount.toDouble() - someConvert.myAmount.toDouble() - addition.toDouble()).toString()
                balance.createOrUpdate()
            } else if (balance.currency == data.currency) {
                balance.amount = (balance.amount.toDouble() + data.amount.toDouble()).toString()
                balance.createOrUpdate()
                }
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        var item = HistoryItem()
        item.id = account.convertCount
        item.fromCurr = someConvert.fromCurr
        item.toCurr = someConvert.toCurr
        item.amount = someConvert.myAmount
        item.convertedAmount = data.amount
        item.addition = addition
        item.time = sdf.format(Calendar.getInstance().time)
        account.convertCount++
        account.createOrUpdate()
        item.save()

        val view = (convertView as ConvertActivity).convertAccountRecyclerView
        view.layoutManager = LinearLayoutManager(convertView as ConvertActivity, LinearLayout.VERTICAL, false)
        val adapter = AccountListAdapter(account.list.toList())
        view.adapter = adapter
        convertView.setData(account.convertCount)
    }

    private fun getAmount(account: Account, curr: String): Double{
        for ( balance in account.list) {
            if (balance.currency == curr){
                return balance.amount.toDouble()
            }
        }
        return 0.0
    }

    override fun onResultSuccess(account: Account) {
        val view = (convertView as ConvertActivity).convertAccountRecyclerView
        view.layoutManager = LinearLayoutManager(convertView as ConvertActivity, LinearLayout.VERTICAL, false)
        val adapter = AccountListAdapter(account.list.toList())
        view.adapter = adapter
        convertView.setData(account.convertCount)
    }

    override fun onResultFail() {
        convertView.setDataError()
    }
}