package app.bankconvert.presenter.interactor

import app.bankconvert.realm.entities.Account
import com.vicpin.krealmextensions.queryFirst
import io.realm.Realm

class MainAccountInteractor {

    interface OnFinishedListener {
        fun onResultSuccess(dataUpdates: Account)
        fun onResultFail()
    }

    fun requestServerData(onFinishedListener: OnFinishedListener) {
        onFinishedListener.onResultSuccess(Account().queryFirst() as Account)
    }
}