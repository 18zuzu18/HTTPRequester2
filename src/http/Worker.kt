package http

import db.isCompanyForConvertAvailable
import db.isCompanyForRequestAvailable
import db.isStockForConvertAvailable
import db.isStockForRequestAvailable
import main.availableRequests
import main.logger
import java.util.logging.Level

class Worker {
    val runnable = Runnable {
        while (!stop) {
            if (isCompanyForConvertAvailable()) {
                convertCompany()
            }
            if (isStockForConvertAvailable()) {
                convertStock()
            }
            Thread.sleep(100)
        }
    }

    val stop = false
    val worker2 = Thread(runnable)

    fun work() {
        worker2.start()

        while (!stop) {
            while (availableRequests.isAvailable()) {
                if (isCompanyForRequestAvailable()) {
                    logger.log(Level.INFO, "Request for Company Information Available")
                    requestCompany()
                } else if (isStockForRequestAvailable()) {
                    logger.log(Level.INFO, "Request for Stock Information Available")
                    requestStock()
                }
            }
            Thread.sleep(50)
        }
    }
}