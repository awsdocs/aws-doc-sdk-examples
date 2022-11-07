/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_polly::model::{OutputFormat, VoiceId};
use aws_sdk_transcribe::model::{LanguageCode, Media, MediaFormat, TranscriptionJobStatus};
use serde_json::{Result, Value};
use std::fs;
use std::path::Path;
use std::time::Duration;
use structopt::StructOpt;
use tokio::io::AsyncWriteExt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The file containing the input text.
    #[structopt(short, long)]
    filename: String,

    /// The name of the job.
    #[structopt(short, long)]
    job_name: String,

    /// The Amazon Simple Storage Service (Amazon S3) bucket to which the MP3
    /// file produced by Polly is uploaded.
    #[structopt(short, long)]
    bucket: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Converts the text in the input file to an MP3 audio file
/// with the ".txt" extension replaced by ".mp3".
/// If successful, returns the name of the audio file.
// snippet-start:[telephone.rust.main-convert_text]
async fn convert_text(verbose: bool, client: &aws_sdk_polly::Client, filename: &str) -> String {
    if verbose {
        println!("Opening text file {} to convert to audio", filename);
        println!();
    }

    let content = fs::read_to_string(filename);

    let resp = client
        .synthesize_speech()
        .output_format(OutputFormat::Mp3)
        .text(content.unwrap())
        .voice_id(VoiceId::Joanna)
        .send();

    // Get MP3 data from response and save it to a file.
    let mut blob = resp
        .await
        .unwrap()
        .audio_stream
        .collect()
        .await
        .expect("failed to read data");

    let parts: Vec<&str> = filename.split('.').collect();
    let out_file = format!("{}{}", String::from(parts[0]), ".mp3");

    let mut file = tokio::fs::File::create(&out_file)
        .await
        .expect("failed to create file");

    file.write_all_buf(&mut blob)
        .await
        .expect("failed to write to file");

    out_file
}
// snippet-end:[telephone.rust.main-convert_text]

/// Saves the file in the Amazon S3 bucket.
// snippet-start:[telephone.rust.main-save_mp3_file]
async fn save_mp3_file(
    verbose: bool,
    client: &aws_sdk_s3::Client,
    bucket: &str,
    filename: &str,
) -> String {
    if verbose {
        println!("Saving file {} to bucket {}", filename, bucket);
        println!();
    }
    let body = aws_sdk_s3::types::ByteStream::from_path(Path::new(filename))
        .await
        .unwrap();

    client
        .put_object()
        .bucket(bucket)
        .key(filename)
        .body(body)
        .send()
        .await
        .unwrap();

    let mut uri: String = "s3://".to_owned();
    uri.push_str(bucket);
    uri.push('/');
    uri.push_str(filename);

    uri
}
// snippet-end:[telephone.rust.main-save_mp3_file]

/// Converts the contents of an MP3 audio file into text.
// snippet-start:[telephone.rust.main-convert_audio]
async fn convert_audio(
    verbose: bool,
    client: &aws_sdk_transcribe::Client,
    uri: &str,
    job_name: &str,
) -> Result<()> {
    if verbose {
        println!("Opening audio file location {} to get text", uri);
        println!();
    }

    let media = Media::builder().media_file_uri(uri).build();

    client
        .start_transcription_job()
        .transcription_job_name(job_name)
        .media(media)
        .media_format(MediaFormat::Mp3)
        .language_code(LanguageCode::EnUs)
        .send()
        .await
        .unwrap();

    let mut snooze: u64 = 100;
    let mut snooze_total = snooze;

    let mut found = false;

    println!("Waiting for transcription job to finish.");
    while !found {
        let resp = client
            .get_transcription_job()
            .transcription_job_name(job_name)
            .send()
            .await
            .unwrap();

        let job = resp.transcription_job.unwrap();

        let status = job.transcription_job_status.unwrap();

        if status == TranscriptionJobStatus::Completed || status == TranscriptionJobStatus::Failed {
            if verbose {
                println!("Waited {} milliseconds for job to finish", snooze_total);
            }

            if status == TranscriptionJobStatus::Completed {
                if !verbose {
                    println!();
                }
                // Get translation and show it.
                println!("Transcript:");
                let uri = job.transcript.unwrap().transcript_file_uri.unwrap();
                if verbose {
                    println!("Got URI for transcription: {}", uri);
                    println!();
                }

                let json_body = reqwest::get(uri).await.unwrap().text().await.unwrap();

                if verbose {
                    println!("body = {:?}", json_body);
                    println!();
                }

                let v: Value = serde_json::from_str(&json_body)?;

                println!("{}", v["results"]["transcripts"][0]["transcript"]);
            }

            found = true;
        } else {
            snooze *= 2;
            snooze_total += snooze;

            tokio::time::sleep(Duration::from_millis(snooze)).await;
        }
    }

    Ok(())
}
// snippet-end:[telephone.rust.main-convert_audio]

