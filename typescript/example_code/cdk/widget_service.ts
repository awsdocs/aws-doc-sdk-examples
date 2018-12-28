//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-comment:[This should be in the lib/ directory]
//snippet-comment:[and only works with my_widget_service.ts in the bin/ directory]
//snippet-comment:[and widgets.js in the resources/ directory.]
//snippet-sourceauthor:[Doug-AWS]
//snippet-sourcedescription:[Creates an S3 bucket, handler for HTTP requests, and API Gateway to Lambda functions.]
//snippet-keyword:[CDK V0.14.1]
//snippet-keyword:[ApiGateway.LambdaIntegration function]
//snippet-keyword:[ApiGateway.RestApi function]
//snippet-keyword:[Bucket.grantReadWrite function]
//snippet-keyword:[Lambda.Function function]
//snippet-keyword:[S3.Bucket function]
//snippet-keyword:[TypeScript]
//snippet-service:[cdk]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-11-05]
// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of the
// License is located at
//
// http://aws.amazon.com/apache2.0/
//
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
//snippet-start:[snippet.cdk.create_widget_service.ts]
import cdk = require('@aws-cdk/cdk');
import apigateway = require('@aws-cdk/aws-apigateway');
import lambda = require('@aws-cdk/aws-lambda');
import s3 = require('@aws-cdk/aws-s3');

export class WidgetService extends cdk.Construct {
  constructor(parent: cdk.Construct, name: string) {
    super(parent, name);

    const bucket = new s3.Bucket(this, 'WidgetStore');

    const handler = new lambda.Function(this, 'WidgetHandler', {
      runtime: lambda.Runtime.NodeJS810,  // So we can use async in widget.js
      code: lambda.Code.directory('resources'),
      handler: 'widgets.main',
      environment: {
        BUCKET: bucket.bucketName
      }
    });

    bucket.grantReadWrite(handler.role);

    const api = new apigateway.RestApi(this, 'widgets-api', {
      restApiName: 'Widget Service',
      description: 'This service serves widgets.'
    });

    const getWidgetsIntegration = new apigateway.LambdaIntegration(handler, {
      requestTemplates:  { "application/json": '{ "statusCode": "200" }' }
    });

    api.root.addMethod('GET', getWidgetsIntegration);   // GET /

    const widget = api.root.addResource('{name}');

    // Add new widget to bucket with: POST /{name}
    const postWidgetIntegration = new apigateway.LambdaIntegration(handler);

    // Get a specific widget from bucket with: GET /{name}
    const getWidgetIntegration = new apigateway.LambdaIntegration(handler);

    // Remove a specific widget from the bucket with: DELETE /{name}
    const deleteWidgetIntegration = new apigateway.LambdaIntegration(handler);

    widget.addMethod('POST', postWidgetIntegration);    // POST /{name}
    widget.addMethod('GET', getWidgetIntegration);       // GET /{name}
    widget.addMethod('DELETE', deleteWidgetIntegration); // DELETE /{name}
  }
}
//snippet-end:[snippet.cdk.create_widget_service.ts]