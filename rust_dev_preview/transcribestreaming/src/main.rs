/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use async_stream::stream;
use aws_config::meta::region::RegionProviderChain;
use aws_sdk_transcribestreaming::model::{
    AudioEvent, AudioStream, LanguageCode, MediaEncoding, TranscriptResultStream,
};
use aws_sdk_transcribestreaming::types::Blob;
use aws_sdk_transcribestreaming::{Client, Error, Region, PKG_VERSION};
use bytes::BufMut;
use std::time::Duration;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the audio file.
    #[structopt(short, long)]
    audio_file: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

const CHUNK_SIZE: usize = 8192;

/// Transcribes an audio file to text.
/// # Arguments
///
/// * `-a AUDIO_FILE` - The name of the audio file.
///   It must be a WAV file, which is converted to __pcm__ format for Amazon Transcribe.
///   Amazon transcribe also supports __ogg-opus__ and __flac__ formats.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        audio_file,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Transcribe client version: {}", PKG_VERSION);
        println!(
            "Region:                    {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Audio filename:            {}", &audio_file);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    let input_stream = stream! {
        let pcm = pcm_data(&*audio_file);
        for chunk in pcm.chunks(CHUNK_SIZE) {
            // Sleeping isn't necessary, but emphasizes the streaming aspect of this
            tokio::time::sleep(Duration::from_millis(100)).await;
            yield Ok(AudioStream::AudioEvent(AudioEvent::builder().audio_chunk(Blob::new(chunk)).build()));
        }
    };

    let mut output = client
        .start_stream_transcription()
        .language_code(LanguageCode::EnGb)
        .media_sample_rate_hertz(8000)
        .media_encoding(MediaEncoding::Pcm)
        .audio_stream(input_stream.into())
        .send()
        .await?;

    let mut full_message = String::new();
    while let Some(event) = output.transcript_result_stream.recv().await? {
        match event {
            TranscriptResultStream::TranscriptEvent(transcript_event) => {
                let transcript = transcript_event.transcript.unwrap();
                for result in transcript.results.unwrap_or_default() {
                    if result.is_partial {
                        if verbose {
                            println!("Partial: {:?}", result);
                        }
                    } else {
                        let first_alternative = &result.alternatives.as_ref().unwrap()[0];
                        full_message += first_alternative.transcript.as_ref().unwrap();
                        full_message.push('\n');
                    }
                }
            }
            otherwise => panic!("received unexpected event type: {:?}", otherwise),
        }
    }
    println!("\nFully transcribed message:\n\n{}", full_message);

    Ok(())
}

fn pcm_data(audio_file: &str) -> Vec<u8> {
    let reader = hound::WavReader::open(audio_file).unwrap();
    let samples_result: hound::Result<Vec<i16>> = reader.into_samples::<i16>().collect();

    let mut pcm: Vec<u8> = Vec::new();
    for sample in samples_result.unwrap() {
        pcm.put_i16_le(sample);
    }
    pcm
}
