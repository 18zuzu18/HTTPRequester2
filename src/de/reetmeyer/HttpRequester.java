package de.reetmeyer;

import de.reetmeyer.db.Database;
import de.reetmeyer.db.DbRequest;
import de.reetmeyer.db.DbResponse;

import java.util.logging.Level;

public class HttpRequester {

    Database db;
    boolean shouldStop = false;

    public HttpRequester(Database db) {
        this.db = db;
    }


    public int run() {

        while (db.getAllOpenRequests().size() > 0 && !shouldStop) {
            // Check for Rate Limit
            loop();

        }
        Main.logger.log(Level.INFO, "No More Requests Open. Exiting Application");
        return 0;
    }

    private void loop() {
        Main.logger.log(Level.INFO, "(Loop) Requests to do: " + db.getAllOpenRequests().size() + "; Rate Limit Free: " + rateLimitFree());
        if (checkForRateLimit()) return; // Rate Limit

        // Get Open Request
        DbRequest r = db.getOneOpenRequests();

        doRequest(r);

    }

    private int rateLimitFree() {
        int a = 5 - db.countRequests(60);
        int b = 500 - db.countRequests(60 * 60 * 24);
        return Math.min(a, b);
    }

    private void doRequest(DbRequest r) {
        Main.logger.log(Level.WARNING, "Dont Stop the Program now! Starting Request in 1s"); // TODO Change to 5-10s
        try {
            Thread.sleep(500); // TODO Change to same 5-10s
        } catch (InterruptedException e) {
            e.printStackTrace();
        } // Wait 5s
        db.setRequestToPossessing(r.id);

        // Do Request
        // TODO


        db.setResponse(new DbResponse(r.id, 200, "Test" + System.currentTimeMillis()));

        Main.logger.log(Level.INFO, "Request Done ");
    }

    final int waitTime = 500; // in ms

    private boolean checkForRateLimit() {
        if (db.countRequests(60) >= 5 || db.countRequests(60 * 60 * 24) >= 500) {
            Main.logger.log(Level.INFO, "Rate Limit Reached. Re-Checking in " + ((float) waitTime / 1000) + "sec");
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
