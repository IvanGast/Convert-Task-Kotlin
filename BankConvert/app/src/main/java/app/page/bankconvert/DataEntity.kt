package app.page.bankconvert

class DataEntity {
    private var amount: Double = 0.toDouble()
    private var currency: String? = null

    fun DataEntity(ammount: Double, currency: String) {
        this.amount = ammount
        this.currency = currency
    }

    fun getAmmount(): Double {
        return amount
    }

    fun setAmmount(ammount: Double) {
        this.amount = ammount
    }

    fun getCurrency(): String? {
        return currency
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }
}