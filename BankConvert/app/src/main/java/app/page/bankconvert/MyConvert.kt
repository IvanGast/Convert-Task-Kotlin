package app.page.bankconvert

class MyConvert {
    private var fromCurr: String? = null
    private var toCurr: String? = null
    private var myAmount: String? = null

    fun MyConvert() {
        this.fromCurr = null
        this.toCurr = null
        this.myAmount = null
    }

    fun getMyAmount(): String {
        return myAmount.toString()
    }
    fun setMyAmount(myAmount: String) {
        this.myAmount = myAmount
    }

    fun getToCurr(): String {
        return toCurr.toString()
    }
    fun setToCurr(toCurr: String) {
        this.toCurr = toCurr
    }

    fun getFromCurr(): String {
        return fromCurr.toString()
    }
    fun setFromCurr(fromCurr: String) {
        this.fromCurr = fromCurr
    }
}