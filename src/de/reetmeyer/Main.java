package de.reetmeyer;

import de.reetmeyer.db.Database;
import de.reetmeyer.db.DbResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {

    public static Logger logger;

    public static void main(String[] args) {
        logger = Logger.getLogger("HttpRequester2");
        logger.setUseParentHandlers(false);
        logger.addHandler(new Handler() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            @Override
            public void publish(LogRecord record) {

                String message = "[" +
                        format.format(new Date()) +
                        "] [" +
                        record.getLevel().getName() +
                        "] [" +
                        record.getSourceClassName() +
                        "::" +
                        record.getSourceMethodName() +
                        "] " +
                        record.getMessage();
                System.out.println(message);
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });
        logger.log(Level.INFO, "Started Logger");
        Database db = new Database("192.168.178.100", 5432, "requester", "9jO8y2t7hB59e88FCO0mrvwZ1VzOwbF7m42cLrP1pdPwONCJsRpKk7ZfVRvDQVKv", "requests");

        HttpRequester requester = new HttpRequester(db);
        int exitCode = requester.run();
        System.exit(exitCode);
    }

    private static void debug(Database db){
        System.out.println("Add Request:");
        System.out.println(db.addRequest("https://jsonplaceholder.typicode.com/todos/1"));
        System.out.println("Get all Requests:");
        db.getAllOpenRequests().forEach(dbRequest -> {
            System.out.println(dbRequest.toString());
        });
        System.out.println("Get one Request:");
        System.out.println(db.getOneOpenRequests().toString());
        db.setRequestToPossessing(4);
//        System.out.println("Response Test");
//        DbResponse response = new DbResponse(12, 200, "Test");
//        db.setResponse(response);
//        System.out.println(response.responseTime.toString());
        System.out.println("Request in last 60m");
        System.out.println(db.countRequests(60*60));
    }

}
