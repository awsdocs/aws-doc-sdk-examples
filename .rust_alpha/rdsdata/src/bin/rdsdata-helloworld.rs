/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_rdsdata::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The SQL query string.
    #[structopt(short, long)]
    query: String,

    /// The Amazon Resource Name (ARN) of your Amazon Aurora Serverless DB cluster.
    #[structopt(short, long)]
    cluster_arn: String,

    /// The ARN of the AWS Secrets Manager secret.
    #[structopt(short, long)]
    secret_arn: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Query a cluster.
// snippet-start:[rdsdata.rust.rdsdata-helloworld]
async fn query_cluster(
    client: &Client,
    cluster_arn: &str,
    query: &str,
    secret_arn: &str,
) -> Result<(), Error> {
    let st = client
        .execute_statement()
        .resource_arn(cluster_arn)
        .database("postgres") // Do not confuse this with db instance name
        .sql(query)
        .secret_arn(secret_arn);

    let result = st.send().await?;

    println!("{:?}", result);
    println!();

    Ok(())
}
// snippet-end:[rdsdata.rust.rdsdata-helloworld]

/// Sends a query to an Aurora serverless cluster in the Region.
/// # Arguments
///
/// * `-q QUERY` - The SQL query to run against the cluster.
///    It should look something like: __"SELECT * FROM pg_catalog.pg_tables limit 1"__.
///    Don't forget you'll likely have to escape some characters.
/// * `-c CLUSTER_ARN` - The ARN of your Aurora Serverless DB cluster.
///    It should look something like __arn:aws:rds:us-west-2:AWS_ACCOUNT:cluster:database-2__.
/// * `-s SECRET_ARN` - The ARN of the Secrets Manager secret.
///    It should look something like: __arn:aws:secretsmanager:us-west-2:AWS_ACCOUNT:secret:database2/test/postgres-b8maVb__.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        cluster_arn,
        query,
        region,
        secret_arn,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("RDS data client version: {}", PKG_VERSION);
        println!(
            "Region:                  {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Cluster ARN:             {}", &cluster_arn);
        println!("Secrets ARN:             {}", &secret_arn);
        println!("Query:");
        println!("  {}", &query);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    query_cluster(&client, &cluster_arn, &query, &secret_arn).await
}
