package app.bankconvert.dagger.module

import android.content.Context
import app.bankconvert.page.convert.ConvertAccountView
import app.bankconvert.presenter.interactor.ConvertAccountInteractor
import app.bankconvert.presenter.presenter.ConvertAccountPresenter
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
@Suppress("unused")
class ConvertPresenterModule {

    @Provides
    fun presenter(contextConvert: ConvertAccountView, convertInteractorConvert: ConvertAccountInteractor): ConvertAccountPresenter{
        return ConvertAccountPresenter(contextConvert, convertInteractorConvert)
    }

    @Provides
    fun contextConvertFactory(context: Context): ConvertAccountView {
        return context as ConvertAccountView
    }

    @Provides
    fun convertInteractorFactory(): ConvertAccountInteractor{
        return ConvertAccountInteractor()
    }


}