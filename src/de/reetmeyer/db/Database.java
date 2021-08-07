package de.reetmeyer.db;

import de.reetmeyer.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Database {

    Connection c = null;

    public Database(String host, int port, String user, String password, String database) {
        Main.logger.log(Level.INFO, "Connecting to Database");
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database,
                            user, password);
            Main.logger.log(Level.INFO, "Connected to Database");
        } catch (ClassNotFoundException e) {
            Main.logger.log(Level.SEVERE, "Cant find JDBC Postgres Class");
            e.printStackTrace();
        } catch (SQLException throwables) {
            Main.logger.log(Level.SEVERE, "Could not connect to Database");
            throwables.printStackTrace();
        }

    }

    public int addRequest(String path) {
        try {
            PreparedStatement smt = c.prepareStatement("INSERT INTO requests (path, status) VALUES (?, 0) RETURNING id");
            smt.setString(1, path);
            ResultSet rs = smt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Main.logger.log(Level.SEVERE, "Failed to add Request to DB; Data: path:" + path);
        }
        return -1;
    }

    public List<DbRequest> getAllOpenRequests() {
        ArrayList<DbRequest> requests = new ArrayList<>();
        try {
            Statement smt = c.createStatement();
            ResultSet rs = smt.executeQuery("SELECT id,path,status FROM requests WHERE status = 0");
            while (rs.next()) {
                requests.add(new DbRequest(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return requests;
    }

    public DbRequest getOneOpenRequests() {
        try {
            Statement smt = c.createStatement();
            ResultSet rs = smt.executeQuery("SELECT id,path,status FROM requests WHERE status = 0 LIMIT 1");
            rs.next();
            return new DbRequest(rs.getInt(1), rs.getString(2), rs.getInt(3));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    public void setRequestToPossessing(int id){
        try {
            Statement smt = c.createStatement();
            smt.executeUpdate("UPDATE requests SET status = 1 WHERE id = " + id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setResponse(DbResponse response) {
        try {
            Statement smt = c.createStatement();
            smt.executeUpdate("UPDATE requests SET status = 2 WHERE id = " + response.id);
            PreparedStatement psmt = c.prepareStatement("INSERT INTO responses (id, code, response) VALUES (?,?,?) RETURNING responsetime");
            psmt.setInt(1, response.id);
            psmt.setInt(2, response.code);
            psmt.setString(3, response.response);
            ResultSet rs = psmt.executeQuery();
            rs.next();
            response.responseTime = rs.getTimestamp(1).toLocalDateTime();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int countRequests(long seconds) {
        try {
            Statement smt = c.createStatement();
            ResultSet rs = smt.executeQuery("SELECT count(*) FROM responses WHERE responsetime BETWEEN now()::timestamp - (interval '1s') * "+seconds+" AND now()::timestamp");
            rs.next();
            return rs.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -1;
        }
    }



}
