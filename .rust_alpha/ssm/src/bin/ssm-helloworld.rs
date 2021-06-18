/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use ssm::model::ParameterType;
use ssm::{Client, Config, Region};

use aws_types::region::{EnvironmentProvider, ProvideRegion};

use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    region: Option<String>,

    /// The parameter name
    #[structopt(short, long)]
    name: String,

    /// The parameter value
    #[structopt(short, long)]
    parameter_value: String,

    /// The parameter description
    #[structopt(short, long)]
    description: String,

    /// Whether to display additional information
    #[structopt(short, long)]
    verbose: bool,
}

/// Creates a new AWS Systems Manager parameter.
/// # Arguments
///
/// * `-n NAME` - The name of the parameter.
/// * `-p PARAMETER_VALUE` - The value of the parameter.
/// * `-d DESCRIPTION` - The description of the parameter.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        name,
        parameter_value,
        description,
        region,
        verbose,
    } = Opt::from_args();

    let region = EnvironmentProvider::new()
        .region()
        .or_else(|| region.as_ref().map(|region| Region::new(region.clone())))
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("SSM client version:   {}", ssm::PKG_VERSION);
        println!("Region:               {:?}", &region);
        println!("Parameter name:       {}", name);
        println!("Paramter value:       {}", parameter_value);
        println!("Paramter description: {}", description);

        tracing_subscriber::fmt::init();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    match client
        .put_parameter()
        .overwrite(true)
        .r#type(ParameterType::String)
        .name(name)
        .value(parameter_value)
        .description(description)
        .send()
        .await
    {
        Ok(response) => {
            println!("Success! Parameter now has version: {}", response.version)
        }
        Err(error) => {
            println!("Got an error putting the parameter: {}", error);
            process::exit(1);
        }
    }
}
