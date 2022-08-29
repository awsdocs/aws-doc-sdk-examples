/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

// snippet-start:[s3.rust.s3-object-lambda-packages]
use aws_endpoint::partition;
use aws_endpoint::partition::endpoint;
use aws_endpoint::{CredentialScope, Partition, PartitionResolver};
use aws_sdk_s3 as s3;
// snippet-end:[s3.rust.s3-object-lambda-packages]

use aws_sdk_s3::{Client, Error, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// Your account #.
    #[structopt(short, long)]
    account: String,

    /// The endpoint.
    #[structopt(short, long)]
    endpoint: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Shows your buckets in the endpoint.
async fn show_buckets(client: &Client) -> Result<(), Error> {
    let resp = client.list_buckets().send().await?;
    let buckets = resp.buckets().unwrap_or_default();
    let num_buckets = buckets.len();

    for bucket in buckets {
        println!("{}", bucket.name().unwrap_or_default());
    }

    println!();
    println!("Found {} buckets.", num_buckets);

    Ok(())
}

// If you're using a FIPs region, add `-fips` after `s3-object-lambda`.
async fn make_uri(endpoint: &str, account: &str) -> &'static str {
    let mut uri = endpoint.to_string();
    uri.push('-');
    uri.push_str(account);
    uri.push_str(".s3-object-lambda.{region}.amazonaws.com");

    Box::leak(uri.into_boxed_str())
}

/// Lists your Amazon S3 buckets in the specified endpoint.
/// # Arguments
///
/// * `-a ACCOUNT` - Your AWS account number.
/// * `-e ENDPOINT` - The endpoint in which the client is created.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        account,
        endpoint,
        verbose,
    } = Opt::from_args();

    println!();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!("Account #:         {}", &account);
        println!("Endpoint:          {}", &endpoint);

        println!();
    }

    // snippet-start:[s3.rust.s3-object-lambda]
    // Create an endpoint resolver that creates S3 Object Lambda endpoints.
    let resolver = PartitionResolver::new(
        Partition::builder()
            .id("aws")
            // This regex captures the region prefix, such as the "us" in "us-east-1",
            //  from the client's current region. This captured value is later fed into
            //  the uri_template.
            // If your region isn't covered by the regex below,
            // you can find additional region capture regexes for other regions
            // at https://github.com/awslabs/aws-sdk-rust/blob/main/sdk/s3/src/aws_endpoint.rs.
            .region_regex(r#"^(us|eu|ap|sa|ca|me|af)\-\w+\-\d+$"#)
            .default_endpoint(endpoint::Metadata {
                uri_template: make_uri(&endpoint, &account).await,
                protocol: endpoint::Protocol::Https,
                signature_versions: endpoint::SignatureVersion::V4,
                // Important: The following overrides the credential scope so that request signing works.
                credential_scope: CredentialScope::builder()
                    .service("s3-object-lambda")
                    .build(),
            })
            .regionalized(partition::Regionalized::Regionalized)
            .build()
            .expect("valid partition"),
        vec![],
    );

    // Load configuration and credentials from the environment.
    let shared_config = aws_config::load_from_env().await;

    // Create an S3 config from the shared config and override the endpoint resolver.
    let s3_config = s3::config::Builder::from(&shared_config)
        .endpoint_resolver(resolver)
        .build();

    // Create an S3 client to send requests to S3 Object Lambda.
    let client = s3::Client::from_conf(s3_config);
    // snippet-end:[s3.rust.s3-object-lambda]

    show_buckets(&client).await
}
