// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.athena;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.athena.AmazonAthena;
import com.amazonaws.services.athena.AmazonAthenaClientBuilder;

/**
 * AthenaClientFactory
 * -------------------------------------
 * This code shows how to create and configure an Amazon Athena client.
 */
public class AthenaClientFactory {
  /**
   * AmazonAthenaClientBuilder to build Athena with the following properties:
   * - Set the region of the client
   * - Use the instance profile from the EC2 instance as the credentials provider
   * - Configure the client to increase the execution timeout.
   */
  private final AmazonAthenaClientBuilder builder = AmazonAthenaClientBuilder.standard()
      .withRegion(Regions.US_WEST_2)
      .withCredentials(InstanceProfileCredentialsProvider.getInstance())
      .withClientConfiguration(
          new ClientConfiguration().withClientExecutionTimeout(ExampleConstants.CLIENT_EXECUTION_TIMEOUT));

  public AmazonAthena createClient() {
    return builder.build();
  }
}
