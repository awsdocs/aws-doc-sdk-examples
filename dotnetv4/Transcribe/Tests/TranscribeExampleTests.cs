﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.TranscribeService;
using Microsoft.Extensions.Configuration;
using TranscribeActions;

namespace TranscribeTests;

/// <summary>
/// Integration tests for the examples.
/// </summary>
public class TranscribeExampleTests
{
    private readonly IConfiguration _configuration;
    private readonly TranscribeWrapper _wrapper;

    public TranscribeExampleTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _wrapper = new TranscribeWrapper(new AmazonTranscribeServiceClient());
    }

    /// <summary>
    /// Create a valid vocabulary. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task VerifyCreateVocabulary_ValidName_ShouldSucceed()
    {
        var vocabularyName = _configuration["customVocabularyName"];
        var phrases = new List<string> { "testPhrase" };
        var vocabularyState = await _wrapper.CreateCustomVocabulary(LanguageCode.EnUS, phrases, vocabularyName);

        while (vocabularyState != VocabularyState.FAILED && vocabularyState != VocabularyState.READY)
        {
            Thread.Sleep(5000);
            vocabularyState = await _wrapper.GetCustomVocabulary(vocabularyName);
        }

        Assert.NotEqual(VocabularyState.FAILED, vocabularyState);
    }

    /// <summary>
    /// Create a non-valid vocabulary. Should throw an exception for a bad request.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task VerifyCreateVocabulary_NonValidName_ShouldThrowException()
    {
        var vocabularyName = " ";
        var phrases = new List<string> { "testPhrase" };

        await Assert.ThrowsAsync<Amazon.TranscribeService.Model.BadRequestException>(async () =>
        {
            await _wrapper.CreateCustomVocabulary(LanguageCode.EnUS, phrases,
                vocabularyName);
        });
    }

    /// <summary>
    /// Get an existing vocabulary. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task GetVocabulary_ValidName_ShouldSucceed()
    {
        var vocabularyName = _configuration["customVocabularyName"];

        var vocabularyState = await _wrapper.GetCustomVocabulary(vocabularyName);

        Assert.Contains(vocabularyState, new List<VocabularyState>()
             {
                 VocabularyState.FAILED,
                 VocabularyState.PENDING,
                 VocabularyState.READY
             });
    }

    /// <summary>
    /// Try to get a vocabulary with the wrong name. Should fail with an exception for a bad name.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    [Trait("Category", "Integration")]
    public async Task GetVocabulary_WrongName_ShouldThrowException()
    {
        await Assert.ThrowsAsync<Amazon.TranscribeService.Model.BadRequestException>(async () =>
        {
            await _wrapper.GetCustomVocabulary("wrongName");
        });
    }

    /// <summary>
    /// Update a custom vocabulary. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    [Trait("Category", "Integration")]
    public async Task UpdateVocabulary_ShouldSucceed()
    {
        var vocabularyName = _configuration["customVocabularyName"];
        var phrases = new List<string> { "testPhraseUpdate" };
        var vocabularyState = await _wrapper.UpdateCustomVocabulary(LanguageCode.EnUS, phrases, vocabularyName);

        while (vocabularyState != VocabularyState.FAILED && vocabularyState != VocabularyState.READY)
        {
            vocabularyState = await _wrapper.GetCustomVocabulary(vocabularyName);
            Thread.Sleep(5000);
        }

        Assert.NotEqual(VocabularyState.FAILED, vocabularyState);
    }

    /// <summary>
    /// Update a custom vocabulary with the wrong name. Should fail with an exception.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    [Trait("Category", "Integration")]
    public async Task UpdateVocabulary_WrongName_ShouldThrowException()
    {
        var phrases = new List<string> { "testPhraseUpdate" };

        await Assert.ThrowsAsync<Amazon.TranscribeService.Model.BadRequestException>(async () =>
        {
            await _wrapper.UpdateCustomVocabulary(LanguageCode.EnUS, phrases, "wrongName");
        });
    }

    /// <summary>
    /// List vocabularies. Should return results.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    [Trait("Category", "Integration")]
    public async Task ListVocabulary_ShouldReturnResults()
    {
        var result = await _wrapper.ListCustomVocabularies();
        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Delete a custom vocabulary. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    [Trait("Category", "Integration")]
    public async Task DeleteVocabulary_ShouldSucceed()
    {
        var vocabularyName = _configuration["customVocabularyName"];
        var result = await _wrapper.DeleteCustomVocabulary(vocabularyName);
        Assert.True(result);
    }

    /// <summary>
    /// Create a valid transcription job. Should complete.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    [Trait("Category", "Integration")]
    public async Task VerifyCreateTranscriptionJob_ValidMedia_ShouldComplete()
    {
        var mediaLocation = _configuration["transcriptionMediaLocation"];
        var transcriptionJobName = _configuration["transcriptionJobName"];

        var transcriptionJob = await _wrapper.StartTranscriptionJob(
            transcriptionJobName, mediaLocation, MediaFormat.Mp3,
            LanguageCode.EnUS, null);

        while (transcriptionJob.TranscriptionJobStatus != TranscriptionJobStatus.COMPLETED
               && transcriptionJob.TranscriptionJobStatus != TranscriptionJobStatus.FAILED)
        {
            transcriptionJob =
                await _wrapper.GetTranscriptionJob(transcriptionJobName);
            Thread.Sleep(5000);
        }

        Assert.Equal(TranscriptionJobStatus.COMPLETED, transcriptionJob.TranscriptionJobStatus);
    }

    /// <summary>
    /// Create a transcription job without media. Should throw an exception for a bad request.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(10)]
    [Trait("Category", "Integration")]
    public async Task VerifyCreateTranscriptionJob_MissingMedia_ShouldThrowException()
    {
        var mediaLocation = _configuration["transcriptionMediaLocation"];
        var transcriptionJobName = _configuration["transcriptionJobName"];

        await Assert.ThrowsAsync<Amazon.TranscribeService.Model.ConflictException>(async () =>
        {
            var transcriptionJob = await _wrapper.StartTranscriptionJob(
                transcriptionJobName, mediaLocation, MediaFormat.Mp3,
                LanguageCode.EnUS, null);

        });
    }

    /// <summary>
    /// List transcription jobs. Should return results.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(11)]
    [Trait("Category", "Integration")]
    public async Task ListTranscriptionJobs_ShouldReturnResults()
    {
        var result = await _wrapper.ListTranscriptionJobs();
        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Delete a transcription job. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(11)]
    [Trait("Category", "Integration")]
    public async Task DeleteTranscriptionJob_ShouldSucceed()
    {
        var transcriptionJobName = _configuration["transcriptionJobName"];
        var result = await _wrapper.DeleteTranscriptionJob(transcriptionJobName);
        Assert.True(result);
    }

    /// <summary>
    /// Create a valid medical transcription job. Should complete.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(12)]
    [Trait("Category", "Integration")]
    public async Task VerifyCreateMedicalTranscriptionJob_ValidMedia_ShouldComplete()
    {
        var mediaLocation = _configuration["transcriptionMediaLocation"];
        var medicalTranscriptionJobName = _configuration["medicalTranscriptionJobName"];
        var outputLocation = _configuration["outputLocation"];

        var medicalTranscriptionJob = await _wrapper.StartMedicalTranscriptionJob(
            medicalTranscriptionJobName, mediaLocation, MediaFormat.Mp3,
            outputLocation, Amazon.TranscribeService.Type.DICTATION);

        while (medicalTranscriptionJob.TranscriptionJobStatus != TranscriptionJobStatus.COMPLETED
               && medicalTranscriptionJob.TranscriptionJobStatus != TranscriptionJobStatus.FAILED)
        {
            medicalTranscriptionJob =
                await _wrapper.GetMedicalTranscriptionJob(medicalTranscriptionJobName);
            Thread.Sleep(5000);
        }

        Assert.Equal(TranscriptionJobStatus.COMPLETED, medicalTranscriptionJob.TranscriptionJobStatus);
    }

    /// <summary>
    /// Create a duplicate medical transcription job. Should throw an exception for a conflict.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(13)]
    [Trait("Category", "Integration")]
    public async Task VerifyCreateMedicalTranscriptionJob_DuplicateName_ShouldThrowException()
    {
        var mediaLocation = _configuration["transcriptionMediaLocation"];
        var medicalTranscriptionJobName = _configuration["medicalTranscriptionJobName"];
        var outputLocation = _configuration["outputLocation"];

        await Assert.ThrowsAsync<Amazon.TranscribeService.Model.ConflictException>(async () =>
        {
            var medicalTranscriptionJob = await _wrapper.StartMedicalTranscriptionJob(
                medicalTranscriptionJobName, mediaLocation, MediaFormat.Mp3,
                outputLocation, Amazon.TranscribeService.Type.DICTATION);
        });
    }

    /// <summary>
    /// List medical transcription jobs. Should return results.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(14)]
    [Trait("Category", "Integration")]
    public async Task ListMedicalTranscriptionJobs_ShouldReturnResults()
    {
        var result = await _wrapper.ListMedicalTranscriptionJobs();
        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Delete a medical transcription job. Should succeed.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(15)]
    [Trait("Category", "Integration")]
    public async Task DeleteMedicalTranscriptionJob_ShouldSucceed()
    {
        var medicalTranscriptionJobName = _configuration["medicalTranscriptionJobName"];
        var result = await _wrapper.DeleteMedicalTranscriptionJob(medicalTranscriptionJobName);
        Assert.True(result);
    }
}