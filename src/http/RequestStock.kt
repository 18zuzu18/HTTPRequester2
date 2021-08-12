package http

import db.data.Company
import db.data.Status
import db.data.StockQueueConvertEntry
import db.data.StockQueueRequestEntry
import main.*
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.logging.Level
import kotlin.math.floor

//fun requestStock() {
//    if (!availableRequests.claimRequest()) {
//        return
//    }
//    val con = sql_db_stock.connection
//    val st = con.createStatement()
//    val res = st.executeQuery("select * from stock.claim_stock_request();")
//    if (res.next()) {
//        val url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY_EXTENDED&symbol=${
//            res.getString(
//                "a"
//            )
//        }&interval=1min&slice=${buildDateSlice(res.getInt("c"), res.getInt("d"))}$key"
//        val request = HttpRequest.newBuilder().GET().uri(URI.create(url))
//            .setHeader("User-Agent", "Java 11 HttpClient").build()
//        logger.log(Level.INFO, url)
//        val com_id = res.getLong("e")
//        val id = res.getLong("b")
//        res.close()
//        st.close()
//        try {
//            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
//            if (response.statusCode() == 200) {
//                val sta =
//                    con.prepareStatement("insert into stock.queue_convert_stock (id_company, response, status, status_timestamp, id_convert) VALUES ($com_id, '${response.body()}', default, (select current_timestamp), default)")
//                if (sta.executeUpdate() == 1) {
//                    val stat = con.createStatement()
//                    stat.executeUpdate("update stock.queue_request_stock set status = 2 where id = $id")
//                    stat.close()
//                }
//                sta.close()
//            }
//        } catch (e: Exception) {
//            logger.log(Level.WARNING, e.message)
//        } finally {
//            con.close()
//        }
//    }
//}

fun requestStock() {
    if (StockQueueRequestEntry.isOpenRequestAvailable() && availableRequests.claimRequest()) {
        val entry = StockQueueRequestEntry.getOpenRequest()
        if (entry != null) {
            val company = Company.getCompany(entry.company)
            if (company != null) {
                val url =
                    "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY_EXTENDED&symbol=${company.symbol}&interval=1min&slice=${
                        buildDateSlice(
                            entry.year,
                            entry.month
                        )
                    }$key"
                val request = HttpRequest.newBuilder().GET().uri(URI.create(url))
                    .setHeader("User-Agent", "Java 11 HttpClient").build()
                logger.log(Level.INFO, url)
                try {
                    val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                    if (response.statusCode() == 200 && isResponseValid(response.body())) {
                        StockQueueConvertEntry.setEntry(
                            StockQueueConvertEntry(
                                entry.company,
                                response.body().replace("'", "''"),
                                Status.Open,
                                Timestamp(System.currentTimeMillis()),
                                -1
                            )
                        )
                        val con = sql_db_stock.connection
                        val stat = con.createStatement()
                        stat.executeUpdate("update stock.queue_request_stock set status = 2 where id = ${entry.id}")
                        stat.close()
                        con.close()
                    }
                } catch (e: Exception) {
                    logger.log(Level.WARNING, e.message)
                    logger.log(Level.WARNING, e.stackTraceToString())
                }
            }
        }
    }
}

fun buildDateSlice(year: Int, month: Int): String {
    val now = LocalDateTime.now()
    val cYear = now.year
    val cMonth = now.monthValue

    var diffMonth = (cYear * 12 + cMonth) - (year * 12 + month)
    val nYear = floor((diffMonth / 12).toDouble()).toInt()
    diffMonth -= nYear * 12

    return "year${nYear + 1}month${diffMonth + 1}"
}