/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use secretsmanager::{Client, Config, Region};

use aws_types::region::{EnvironmentProvider, ProvideRegion};

use structopt::StructOpt;

use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the secret
    #[structopt(short, long)]
    name: String,

    /// The value of the secret
    #[structopt(short, long)]
    value: String,
    /// Whether to display additonal runtime information
    #[structopt(short, long)]
    info: bool,
}

#[tokio::main]
async fn main() {
    let Opt {
        info,
        name,
        region,
        value,
    } = Opt::from_args();

    let region = EnvironmentProvider::new()
        .region()
        .or_else(|| region.as_ref().map(|region| Region::new(region.clone())))
        .unwrap_or_else(|| Region::new("us-west-2"));

    if info {
        println!(
            "SecretsManager client version: {}\n",
            secretsmanager::PKG_VERSION
        );
        println!("Region:       {:?}", &region);
        println!("Secret name:  {}", name);
        println!("Secret value: {}", value);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();

    let client = Client::from_conf(config);

    match client
        .create_secret()
        .name(name)
        .secret_string(value)
        .send()
        .await
    {
        Ok(_) => println!("Created secret"),
        Err(e) => panic!("Failed to create secret: {}", e),
    };
}
