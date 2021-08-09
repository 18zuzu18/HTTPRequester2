package de.reetmeyer;

import de.reetmeyer.db.Database;
import de.reetmeyer.db.DbRequest;
import de.reetmeyer.db.DbResponse;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class HttpRequester {

    Database db;
    boolean shouldStop = false;

    final String[] keys = {
            "3EX8ITM32PNARSSK",
            "5WCSEVIIQ88QL7RJ",
            "2J6QAHHLQ4D6Q4WC",
            "4FWMO4YJRB4M00B6",
            "B38ZORQKPYB6JSES",
            "EP4VELBU2N84C86J",
            "SIWA7MXNZ0LGBHBO",
            "HMAFJNLTAASRJ9JH"
    };
    final String[] testKeys = {
            "TS44EBCI0A9B0PIK"
    };

    public HttpRequester(Database db) {
        this.db = db;
    }


    public int run() {

        while (db.getAllOpenRequests().size() > 0 && !shouldStop) {
            // Check for Rate Limit
            for (String key : keys) {
                loop(key);
                if (db.getAllOpenRequests().size() <= 0) break;
            }

        }
        Main.logger.log(Level.INFO, "No More Requests Open. Exiting Application");
        return 0;
    }

    private void loop(String key) {
        Main.logger.log(Level.INFO, "(Loop) (Key: " + key + ") (Requests to do: " + db.getAllOpenRequests().size() + ") (Rate Limit Free: " + rateLimitFree(key) + ")");
        if (checkForRateLimit(key)) return; // Rate Limit

        // Get Open Request
        DbRequest r = db.getOneOpenRequests();

        doRequest(r, key);

    }

    private int rateLimitFree(String key) {
        int a = 5 - db.countRequests(60, key);
        int b = 500 - db.countRequests(60 * 60 * 24, key);
        return Math.min(a, b);
    }

    private void doRequest(DbRequest r, String key) {
        Main.logger.log(Level.WARNING, "Dont Stop the Program now! Starting Request in 1s");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } // Wait 5s
        db.setRequestToPossessing(r.id);

        // Do Request
        String path = r.path + "&apikey=" + key;
//        String path = r.path;
        Response response = executeRequest(path);
        System.out.println(path);


        db.setResponse(new DbResponse(r.id, response.httpCode, response.content, key));

        Main.logger.log(Level.INFO, "Request Done ");
    }

    final int waitTime = 500; // in ms

    private boolean checkForRateLimit(String key) {
        if (db.countRequests(60, key) >= 5 || db.countRequests(60 * 60 * 24, key) >= 500) {
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

    private final boolean ssl = true;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    private Response executeRequest(String uri) {
        Response r = new Response();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .setHeader("User-Agent", "Java 11 HttpClient")
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            r.httpCode = response.statusCode();
            r.content = response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return r;
    }

    private class Response {
        String content;
        int httpCode;
    }
}
