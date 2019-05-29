package app.bankconvert.dagger.module

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
@Suppress("unused")
class ContextModule(internal var context: Context) {
    @Provides
    fun context(): Context {
        return context
    }
}