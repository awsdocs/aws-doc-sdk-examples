/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

// For command-line arguments.
use structopt::StructOpt;

use lambda::{Client, Config, Region};

use aws_types::region::ProvideRegion;

use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region. Overrides environment variable AWS_DEFAULT_REGION.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Whether to display additional runtime information
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the ARNs of your Lambda functions.
/// # Arguments
///
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
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
        println!("Lambda client version: {}", lambda::PKG_VERSION);
        println!("Region:                {:?}", &region);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    match client.list_functions().send().await {
        Ok(resp) => {
            println!("Function ARNs:");

            let functions = resp.functions.unwrap_or_default();

            for function in &functions {
                match &function.function_arn {
                    None => {}
                    Some(f) => {
                        println!("{}", f);
                    }
                }
            }

            println!("Found {} functions", functions.len());
        }
        Err(e) => {
            println!("Got an error listing functions:");
            println!("{}", e);
            process::exit(1);
        }
    };
}
