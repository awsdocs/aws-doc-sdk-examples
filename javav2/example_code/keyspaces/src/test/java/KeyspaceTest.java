/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.example.keyspace.ScenarioKeyspaces;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.keyspaces.KeyspacesClient;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KeyspaceTest {
    public static final String DASHES = new String(new char[80]).replace("\0", "-") ;
    private static String fileName = "<Replace with the JSON file that contains movie data>" ;
    private static String keyspaceName = "<Replace with the name of the keyspace to create>";
    private static String titleUpdate = "The Family";
    private static int yearUpdate = 2013 ;
    private static String tableName = "Movie" ;
    private static String tableNameRestore = "MovieRestore" ;
    private static KeyspacesClient keyClient;
    private static CqlSession session;

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        keyClient = KeyspacesClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        DriverConfigLoader loader = DriverConfigLoader.fromClasspath("application.conf");
        session = CqlSession.builder()
            .withConfigLoader(loader)
            .build();
   }

    @Test
    @Order(1)
    public void scenarioTest() throws InterruptedException, IOException {
        System.out.println(DASHES);
        System.out.println("1. Create a keyspace.");
        ScenarioKeyspaces.createKeySpace(keyClient, keyspaceName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        Thread.sleep(5000);
        System.out.println("2. Check for keyspace existence.");
        ScenarioKeyspaces.checkKeyspaceExistence(keyClient, keyspaceName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. List up to 10 keyspaces using a paginator.");
        ScenarioKeyspaces.listKeyspacesPaginator(keyClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Create a table with a simple movie data schema, and enable point-in-time recovery.");
        ScenarioKeyspaces.createTable(keyClient, keyspaceName, tableName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Check for the table to be in an Active state.");
        Thread.sleep(6000);
        ScenarioKeyspaces.checkTable(keyClient, keyspaceName, tableName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Use a Cassandra driver to insert some records into the Movie table.");
        Thread.sleep(6000);
        ScenarioKeyspaces.loadData(session, fileName, keyspaceName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Get all records from the Movie table.");
        ScenarioKeyspaces.getMovieData(session, keyspaceName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Get a specific Movie.");
        ScenarioKeyspaces.getSpecificMovie(session, keyspaceName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Get a UTC timestamp for the current time.");
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        System.out.println("DATETIME = " + Date.from(utc.toInstant()));
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("10. Update the table schema to add a watched Boolean column.");
        ScenarioKeyspaces.updateTable(keyClient, keyspaceName, tableName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("11. Update an item as watched.");
        Thread.sleep(10000); // Wait 10 secs for the update.
        ScenarioKeyspaces.updateRecord(session, keyspaceName, titleUpdate, yearUpdate);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("12.  Query for items with watched = True.");
        ScenarioKeyspaces.getWatchedData(session, keyspaceName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("13. Restore the table back to the previous state using the timestamp.");
        System.out.println("Note that the restore operation can take up to 20 minutes.");
        ScenarioKeyspaces.restoreTable(keyClient, keyspaceName, utc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("14. Check for completion of the restore action.");
        Thread.sleep(5000);
        ScenarioKeyspaces.checkRestoredTable(keyClient, keyspaceName, "MovieRestore");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("15. Delete both tables.");
        ScenarioKeyspaces.deleteTable(keyClient, keyspaceName, tableName);
        ScenarioKeyspaces.deleteTable(keyClient, keyspaceName, tableNameRestore);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("16. Confirm that the table was deleted.");
        ScenarioKeyspaces.checkTableDelete(keyClient, keyspaceName, tableName);
        ScenarioKeyspaces.checkTableDelete(keyClient, keyspaceName, tableNameRestore);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("17. Delete the keyspace.");
        ScenarioKeyspaces.deleteKeyspace(keyClient, keyspaceName);
        System.out.println(DASHES);
    }
}








