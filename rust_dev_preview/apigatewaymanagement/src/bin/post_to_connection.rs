/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_apigatewaymanagement::types::Blob;
use aws_sdk_apigatewaymanagement::{config, Client, Endpoint, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
/// AWS apigatewaymanagenent must be used with a custom endpoint, which this example demonstrates how to set.
///
/// Usage:
/// 1. Setup a Websocket API Gateway endpoint with a route configured.
/// 2. Connect to the route with `wscat`: `wscat -c wss://<api-id>.execute-api.<region>.amazonaws.com/<stage>/`
/// 3. Determine the connection ID (eg. by configuring your route to echo the connection ID into the websocket)
/// 4. Invoke this example. The `data` sent should appear in `wscat`
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,

    /// API ID for your API.
    #[structopt(short, long)]
    api_id: String,

    /// Deployment stage for your API.
    #[structopt(short, long)]
    stage: String,

    /// Connection ID to send data to.
    #[structopt(short, long)]
    connection_id: String,

    /// Data to send to the connection.
    #[structopt(short, long)]
    data: String,
}

// snippet-start:[apigatewaymanagement.rust.post_to_connection]
async fn send_data(
    client: &aws_sdk_apigatewaymanagement::Client,
    con_id: &str,
    data: &str,
) -> Result<(), aws_sdk_apigatewaymanagement::Error> {
    client
        .post_to_connection()
        .connection_id(con_id)
        .data(Blob::new(data))
        .send()
        .await?;

    Ok(())
}
// snippet-end:[apigatewaymanagement.rust.post_to_connection]

/// Sends the provided data to the specified connection.
///
/// # Arguments
///
/// * `-a API-ID` - The ID for your API.
/// * `-s STAGE` - The stage for your API.
/// * `-c CONNECTION-ID` - The ID of the connection.
/// * `-d DATA` - The data sent to the connection.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        verbose,
        api_id,
        stage,
        connection_id,
        data,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    let region = region_provider.region().await.expect("region must be set");
    if verbose {
        println!("APIGatewayManagement client version: {}", PKG_VERSION);
        println!("Region:                              {}", region.as_ref());
        println!("API ID:                              {}", api_id);
        println!("API stage:                           {}", stage);
        println!("Connection ID:                       {}", connection_id);
        println!("Data:");
        println!("  {}", data);

        println!();
    }

    // snippet-start:[apigatewaymanagement.rust.post_to_connection_client]
    let endpoint = Endpoint::immutable(format!(
        "https://{api_id}.execute-api.{region}.amazonaws.com/{stage}",
        api_id = api_id,
        region = region,
        stage = stage
    ))
    .expect("valid endpoint");

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let api_management_config = config::Builder::from(&shared_config)
        .endpoint_resolver(endpoint)
        .build();
    let client = Client::from_conf(api_management_config);
    // snippet-end:[apigatewaymanagement.rust.post_to_connection_client]

    send_data(&client, &connection_id, &data).await
}
