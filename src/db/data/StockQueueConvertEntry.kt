package db.data

import main.sql_db_stock
import java.sql.Timestamp

data class StockQueueConvertEntry(
    val idCompany: Int,
    val response: String,
    val status: Status,
    val statusTimestamp: Timestamp,
    val idConvert: Long
) {
    companion object {
        fun getOpenConvert(): StockQueueConvertEntry? {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            val res = s.executeQuery("select * from stock.claim_stock_convert();")
            if (res.next()) {
                return StockQueueConvertEntry(
                    res.getInt("a"),
                    res.getString("b"),
                    getStatus(res.getInt("d"))!!,
                    res.getTimestamp("e"),
                    res.getLong("c")
                )
            }
            res.close()
            s.close()
            c.close()
            return null
        }

        fun setEntry(entry: StockQueueConvertEntry) {
            val c = sql_db_stock.connection
            val s =
                c.prepareStatement("insert into stock.queue_convert_stock (id_company, response, status, status_timestamp, id_convert) VALUES (${entry.idCompany}, '${entry.response}', ${entry.status.id}, ?, ${
                    if (entry.idConvert.toInt() == -1) {
                        "default"
                    } else {
                        entry.idConvert
                    }
                });")
            s.setTimestamp(1, entry.statusTimestamp)
            s.executeUpdate()
            s.close()
            c.close()
        }
    }
}
