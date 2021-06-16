/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
use std::fs;
use std::process;

use polly::model::{OutputFormat, VoiceId};
use polly::{Client, Config, Region};

use aws_types::region::{EnvironmentProvider, ProvideRegion};

use structopt::StructOpt;
use tokio::io::AsyncWriteExt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    region: Option<String>,

    /// The file containing the text to synthesize
    #[structopt(short, long)]
    filename: String,

    /// Whether to show additional output
    #[structopt(short, long)]
    verbose: bool,
}

/// Synthesizes UTF-8 input, plain text or SSML, to a stream of bytes in a file.
/// # Arguments
///
/// * `-f FILENAME` - The name of the file containing the text to synthesize.
///    The output is saved in MP3 format in a file with the same basename, but with an __mp3__ extension.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        filename,
        region,
        verbose,
    } = Opt::from_args();

    let region = EnvironmentProvider::new()
        .region()
        .or_else(|| region.as_ref().map(|region| Region::new(region.clone())))
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("polly client version: {}\n", polly::PKG_VERSION);
        println!("Region:   {:?}", &region);
        println!("Filename: {}", filename);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();

    let client = Client::from_conf(config);

    let content = fs::read_to_string(&filename);

    let resp = match client
        .synthesize_speech()
        .output_format(OutputFormat::Mp3)
        .text(content.unwrap())
        .voice_id(VoiceId::Joanna)
        .send()
        .await
    {
        Ok(output) => output,
        Err(e) => {
            println!("Got an error synthesizing speech:");
            println!("{}", e);
            process::exit(1);
        }
    };

    // Get MP3 data from response and save it
    let mut blob = resp
        .audio_stream
        .collect()
        .await
        .expect("failed to read data");

    let parts: Vec<&str> = filename.split('.').collect();
    let out_file = format!("{}{}", String::from(parts[0]), ".mp3");

    let mut file = tokio::fs::File::create(out_file)
        .await
        .expect("failed to create file");

    file.write_all_buf(&mut blob)
        .await
        .expect("failed to write to file");
}
