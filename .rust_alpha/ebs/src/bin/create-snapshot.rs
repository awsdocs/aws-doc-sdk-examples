/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_ebs::model::ChecksumAlgorithm;
use aws_sdk_ebs::{ByteStream, Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
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

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("EBS version: {}", PKG_VERSION);
        println!("Description: {}", description);
        println!("Region:      {}", region.region().unwrap().as_ref());
        println!();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    let snapshot = client
        .start_snapshot()
        .description(description)
        .encrypted(false)
        .volume_size(1)
        .send()
        .await?;

    let snapshot_id = snapshot.snapshot_id.unwrap();
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

        client
            .put_snapshot_block()
            .snapshot_id(&snapshot_id)
            .block_index(idx as i32)
            .block_data(ByteStream::from(block))
            .checksum(checksum)
            .checksum_algorithm(ChecksumAlgorithm::ChecksumAlgorithmSha256)
            .data_length(EBS_BLOCK_SIZE as i32)
            .send()
            .await?;
    }
    client
        .complete_snapshot()
        .changed_blocks_count(2)
        .snapshot_id(&snapshot_id)
        .send()
        .await?;

    println!("Snapshot ID {}", snapshot_id);
    println!("The state is 'completed' when all of the modified blocks have been transferred to Amazon S3.");
    println!("Use the get-snapshot-state code example to get the state of the snapshot.");

    Ok(())
}
