package http

fun isResponseValid(res: String): Boolean {
    if (res.startsWith("{\\n\"error\": \"Invalid slice parameter. The following slice}") || res.startsWith("{\\n\"Note\": \"Thank you for using Alpha Vantage! Our standard")) {
        return false
    }
    return true
}
