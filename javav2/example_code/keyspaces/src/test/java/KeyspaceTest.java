// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.datastax.oss.driver.api.core.CqlSession;
import com.example.keyspace.HelloKeyspaces;
import org.junit.jupiter.api.Tag;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.keyspaces.KeyspacesClient;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KeyspaceTest {
     private static KeyspacesClient keyClient;
    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        keyClient = KeyspacesClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void helloTest()  {
        HelloKeyspaces.listKeyspaces(keyClient);
    }

}
