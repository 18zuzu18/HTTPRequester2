package http

import db.data.Company
import db.data.CompanyQueueConvertEntry
import db.data.CompanyQueueRequestEntry
import main.*
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.logging.Level

//fun requestCompany() {
//    if (!availableRequests.claimRequest()) {
//        return
//    }
//    val con = sql_db_stock.connection
//    val st = con.createStatement()
//    val res = st.executeQuery("select * from stock.claim_company_request();")
//    if (res.next()) {
//        val request = HttpRequest.newBuilder().GET()
//            .uri(URI.create("https://www.alphavantage.co/query?function=OVERVIEW&symbol=${res.getString("b")}$key"))
//            .setHeader("User-Agent", "Java 11 HttpClient").build()
//        logger.log(Level.INFO, request.uri().toString())
//        val id = res.getLong("a")
//        res.close()
//        st.close()
//        try {
//            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
//            if (response.statusCode() == 200) {
//                val sta = con.prepareStatement(
//                    "insert into stock.queue_convert_company (id_request, response, status, status_timestamp) VALUES ($id, '${
//                        response.body().replace("'", "''")
//                    }', default, ?);"
//                )
//                sta.setTimestamp(1, Timestamp(Calendar.getInstance().timeInMillis))
//                if (sta.executeUpdate() == 1) {
//                    val stat = con.createStatement()
//                    stat.executeUpdate("update stock.queue_request_company set status = 2 where id = $id;")
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

fun requestCompany() {
    if (CompanyQueueRequestEntry.isOpenRequestAvailable() && availableRequests.claimRequest()) {
        val reqObj = CompanyQueueRequestEntry.getOpenRequest()
        if (reqObj != null) {
            val request = HttpRequest.newBuilder().GET()
                .uri(URI.create("https://www.alphavantage.co/query?function=OVERVIEW&symbol=${reqObj.symbol}$key"))
                .setHeader("User-Agent", "Java 11 HttpClient").build()
            logger.log(Level.INFO, request.uri().toString())
            try {
                val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                if (response.statusCode() == 200 && isResponseValid(response.body())) {
                    CompanyQueueConvertEntry.setEntry(reqObj.id, response.body())
                }
            } catch (e: Exception) {
                logger.log(Level.WARNING, e.message)
                reqObj.resetStatus()
            }
        }
    }
}