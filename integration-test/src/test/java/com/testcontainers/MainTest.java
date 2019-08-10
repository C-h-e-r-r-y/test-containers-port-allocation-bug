package com.testcontainers;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * @author nsheremet
 */
public class MainTest {

  private static final int CASSANDRA_PORT = 9042;
  private static final int APPLICATION_PORT = 8084;
  private static final String CASSANDRA_NAME = "cassandra";
  private static final String APPLICATION_NAME = "application";
  private DockerComposeContainer container;

  @Before
  public void setUp() throws Exception {
    String file = MainTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    File compose = new File(file).toPath()
        .resolve("docker-compose.yaml").toFile();
    container = new DockerComposeContainer(compose);
    container
        .withExposedService(CASSANDRA_NAME, CASSANDRA_PORT)
        .withExposedService(APPLICATION_NAME, APPLICATION_PORT, Wait.forListeningPort());
    container.start();
    while (true) {
      try (Cluster awaitCluster = Cluster
          .builder().addContactPointsWithPorts(
              new InetSocketAddress("localhost", container.getServicePort(CASSANDRA_NAME, CASSANDRA_PORT))).build();
           Session awaitSession = awaitCluster.newSession()) {
        ResultSet rs = awaitSession.execute("select release_version from system.local");
        if (StringUtils.isNotEmpty(rs.one().getString("release_version"))) {
          break;
        }
      } catch (Exception ex) {
        System.out.println("Await Cassandra next step");
        Thread.sleep(5_000);
      }
    }
  }

  @After
  public void tearDown() throws Exception {
    if (container != null) {
      container.stop();
    }
  }

  @Test
  public void first() {

  }

  @Test
  public void second() {

  }
}
