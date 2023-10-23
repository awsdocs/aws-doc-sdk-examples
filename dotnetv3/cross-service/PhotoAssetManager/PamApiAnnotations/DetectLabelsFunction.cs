// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Amazon.Lambda.Core;
using Amazon.Lambda.S3Events;
using Amazon.Rekognition;
using Amazon.S3;
using Amazon.Util;
using AWS.Lambda.Powertools.Logging;
using PamServices;

// Assembly attribute to enable the AWS Lambda function's JSON input to be converted into a .NET class.
//[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace PamApiAnnotations;

/// <summary>
/// The function code for detecting and storing labels for an uploaded image.
/// </summary>
public class DetectLabelsFunction
{
    IAmazonS3 S3Client { get; set; }
    private readonly ImageService _imageService;
    private readonly LabelService _labelService;

    /// <summary>
    /// Default constructor. This constructor is used by AWS Lambda to construct the instance. When invoked in a Lambda environment,
    /// the AWS credentials will come from the AWS Identity and Access Management (IAM) role associated with the function. The AWS Region will be set to the
    /// Region the Lambda function is executed in.
    /// </summary>
    public DetectLabelsFunction()
    {
        S3Client = new AmazonS3Client();
        _imageService = new ImageService(new AmazonRekognitionClient());
        _labelService = new LabelService(new DynamoDBContext(new AmazonDynamoDBClient()));

        var labelsTableName = Environment.GetEnvironmentVariable("LABELS_TABLE_NAME");

        if (labelsTableName != null)
        {
            AWSConfigsDynamoDB.Context.AddMapping(new TypeMapping(typeof(Label), labelsTableName));
        }
    }

    /// <summary>
    /// Constructs an instance with a preconfigured S3 client. This can be used for testing the outside of the Lambda environment.
    /// </summary>
    /// <param name="s3Client"></param>
    public DetectLabelsFunction(IAmazonS3 s3Client)
    {
        this.S3Client = s3Client;
        this._imageService = new ImageService(new AmazonRekognitionClient());
        this._labelService = new LabelService(new DynamoDBContext(new AmazonDynamoDBClient()));
    }

    /// <summary>
    /// Responds to an Amazon S3 upload event and processes and stores the labels for the new image.
    /// </summary>
    /// <param name="evnt">The event object.</param>
    /// <param name="context">The Lambda context.</param>
    /// <returns>Async task.</returns>
    public async Task FunctionHandler(S3Event evnt, ILambdaContext context)
    {
        var s3Event = evnt.Records?[0].S3;
        try
        {
            Logger.LogInformation($"Processing object {s3Event!.Object.Key} from bucket {s3Event.Bucket.Name}");

            var detectedLabels = await _imageService.DetectLabels(s3Event.Object.Key, s3Event.Bucket.Name);
            await _labelService.AddImageLabels(s3Event.Object.Key, detectedLabels);
            Logger.LogInformation($"Added labels {string.Join(',', detectedLabels)}");
        }
        catch (Exception e)
        {
            Logger.LogError($"Error getting object {s3Event!.Object.Key} from bucket {s3Event.Bucket.Name}. " +
                            $"Make sure they exist and your bucket is in the same region as this function.", e);
            throw;
        }
    }
}