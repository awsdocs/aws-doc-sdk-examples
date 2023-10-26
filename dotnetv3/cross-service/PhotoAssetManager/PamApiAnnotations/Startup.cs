// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Amazon.Rekognition;
using Amazon.S3;
using Amazon.SimpleNotificationService;
using Amazon.Util;
using PamServices;

namespace PamApiAnnotations;

[Amazon.Lambda.Annotations.LambdaStartup]
public class Startup
{
    public Startup()
    {
        ConfigureDynamoDB();
    }

    /// <summary>
    /// Services for AWS Lambda functions can be registered in the services dependency injection container in this method. 
    ///
    /// The services can be injected into the Lambda function through the containing type's constructor or as a
    /// parameter in the Lambda function using the FromService attribute. Services injected for the constructor have
    /// the lifetime of the Lambda compute container. Services injected as parameters are created within the scope
    /// of the function invocation.
    /// </summary>
    public void ConfigureServices(IServiceCollection services)
    {
        // Add the services to the DI container.

        services.AddAWSService<IAmazonS3>();
        services.AddAWSService<IAmazonDynamoDB>();
        services.AddAWSService<IAmazonRekognition>();
        services.AddAWSService<IAmazonSimpleNotificationService>();
        services.AddTransient<IDynamoDBContext, DynamoDBContext>();

        services.AddTransient<LabelService>();
        services.AddTransient<StorageService>();
        services.AddTransient<ImageService>();
        services.AddTransient<NotificationService>();

        services.AddCors();
        services.AddControllers();
    }

    /// <summary>
    /// Configure a table mapping for the environment variable table name.
    /// </summary>
    private void ConfigureDynamoDB()
    {
        var labelsTableName = Environment.GetEnvironmentVariable("LABELS_TABLE_NAME");
        if (labelsTableName != null)
        {
            AWSConfigsDynamoDB.Context.AddMapping(new TypeMapping(typeof(Label), labelsTableName));
        }
    }
}