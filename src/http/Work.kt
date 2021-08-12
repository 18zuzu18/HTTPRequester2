package http

import db.data.Company
import db.data.CompanyQueueConvertEntry
import db.data.StockQueueConvertEntry
import main.logger
import main.sql_db_stock
import org.json.JSONObject
import java.sql.Timestamp
import java.util.*
import java.util.logging.Level

fun convertCompany() {
    val entry = CompanyQueueConvertEntry.getOpenConvert()
    if (entry != null) {
        logger.log(Level.INFO, "convert Company")
        val json = JSONObject(entry.response)
        val c1 = sql_db_stock.connection
        val s1 = c1.createStatement()
        try {
            Company.setCompany(Company(entry.id, json.get("Symbol") as String))
            var values = ""
            values += ",(default, ${entry.id}, 2019, 10, default)"
            values += ",(default, ${entry.id}, 2019, 11, default)"
            values += ",(default, ${entry.id}, 2019, 12, default)"
            values += ",(default, ${entry.id}, 2020, 1, default)"
            values += ",(default, ${entry.id}, 2020, 2, default)"
            values += ",(default, ${entry.id}, 2020, 3, default)"
            values += ",(default, ${entry.id}, 2020, 4, default)"
            values += ",(default, ${entry.id}, 2020, 5, default)"
            values += ",(default, ${entry.id}, 2020, 6, default)"
            values += ",(default, ${entry.id}, 2020, 7, default)"
            values += ",(default, ${entry.id}, 2020, 8, default)"
            values += ",(default, ${entry.id}, 2020, 9, default)"
            values += ",(default, ${entry.id}, 2020, 10, default)"
            values += ",(default, ${entry.id}, 2020, 11, default)"
            values += ",(default, ${entry.id}, 2020, 12, default)"
            values += ",(default, ${entry.id}, 2021, 1, default)"
            values += ",(default, ${entry.id}, 2021, 2, default)"
            values += ",(default, ${entry.id}, 2021, 3, default)"
            values += ",(default, ${entry.id}, 2021, 4, default)"
            values += ",(default, ${entry.id}, 2021, 5, default)"
            values += ",(default, ${entry.id}, 2021, 6, default)"
            values += ",(default, ${entry.id}, 2021, 7, default)"

            if (s1.executeUpdate(
                    "insert into stock.queue_request_stock (id, id_company, year, month, status) VALUES ${
                        values.substring(
                            1
                        )
                    };"
                ) > 1
            ) {
                s1.executeUpdate("update stock.queue_convert_company set status = 2 where id_request = ${entry.id}")
            }
        } catch (e: Exception) {
            logger.log(Level.WARNING, e.message)
        }
        s1.close()
        c1.close()
    }
}

fun convertStock() {
    val entry = StockQueueConvertEntry.getOpenConvert()
    var values = ""
    if (entry != null) {
        logger.log(Level.INFO, "convert Stock")
        val rows = entry.response.replace("\r", "").split('\n')
        val dates = mutableListOf<Timestamp>()
        for (i in rows.indices) {
            if (i > 0 && rows[i].length > 10 && rows[i].contains(',')) {
                try {
                    val cols = rows[i].split(',')
                    values += ",(${entry.idCompany}, ?, ${cols[1]}, ${cols[2]}, ${cols[3]}, ${cols[4]}, ${cols[5]})"
                    dates.add(convertTimeStamp(cols[0]))
                } catch (e: Exception) {
                    logger.log(Level.WARNING, e.message)
                    logger.log(Level.WARNING, e.stackTraceToString())
                }
            }
        }
        try {
            val c1 = sql_db_stock.connection
            val s1 = c1.prepareStatement(
                "insert into stock.stock (id_company, time, open, high, low, close, volume) VALUES ${
                    values.substring(1)
                }"
            )
            for (i in dates.indices) {
                s1.setTimestamp(i + 1, dates[i])
            }
            if (s1.executeUpdate() > 1) {
                s1.close()
                val s2 = c1.createStatement()
                s2.executeUpdate("update stock.queue_convert_stock set status = 2 where id_convert = ${entry.idConvert};")
                s2.close()
            }
            c1.close()
            s1.close()
        } catch (e: Exception) {
            logger.log(Level.WARNING, e.message)
        }
    }
}

fun convertTimeStamp(str: String): Timestamp {
    val calendar = Calendar.getInstance()
    calendar.set(
        str.substring(0, 4).toInt(),
        str.substring(5, 7).toInt(),
        str.substring(8, 10).toInt(),
        str.substring(11, 13).toInt(),
        str.substring(14, 16).toInt()
    )
    return Timestamp(calendar.timeInMillis)
}