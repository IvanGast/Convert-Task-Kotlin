package app.bankconvert.setup

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class ApplicationSetup: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        var config = RealmConfiguration.Builder()
        config.name("realmAccount")
        config.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(config.build())
    }
}