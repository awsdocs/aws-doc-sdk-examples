/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_ssm::model::ParameterType;
use aws_sdk_ssm::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The parameter name.
    #[structopt(short, long)]
    name: String,

    /// The parameter value.
    #[structopt(short, long)]
    parameter_value: String,

    /// The parameter title (description).
    #[structopt(short, long)]
    title: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Creates a parameter.
// snippet-start:[ssm.rust.create-parameter]
async fn make_parameter(
    client: &Client,
    name: &str,
    value: &str,
    description: &str,
) -> Result<(), Error> {
    let resp = client
        .put_parameter()
        .overwrite(true)
        .r#type(ParameterType::String)
        .name(name)
        .value(value)
        .description(description)
        .send()
        .await?;

    println!("Success! Parameter now has version: {}", resp.version());

    Ok(())
}
// snippet-end:[ssm.rust.create-parameter]

/// Creates a new AWS Systems Manager parameter in the Region.
/// # Arguments
///
/// * `-n NAME` - The name of the parameter.
/// * `-p PARAMETER_VALUE` - The value of the parameter.
/// * `-t TITLE` - The description of the parameter.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        name,
        parameter_value,
        title,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("SQS client version:   {}", PKG_VERSION);
        println!(
            "Region:               {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Parameter name:       {}", &name);
        println!("Paramter value:       {}", &parameter_value);
        println!("Paramter description: {}", &title);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    make_parameter(&client, &name, &parameter_value, &title).await
}
