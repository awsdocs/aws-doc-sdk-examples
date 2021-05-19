/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use kinesis::{Client, Config, Region};

use aws_types::region::{ProvideRegion};

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    default_region: Option<String>,

    #[structopt(short, long)]
    info: String,

    #[structopt(short, long)]
    key: String,

    #[structopt(short, long)]
    name: String,

    #[structopt(short, long)]
    verbose: bool,
}

#[tokio::main]
async fn main() {
    let Opt {
        info,
        key,
        name,
        default_region,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("Kinesis client version: {}\n", kinesis::PKG_VERSION);
        println!("Region:      {:?}", &region);
        println!("Info:");
        println!("\n{}\n", info);
        println!("Partition key: {}", key);
        println!("Stream name:   {}", name);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();

    let client = Client::from_conf(config);

    let blob = kinesis::Blob::new(info);

    match client
        .put_record()
        .data(blob)
        .partition_key(key)
        .stream_name(name)
        .send()
        .await
    {
        Ok(_) => println!("Put record into stream."),
        Err(e) => {
            println!("Got an error putting record:");
            println!("{}", e);
            process::exit(1);
        }
    };
}
