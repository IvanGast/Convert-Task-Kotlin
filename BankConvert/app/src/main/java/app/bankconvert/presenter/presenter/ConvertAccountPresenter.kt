package app.bankconvert.presenter.presenter

import app.bankconvert.page.convert.Convert
import app.bankconvert.page.convert.ConvertAccountView
import app.bankconvert.realm.entities.Account
import app.bankconvert.presenter.interactor.ConvertAccountInteractor
import app.bankconvert.realm.entities.HistoryItem
import app.bankconvert.retrofit.model.DataEntity
import com.vicpin.krealmextensions.createOrUpdate
import com.vicpin.krealmextensions.save
import java.text.SimpleDateFormat
import java.util.*

class ConvertAccountPresenter(private var convertView: ConvertAccountView, private val interactorConvert: ConvertAccountInteractor)
: ConvertAccountInteractor.OnFinishedListener {

    private val EUR = "EUR"
    private val USD = "USD"
    private val JPY = "JPY"

    fun getData() {
        interactorConvert.requestServerData(this)
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

    fun onUpdate(account: Account, someConvert: Convert, data: DataEntity, addition: Double) {
        if (someConvert.getFromCurr() == EUR) {
            account.eurAmmount = account.eurAmmount - someConvert.getMyAmount().toDouble() - addition
        } else {
            if (someConvert.getFromCurr() == USD){
                account.usdAmmount = account.usdAmmount - someConvert.getMyAmount().toDouble() - addition
            } else {
                account.jpyAmmount = account.jpyAmmount - someConvert.getMyAmount().toDouble() - addition
            }
        }

        if ( data.currency == EUR) {
            account.eurAmmount = account.eurAmmount + data.amount.toDouble()
        } else {
            if (data.currency == USD) {
                account.usdAmmount = account.usdAmmount + data.amount.toDouble()
            } else {
                account.jpyAmmount = account.jpyAmmount + data.amount.toDouble()
            }
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val date = Calendar.getInstance().time

        var item = HistoryItem()
        item.id = account.convertCount
        item.fromCurr = someConvert.getFromCurr()
        item.toCurr = someConvert.getToCurr()
        item.amount = someConvert.getMyAmount()
        item.convertedAmount = data.amount
        item.addition = addition.toString()
        item.time = sdf.format(date)

        account.convertCount++

        account.createOrUpdate()
        item.save()

        var arrUpdates = arrayListOf<String>()
        arrUpdates.add(account.convertCount.toString())
        arrUpdates.add(account.eurAmmount.toString())
        arrUpdates.add(account.usdAmmount.toString())
        arrUpdates.add(account.jpyAmmount.toString())
        convertView.setData(arrUpdates)
    }

}