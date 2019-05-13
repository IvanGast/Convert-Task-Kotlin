package app.bankconvert.realm.entities

import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

open class Account : RealmObject() {
    @PrimaryKey open var id: Int = 1
    open var  eurAmmount: Double = 0.0
    open var usdAmmount: Double = 0.0
    open var jpyAmmount: Double = 0.0
    open var convertCount: Int = 0
}