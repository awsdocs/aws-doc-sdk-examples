/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use sns::{Client, Config, Region};
use std::process::exit;

use aws_types::region::ProvideRegion;

use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region. Overrides environment variable AWS_DEFAULT_REGION.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Specifies the email address to subscribe to the topic
    #[structopt(short, long)]
    email_address: String,

    /// Whether to display additional runtime information
    #[structopt(short, long)]
    verbose: bool,
}

/// Subscribes an email address and publishes a message to a topic.
/// If the email address has not been confirmed for the topic,
/// a confirmation request is also sent to the email address.
/// # Arguments
///
/// * `-e EMAIL_ADDRESS` - The email address of a user subscribing to the topic.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), sns::Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        default_region,
        email_address,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("SNS client version: {}", sns::PKG_VERSION);
        println!("Region:             {:?}", &region);
        println!("Email address:      {}", &email_address);
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    let topics = client.list_topics().send().await?;
    let mut topics = topics.topics.unwrap_or_default();
    let topic_arn = match topics.pop() {
        Some(topic) => topic.topic_arn.expect("topics have ARNs"),
        None => {
            eprintln!("No topics in this account. Please create a topic to proceed");
            exit(1);
        }
    };

    println!("Receiving on topic with ARN: `{}`", topic_arn);

    let rsp = client
        .subscribe()
        .topic_arn(&topic_arn)
        .protocol("email")
        .endpoint(email_address)
        .send()
        .await?;

    println!("Added a subscription: {:?}", rsp);

    let rsp = client
        .publish()
        .topic_arn(&topic_arn)
        .message("hello sns!")
        .send()
        .await?;

    println!("Published message: {:?}", rsp);

    Ok(())
}
