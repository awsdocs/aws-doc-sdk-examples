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

    /// Whether to display additional runtime information
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the name and status of your CloudFormation stacks in the region.
/// # Arguments
///
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), cloudformation::Error> {
    tracing_subscriber::fmt::init();

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
        println!(
            "CloudFormation client version: {}\n",
            cloudformation::PKG_VERSION
        );
        println!("Region:                   {:?}", &region);
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    let stacks = client.list_stacks().send().await?;

    for s in stacks.stack_summaries.unwrap_or_default() {
        println!("{}", s.stack_name.as_deref().unwrap_or_default());
        println!("  Status: {:?}", s.stack_status.unwrap());
        println!();
    }

    Ok(())
}
