/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_types::region::ProvideRegion;

use cloudformation::{Client, Config, Region};

use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region. Overrides environment variable AWS_DEFAULT_REGION.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// The name of the stack.
    #[structopt(short, long)]
    stack_name: String,

    /// Whether to display additional runtime information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Retrieves the status of a CloudFormation stack in the region.
/// # Arguments
///
/// * `-s STACK-NAME` - The name of the stack.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), cloudformation::Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        default_region,
        stack_name,

        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!(
            "CloudFormation client version: {}",
            cloudformation::PKG_VERSION
        );
        println!("Region:                   {:?}", &region);
        println!("Stack:                    {}", &stack_name);
        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    let resp = client
        .describe_stacks()
        .stack_name(stack_name)
        .send()
        .await?;

    println!(
        "Stack status: {:?}",
        resp.stacks
            .unwrap_or_default()
            .pop()
            .unwrap()
            .stack_status
            .unwrap()
    );
    println!();

    Ok(())
}
