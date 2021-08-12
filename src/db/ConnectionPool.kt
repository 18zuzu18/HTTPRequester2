package db

import com.zaxxer.hikari.HikariDataSource
import java.sql.SQLException
import com.zaxxer.hikari.HikariConfig
import main.logger
import java.sql.Connection
import java.util.logging.Level

class ConnectionPool {
    private val ds: HikariDataSource

    @get:Throws(SQLException::class)
    val connection: Connection
        get() = ds.connection

    init {
        logger.log(Level.INFO, "Start HikariCP and connect to Database")
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:postgresql://192.168.178.117:5432/stock"
        config.username = "stocker"
        config.password = "abc123.."
        config.maximumPoolSize = 100
        config.minimumIdle = 3
        ds = HikariDataSource(config)

        try {
            logger.log(Level.INFO, "Connected to Database")
        } catch (e: ClassNotFoundException) {
            logger.log(Level.SEVERE, "Cant find JDBC Postgres Class")
            e.printStackTrace()
        } catch (throwables: SQLException) {
            logger.log(Level.SEVERE, "Could not connect to Database")
            throwables.printStackTrace()
        }
    }
}
