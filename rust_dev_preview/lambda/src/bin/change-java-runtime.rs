/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_lambda::model::Runtime;
use aws_sdk_lambda::{Client, Error};
use lambda_code_examples::{make_client, ArnOpt};
use structopt::StructOpt;

// Change Java runtime in Lambda function.
// snippet-start:[lambda.rust.change-java-runtime]
async fn set_runtimes(client: &Client, arn: &str) -> Result<(), Error> {
    // Get function's runtime
    let resp = client.list_functions().send().await?;

    for function in resp.functions.unwrap_or_default() {
        // We only change the runtime for the specified function.
        if arn == function.function_arn.unwrap() {
            let rt = function.runtime.unwrap();
            // We only change the Java runtime.
            if [Runtime::Java11, Runtime::Java8].contains(&rt) {
                // Change it to Java8a12 (Corretto).
                println!("Original runtime: {:?}", rt);
                let result = client
                    .update_function_configuration()
                    .function_name(function.function_name.unwrap())
                    .runtime(Runtime::Java8al2)
                    .send()
                    .await?;

                let result_rt = result.runtime.unwrap();
                println!("New runtime: {:?}", result_rt);
            }
        }
    }

    Ok(())
}
// snippet-end:[lambda.rust.change-java-runtime]

/// Sets a Lambda function's Java runtime to Corretto.
/// # Arguments
///
/// * `-a ARN` - The ARN of the Lambda function.
/// * `[-r -REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let ArnOpt { arn, base } = ArnOpt::from_args();

    let client = make_client(base).await;

    set_runtimes(&client, &arn).await
}
