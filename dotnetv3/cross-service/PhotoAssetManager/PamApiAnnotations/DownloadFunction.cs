// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Text.Json;
using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Amazon.Lambda.Core;
using Amazon.S3;
using Amazon.SimpleNotificationService;
using Amazon.Util;
using PamApi;
using PamServices;

namespace PamApiAnnotations;

/// <summary>
/// The function code for downloading and zipping
/// images from a list of labels.
/// </summary>
public class DownloadFunction
{
    IAmazonS3 S3Client { get; set; }
    private readonly StorageService _storageService;
    private readonly NotificationService _notificationService;
    private readonly LabelService _labelService;

    /// <summary>
    /// Default constructor. This constructor is used by Lambda to construct the instance. When invoked in a Lambda environment
    /// the AWS credentials will come from the IAM role associated with the function and the AWS region will be set to the
    /// region the Lambda function is executed in.
    /// </summary>
    public DownloadFunction()
    {
        S3Client = new AmazonS3Client();
        _notificationService = new NotificationService(new AmazonSimpleNotificationServiceClient());
        _storageService = new StorageService(S3Client);
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
    public DownloadFunction(IAmazonS3 s3Client)
    {
        this.S3Client = s3Client;
        _notificationService = new NotificationService(new AmazonSimpleNotificationServiceClient());
        _storageService = new StorageService(S3Client);
        _labelService = new LabelService(new DynamoDBContext(new AmazonDynamoDBClient()));
    }

    /// <summary>
    /// Starts the download and zip operation.
    /// </summary>
    /// <param name="input">The string input.</param>
    /// <param name="context">The Lambda context.</param>
    /// <returns>Async task.</returns>
    public async Task FunctionHandler(DownloadRequest request, ILambdaContext context)
    {
        var storageBucketName = Environment.GetEnvironmentVariable("STORAGE_BUCKET_NAME");
        var workingBucketName = Environment.GetEnvironmentVariable("WORKING_BUCKET_NAME");
        var topicArn = Environment.GetEnvironmentVariable("NOTIFICATION_TOPIC");

        try
        {
            context.Logger.LogInformation($"Starting download and zip operation: {JsonSerializer.Serialize(request)}");
            var labelsList = request.labels.ToList();
            var imageKeys = await _labelService.GetAllImagesForLabels(labelsList);
            var zipArchiveUrl =
                await _storageService.GenerateZipFromImages(imageKeys, storageBucketName!,
                    workingBucketName!);
            await _notificationService.SendNotification(topicArn!, "Image download",
                $"Your images are available here: {zipArchiveUrl}");
            LambdaLogger.Log($"url: {zipArchiveUrl}");
        }
        catch (Exception e)
        {
            context.Logger.LogInformation($"Error starting download.");
            context.Logger.LogInformation(e.Message);
            context.Logger.LogInformation(e.StackTrace);
            throw;
        }
    }
}