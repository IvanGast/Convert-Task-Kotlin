package app.page.bankconvert

import android.os.AsyncTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class GetApiData : AsyncTask<String, Void, DataEntity>() {

        public override fun doInBackground(vararg params: String): DataEntity? {
            val myUrl = "http://api.evp.lt/currency/commercial/exchange/" + params[0] + "-" + params[1] + "/" + params[2] + "/latest"
            var myData: String? = null
            try {
                val url = URL(myUrl)
                val urlConnection = url.openConnection() as HttpURLConnection
                try {
                    val inRd = InputStreamReader(urlConnection.inputStream)
                    val rd = BufferedReader(inRd)
                    myData = rd.readLine()
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val gson = Gson()
            val dataType = object : TypeToken<DataEntity>() {}.type

            return gson.fromJson<DataEntity>(myData, dataType)
        }
}