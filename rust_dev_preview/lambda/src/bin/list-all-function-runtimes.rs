/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_lambda::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region in which the client is created.
    #[structopt(short, long)]
    region: Option<String>,

    /// Just show runtimes for indicated language.
    /// dotnet, go, node, java, etc.
    #[structopt(short, long)]
    language: Option<String>,

    /// Whether to display additional runtime information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the ARNs and runtimes of all Lambda functions in all Regions.
// snippet-start:[lambda.rust.list-all-function-runtimes]
async fn show_lambdas(verbose: bool, language: &str, reg: &'static str) {
    let region_provider = RegionProviderChain::default_provider().or_else(reg);
    let config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&config);

    let resp = client.list_functions().send().await;
    let functions = resp.unwrap().functions.unwrap_or_default();
    let max_functions = functions.len();
    let mut num_functions = 0;

    for function in functions {
        let rt_str: String = String::from(function.runtime().unwrap().as_ref());
        // If language is set (!= ""), show only those with that runtime.
        let ok = rt_str
            .to_ascii_lowercase()
            .contains(&language.to_ascii_lowercase());
        if ok || language.is_empty() {
            println!("  ARN:     {}", function.function_arn().unwrap());
            println!("  Runtime: {}", rt_str);
            println!();

            num_functions += 1;
        }
    }

    if num_functions > 0 || verbose {
        println!(
            "Found {} function(s) (out of {}) in {} region.",
            num_functions, max_functions, reg
        );
        println!();
    }
}
// snippet-end:[lambda.rust.list-all-function-runtimes]

/// Lists the ARNs and runtimes of your Lambda functions in all available regions.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        language,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("EC2 client version:    {}", aws_sdk_ec2::PKG_VERSION);
        println!("Lambda client version: {}", PKG_VERSION);
        println!(
            "Region:                {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    // Get list of available regions.

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let ec2_client = aws_sdk_ec2::Client::new(&shared_config);

    let resp = ec2_client.describe_regions().send().await;

    for region in resp.unwrap().regions.unwrap_or_default() {
        let reg: &'static str = Box::leak(region.region_name.unwrap().into_boxed_str());
        show_lambdas(verbose, language.as_deref().unwrap_or_default(), reg).await;
    }

    Ok(())
}
