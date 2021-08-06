/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_polly::model::{Engine, Voice};
use aws_sdk_polly::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Displays a list of the voices and their language, and those supporting a neural engine, in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { region, verbose } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Polly client version: {}", PKG_VERSION);
        println!(
            "Region:               {}",
            region.region().unwrap().as_ref()
        );
        println!();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    let mut tok = None;
    let mut voices: Vec<Voice> = vec![];

    // Below is an an example of how pagination can be implemented manually.
    loop {
        let mut req = client.describe_voices();

        if let Some(tok) = tok {
            req = req.next_token(tok);
        }

        let resp = req.send().await?;

        for voice in resp.voices.unwrap_or_default() {
            println!(
                "I can speak as: {} in {:?}",
                voice.name.as_ref().unwrap(),
                voice.language_name.as_ref().unwrap()
            );
            voices.push(voice);
        }

        tok = match resp.next_token {
            Some(next) => Some(next),
            None => break,
        };
    }

    let neural_voices = voices
        .iter()
        .filter(|voice| {
            voice
                .supported_engines
                .as_deref()
                .unwrap_or_default()
                .contains(&Engine::Neural)
        })
        .map(|voice| voice.id.as_ref().unwrap())
        .collect::<Vec<_>>();

    println!();
    println!("Voices supporting a neural engine: {:?}", neural_voices);
    println!();

    Ok(())
}
