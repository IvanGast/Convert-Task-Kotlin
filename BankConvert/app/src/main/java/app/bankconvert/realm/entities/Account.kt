package app.bankconvert.realm.entities

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Account : RealmObject() {
    @PrimaryKey open var id: Int = 1
    open var list: RealmList<Balance> = RealmList()
    open var convertCount: Int = 0
}