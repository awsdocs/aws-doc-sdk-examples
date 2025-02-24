// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.glue.CfnDatabase;
import software.amazon.awscdk.services.glue.CfnTable;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityResolutionCdkStack extends Stack {
    public EntityResolutionCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EntityResolutionCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        final String jsonGlueTableName = "jsongluetable";
        final String csvGlueTableName = "csvgluetable";

        // 1. Create an S3 bucket for the Glue Data Table
        String uniqueId = UUID.randomUUID().toString().replace("-", ""); // Remove dashes to ensure compatibility

        Bucket erBucket = Bucket.Builder.create(this, "ErBucket")
                .bucketName("erbucket" + uniqueId)
                .versioned(false)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        // 2. Create a Glue database
        CfnDatabase glueDatabase = CfnDatabase.Builder.create(this, "GlueDatabase")
            .catalogId(this.getAccount())
            .databaseInput(CfnDatabase.DatabaseInputProperty.builder()
                .name("entity_resolution_db")
                .build())
            .build();

        // 3. Create a Glue table referencing the S3 bucket
/*        CfnTable glueTable = CfnTable.Builder.create(this, "GlueTable")
            .catalogId(this.getAccount())
            .databaseName(glueDatabase.getRef()) // Ensure Glue Table references the database correctly
            .tableInput(CfnTable.TableInputProperty.builder()
                .name("entity_resolution") // Fixed table name reference
                .tableType("EXTERNAL_TABLE")
                .storageDescriptor(CfnTable.StorageDescriptorProperty.builder()
                    .columns(List.of(
                        CfnTable.ColumnProperty.builder().name("id").type("string").build(), // Fixed: id is a string,
                        CfnTable.ColumnProperty.builder().name("name").type("string").build(),
                        CfnTable.ColumnProperty.builder().name("email").type("string").build()
                    ))
                    .location("s3://" + glueDataBucket.getBucketName() + "/data/") // Append subpath for data
                    .inputFormat("org.apache.hadoop.mapred.TextInputFormat")
                    .outputFormat("org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat")
                    .serdeInfo(CfnTable.SerdeInfoProperty.builder()
                        .serializationLibrary("org.openx.data.jsonserde.JsonSerDe") // Set JSON SerDe
                        .parameters(Map.of("serialization.format", "1")) // Optional: Set the format for JSON
                        .build())
                    .build())
                .build())
            .build();*/

        final CfnTable jsonErGlueTable = createGlueTable(jsonGlueTableName
                , jsonGlueTableName
                , glueDatabase.getRef()
                , Map.of("id", "string", "name", "string", "email", "string")
                , "s3://" + erBucket.getBucketName() + "/jsonData/"
                , "org.openx.data.jsonserde.JsonSerDe");

        // Ensure Glue Table is created after the Database
        jsonErGlueTable.addDependency(glueDatabase);

        final CfnTable csvErGlueTable = createGlueTable(csvGlueTableName
                , csvGlueTableName
                , glueDatabase.getRef()
                , Map.of("id", "string", "name", "string", "email", "string", "phone", "string")
                , "s3://" + erBucket.getBucketName() + "/csvData/"
                , "org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe");
        // Ensure Glue Table is created after the Database
        csvErGlueTable.addDependency(glueDatabase);

        // 4. Create an IAM Role for AWS Entity Resolution
        Role entityResolutionRole = Role.Builder.create(this, "EntityResolutionRole")
            .assumedBy(new ServicePrincipal("entityresolution.amazonaws.com")) // AWS Entity Resolution assumes this role
            .managedPolicies(List.of(
                ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess"),
                ManagedPolicy.fromAwsManagedPolicyName("AWSEntityResolutionConsoleFullAccess"),
                ManagedPolicy.fromAwsManagedPolicyName("AWSGlueConsoleFullAccess"),
                ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSGlueServiceRole")
            ))
            .build();

        new CfnOutput(this, "EntityResolutionRoleArn", CfnOutputProps.builder()
            .value(entityResolutionRole.getRoleArn())
            .description("The ARN of the EntityResolution Role")
            .build());

        // Add custom permissions for Entity Resolution
        entityResolutionRole.addToPolicy(PolicyStatement.Builder.create()
            .actions(List.of(
                "entityresolution:StartMatchingWorkflow",
                "entityresolution:GetMatchingWorkflow"
            ))
            .resources(List.of("*")) // Adjust permissions if needed
            .build());

        // ------------------------ OUTPUTS --------------------------------------
        new CfnOutput(this, "JsonErGlueTableArn", CfnOutputProps.builder()
            .value(createGlueTableArn(jsonErGlueTable, jsonGlueTableName))
            .description("The ARN of the Json Glue Table")
            .build());

        new CfnOutput(this, "CsvErGlueTableArn", CfnOutputProps.builder()
                .value(createGlueTableArn(csvErGlueTable, csvGlueTableName))
                .description("The ARN of the CSV Glue Table")
                .build());

        new CfnOutput(this, "GlueDataBucketName", CfnOutputProps.builder()
            .value(erBucket.getBucketName()) // Outputs the bucket name
            .description("The name of the Glue Data Bucket")
            .build());
    }

    CfnTable createGlueTable(String id, String tableName, String databaseRef, Map<String, String> schemaMap, String dataLocation, String serializationLib){
        return CfnTable.Builder.create(this, id)
                .catalogId(this.getAccount())
                .databaseName(databaseRef) // Ensure Glue Table references the database correctly
                .tableInput(CfnTable.TableInputProperty.builder()
                        .name(tableName) // Fixed table name reference
                        .tableType("EXTERNAL_TABLE")
                        .storageDescriptor(CfnTable.StorageDescriptorProperty.builder()
                                .columns(createColumns(schemaMap))
                                .location(dataLocation) // Append subpath for data
                                .inputFormat("org.apache.hadoop.mapred.TextInputFormat")
                                .outputFormat("org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat")
                                .serdeInfo(CfnTable.SerdeInfoProperty.builder()
                                        .serializationLibrary(serializationLib) // Set JSON SerDe
                                        .parameters(Map.of("serialization.format", "1")) // Optional: Set the format for JSON
                                        .build())
                                .build())
                        .build())
                .build();
    }
    List<CfnTable.ColumnProperty> createColumns(Map<String, String> schemaMap) {
        return schemaMap.entrySet().stream()
                .map(entry -> CfnTable.ColumnProperty.builder()
                        .name(entry.getKey())
                        .type(entry.getValue())
                        .build())
                .toList();
    }

    String createGlueTableArn(CfnTable glueTable, String glueTableName) {
        return String.format("arn:aws:glue:%s:%s:table/%s/%s"
                , this.getRegion()
                , this.getAccount()
                , glueTable.getDatabaseName()
                , glueTableName
        );
    }
}
