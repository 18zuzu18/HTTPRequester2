package de.reetmeyer;

import de.reetmeyer.db.Database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Script {

    public static void main(String[] args) throws IOException {
        File csv = new File("C:\\Users\\minec\\Downloads\\sp500.csv");
        if (!csv.exists()) {
            System.err.println("Faield to find sp500.csv");
            return;
        }
        String file = Files.readString(csv.toPath());
        String[] lines = file.split("\n");
        Main.logger = Logger.getLogger("script");

        Database db = new Database("192.168.178.100", 5432, "requester", "9jO8y2t7hB59e88FCO0mrvwZ1VzOwbF7m42cLrP1pdPwONCJsRpKk7ZfVRvDQVKv", "requests");


        for (int i = 0; i < lines.length; i++) {
            db.addRequest("https://www.alphavantage.co/query?function=OVERVIEW&symbol="+lines[i]);
        }
    }

}
