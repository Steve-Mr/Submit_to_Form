package com.maary.logger.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class Transaction(
    @ColumnInfo(name = "transaction") val trans: Double,
    @ColumnInfo(name = "type") val type: String,
    @PrimaryKey @ColumnInfo(name = "time") val time: Int) {
}