/// Synthesizes a plain text (UTF-8) input file to an audio file, converts that audio file to text, and displays the text.
/// # Arguments
///
/// * `-f FILENAME` - The name of the input file.
///    The output is saved in PCM format in a file with the same basename, but with a __pcm__ extension.
/// * `-b BUCKET` - The Amazon S3 bucket to which the MP3 file is uploaded.
/// * `-j JOB-NAME` - The name of the job. Must be unique.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<()> {
    tracing_subscriber::fmt::init();

    let Opt {
        bucket,
        filename,
        job_name,
        region,
        verbose,
    } = Opt::from_args();

    // Copy the Region for each service client.
    let polly_region = region.clone();
    let s3_region = region.clone();
    let transcribe_region = region.clone();

    // Create region provider for each service client.
    let polly_region_provider =
        RegionProviderChain::first_try(polly_region.map(aws_sdk_polly::Region::new))
            .or_default_provider()
            .or_else(aws_sdk_polly::Region::new("us-west-2"));

    let s3_region_provider =
        RegionProviderChain::first_try(s3_region.map(aws_sdk_polly::Region::new))
            .or_default_provider()
            .or_else(aws_sdk_polly::Region::new("us-west-2"));

    let transcribe_region_provider =
        RegionProviderChain::first_try(transcribe_region.map(aws_sdk_transcribe::Region::new))
            .or_default_provider()
            .or_else(aws_sdk_transcribe::Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Polly client version:     {}", aws_sdk_polly::PKG_VERSION);
        println!("S3 client version:        {}", aws_sdk_s3::PKG_VERSION);
        println!(
            "Transcribe client version {}",
            aws_sdk_transcribe::PKG_VERSION
        );
        println!(
            "Region:                   {}",
            polly_region_provider.region().await.unwrap().as_ref()
        );
        println!("Filename:                 {}", &filename);
        println!("Bucket:                   {}", &bucket);
        println!("Job name:                 {}", &job_name);
        println!();
    }

    // Create configurations for each service client.
    let polly_shared_config = aws_config::from_env()
        .region(polly_region_provider)
        .load()
        .await;
    let s3_shared_config = aws_config::from_env()
        .region(s3_region_provider)
        .load()
        .await;
    let transcribe_shared_config = aws_config::from_env()
        .region(transcribe_region_provider)
        .load()
        .await;

    // Create service clients.
    let polly_client = aws_sdk_polly::Client::new(&polly_shared_config);
    let s3_client = aws_sdk_s3::Client::new(&s3_shared_config);
    let transcribe_client = aws_sdk_transcribe::Client::new(&transcribe_shared_config);

    // Convert text to MP3 file.
    let mp3_file = convert_text(verbose, &polly_client, &filename).await;

    // Save MP3 file in Amazon S3 bucket.
    let uri = save_mp3_file(verbose, &s3_client, &bucket, &mp3_file).await;

    // Transcribe MP3 file and show results.
    convert_audio(verbose, &transcribe_client, &uri, &job_name)
        .await
        .expect("Could not convert audio file to text");

    Ok(())
}
