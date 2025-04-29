// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.fleetwise;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotfleetwise.IoTFleetWiseClient;
import software.amazon.awssdk.services.iotfleetwise.model.*;
import software.amazon.awssdk.services.iotfleetwise.paginators.ListSignalCatalogsIterable;

// snippet-start:[iotfleetwise.java2.hello.main]
public class HelloFleetwise {

        public static void main(String[] args) {
            ListSignalCatalogs();
        }

        public static void  ListSignalCatalogs() {
            try (IoTFleetWiseClient fleetWiseClient = IoTFleetWiseClient.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build()) {

                ListSignalCatalogsRequest request = ListSignalCatalogsRequest.builder()
                        .maxResults(10) // Optional: limit per page
                        .build();

                ListSignalCatalogsIterable paginator = fleetWiseClient.listSignalCatalogsPaginator(request);
                boolean found = false;

                for (ListSignalCatalogsResponse response : paginator) {
                    for (SignalCatalogSummary summary : response.summaries()) {
                        found = true;
                        System.out.println("Catalog Name: " + summary.name());
                        System.out.println("ARN: " + summary.arn());
                        System.out.println("Created: " + summary.creationTime());
                        System.out.println("Last Modified: " + summary.lastModificationTime());
                        System.out.println("---------------");
                    }
                }

                if (!found) {
                    System.out.println("No AWS Fleetwise Signal Catalogs were found.");
                }

            } catch (IoTFleetWiseException e) {
                System.err.println("Error listing signal catalogs: " + e.awsErrorDetails().errorMessage());
                throw new RuntimeException(e);
            }
        }
    }
// snippet-end:[iotfleetwise.java2.hello.main]