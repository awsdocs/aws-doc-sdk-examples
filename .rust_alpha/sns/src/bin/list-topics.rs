/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_types::region::ProvideRegion;
use sns::{Client, Config, Region};
use std::process::exit;
use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Whether to display addtional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists your Amazon SNS topics.
/// # Arguments
///
/// * `[-d DEFAULT-REGION]` - The AWS Region containing the Amazon SNS topic.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), sns::Error> {
    let Opt {
        default_region,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("SNS client version: {}", sns::PKG_VERSION);
        println!("AWS Region:         {:?}", &region);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    let topic_output = client.list_topics().send().await?;
    //let mut topics = topics.topics.unwrap_or_default();

    println!("Topic ARNs:");

    match topic_output.topics {
        None => {
            println!("Did not find any topics in this region.");
            exit(1);
        }
        Some(topics) => {
            for (_, topic) in topics.iter().enumerate() {
                match &topic.topic_arn {
                    None => {}
                    Some(arn) => {
                        println!("{}", arn);
                    }
                }
            }
        }
    }

    Ok(())
}
