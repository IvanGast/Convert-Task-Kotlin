package app.bankconvert.presenter.presenter

import app.bankconvert.page.main.MainAccountView
import app.bankconvert.realm.entities.Account
import app.bankconvert.presenter.interactor.MainAccountInteractor

class MainAccountPresenter(private var mainView: MainAccountView, private val interactorConvert: MainAccountInteractor)
: MainAccountInteractor.OnFinishedListener {

    fun getData() {
        interactorConvert.requestServerData(this)
    }

    override fun onResultSuccess(dataUpdates: Account) {
        var arrUpdates = arrayListOf<String>()
        arrUpdates.add(dataUpdates.convertCount.toString())
        arrUpdates.add(dataUpdates.eurAmmount.toString())
        arrUpdates.add(dataUpdates.usdAmmount.toString())
        arrUpdates.add(dataUpdates.jpyAmmount.toString())

        mainView.setData(arrUpdates)
    }

    override fun onResultFail() {
        mainView.setDataError()
    }

}