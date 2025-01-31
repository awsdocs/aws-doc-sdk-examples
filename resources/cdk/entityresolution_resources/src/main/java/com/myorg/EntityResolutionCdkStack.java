// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.s3.*;
import software.amazon.awscdk.services.glue.*;
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

        // 1. Create an S3 bucket for the Glue Data Table
        String uniqueId = UUID.randomUUID().toString().replace("-", ""); // Remove dashes to ensure compatibility
        Bucket glueDataBucket = Bucket.Builder.create(this, "GlueDataBucket")
            .bucketName("glue-" + uniqueId)
            .versioned(true)
            .build();

        // 2. Create a Glue database
        CfnDatabase glueDatabase = CfnDatabase.Builder.create(this, "GlueDatabase")
            .catalogId(this.getAccount())
            .databaseInput(CfnDatabase.DatabaseInputProperty.builder()
                .name("entity_resolution_db")
                .build())
            .build();

        // 3. Create a Glue table referencing the S3 bucket
        CfnTable glueTable = CfnTable.Builder.create(this, "GlueTable")
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
            .build();

        // Ensure Glue Table is created after the Database
        glueTable.addDependency(glueDatabase);

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

        // Add custom permissions for Entity Resolution
        entityResolutionRole.addToPolicy(PolicyStatement.Builder.create()
            .actions(List.of(
                "entityresolution:StartMatchingWorkflow",
                "entityresolution:GetMatchingWorkflow"
            ))
            .resources(List.of("*")) // Adjust permissions if needed
            .build());

        // 5. Create an S3 bucket for output data
        Bucket outputBucket = Bucket.Builder.create(this, "OutputBucket")
            .bucketName("entity-resolution-output-" + id.toLowerCase())
            .versioned(true)
            .build();

        // 6. Output the Role ARN
        new CfnOutput(this, "EntityResolutionArn", CfnOutputProps.builder()
            .value(entityResolutionRole.getRoleArn())
            .description("The ARN of the Glue Role")
            .build());

        // 7. Construct and output the Glue Table ARN
        String glueTableArn = String.format("arn:aws:glue:%s:%s:table/%s/%s",
            this.getRegion(),            // Region where the stack is deployed
            this.getAccount(),           // AWS account ID
            glueDatabase.getRef(),       // Glue database name (resolved reference)
            "entity_resolution"          // Corrected table name
        );

        new CfnOutput(this, "GlueTableArn", CfnOutputProps.builder()
            .value(glueTableArn)
            .description("The ARN of the Glue Table")
            .build());

        // 8. Output the name of the Glue Data Bucket
        new CfnOutput(this, "GlueDataBucketName", CfnOutputProps.builder()
            .value(glueDataBucket.getBucketName()) // Outputs the bucket name
            .description("The name of the Glue Data Bucket")
            .build());
    }
}
