/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_lambda::{Client, Error};
use lambda_code_examples::{make_client, Opt};
use structopt::StructOpt;

// Lists the ARNs of your Lambda functions.
// snippet-start:[lambda.rust.list-functions]
async fn show_arns(client: &Client) -> Result<(), Error> {
    let resp = client.list_functions().send().await?;

    println!("Function ARNs:");

    let functions = resp.functions().unwrap_or_default();
    let num_funcs = functions.len();

    for function in functions {
        println!("{}", function.function_arn().unwrap_or_default());
    }

    println!();
    println!("Found {} functions in the region", num_funcs);

    Ok(())
}
// snippet-end:[lambda.rust.list-functions]

/// Lists the Amazon Resource Names (ARNs) of your AWS Lambda functions in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let client = make_client(Opt::from_args()).await;

    show_arns(&client).await
}
