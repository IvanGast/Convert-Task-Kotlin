package app.bankconvert.page.convert

interface ConvertAccountView {
    fun setData(updatedCount: Int)
    fun setDataError()
    fun displayError(message: String)
    fun handleResponse(addition: Double, amount: String, someConvert: Convert)
    fun handleError(error: Throwable)
}