package app.bankconvert.presenter.presenter

import android.content.res.Resources
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import app.bankconvert.presenter.adapter.AccountListAdapter
import app.bankconvert.page.main.MainAccountView
import app.bankconvert.page.main.MainActivity
import app.bankconvert.presenter.interactor.MainAccountInteractor
import app.bankconvert.realm.entities.Account
import app.bankconvert.realm.entities.Balance
import app.page.bankconvert.R
import com.vicpin.krealmextensions.createOrUpdate
import com.vicpin.krealmextensions.queryFirst
import kotlinx.android.synthetic.main.content_main.*

class MainAccountPresenter(private var mainView: MainAccountView,
                           private val interactorConvert: MainAccountInteractor)
                            : MainAccountInteractor.OnFinishedListener {
    fun getData() {
        if (Account().queryFirst() == null) {
            var acc = Account()
            val arr = Resources.getSystem().getStringArray(R.array.currency_array)
            var i = 1
            for (curr in arr) {
                val balance = Balance()
                balance.id = i
                balance.currency = curr
                if (curr == "EUR") {
                    balance.amount = "1000"
                }
                i++
                acc.list.add(balance)
            }
            acc.createOrUpdate()
        }
        interactorConvert.requestServerData(this)
    }

    override fun onResultSuccess(account: Account) {
        val view = (mainView as MainActivity).mainAccountRecyclerView
        val adapter = AccountListAdapter(account.list.toList())
        view.layoutManager = LinearLayoutManager(mainView as MainActivity, LinearLayout.VERTICAL, false)
        view.adapter = adapter
        mainView.setData(account.convertCount)
    }

    override fun onResultFail() {
        mainView.setDataError()
    }

}