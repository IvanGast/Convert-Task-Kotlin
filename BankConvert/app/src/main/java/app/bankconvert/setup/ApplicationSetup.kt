package app.bankconvert.setup

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class ApplicationSetup: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        var c = RealmConfiguration.Builder()
        c.name("realmAccount")
        c.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(c.build())
    }
}