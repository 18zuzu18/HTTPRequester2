package db.data

import main.sql_db_stock

data class Company(val id: Long, val symbol: String) {
    companion object {
        fun getCompany(id: Int): Company? {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            val res = s.executeQuery("select * from stock.company where id = $id limit 1;")
            if (res.next()) {
                return Company(
                    res.getInt("id").toLong(),
                    res.getString("symbol")
                )
            }
            res.close()
            s.close()
            c.close()
            return null
        }

        fun getCompany(symbol: String): Company? {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            val res = s.executeQuery("select * from stock.company where symbol = $symbol limit 1;")
            if (res.next()) {
                return Company(
                    res.getInt("id").toLong(),
                    res.getString("symbol")
                )
            }
            res.close()
            s.close()
            c.close()
            return null
        }

        fun setCompany(company: Company) {
            val c = sql_db_stock.connection
            val s = c.createStatement()
            s.executeUpdate("insert into stock.company (id, symbol) VALUES (${company.id}, '${company.symbol}');")
            s.close()
            c.close()
        }
    }
}
