/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_eks::model::VpcConfigRequest;
use aws_sdk_eks::Region;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    region: Option<String>,

    #[structopt(short, long)]
    cluster_name: String,

    /// Role ARN for the cluster
    /// To create a role-arn:
    ///
    /// 1. Follow instructions to create an IAM role:
    /// https://docs.aws.amazon.com/eks/latest/userguide/service_IAM_role.html
    ///
    /// 2. Copy role arn
    #[structopt(long)]
    role_arn: String,

    /// subnet id
    ///
    /// At least two subnet ids must be specified. The subnet ids must be in two separate AZs
    #[structopt(short, long)]
    subnet_id: Vec<String>,
}

#[tokio::main]
#[allow(clippy::result_large_err)]
async fn main() -> Result<(), aws_sdk_eks::Error> {
    let Opt {
        region,
        cluster_name,
        role_arn,
        subnet_id,
    } = Opt::from_args();
    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = aws_sdk_eks::Client::new(&shared_config);

    let cluster = client
        .create_cluster()
        .name(&cluster_name)
        .role_arn(role_arn)
        .resources_vpc_config(
            VpcConfigRequest::builder()
                .set_subnet_ids(Some(subnet_id))
                .build(),
        )
        .send()
        .await?;
    println!("cluster created: {:?}", cluster);

    let cluster_deleted = client.delete_cluster().name(&cluster_name).send().await?;
    println!("cluster deleted: {:?}", cluster_deleted);
    Ok(())
}
