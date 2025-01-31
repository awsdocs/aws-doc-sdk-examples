// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.MediaConvert;
using MediaConvertActions;
using Microsoft.Extensions.Configuration;

namespace CreateJob;

/// <summary>
/// Create an AWS Elemental MediaConvert job.
/// </summary>
public class CreateJob
{
    static async Task Main(string[] args)
    {
        var _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("settings.json") // Load settings from .json file.
            .AddJsonFile("settings.local.json", true) // Optionally, load local settings.
            .Build();

        // snippet-start:[MediaConvert.dotnetv3.Setup]

        // MediaConvert role Amazon Resource Name (ARN).
        // For information on creating this role, see
        // https://docs.aws.amazon.com/mediaconvert/latest/ug/creating-the-iam-role-in-mediaconvert-configured.html.
        var mediaConvertRole = _configuration["mediaConvertRoleARN"];

        // Include the file input and output locations in settings.json or settings.local.json.
        var fileInput = _configuration["fileInput"];
        var fileOutput = _configuration["fileOutput"];

        AmazonMediaConvertClient mcClient = new AmazonMediaConvertClient();

        var wrapper = new MediaConvertWrapper(mcClient);
        // snippet-end:[MediaConvert.dotnetv3.Setup]

        // snippet-start:[MediaConvert.dotnetv3.CreateJobSetup]
        Console.WriteLine(new string('-', 80));
        Console.WriteLine($"Creating job for input file {fileInput}.");
        var jobId = await wrapper.CreateJob(mediaConvertRole!, fileInput!, fileOutput!);
        Console.WriteLine($"Created job with Job ID: {jobId}");
        Console.WriteLine(new string('-', 80));
        // snippet-end:[MediaConvert.dotnetv3.CreateJobSetup]

        // snippet-start:[MediaConvert.dotnetv3.GetJobSetup]
        Console.WriteLine(new string('-', 80));
        Console.WriteLine($"Getting job information for Job ID {jobId}");
        var job = await wrapper.GetJobById(jobId);
        Console.WriteLine($"Job {job.Id} created on {job.CreatedAt:d} has status {job.Status}.");
        Console.WriteLine(new string('-', 80));
        // snippet-end:[MediaConvert.dotnetv3.GetJobSetup]

        // snippet-start:[MediaConvert.dotnetv3.ListJobsSetup]
        Console.WriteLine(new string('-', 80));
        Console.WriteLine($"Listing all complete jobs.");
        var completeJobs = await wrapper.ListAllJobsByStatus(JobStatus.COMPLETE);
        completeJobs.ForEach(j =>
        {
            Console.WriteLine($"Job {j.Id} created on {j.CreatedAt:d} has status {j.Status}.");
        });
        // snippet-end:[MediaConvert.dotnetv3.ListJobsSetup]

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("MediaConvert Create Job example complete.");
        Console.WriteLine(new string('-', 80));
    }
}