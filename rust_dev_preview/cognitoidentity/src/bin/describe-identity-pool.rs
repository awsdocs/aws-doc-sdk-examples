/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_cognitoidentity::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The ID of the identity pool to describe.
    #[structopt(short, long)]
    identity_pool_id: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Describes an identity pool
// snippet-start:[cognitoidentity.rust.describe-identity-pool]
async fn describe_pool(client: &Client, id: &str) -> Result<(), Error> {
    let response = client
        .describe_identity_pool()
        .identity_pool_id(id)
        .send()
        .await?;

    let allow_classic = response.allow_classic_flow().unwrap_or_default();
    let allow_unauth_ids = response.allow_unauthenticated_identities();
    println!("  Allow classic flow                {}", allow_classic);
    println!("  Allow unauthenticated identities: {}", allow_unauth_ids);
    if let Some(providers) = response.cognito_identity_providers() {
        println!("  Identity Providers:");
        for provider in providers {
            let client_id = provider.client_id().unwrap_or_default();
            let name = provider.provider_name().unwrap_or_default();
            let server_side_check = provider.server_side_token_check().unwrap_or_default();

            println!("    Client ID:                {}", client_id);
            println!("    Name:                     {}", name);
            println!("    Service-side token check: {}", server_side_check);
            println!();
        }
    }

    let developer_provider = response.developer_provider_name().unwrap_or_default();
    let id = response.identity_pool_id().unwrap_or_default();
    let name = response.identity_pool_name().unwrap_or_default();

    println!("  Developer provider:               {}", developer_provider);
    println!("  Identity pool ID:                 {}", id);
    println!("  Identity pool name:               {}", name);

    if let Some(tags) = response.identity_pool_tags() {
        println!("  Tags:");
        for (key, value) in tags {
            println!("    key:   {}", key);
            println!("    value: {}", value);
        }
    }

    if let Some(open_id_arns) = response.open_id_connect_provider_ar_ns() {
        println!("  Open ID provider ARNs:");
        for arn in open_id_arns {
            println!("    {}", arn);
        }
    }

    if let Some(saml_arns) = response.saml_provider_ar_ns() {
        println!("  SAML provider ARNs:");
        for arn in saml_arns {
            println!("    {}", arn);
        }
    }

    // SupportedLoginProviders
    if let Some(login_providers) = response.supported_login_providers() {
        println!("  Supported login providers:");
        for (key, value) in login_providers {
            println!("    key:   {}", key);
            println!("    value: {}", value);
        }
    }

    println!();

    Ok(())
}
// snippet-end:[cognitoidentity.rust.describe-identity-pool]

/// Displays some information about an Amazon Cognito identitiy pool.
/// # Arguments
///
/// * `-i IDENTITY-POOL-ID` - The ID of the identity pool to describe.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        identity_pool_id,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("Cognito client version: {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Identity pool ID:       {}", identity_pool_id);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    describe_pool(&client, &identity_pool_id).await
}
