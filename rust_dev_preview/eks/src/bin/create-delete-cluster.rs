/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_eks::model::VpcConfigRequest;
use aws_sdk_eks::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The unique name to give to your cluster.
    #[structopt(short, long)]
    cluster_name: String,

    /// The Amazon Resource Name (ARN) of the IAM role that provides permissions
    /// for the Kubernetes control plane to make calls to AWS API operations on your behalf.
    #[structopt(long)]
    arn: String,

    /// The subnet IDs for your Amazon EKS nodes.
    #[structopt(short, long)]
    subnet_ids: Vec<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Create a cluster.
// snippet-start:[eks.rust.create-delete-cluster-create]
async fn make_cluster(
    client: &aws_sdk_eks::Client,
    name: &str,
    arn: &str,
    subnet_ids: Vec<String>,
) -> Result<(), aws_sdk_eks::Error> {
    let cluster = client
        .create_cluster()
        .name(name)
        .role_arn(arn)
        .resources_vpc_config(
            VpcConfigRequest::builder()
                .set_subnet_ids(Some(subnet_ids))
                .build(),
        )
        .send()
        .await?;
    println!("cluster created: {:?}", cluster);

    Ok(())
}
// snippet-end:[eks.rust.create-delete-cluster-create]

// Delete a cluster.
// snippet-start:[eks.rust.create-delete-cluster-delete]
async fn remove_cluster(
    client: &aws_sdk_eks::Client,
    name: &str,
) -> Result<(), aws_sdk_eks::Error> {
    let cluster_deleted = client.delete_cluster().name(name).send().await?;
    println!("cluster deleted: {:?}", cluster_deleted);

    Ok(())
}
// snippet-end:[eks.rust.create-delete-cluster-delete]

/// Creates and deletes an Amazon Elastic Kubernetes Service cluster.
/// # Arguments
///
/// * `-a ARN]` - The ARN of the role for the cluster.
/// * `-c CLUSTER-NAME` - The name of the cluster.
/// * `-s SUBNET-IDS` - The subnet IDs of the cluster.
///   You must specify at least two subnet IDs in separate AZs.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let Opt {
        arn,
        cluster_name,
        region,
        subnet_ids,
        verbose,
    } = Opt::from_args();

    if verbose {
        tracing_subscriber::fmt::init();
    }

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    if verbose {
        println!();
        println!("EKS client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );

        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    make_cluster(&client, &cluster_name, &arn, subnet_ids).await?;

    remove_cluster(&client, &cluster_name).await
}
