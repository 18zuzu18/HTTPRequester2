package db.data

import main.sql_db_stock

class Stock {
    companion object {
        fun addEntries(entries: List<StockEntry>) {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            val values = mutableListOf<String>()
            for (entry in entries) {
                values.add(entry.toValueString())
            }
            s.executeUpdate("insert into stock.stock (id_company, time, open, high, low, close, volume) VALUES ${values.joinToString()}")
            s.close()
            c.close()
        }
    }
}