package app.page.bankconvert

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.DecimalFormat

class MyAdapter(private val context: Context,
                private val dataSource: ArrayList<MyHistoryItem>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item, parent, false)

        val amountTextView = rowView.findViewById(R.id.amountTextView) as TextView
        val commissionTextView = rowView.findViewById(R.id.commissionTextView) as TextView
        val timeTextView = rowView.findViewById(R.id.timeTextView) as TextView

        val df = DecimalFormat("#.##")
        val dff = DecimalFormat("#.###")


        val item = getItem(position) as MyHistoryItem
        amountTextView.text = "Konvertuota " + df.format(item.myAmount.toDouble()) + " " + item.fromCurr + " į " + df.format(item.convertedAmount.toDouble()) + " " + item.toCurr
        commissionTextView.text = "Komisija : " + dff.format(item.addition.toDouble()) + " " + item.fromCurr
        timeTextView.text = "Laikas : " + item.time

        return rowView
    }
}