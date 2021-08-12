package db.data

import main.sql_db_stock
import java.sql.Timestamp

class CompanyQueueConvertEntry(val id: Long, val response: String, status: Status, statusTimestamp: Timestamp) {
    companion object {
        fun getOpenConvert(): CompanyQueueConvertEntry? {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            val res = s.executeQuery("select * from stock.claim_company_convert();")
            if (res.next()) {
                return CompanyQueueConvertEntry(
                    res.getLong("a"),
                    res.getString("b"),
                    getStatus(res.getInt("c"))!!,
                    res.getTimestamp("d")
                )
            }
            res.close()
            s.close()
            c.close()
            return null
        }

        fun setEntry(id: Long, response: String) {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            s.executeUpdate("insert into stock.queue_convert_company (id_request, response, status, status_timestamp) VALUES ($id, '${response.replace("'", "''")}', default, (select current_timestamp));")
            s.close()
            c.close()
        }
    }
}