/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::model::{Delete, ObjectIdentifier};
use aws_sdk_s3::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the bucket.
    #[structopt(short, long)]
    bucket: String,

    /// The objects to delete.
    #[structopt(short, long)]
    objects: Vec<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Deletes objects from a bucket.
// snippet-start:[s3.rust.delete-objects]
async fn remove_objects(client: &Client, bucket: &str, objects: Vec<String>) -> Result<(), Error> {
    let mut delete_objects: Vec<ObjectIdentifier> = vec![];

    for obj in objects {
        let obj_id = ObjectIdentifier::builder().set_key(Some(obj)).build();
        delete_objects.push(obj_id);
    }

    let delete = Delete::builder().set_objects(Some(delete_objects)).build();

    client
        .delete_objects()
        .bucket(bucket)
        .delete(delete)
        .send()
        .await?;

    println!("Objects deleted.");

    Ok(())
}
// snippet-end:[s3.rust.delete-objects]

/// Removes objects from an Amazon S3 bucket.
/// # Arguments
///
/// * `-b BUCKET` - The name of the bucket.
/// * `-o OBJECTS` - The names of the objects to delete.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        bucket,
        objects,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!(
            "Region:            {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Bucket:            {}", &bucket);
        println!("Objects:           {:?}", &objects);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    remove_objects(&client, &bucket, objects).await
}
