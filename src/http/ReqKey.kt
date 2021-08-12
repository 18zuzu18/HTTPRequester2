package http

import main.sql_db_stock
import java.security.InvalidParameterException

class ReqKey {
    var key: String = ""
    var id: Int = -1

    init {
        val c = sql_db_stock.connection
        val s = c.createStatement()
        val res = s.executeQuery("select * from stock.keys where assign = 'eric'")
        var ok = false
        if (res.next()) {
            ok = true
            key = res.getString("key")
            id = res.getInt("id")
            res.close()
        }
        s.close()
        c.close()
        if (!ok) {
            throw InvalidParameterException("key for user eric doesn't found")
        }
    }
}