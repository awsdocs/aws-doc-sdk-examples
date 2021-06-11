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

    /// The email address to send the message to.
    #[structopt(short, long)]
    email: String,

    /// The message to send.
    #[structopt(short, long)]
    message: String,

    /// Whether to display addtional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Sends a `hello sns!` message to the specified email address.
/// If the email address has not subscribed to the topic,
/// the receipient must confirm their address before they get a message.
/// # Arguments
///
/// * `-e EMAIL` - The email address to send the message to.
/// * `-m MESSAGE` - The message to send.
/// * `[-d DEFAULT-REGION]` - The AWS Region containing the Amazon SNS topic.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), sns::Error> {
    let Opt {
        default_region,
        email,
        message,
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
        println!("Email:              {}", email);
        println!("Message:            {}", message);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    let topics = client.list_topics().send().await?;
    let mut topics = topics.topics.unwrap_or_default();

    let topic_arn = match topics.pop() {
        Some(topic) => topic.topic_arn.expect("topics have ARNs"),
        None => {
            eprintln!("No topics in this account. Please create a topic to proceed");
            exit(1);
        }
    };

    println!("receiving on `{}`", topic_arn);

    let rsp = client
        .subscribe()
        .topic_arn(&topic_arn)
        .protocol("email")
        .endpoint(email)
        .send()
        .await?;

    println!("added a subscription: {:?}", rsp);

    let rsp = client
        .publish()
        .topic_arn(&topic_arn)
        .message(message)
        .send()
        .await?;

    println!("published a message: {:?}", rsp);

    Ok(())
}
