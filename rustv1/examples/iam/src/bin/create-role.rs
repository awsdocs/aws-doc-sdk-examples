/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_iam::{config::Region, meta::PKG_VERSION, Client, Error};
use clap::Parser;
use std::fs;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the role.
    #[structopt(short, long)]
    name: String,

    /// The name of the file containing the policy document.
    #[structopt(short, long)]
    policy_file: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Creates a role.
// snippet-start:[iam.rust.create-role]
async fn make_role(client: &Client, policy: &str, name: &str) -> String {
    let resp = client
        .create_role()
        .assume_role_policy_document(policy)
        .role_name(name)
        .send()
        .await;

    match resp {
        Ok(output) => {
            format!("Created role with ARN {}", output.role().unwrap().arn())
        }
        Err(err) => format!("Error creating role: {:?}", err),
    }
}
// snippet-end:[iam.rust.create-role]

/// Creates an IAM role in the Region.
///
/// # Arguments
///
/// * `-a ACCOUNT-ID` - Your account ID.
/// * `-b BUCKET` - The name of the bucket where Config stores information about resources.
/// * `-n NAME` - The name of the role.
/// * `-p POLICY-NAME` - The name of the JSON file containing the policy document.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        name,
        policy_file,
        region,
        verbose,
    } = Opt::parse();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("IAM client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Role name:          {}", &name);
        println!("Policy doc filename {}", &policy_file);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    // Read policy doc from file as a string
    let policy_doc = fs::read_to_string(policy_file).expect("Unable to read file");

    let response = make_role(&client, policy_doc.as_str(), &name).await;
    println!("{response}");

    Ok(())
}

#[cfg(test)]
mod test {
    use crate::make_role;

    #[tokio::test]
    async fn test_make_role() {
        let client = sdk_examples_test_utils::single_shot_client!(
            sdk: aws_sdk_iam,
            request: "request body",
            status: 500,
            response: "error body"
        );

        let response = make_role(&client, "{}", "test_role").await;
        assert!(response.starts_with("Error creating role: "));
    }
}
