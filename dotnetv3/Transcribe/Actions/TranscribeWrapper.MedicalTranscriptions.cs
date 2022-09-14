// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Net;
using Amazon.TranscribeService;
using Amazon.TranscribeService.Model;

namespace TranscribeActions;

/// <summary>
/// Wrapper methods to use Amazon Transcribe with medical transcription jobs.
/// </summary>
public partial class TranscribeWrapper
{
    // snippet-start:[Transcribe.dotnetv3.StartMedicalTranscriptionJob]

    /// <summary>
    /// Start a medical transcription job for a media file. This method returns
    /// as soon as the job is started.
    /// </summary>
    /// <param name="jobName">A unique name for the medical transcription job.</param>
    /// <param name="mediaFileUri">The URI of the media file, typically an Amazon S3 location.</param>
    /// <param name="mediaFormat">The format of the media file.</param>
    /// <param name="outputBucketName">Location for the output, typically an Amazon S3 location.</param>
    /// <param name="transcriptionType">Conversation or dictation transcription type.</param>
    /// <returns>A MedicalTransactionJob instance with information on the new job.</returns>
    public async Task<MedicalTranscriptionJob> StartMedicalTranscriptionJob(
        string jobName, string mediaFileUri,
        MediaFormat mediaFormat, string outputBucketName, Amazon.TranscribeService.Type transcriptionType)
    {
        var response = await _amazonTranscribeService.StartMedicalTranscriptionJobAsync(
            new StartMedicalTranscriptionJobRequest()
            {
                MedicalTranscriptionJobName = jobName,
                Media = new Media()
                {
                    MediaFileUri = mediaFileUri
                },
                MediaFormat = mediaFormat,
                LanguageCode =
                    LanguageCode
                        .EnUS, // The value must be en-US for medical transcriptions.
                OutputBucketName = outputBucketName,
                OutputKey =
                    jobName, // The value is a key used to fetch the output of the transcription.
                Specialty = Specialty.PRIMARYCARE, // The value PRIMARYCARE must be set.
                Type = transcriptionType
            });
        return response.MedicalTranscriptionJob;
    }

    // snippet-end:[Transcribe.dotnetv3.StartMedicalTranscriptionJob]

    // snippet-start:[Transcribe.dotnetv3.GetMedicalTranscriptionJob]

    /// <summary>
    /// Get details about a medical transcription job.
    /// </summary>
    /// <param name="jobName">A unique name for the medical transcription job.</param>
    /// <returns>A MedicalTranscriptionJob instance with information on the requested job.</returns>
    public async Task<MedicalTranscriptionJob> GetMedicalTranscriptionJob(string jobName)
    {
        var response = await _amazonTranscribeService.GetMedicalTranscriptionJobAsync(
            new GetMedicalTranscriptionJobRequest()
            {
                MedicalTranscriptionJobName = jobName
            });
        return response.MedicalTranscriptionJob;
    }

    // snippet-end:[Transcribe.dotnetv3.GetMedicalTranscriptionJob]

    // snippet-start:[Transcribe.dotnetv3.ListMedicalTranscriptionJobs]

    /// <summary>
    /// List medical transcription jobs, optionally with a name filter.
    /// </summary>
    /// <param name="jobNameContains">Optional name filter for the medical transcription jobs.</param>
    /// <returns>A list of summaries about medical transcription jobs.</returns>
    public async Task<List<MedicalTranscriptionJobSummary>> ListMedicalTranscriptionJobs(
        string? jobNameContains = null)
    {
        var response = await _amazonTranscribeService.ListMedicalTranscriptionJobsAsync(
            new ListMedicalTranscriptionJobsRequest()
            {
                JobNameContains = jobNameContains
            });
        return response.MedicalTranscriptionJobSummaries;
    }

    // snippet-end:[Transcribe.dotnetv3.ListMedicalTranscriptionJobs]

    // snippet-start:[Transcribe.dotnetv3.DeleteMedicalTranscriptionJob]

    /// <summary>
    /// Delete a medical transcription job. Also deletes the transcript associated with the job.
    /// </summary>
    /// <param name="jobName">Name of the medical transcription job to delete.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> DeleteMedicalTranscriptionJob(string jobName)
    {
        var response = await _amazonTranscribeService.DeleteMedicalTranscriptionJobAsync(
            new DeleteMedicalTranscriptionJobRequest()
            {
                MedicalTranscriptionJobName = jobName
            });
        return response.HttpStatusCode == HttpStatusCode.OK;
    }

    // snippet-end:[Transcribe.dotnetv3.DeleteMedicalTranscriptionJob]
}
