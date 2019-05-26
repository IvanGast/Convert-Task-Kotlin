package app.bankconvert.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HistoryItem : RealmObject() {
    @PrimaryKey open var id: Int = 1
    open var fromCurr: String = ""
    open var toCurr: String = ""
    open var amount: String = ""
    open var convertedAmount: String = ""
    open var addition: String = ""
    open var time: String = ""
}