// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Net;
using Amazon.TranscribeService;
using Amazon.TranscribeService.Model;

namespace TranscribeActions;

/// <summary>
/// Wrapper methods to use Amazon Transcribe with transcription jobs.
/// </summary>
public partial class TranscribeWrapper
{
    private readonly IAmazonTranscribeService _amazonTranscribeService;

    /// <summary>
    /// Constructor that uses the injected Amazon Transcribe client.
    /// </summary>
    /// <param name="amazonTranscribeService">Amazon Transcribe Service</param>
    public TranscribeWrapper(IAmazonTranscribeService amazonTranscribeService)
    {
        _amazonTranscribeService = amazonTranscribeService;
    }

    // snippet-start:[Transcribe.dotnetv3.StartTranscriptionJob]

    /// <summary>
    /// Start a transcription job for a media file. This method returns
    /// as soon as the job is started.
    /// </summary>
    /// <param name="jobName">A unique name for the transcription job.</param>
    /// <param name="mediaFileUri">The Uri of the media file, typically an S3 location.</param>
    /// <param name="mediaFormat">The format of the media file.</param>
    /// <param name="languageCode">The language code of the media file, such as en-US.</param>
    /// <param name="vocabularyName">Optional name of a custom vocabulary.</param>
    /// <returns>A TranscriptionJob instance with information on the new job.</returns>
    public async Task<TranscriptionJob> StartTranscriptionJob(string jobName, string mediaFileUri, 
        MediaFormat mediaFormat, LanguageCode languageCode, string? vocabularyName)
    {
        var response = await _amazonTranscribeService.StartTranscriptionJobAsync(
            new StartTranscriptionJobRequest()
            {
                TranscriptionJobName = jobName,
                Media = new Media()
                {
                    MediaFileUri = mediaFileUri
                },
                MediaFormat = mediaFormat,
                LanguageCode = languageCode,
                Settings = vocabularyName != null ? new Settings()
                {
                    VocabularyName = vocabularyName
                } : null
            });
        return response.TranscriptionJob;
    }

    // snippet-end:[Transcribe.dotnetv3.StartTranscriptionJob]

    // snippet-start:[Transcribe.dotnetv3.GetTranscriptionJob]

    /// <summary>
    /// Get details about a transcription job.
    /// </summary>
    /// <param name="jobName">A unique name for the transcription job.</param>
    /// <returns>A TranscriptionJob instance with information on the requested job.</returns>
    public async Task<TranscriptionJob> GetTranscriptionJob(string jobName)
    {
        var response = await _amazonTranscribeService.GetTranscriptionJobAsync(
            new GetTranscriptionJobRequest()
            {
                TranscriptionJobName = jobName
            });
        return response.TranscriptionJob;
    }

    // snippet-end:[Transcribe.dotnetv3.GetTranscriptionJob]

    // snippet-start:[Transcribe.dotnetv3.ListTranscriptionJobs]

    /// <summary>
    /// List transcription jobs, optionally with a name filter.
    /// </summary>
    /// <param name="jobNameContains">Optional name filter for the transcription jobs.</param>
    /// <returns>A list of summaries about transcription jobs.</returns>
    public async Task<List<TranscriptionJobSummary>> ListTranscriptionJobs(string? jobNameContains = null)
    {
        var response = await _amazonTranscribeService.ListTranscriptionJobsAsync(
            new ListTranscriptionJobsRequest()
            {
                JobNameContains = jobNameContains
            });
        return response.TranscriptionJobSummaries;
    }

    // snippet-end:[Transcribe.dotnetv3.ListTranscriptionJobs]

    // snippet-start:[Transcribe.dotnetv3.DeleteTranscriptionJob]

    /// <summary>
    /// Delete a transcription job. Also deletes the transcript associated with the job.
    /// </summary>
    /// <param name="jobName">Name of the transcription job to delete.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> DeleteTranscriptionJob(string jobName)
    {
        var response = await _amazonTranscribeService.DeleteTranscriptionJobAsync(
            new DeleteTranscriptionJobRequest()
            {
                TranscriptionJobName = jobName
            });
        return response.HttpStatusCode == HttpStatusCode.OK;
    }

    // snippet-end:[Transcribe.dotnetv3.DeleteTranscriptionJob]

}
