package db

import main.sql_db_stock

fun isCompanyForRequestAvailable(): Boolean {
    val c = sql_db_stock.connection
    val s = c.createStatement()
    val res = s.executeQuery("select count(status) from stock.queue_request_company where status = 0")
    val o = (res.next() && res.getLong("count") > 0)
    res.close()
    s.close()
    c.close()
    return o
}

fun isCompanyForConvertAvailable(): Boolean {
    val c = sql_db_stock.connection
    val s = c.createStatement()
    val res = s.executeQuery("select count(status) from stock.queue_convert_company where status = 0")
    val o = (res.next() && res.getLong("count") > 0)
    res.close()
    s.close()
    c.close()
    return o
}

fun isStockForRequestAvailable(): Boolean {
    val c = sql_db_stock.connection
    val s = c.createStatement()
    val res = s.executeQuery("select count(status) from stock.queue_request_stock where status = 0")
    val o = (res.next() && res.getLong("count") > 0)
    res.close()
    s.close()
    c.close()
    return o
}

fun isStockForConvertAvailable(): Boolean {
    val c = sql_db_stock.connection
    val s = c.createStatement()
    val res = s.executeQuery("select count(status) from stock.queue_convert_stock where status = 0")
    val o = (res.next() && res.getLong("count") > 0)
    res.close()
    s.close()
    c.close()
    return o
}