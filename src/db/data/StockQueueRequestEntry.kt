package db.data

import main.sql_db_stock

data class StockQueueRequestEntry(val id: Long, val company: Int, val year: Int, val month: Int, val status: Status) {
    companion object {
        fun getOpenRequest(): StockQueueRequestEntry? {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            val res = s.executeQuery("select * from stock.claim_stock_request();")
            if (res.next()) {
                return StockQueueRequestEntry(
                    res.getLong("b"),
                    res.getInt("e"),
                    res.getInt("c"),
                    res.getInt("d"),
                    getStatus(res.getInt("f"))!!
                )
            }
            res.close()
            s.close()
            c.close()
            return null
        }

        fun isOpenRequestAvailable(): Boolean {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            var o = false
            val res = s.executeQuery("select count(status) from stock.queue_request_stock where status = 0 limit 1;")
            if (res.next()) {
                o = res.getLong("count") > 0
            }
            res.close()
            s.close()
            c.close()
            return o
        }
    }
}
