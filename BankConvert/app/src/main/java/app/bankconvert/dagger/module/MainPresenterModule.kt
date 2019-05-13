package app.bankconvert.dagger.module

import android.content.Context
import app.bankconvert.page.main.MainAccountView
import app.bankconvert.presenter.interactor.MainAccountInteractor
import app.bankconvert.presenter.presenter.MainAccountPresenter
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
@Suppress("unused")
class MainPresenterModule {

    @Provides
    fun presenter(contextMain: MainAccountView, mainInteractorConvert: MainAccountInteractor): MainAccountPresenter {
        return MainAccountPresenter(contextMain, mainInteractorConvert)
    }

    @Provides
    fun contextMainFactory(context: Context): MainAccountView {
        return context as MainAccountView
    }

    @Provides
    fun mainInteractorFactory(): MainAccountInteractor {
        return MainAccountInteractor()
    }


}