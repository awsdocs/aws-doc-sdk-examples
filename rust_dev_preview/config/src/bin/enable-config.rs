/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_config::model::{
    ConfigSnapshotDeliveryProperties, ConfigurationRecorder, DeliveryChannel,
    MaximumExecutionFrequency, RecordingGroup, ResourceType,
};
use aws_sdk_config::{Client, Error, Region, PKG_VERSION};
use std::process;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the Amazon bucket.
    #[structopt(short, long)]
    bucket: String,

    /// The ARN of the IAM role.
    #[structopt(short, long)]
    iam_arn: String,

    /// The ARN of the KMS key used to encrypt the data placed in the bucket.
    #[structopt(short, long)]
    kms_arn: String,

    /// The name of the configuration.
    #[structopt(default_value = "default", short, long)]
    name: String,

    /// The prefix for the bucket.
    #[structopt(short, long)]
    prefix: String,

    /// The ARN of the Amazon SNS topic.
    #[structopt(short, long)]
    sns_arn: String,

    /// The type of resource to record info about.
    #[structopt(default_value = "AWS::DynamoDB::Table", short, long)]
    type_: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Enables config.
// snippet-start:[config.rust.enable-config]
async fn enable_config(
    client: &Client,
    name: &str,
    kms_arn: &str,
    bucket: &str,
    sns_arn: &str,
    iam_arn: &str,
    prefix: &str,
) -> Result<(), Error> {
    // If we already have a configuration recorder in the Region, we cannot create another.
    let resp = client.describe_configuration_recorders().send().await?;

    let recorders = resp.configuration_recorders().unwrap_or_default();

    if !recorders.is_empty() {
        println!("You already have a configuration recorder in this region");
        println!("Use delete-configuration-recorder to delete it before you call this again.");

        for recorder in recorders {
            println!("Recorder: {}", recorder.name().unwrap_or_default());
        }

        process::exit(1);
    }

    // If we already have a delivery channel in the Region, we cannot create another.
    let resp = client.describe_delivery_channels().send().await?;

    let channels = resp.delivery_channels().unwrap_or_default();

    let num_channels = channels.len();

    if num_channels != 0 {
        println!("You already have a delivery channel in this region");
        println!("Use delete-delivery-channel to delete it before you call this again.");

        for channel in channels {
            println!("  Channel: {}", channel.name().unwrap_or_default());
        }

        process::exit(1);
    }

    let resource_types: Vec<ResourceType> = vec![ResourceType::Topic];

    let rec_group = RecordingGroup::builder()
        .set_resource_types(Some(resource_types))
        .build();

    let cfg_recorder = ConfigurationRecorder::builder()
        .name(name)
        .role_arn(iam_arn)
        .set_recording_group(Some(rec_group))
        .build();

    client
        .put_configuration_recorder()
        .configuration_recorder(cfg_recorder)
        .send()
        .await?;

    println!("Configured recorder.");

    // Create delivery channel
    let snapshot_props = ConfigSnapshotDeliveryProperties::builder()
        .delivery_frequency(MaximumExecutionFrequency::TwelveHours)
        .build();

    let delivery_channel = DeliveryChannel::builder()
        .name(name)
        .s3_bucket_name(bucket)
        .s3_key_prefix(prefix)
        .s3_kms_key_arn(kms_arn)
        .sns_topic_arn(sns_arn)
        .config_snapshot_delivery_properties(snapshot_props)
        .build();

    client
        .put_delivery_channel()
        .delivery_channel(delivery_channel)
        .send()
        .await?;

    println!("Configured delivery channel.");

    Ok(())
}
// snippet-end:[config.rust.enable-config]

/// Enables AWS Config for a resource type, in the Region.
///
/// # Arguments
///
/// * `-b BUCKET` - The name of the Amazon bucket to which AWS Config delivers configuration snapshots and configuration history files.
/// * `-i IAM-ARN` - The ARN of the IAM role that used to describe the AWS resources associated with the account.
/// * `-k KMS-ARN` - The ARN of the KMS key that used to encrypt the data in the bucket.
/// * `-p PREFIX` - The prefix for the bucket.
/// * `-s SNS-ARN` - The ARN of the Amazon SNS topic to which AWS Config sends notifications about configuration changes.
/// * `[-t TYPE]` - The type of resource for AWS Config to support.
///   If not supplied, defaults to `AWS::DynamoDB::Table` (DynamoDB tables).
/// * `[-n NAME]` - The name of the configuration.
///   If not supplied, defaults to `default`.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
/// Need: s3 key prefix AND kms key
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        bucket,
        iam_arn,
        kms_arn,
        name,
        prefix,
        sns_arn,
        type_,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Config client version:          {}", PKG_VERSION);
        println!(
            "Region:                {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Resource type:                  {}", type_);
        println!("Config (delivery channel) name: {}", name);
        println!("Bucket:                         {}", bucket);
        println!("Prefix:                         {}", prefix);
        println!("SNS ARN:                        {}", sns_arn);
        println!("IAM ARN:                        {}", iam_arn);
        println!("KMS ARN:                        {}", kms_arn);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    enable_config(
        &client, &name, &kms_arn, &bucket, &sns_arn, &iam_arn, &prefix,
    )
    .await
}
