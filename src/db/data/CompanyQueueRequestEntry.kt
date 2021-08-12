package db.data

import main.sql_db_stock

class CompanyQueueRequestEntry(val id: Long, val symbol: String, status: Status) {
    fun resetStatus() {
        val c = sql_db_stock.connection
        val s = c.createStatement()
        s.executeUpdate("update stock.queue_request_company set status = 0 where id = $id")
        s.close()
        c.close()
    }

    companion object {
        fun getOpenRequest(): CompanyQueueRequestEntry? {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            val res = s.executeQuery("select * from stock.claim_company_request();")
            if (res.next()) {
                return CompanyQueueRequestEntry(
                    res.getLong("a"),
                    res.getString("b"),
                    getStatus(res.getInt("c"))!!
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
            val res = s.executeQuery("select count(status) from stock.queue_request_company where status = 0 limit 1;")
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