package app.bankconvert.presenter.interactor

import app.bankconvert.realm.entities.Account
import com.vicpin.krealmextensions.queryFirst

class ConvertAccountInteractor {
    interface OnFinishedListener {
        fun onResultSuccess(account: Account)
        fun onResultFail()
    }

    fun requestServerData(onFinishedListener: OnFinishedListener) {
        onFinishedListener.onResultSuccess(Account().queryFirst() as Account)
    }
}