package io.github.skarware.exchangerates;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;

@SpringBootApplication
public class ExchangeRatesAngularSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRatesAngularSpringApplication.class, args);
    }

    /**
     * Bean to expose in-memory H2 database via a TCP port. By default, in-memory H2 database can be only accessed within the same virtual machine and class loader.
     * However, the org.h2.tools.Server class allows to access in-memory database from external applications running on the same host or remote hosts.
     * The database can be accessed on the jdbc:h2:tcp://localhost:9092/mem:db_name path, using jdbc driver (org.h2.Driver).
     * NOTICE: To use the org.h2.tools.Server class during code build Maven H2 dependency must be set to compile scope.
     *
     * @return org.h2.tools.Server
     * @throws SQLException
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }
}
