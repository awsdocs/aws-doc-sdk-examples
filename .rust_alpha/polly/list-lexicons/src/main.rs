/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use polly::{Client, Config, Region};

use aws_types::region::{ProvideRegion};

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Activate verbose mode
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the Amazon Polly lexicons in the region.
/// # Arguments
///
/// * `[-d DEFAULT-REGION]` - The region containing the lexicons.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt { default_region, verbose } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("polly client version: {}\n", polly::PKG_VERSION);
        println!("Region:      {:?}", &region);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();

    let client = Client::from_conf(config);

    match client.list_lexicons().send().await {
        Ok(resp) => {
            println!("Lexicons:");
            let lexicons = resp.lexicons.unwrap_or_default();

            for lexicon in &lexicons {
                println!(
                    "  Name:     {}",
                    lexicon.name.as_deref().unwrap_or_default()
                );
                println!(
                    "  Language: {:?}\n",
                    lexicon
                        .attributes
                        .as_ref()
                        .map(|attrib| attrib
                            .language_code
                            .as_ref()
                            .expect("languages must have language codes"))
                        .expect("languages must have attributes")
                );
            }
            println!("Found {} lexicons.\n", lexicons.len());
        }
        Err(e) => {
            println!("Got an error listing lexicons:");
            println!("{}", e);
            process::exit(1);
        }
    };
}
