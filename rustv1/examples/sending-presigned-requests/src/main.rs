// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::presigning::{PresignedRequest, PresigningConfig};
use aws_sdk_s3::{config::Region, meta::PKG_VERSION, Client};
use clap::Parser;
use hyper::http::{HeaderName, HeaderValue};
use hyper_util::rt::TokioExecutor;
use std::error::Error;
use std::time::Duration;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The data to be uploaded to S3.
    #[structopt(short, long)]
    body: String,

    /// The name of the bucket.
    #[structopt(short, long)]
    bucket: String,

    /// The object key.
    #[structopt(short, long)]
    object: String,

    /// How long in seconds before the presigned request should expire.
    #[structopt(short, long, default_value = "900")]
    expires_in: u64,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Adds an object to a bucket and returns a public URI.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
/// * `-b BUCKET` - The bucket where the object is uploaded.
/// * `-o OBJECT` - The name of the file to upload to the bucket.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-e EXPIRES_IN]` - The amount of time the presigned request should be valid for.
///   If not given, this defaults to 15 minutes.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        body,
        bucket,
        object,
        expires_in,
        verbose,
    } = Opt::parse();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    println!();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!("Region:            {}", shared_config.region().unwrap());
        println!("Bucket:            {}", &bucket);
        println!("Object:            {}", &object);
        println!("Body:              {}", &body);
        println!("Expires in:        {} seconds", expires_in);
        println!();
    }

    let expires_in = Duration::from_secs(expires_in);

    let presigned_request = client
        .put_object()
        .bucket(bucket)
        .key(object)
        .presigned(PresigningConfig::expires_in(expires_in)?)
        .await?;

    println!("Object URI: {}", presigned_request.uri());

    // Presigned requests can be used in several ways. Here are a few examples:
    print_as_curl_request(&presigned_request, Some(body.as_str()));
    send_presigned_request_with_reqwest(&presigned_request, body.clone()).await;
    send_presigned_request_with_hyper(presigned_request, body.clone()).await;

    Ok(())
}

/// This function demonstrates how you can convert a presigned request into a cURL command
/// that you can run from your terminal of choice.
///
/// _NOTE:_ This only prints out the command, it's up to you to copy-paste it and run it.
fn print_as_curl_request(presigned_req: &PresignedRequest, body: Option<&str>) {
    println!(
        "curl -X {} {} \\",
        presigned_req.method(),
        presigned_req.uri()
    );

    if let Some(body) = body {
        println!("-d '{}' \\", body);
    }

    for (name, value) in presigned_req.headers() {
        // This value conversion method is naïve and will drop values that aren't valid UTF8
        // It's only here for demonstration purposes; Don't use this unless you're confident
        // that your header values are valid UTF-8
        println!("-H '{}: {}' \\", name, value)
    }

    println!("--verbose");
}

/// This function demonstrates how you can send a presigned request using [hyper](https://crates.io/crates/hyper)
async fn send_presigned_request_with_hyper(req: PresignedRequest, body: String) {
    let conn = hyper_tls::HttpsConnector::new();
    let client = hyper_util::client::legacy::Client::builder(TokioExecutor::new()).build(conn);
    let req = req.into_http_1x_request(body);

    let res = client.request(req).await;

    match res {
        Ok(res) => {
            println!("Response: {:?}", res)
        }
        Err(err) => {
            println!("Error: {}", err)
        }
    }
}

/// This function demonstrates how you can send a presigned request using [reqwest](https://crates.io/crates/reqwest)
async fn send_presigned_request_with_reqwest(
    req: &PresignedRequest,
    body: impl Into<reqwest::Body>,
) {
    let client = reqwest::Client::new();
    let res = client
        .request(req.method().parse().expect("converted method"), req.uri())
        .headers(
            req.headers()
                .map(|(name, value)| {
                    (
                        HeaderName::try_from(name).expect("converted header name"),
                        HeaderValue::from_str(value).expect("converted header value"),
                    )
                })
                .collect(),
        )
        .body(body)
        .send()
        .await;

    match res {
        Ok(res) => {
            println!("Response: {:?}", res)
        }
        Err(err) => {
            println!("Error: {}", err)
        }
    }
}
