/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use ssm::{Client, Config, Region};

use aws_types::region::{EnvironmentProvider, ProvideRegion};

use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the names of your AWS Systems Manager parameters.
/// # Arguments
///
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt { region, verbose } = Opt::from_args();

    let region = EnvironmentProvider::new()
        .region()
        .or_else(|| region.as_ref().map(|region| Region::new(region.clone())))
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("SSM client version:   {}", ssm::PKG_VERSION);
        println!("Region:               {:?}", &region);

        tracing_subscriber::fmt::init();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    println!("Parameter names:");

    match client.describe_parameters().send().await {
        Ok(response) => {
            for param in response.parameters.unwrap().iter() {
                match &param.name {
                    None => {}
                    Some(n) => {
                        println!("  {}", n);
                    }
                }
            }
        }
        Err(error) => {
            println!("Got an error listing the parameter names: {}", error);
            process::exit(1);
        }
    }

    println!();
}
