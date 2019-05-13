package app.bankconvert.dagger

import app.bankconvert.dagger.module.ConvertPresenterModule
import app.bankconvert.dagger.module.RequestModule
import app.bankconvert.dagger.module.MainPresenterModule
import app.bankconvert.presenter.presenter.ConvertAccountPresenter
import app.bankconvert.presenter.presenter.MainAccountPresenter
import app.bankconvert.retrofit.network.RequestInterface
import dagger.Component

@Component(modules = [RequestModule::class, ConvertPresenterModule::class, MainPresenterModule::class])
interface RequestInterfaceComponent {
    fun getRequestService(): RequestInterface
    fun getConvertPresenter(): ConvertAccountPresenter
    fun getMainPresenter(): MainAccountPresenter
}