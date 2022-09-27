/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_lambda::Error;
use lambda_code_examples::{make_client, make_config, Opt as BaseOpt};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    #[structopt(flatten)]
    base: BaseOpt,

    /// Just show runtimes for indicated language.
    /// dotnet, go, node, java, etc.
    #[structopt(short, long)]
    language: Option<String>,
}

/// Lists the ARNs and runtimes of all Lambda functions in all Regions.
// snippet-start:[lambda.rust.list-all-function-runtimes]
async fn show_lambdas(language: &str, region: &str, verbose: bool) -> Result<(), Error> {
    let language = language.to_ascii_lowercase();
    let client = make_client(BaseOpt {
        region: Some(region.to_string()),
        verbose: false,
    })
    .await;
    let resp = client.list_functions().send().await?;
    let resp_functions = resp.functions.unwrap_or_default();
    let total_functions = resp_functions.len();

    let functions = resp_functions
        .iter()
        .map(|func| {
            (
                func,
                func.runtime()
                    .map(|rt| String::from(rt.as_ref()))
                    .unwrap_or_else(|| String::from("Unknown")),
            )
        })
        .filter(|(_, runtime)| {
            language.is_empty() || runtime.to_ascii_lowercase().contains(&language)
        });

    for (func, rt_str) in functions {
        println!("  ARN:     {}", func.function_arn().unwrap());
        println!("  Runtime: {}", rt_str);
        println!();
    }

    let num_functions = resp_functions.len();
    if num_functions > 0 || verbose {
        println!(
            "Found {} function(s) (out of {}) in {} region.",
            num_functions, total_functions, region,
        );
        println!();
    }

    Ok(())
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

    let Opt { language, base } = Opt::from_args();
    let language = language.as_deref().unwrap_or_default();
    let verbose = base.verbose;

    let sdk_config = make_config(base).await;

    let ec2_client = aws_sdk_ec2::Client::new(&sdk_config);

    match ec2_client.describe_regions().send().await {
        Ok(resp) => {
            for region in resp.regions.unwrap_or_default() {
                match region.region_name() {
                    Some(region) => {
                        show_lambdas(language, region, verbose)
                            .await
                            .unwrap_or_else(|err| eprintln!("{:?}", err));
                    }
                    None => {}
                }
            }
        }
        Err(err) => eprintln!("Failed to describe ec2 regions: {}", err),
    }

    Ok(())
}
