/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_lambda::{Client, Error};
use lambda_code_examples::{make_client, ArnOpt};
use structopt::StructOpt;

// Runs a Lambda function.
// snippet-start:[lambda.rust.invoke-function]
async fn run_function(client: &Client, arn: &str) -> Result<(), Error> {
    client.invoke().function_name(arn).send().await?;

    println!("Invoked function.");

    Ok(())
}
// snippet-end:[lambda.rust.invoke-function]

/// Invokes a Lambda function by its ARN.
/// # Arguments
///
/// * `-a ARN` - The ARN of the Lambda function.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let ArnOpt { arn, base } = ArnOpt::from_args();

    let client = make_client(base).await;

    run_function(&client, &arn).await
}
