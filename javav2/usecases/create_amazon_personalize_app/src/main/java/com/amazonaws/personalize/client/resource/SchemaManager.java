/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import java.util.List;

import software.amazon.awssdk.services.personalize.model.DatasetSchemaSummary;
import software.amazon.awssdk.services.personalize.model.CreateSchemaRequest;
import software.amazon.awssdk.services.personalize.model.DescribeSchemaRequest;
import software.amazon.awssdk.services.personalize.model.DescribeSchemaResponse;
import software.amazon.awssdk.services.personalize.model.DeleteSchemaRequest;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalize.model.PersonalizeException;
import software.amazon.awssdk.services.personalize.model.ListSchemasRequest;
import software.amazon.awssdk.services.personalize.model.ListSchemasResponse;

public class SchemaManager extends AbstractResourceManager {

    private final String schema;

    public SchemaManager(PersonalizeClient personalize, String name, String schema) {
        super(personalize, name);
        this.schema = schema;
    }

    @Override
    protected String createResourceInternal() {

        try {
            CreateSchemaRequest createSchemaRequest = CreateSchemaRequest.builder()
                    .name(getName())
                    .schema(schema)
                    .build();

            String schemaArn = getPersonalize().createSchema(createSchemaRequest).schemaArn();

            System.out.println("Schema arn: " + schemaArn);

            return schemaArn;

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected void deleteResourceInternal(String arn) {
        try {
            DeleteSchemaRequest deleteSchemaRequest = DeleteSchemaRequest
                    .builder()
                    .schemaArn(getArnForResource(getName()))
                    .build();

            getPersonalize().deleteSchema(deleteSchemaRequest);

        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    @Override
    protected String getResourceStatus(String arn) {

        try {
            DescribeSchemaRequest describeSchemaRequest = DescribeSchemaRequest.builder()
                    .schemaArn(arn)
                    .build();
            DescribeSchemaResponse dsr = getPersonalize().describeSchema(describeSchemaRequest);
            return dsr.schema() != null ? "ACTIVE" : null;
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    @Override
    protected String getArnForResource(String name) {
        try {
            ListSchemasRequest listSchemasRequest = ListSchemasRequest.builder()
                    .maxResults(100)
                    .build();
            ListSchemasResponse lsr = getPersonalize().listSchemas(listSchemasRequest);
            List<DatasetSchemaSummary> list = lsr.schemas();
            for (DatasetSchemaSummary ss : list) {
                if (ss.name().equals(name)) {
                    return ss.schemaArn();
                }
            }
        } catch (PersonalizeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

}
