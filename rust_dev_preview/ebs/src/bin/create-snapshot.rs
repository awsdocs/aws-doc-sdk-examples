/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_ebs::model::ChecksumAlgorithm;
use aws_sdk_ebs::types::ByteStream;
use aws_sdk_ebs::{Client, Error, Region, PKG_VERSION};
use sha2::Digest;
use structopt::StructOpt;

/// Amazon EBS only supports one fixed size of block
const EBS_BLOCK_SIZE: usize = 524288;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The snapshot's description.
    #[structopt(short, long)]
    description: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Start the create snapshot process.
// snippet-start:[ebs.rust.create-snapshot-start]
async fn start(client: &Client, description: &str) -> Result<String, Error> {
    let snapshot = client
        .start_snapshot()
        .description(description)
        .encrypted(false)
        .volume_size(1)
        .send()
        .await?;

    Ok(snapshot.snapshot_id.unwrap())
}
// snippet-end:[ebs.rust.create-snapshot-start]

// Adds a block of data to a snapshot.
// snippet-start:[ebs.rust.create-snapshot-add_block]
async fn add_block(
    client: &Client,
    id: &str,
    idx: usize,
    block: Vec<u8>,
    checksum: &str,
) -> Result<(), Error> {
    client
        .put_snapshot_block()
        .snapshot_id(id)
        .block_index(idx as i32)
        .block_data(ByteStream::from(block))
        .checksum(checksum)
        .checksum_algorithm(ChecksumAlgorithm::ChecksumAlgorithmSha256)
        .data_length(EBS_BLOCK_SIZE as i32)
        .send()
        .await?;

    Ok(())
}
// snippet-end:[ebs.rust.create-snapshot-add_block]

// Finishes a snapshot.
// snippet-start:[ebs.rust.create-snapshot-finish]
async fn finish(client: &Client, id: &str) -> Result<(), Error> {
    client
        .complete_snapshot()
        .changed_blocks_count(2)
        .snapshot_id(id)
        .send()
        .await?;

    println!("Snapshot ID {}", id);
    println!("The state is 'completed' when all of the modified blocks have been transferred to Amazon S3.");
    println!("Use the get-snapshot-state code example to get the state of the snapshot.");

    Ok(())
}
// snippet-end:[ebs.rust.create-snapshot-finish]

/// Creates an Amazon Elastic Block Store snapshot using generated data.
/// # Arguments
///
/// * `-d DESCRIPTION` - The description of the snapshot.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        description,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("EBS client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Description:        {}", description);

        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    let snapshot_id = start(&client, &description).await.unwrap();

    let mut blocks = vec![];

    // Append a block of all 1s.
    let mut block: Vec<u8> = Vec::new();
    block.resize(EBS_BLOCK_SIZE, 1);
    blocks.push(block);

    // Append a block of all 0s.
    let mut block: Vec<u8> = Vec::new();
    block.resize(EBS_BLOCK_SIZE, 0);
    blocks.push(block);

    for (idx, block) in blocks.into_iter().enumerate() {
        let mut hasher = sha2::Sha256::new();
        hasher.update(&block);
        let checksum = hasher.finalize();
        let checksum = base64::encode(&checksum[..]);

        add_block(&client, &snapshot_id, idx, block, &checksum).await?;
    }

    finish(&client, &snapshot_id).await
}
