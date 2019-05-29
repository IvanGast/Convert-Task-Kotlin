package app.bankconvert.presenter.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.bankconvert.realm.entities.Balance
import app.page.bankconvert.R
import kotlinx.android.synthetic.main.account_list_item.view.*
import java.text.DecimalFormat

class AccountListAdapter(val dataSource: List<Balance>) : RecyclerView.Adapter<AccountListAdapter.ViewItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewItemHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.account_list_item, parent, false)
        return ViewItemHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSource.size    }

    override fun onBindViewHolder(itemHolder: ViewItemHolder, position: Int) {
        val item = getItem(position)
        val df = DecimalFormat("#.##")
        itemHolder.amount.text = df.format(item.amount.toDouble()).toString()
        itemHolder.currency.text = item.currency
    }

    private fun getItem(position: Int): Balance {
        return dataSource[position]
    }

    class ViewItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount = view.amountTextView
        val currency = view.currencyTextView
    }
}