package db.data

import java.sql.Timestamp
import java.time.format.DateTimeFormatter

data class StockEntry(
    val idCompany: Int,
    val time: Timestamp,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Long
) {
    fun toValueString(): String {
        return "($idCompany, ${
            time.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"))
        }, $open, $high, $low, $close, $volume)"
    }
}
