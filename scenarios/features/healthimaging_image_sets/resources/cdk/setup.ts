#!/usr/bin/env node
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Purpose
//
// Defines an AWS CloudFormation stack that creates:
//  1. A HealthImaging data store
// .2. An input Aws S3 bucket for importing DICOM files.
//  3. An output Aws S3 bucket for importing DICOM files.
//  4. An IAM role for importing DICOM files.
//


import {CfnOutput, CfnParameter, RemovalPolicy, Stack, StackProps, App} from 'aws-cdk-lib';
import {Construct} from "constructs"
import {Effect, PolicyStatement, Role, ServicePrincipal} from 'aws-cdk-lib/aws-iam';
import {Bucket} from "aws-cdk-lib/aws-s3";
import {CfnDatastore} from "aws-cdk-lib/aws-healthimaging";


export class SetupStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props);

        const datastoreName = new CfnParameter(this, "datastoreName", {
            type: "String",
            description: "The name for the data store."
        });

        const userAccountID = new CfnParameter(this, "userAccountID", {
            type: "String",
            description: "The user's account ID used for the IAM role."
        });

        const inputBucket = new Bucket(this, "doc-example-dicom_in_bucket", {
            removalPolicy: RemovalPolicy.DESTROY,
            autoDeleteObjects: true
        });
        const outputBucket = new Bucket(this, "doc-example-dicom_out_bucket", {
            removalPolicy: RemovalPolicy.DESTROY,
            autoDeleteObjects: true
        });

        const datastore = new CfnDatastore(this, 'MyCfnDatastore', /* all optional props */ {
            datastoreName: datastoreName.valueAsString,

        });

        const service_principal = new ServicePrincipal('medical-imaging.amazonaws.com', {
                conditions: {
                    "ForAllValues:StringEquals": {
                        "aws:SourceAccount": userAccountID.valueAsString
                    },
                    "ForAllValues:ArnEquals": {
                        "aws:SourceArn": datastore.attrDatastoreArn
                    }
                }
            }
        );

        const import_role = new Role(this, 'doc-example-import-role', {
            assumedBy: service_principal,
        });

        import_role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            resources: [inputBucket.bucketArn, outputBucket.bucketArn],
            actions: [
                's3:ListBucket'
            ]
        }));

        import_role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            resources: [inputBucket.bucketArn + "/*"],
            actions: ['s3:GetObject']
        }));

        import_role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            resources: [outputBucket.bucketArn + "/*"],
            actions: ['s3:PutObject']
        }));

        new CfnOutput(this, 'OutputBucketName', {value: outputBucket.bucketName});
        new CfnOutput(this, 'InputBucketName', {value: inputBucket.bucketName});
        new CfnOutput(this, 'RoleArn', {value: import_role.roleArn});
        new CfnOutput(this, 'DatastoreID', {value: datastore.attrDatastoreId});
    }
}

const stackName = 'doc-example-healthimaging-workflow-stack'

const app = new App();

new SetupStack(app, stackName);
