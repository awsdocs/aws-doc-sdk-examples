// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Net;
using System.Text.Json;
using Amazon.S3;
using Amazon.S3.Model;
using Amazon.TranscribeService;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace TranscribeActions;

/// <summary>
/// Amazon Transcribe action examples.
/// </summary>
public class ActionExamples
{
    public static ActionExamples CreateInstance()
    {
        return new ActionExamples();
    }

    private static readonly string sepBar = new('-', 80);
    private static ILogger logger = null!;
    private static TranscribeWrapper transcribeWrapper = null!;
    static readonly HttpClient httpClient = new HttpClient();

    // Set this value to the S3 location of a media file.
    // A sample media file is provided in the media folder of this solution.
    private static readonly string transcriptionMediaLocation =
        "https://[example-media-bucket].s3.amazonaws.com/Jabberwocky.mp3";
    // Set this value to an S3 folder name where the output can be stored.
    private static readonly string transcriptionMediaOutputLocation = "example-output-bucket";
    private static readonly string customVocabularyName = "Example-jabber-vocabulary";

    static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon Transcribe service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonTranscribeService>()
                    .AddScoped<TranscribeWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        }).CreateLogger<ActionExamples>();

        transcribeWrapper = host.Services.GetRequiredService<TranscribeWrapper>();

        try
        {
            Console.WriteLine(sepBar);
            Console.WriteLine("Welcome to the Amazon Transcribe examples!");
            Console.WriteLine(sepBar);

            await CreateVocabulary();

            var transcriptionName = await StartTranscription();

            await CleanupTranscription(transcriptionName);

            var medicalTranscriptionName = await StartMedicalTranscription();

            await CleanupMedicalTranscription(medicalTranscriptionName);

            await CleanupVocabulary();

            Console.WriteLine(sepBar);
            Console.WriteLine("Finished running examples.");
            Console.WriteLine(sepBar);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "There was a problem executing the examples.");
        }
    }

    /// <summary>
    /// Run the wrapper methods to start a transcription job and get information about the job.
    /// </summary>
    /// <returns>The name of the new transcription job.</returns>
    public static async Task<string> StartTranscription()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Start a transcription.");

        var transcriptionName = $"ExampleTranscription_{DateTime.Now.Ticks}";

        var transcriptionJob = await transcribeWrapper.StartTranscriptionJob(
            transcriptionName, transcriptionMediaLocation, MediaFormat.Mp3,
            LanguageCode.EnUS, null);

        Console.WriteLine($"Transcription started: {transcriptionJob.TranscriptionJobName}, " +
                          $"status: {transcriptionJob.TranscriptionJobStatus}.");

        Console.WriteLine($"Waiting for transcription {transcriptionName}.");

        while (transcriptionJob.TranscriptionJobStatus != TranscriptionJobStatus.COMPLETED
               && transcriptionJob.TranscriptionJobStatus != TranscriptionJobStatus.FAILED)
        {
            transcriptionJob =
                await transcribeWrapper.GetTranscriptionJob(transcriptionName);
            Thread.Sleep(5000);
        }

        Console.WriteLine($"Transcription status:  {transcriptionJob.TranscriptionJobStatus}, " +
                          $"completed at: {transcriptionJob.CompletionTime}.");

        if (transcriptionJob.TranscriptionJobStatus == TranscriptionJobStatus.COMPLETED)
        {
            var url = transcriptionJob.Transcript.TranscriptFileUri;

            var transcriptionResult = await httpClient.GetAsync(url);
            var resultStream = await transcriptionResult.Content.ReadAsStreamAsync();

            var transcriptionObject = await JsonDocument.ParseAsync(resultStream);
            var transcriptionText = transcriptionObject.RootElement
                .GetProperty("results").GetProperty("transcripts")[0]
                .GetProperty("transcript");

            Console.WriteLine(sepBar);
            Console.WriteLine($"Transcript:");
            Console.WriteLine(transcriptionText);
        }

        Console.WriteLine(sepBar);
        return transcriptionName;
    }

    /// <summary>
    /// Run the wrapper methods to list jobs and delete a transcription job.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task CleanupTranscription(string transcriptionName)
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("List the transcriptions for the current account.");

        var transcriptionJobs = await transcribeWrapper.ListTranscriptionJobs();

        transcriptionJobs.ForEach(j =>
            Console.WriteLine($"Transcription job {j.TranscriptionJobName}" +
                              $"status {j.TranscriptionJobStatus}, created on " +
                              $"{j.CreationTime.ToShortDateString()} at " +
                              $"{j.CreationTime.ToShortTimeString()}."));

        Console.WriteLine("Delete a transcription job.");

        var success = await transcribeWrapper.DeleteTranscriptionJob(transcriptionName);

        Console.WriteLine(success ? $"Transcription {transcriptionName} deleted."
            : $"Unable to delete transcription {transcriptionName}.");
        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper methods to create and update a custom vocabulary.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task CreateVocabulary()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Create a custom vocabulary to try to improve transcription accuracy.");

        var phrases = new List<string>
        {
            "brillig", "slithy", "borogoves", "mome", "raths", "Jub-Jub", "frumious",
            "manxome", "Tumtum", "uffish", "whiffling", "tulgey", "thou", "frabjous",
            "callooh","callay"
        };

        await transcribeWrapper.CreateCustomVocabulary(LanguageCode.EnUS,
            phrases, customVocabularyName);

        Console.WriteLine("Get the custom vocabulary state and wait until it is ready.");
        VocabularyState vocabularyState = null!;

        while (vocabularyState != VocabularyState.FAILED && vocabularyState != VocabularyState.READY)
        {
            vocabularyState = await transcribeWrapper.GetCustomVocabulary(customVocabularyName);
            Thread.Sleep(5000);
        }

        Console.WriteLine("Update the vocabulary.");

        // add a phrase to the list
        phrases.Add("chortled");

        vocabularyState = await transcribeWrapper.UpdateCustomVocabulary(LanguageCode.EnUS,
            phrases, customVocabularyName);

        while (vocabularyState != VocabularyState.PENDING)
        {
            vocabularyState = await transcribeWrapper.GetCustomVocabulary(customVocabularyName);
            Thread.Sleep(5000);
        }

        Console.WriteLine($"Transcription vocabulary status: {vocabularyState}.");
        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper methods to list and delete a vocabulary.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task CleanupVocabulary()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("List the vocabularies for the current account.");

        var vocabularies = await transcribeWrapper.ListCustomVocabularies();

        vocabularies.ForEach(v =>
            Console.WriteLine($"Vocabulary {v.VocabularyName}" +
                              $"status {v.VocabularyState}, last modified on " +
                              $"{v.LastModifiedTime} at {v.LastModifiedTime.ToShortTimeString()}."));

        Console.WriteLine("Delete the custom vocabulary.");

        var success = await transcribeWrapper.DeleteCustomVocabulary(customVocabularyName);

        Console.WriteLine(success ? $"Custom vocabulary {customVocabularyName} deleted."
            : $"Unable to delete vocabulary {customVocabularyName}");

        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper methods to start a medical transcription job and get information about the job.
    /// </summary>
    /// <returns>The name of the new medical transcription job.</returns>
    public static async Task<string> StartMedicalTranscription()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Start a medical transcription.");

        var medicalTranscriptionName = $"ExampleMedicalTranscription_{DateTime.Now.Ticks}";

        var medicalTranscriptionJob = await transcribeWrapper.StartMedicalTranscriptionJob(
            medicalTranscriptionName, transcriptionMediaLocation, MediaFormat.Mp3,
            transcriptionMediaOutputLocation, Amazon.TranscribeService.Type.DICTATION);

        Console.WriteLine($"Medical transcription started: {medicalTranscriptionJob}, " +
                          $"status: {medicalTranscriptionJob.TranscriptionJobStatus}.");

        Console.WriteLine($"Waiting for medical transcription {medicalTranscriptionName}.");

        while (medicalTranscriptionJob.TranscriptionJobStatus != TranscriptionJobStatus.COMPLETED
               && medicalTranscriptionJob.TranscriptionJobStatus != TranscriptionJobStatus.FAILED)
        {
            medicalTranscriptionJob =
                await transcribeWrapper.GetMedicalTranscriptionJob(medicalTranscriptionName);
            Thread.Sleep(5000);
        }

        Console.WriteLine($"Transcription status:  {medicalTranscriptionJob.TranscriptionJobStatus}, " +
                          $"completed at: {medicalTranscriptionJob.CompletionTime}.");

        if (medicalTranscriptionJob.TranscriptionJobStatus == TranscriptionJobStatus.COMPLETED)
        {
            Console.WriteLine(sepBar);
            Console.WriteLine($"Transcription result url:");
            Console.WriteLine(medicalTranscriptionJob.Transcript.TranscriptFileUri);

            var s3client = new AmazonS3Client();
            var output = await s3client.GetObjectAsync(new GetObjectRequest()
            {
                BucketName = transcriptionMediaOutputLocation,
                Key = medicalTranscriptionName
            });

            if (output.HttpStatusCode == HttpStatusCode.OK)
            {
                var transcriptionObject =
                    await JsonDocument.ParseAsync(output.ResponseStream);
                var transcriptionText = transcriptionObject.RootElement
                    .GetProperty("results").GetProperty("transcripts")[0]
                    .GetProperty("transcript");

                Console.WriteLine(sepBar);
                Console.WriteLine($"Medical transcript:");
                Console.WriteLine(transcriptionText);
            }
        }

        Console.WriteLine(sepBar);
        return medicalTranscriptionName;
    }


    /// <summary>
    /// Run the wrapper methods to list medical transcription jobs and delete one job.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task CleanupMedicalTranscription(string medicalTranscriptionName)
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("List the medical transcriptions for the current account.");

        var transcriptionJobs = await transcribeWrapper.ListMedicalTranscriptionJobs();

        transcriptionJobs.ForEach(j =>
            Console.WriteLine($"Medical transcription job {j.MedicalTranscriptionJobName}" +
                              $"status {j.TranscriptionJobStatus}, created on " +
                              $"{j.CreationTime.ToShortDateString()} at " +
                              $"{j.CreationTime.ToShortTimeString()}."));

        Console.WriteLine("Delete a medical transcription job.");

        var success = await transcribeWrapper.DeleteMedicalTranscriptionJob(medicalTranscriptionName);

        Console.WriteLine(success ? $"Medical transcription {medicalTranscriptionName} deleted."
            : $"Unable to delete transcription {medicalTranscriptionName}.");

        Console.WriteLine(sepBar);
    }
}