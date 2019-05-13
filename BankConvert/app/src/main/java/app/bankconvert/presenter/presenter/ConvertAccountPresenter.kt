package app.bankconvert.presenter.presenter

import app.bankconvert.page.convert.ConvertAccountView
import app.bankconvert.page.convert.ConvertActivity
import app.bankconvert.realm.entities.Account
import app.bankconvert.presenter.interactor.ConvertAccountInteractor

class ConvertAccountPresenter(private var convertView: ConvertAccountView, private val myInteractorConvert: ConvertAccountInteractor)
: ConvertAccountInteractor.OnFinishedListener {

    fun getData() {
        myInteractorConvert.requestServerData(this)
    }

    override fun onResultSuccess(dataUpdates: Account) {
        var arrUpdates = arrayListOf<String>()
        arrUpdates.add(dataUpdates.convertCount.toString())
        arrUpdates.add(dataUpdates.eurAmmount.toString())
        arrUpdates.add(dataUpdates.usdAmmount.toString())
        arrUpdates.add(dataUpdates.jpyAmmount.toString())
        convertView.setData(arrUpdates)
    }

    override fun onResultFail() {
        convertView.setDataError()
    }

}