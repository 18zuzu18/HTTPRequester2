package http

import main.logger
import main.reqKey
import main.sql_db_stock
import java.sql.Date
import java.time.LocalDateTime
import java.util.*
import java.util.logging.Level

class AvailableRequests {
    private var day = AvailableRequestsDay(500, LocalDateTime.now().dayOfYear)

    init {
        val c = sql_db_stock.connection
        val stm = c.prepareStatement("select * from stock.keys_day_usage where date = ? and id_key = ${reqKey.id};")
        val now = LocalDateTime.now()
        val cal: Calendar = Calendar.getInstance()
        cal.set(now.year, now.monthValue - 1, now.dayOfMonth)
        stm.setDate(1, Date(cal.timeInMillis))
        val res = stm.executeQuery()
        if (res.next()) {
            day = AvailableRequestsDay(500, LocalDateTime.now().dayOfYear, res.getInt("count"))
            res.close()
        } else {
            val co = sql_db_stock.connection
            val s = co.prepareStatement("insert into stock.keys_day_usage (id_key, date, count) VALUES (${reqKey.id}, ?, 0)")
            s.setDate(1, Date(cal.timeInMillis))
            s.executeUpdate()
            s.close()
            co.close()
        }
        stm.close()
        c.close()
    }

    fun claimRequest(): Boolean {
        return if (isAvailable()) {
            day.claimRequest()
            true
        } else {
            false
        }
    }

    fun isAvailable(): Boolean {
        checkDay()
        return day.isAvailable()
    }

    private fun checkDay() {
        if (day.day != LocalDateTime.now().dayOfYear) {
            val now = LocalDateTime.now()
            val cal: Calendar = Calendar.getInstance()
            cal.set(now.year, now.monthValue - 1, now.dayOfMonth)

            val co = sql_db_stock.connection
            val s = co.prepareStatement("insert into stock.keys_day_usage (id_key, date, count) VALUES (${reqKey.id}, ?, 0)")
            s.setDate(1, Date(cal.timeInMillis))
            s.executeUpdate()
            s.close()
            co.close()
            day = AvailableRequestsDay(500, now.dayOfYear)
        }
    }
}

class AvailableRequestsMinute(private val numMax: Int, val minute: Int, private var numCurrent: Int = 0) {
    fun claimRequest(): Boolean {
        return if (isAvailable()) {
            numCurrent++
            true
        } else {
            false
        }
    }

    fun isAvailable(): Boolean {
        return numCurrent < numMax
    }
}

class AvailableRequestsDay(private val numMax: Int, val day: Int, private var numCurrent: Int = 0) {
    private var minute = AvailableRequestsMinute(4, LocalDateTime.now().minute)

    fun claimRequest(): Boolean {
        check()
        if (!isAvailable()) {
            return false
        }

        return if (minute.claimRequest()) {
            numCurrent++
            val c = sql_db_stock.connection
            val s = c.createStatement()
            s.executeUpdate("update stock.keys_day_usage set count = count + 1 where id_key = ${reqKey.id}")
            s.close()
            c.close()
            logger.log(Level.INFO, "used requests $numCurrent")
            true
        } else {
            false
        }
    }

    fun isAvailable(): Boolean {
        check()
        return numCurrent < numMax && minute.isAvailable()
    }

    private fun check() {
        val now = LocalDateTime.now().minute
        if (minute.minute != now) {
            minute = AvailableRequestsMinute(4, now)
        }
    }
}