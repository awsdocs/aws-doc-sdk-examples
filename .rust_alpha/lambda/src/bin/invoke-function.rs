/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

// types from the Rust standard library
use std::{process, str};

// For command-line arguments.
use structopt::StructOpt;

// types from the AWS SDK for Rust
use aws_types::region::ProvideRegion;
use lambda::{error::InvokeErrorKind, Client, Config, Region, SdkError};

// types from other third-party crates
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region. Overrides environment variable AWS_DEFAULT_REGION.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Specifies the Lambda function's ARN
    #[structopt(short, long)]
    arn: String,

    /// Whether to display additional runtime information
    #[structopt(short, long)]
    verbose: bool,
}

/// Invokes a Lambda function by its ARN.
/// # Arguments
///
/// * `-a ARN` - The ARN of the Lambda function.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        arn,
        default_region,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("Lambda client version: {}", lambda::PKG_VERSION);
        println!("Region:                {:?}", &region);
        println!("Lambda function ARN:   {}", arn);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    // The AWS SDK for Rust service clients can be instantiated in a few
    // different ways. The way we're instantiating it here is to first build
    // a Config struct, then pass the Config to the Client.
    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    // we call the `invoke()` method on the client. The term 'invoke' is a bit
    // overloaded in the industry, but here it refers to a method named
    // 'Invoke' as part of the AWS Lambda API.
    // <https://docs.aws.amazon.com/lambda/latest/dg/API_Invoke.html>
    //
    // The Invoke API accepts several arguments, but we're only going to
    // include the required arguments in this example. The function name can
    // be the human friendly name with or without a version alias. It can be a
    // full ARN, or a partial ARN that includes the AWS account ID. following
    // are examples of each that will point to the same AWS resource.
    //
    // short name: "example-function"
    // short name with version alias: "example-function:LATEST"
    // partial ARN: "072326518754:function:example-function"
    // full ARN: "arn:aws:lambda:us-west-2:072326518754:function:example-function"
    //
    // We are going to use the full ARN to prevent any ambiguity in which
    // function will be invoked.
    match client.invoke().function_name(arn).send().await {
        // If the API call returns without an error, the Lambda Invoke API
        // returns a response object containing the following:
        //
        // StatusCode: The HTTP status code is in the 200 range for a
        //   successful request
        //
        // ExecutedVersion: The version of the function that executed. When
        //   you invoke a function with an alias, this indicates which version
        //   the alias resolved to.
        //
        // FunctionError: If present, indicates that an error occurred during
        //   function execution. Details about the error are included in the
        //   response payload.
        //
        // LogResult: The last 4 KB of the execution log, which is base64
        //    encoded.
        //
        // Payload: The response from the function, or an error object.
        //
        // For our example, we are just going to interact with the payload
        // portion of the response
        Ok(resp) => {
            // if the payload is not None, then we're safe to try and decode it
            // from the utf encoding we get back from the service into
            // something a bit more human friendly.
            if let Some(blob) = resp.payload {
                let s = str::from_utf8(blob.as_ref()).expect("invalid utf-8");
                println!("Response: {:?}", s);
            }
        }

        // If we want to handle a specific error in a specific way, we can
        // match it here. The SDK will automatically attempt to retry errors
        // that are considered 'retryable'. These types of errors generally
        // include responses where a service would return an HTTP 500 code to
        // indicate a server-side error. This means that the errors received by
        // your code here only need to address the rest of the errors that the
        // SDK wouldn't know how to 'retry', such as when the resource doesn't
        // exist.
        //
        // For our example, we will simply print that the function doesn't
        // exist and return a non-zero exit code to indicate the failure.
        Err(SdkError::ServiceError { err, .. })
            if matches!(err.kind, InvokeErrorKind::ResourceNotFoundError(_)) =>
        {
            println!("This lambda function does not exist: {}", err);
            process::exit(1);
        }
        // For any other kind of error, we will want to know more information
        // about it so that we can better understand how to handle it in the
        // future. We are going to print the error message and again return a
        // non-zero status code to indicate the failure.
        Err(err) => {
            println!("Got an error invoking the function: {}", err);
            process::exit(1);
        }
    };
}
