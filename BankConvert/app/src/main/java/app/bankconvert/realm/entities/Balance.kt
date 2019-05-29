package app.bankconvert.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Balance : RealmObject() {
    @PrimaryKey open var id: Int = 1
    open var amount: String = "0"
    open var currency: String = ""
}