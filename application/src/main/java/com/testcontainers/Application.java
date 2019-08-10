package com.testcontainers;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetSocketAddress;

@SpringBootApplication
@RestController
public class Application {

  @GetMapping("/touch")
  public String greeting() throws Exception {
    try (Cluster awaitCluster = Cluster.builder().addContactPointsWithPorts(new InetSocketAddress("cassandra", 9042))
        .build();
         Session awaitSession = awaitCluster.newSession()) {
      ResultSet rs = awaitSession.execute("select release_version from system.local");
      return rs.one().getString("release_version");
    }
  }


  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
