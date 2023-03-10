package com.maary.logger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maary.logger.database.Transaction
import java.text.SimpleDateFormat
import java.util.*

class TransactionListAdapter(private val onItemClick: (Int) -> Boolean, private val onItemLongClick: (Int) -> Boolean) : ListAdapter<Transaction, TransactionListAdapter.TransactionViewHolder>(TransactionsComparator()) {

    class TransactionViewHolder(itemView: View, onClick: (Int) -> Boolean ,onLongClick: (Int) -> Boolean) : RecyclerView.ViewHolder(itemView) {
        private val transactionAmountView: TextView = itemView.findViewById(R.id.transaction_amount)
        private val transactionTimeView: TextView = itemView.findViewById(R.id.transaction_time)
        private val transactionTypeView: TextView = itemView.findViewById(R.id.transaction_type)

        init {
            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
            itemView.setOnLongClickListener {
                val time = transactionTimeView.text
                val id = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(time as String)
                onLongClick((id!!.time/1000).toInt())
            }
        }

        fun bind(amount:Double?, time:Int?, type:String?) {
            transactionAmountView.text = amount.toString()
            transactionTimeView.text =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault())
                    .format(time!!.toLong()*1000)
            transactionTypeView.text = type
        }

        companion object {
            fun create(parent: ViewGroup, onClick: (Int) -> Boolean, onLongClick: (Int) -> Boolean) : TransactionViewHolder {
                val view:View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.transaction_item, parent, false)
                return TransactionViewHolder(view, onClick ,onLongClick)
            }
        }
    }

    class TransactionsComparator : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.time == newItem.time
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder.create(parent, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.trans, current.time, current.type)
    }
}