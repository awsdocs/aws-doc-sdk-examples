//snippet-sourcedescription:[CreateDomainSchema.java demonstrates how to create
// an Amazon Personalize schema for a dataset in a domain dataset group.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Personalize]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[3/5/2022]
//snippet-sourceauthor:[seashman - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.personalize;

//snippet-start:[personalize.java2.create_domain_schema.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.model.CreateSchemaRequest;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
//snippet-end:[personalize.java2.create_domain_schema.import]

public class CreateDomainSchema {

    public static void main(String[] args) {

        final String USAGE = "Usage:\n" +
                "    CreateSchema <name, domain, schemaLocation>\n\n" +
                "Where:\n" +
                "   name - The name for the schema.\n" +
                "   domain - The domain of the dataset's domain dataset group.\n" +
                "   schemaLocation - the location of the schema JSON file.\n\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String schemaName = args[0];
        String domain = args[1];
        String filePath = args[2];


        Region region = Region.US_WEST_2;

        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();

        createDomainSchema(personalizeClient, schemaName, domain, filePath);

        personalizeClient.close();

    }
    //snippet-start:[personalize.java2.create_domain_schema.main]
    public static String createDomainSchema(PersonalizeClient personalizeClient, String schemaName, String domain, String filePath) {

        String schema = null;
        try {
            schema = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            CreateSchemaRequest createSchemaRequest = CreateSchemaRequest.builder()
                    .name(schemaName)
                    .domain(domain)
                    .schema(schema)
                    .build();

            String schemaArn = personalizeClient.createSchema(createSchemaRequest).schemaArn();

            System.out.println("Schema arn: " + schemaArn);

            return schemaArn;

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    //snippet-end:[personalize.java2.create_domain_schema.main]


}
