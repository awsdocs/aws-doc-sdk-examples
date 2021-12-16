/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::model::{
    CompressionType, CsvInput, CsvOutput, ExpressionType, FileHeaderInfo, InputSerialization,
    OutputSerialization, SelectObjectContentEventStream,
};
use aws_sdk_s3::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the bucket containing the object (CSV file).
    #[structopt(short, long)]
    bucket: String,

    /// The object key to scan. This example expects the object to be an uncompressed CSV file with:

    /// Name,PhoneNumber,City,Occupation
    /// Person1,(nnn) nnn-nnnn,City1,Occupation1
    /// ...
    /// PersonN,(nnn) nnn-nnnn,CityN,OccupationN
    #[structopt(short, long)]
    object: String,

    /// The name of the person to scan for.
    #[structopt(short, long)]
    name: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Get object content.
// snippet-start:[s3.rust.select-object-content]
async fn get_content(client: &Client, bucket: &str, object: &str, name: &str) -> Result<(), Error> {
    let mut person: String = "SELECT * FROM s3object s WHERE s.\"Name\" = '".to_owned();
    person.push_str(name);
    person.push('\'');

    let mut output = client
        .select_object_content()
        .bucket(bucket)
        .key(object)
        .expression_type(ExpressionType::Sql)
        .expression(person)
        .input_serialization(
            InputSerialization::builder()
                .csv(
                    CsvInput::builder()
                        .file_header_info(FileHeaderInfo::Use)
                        .build(),
                )
                .compression_type(CompressionType::None)
                .build(),
        )
        .output_serialization(
            OutputSerialization::builder()
                .csv(CsvOutput::builder().build())
                .build(),
        )
        .send()
        .await?;

    while let Some(event) = output.payload.recv().await? {
        match event {
            SelectObjectContentEventStream::Records(records) => {
                println!(
                    "Record: {}",
                    records
                        .payload()
                        .map(|p| std::str::from_utf8(p.as_ref()).unwrap())
                        .unwrap_or("")
                );
            }
            SelectObjectContentEventStream::Stats(stats) => {
                println!("Stats: {:?}", stats.details().unwrap());
            }
            SelectObjectContentEventStream::Progress(progress) => {
                println!("Progress: {:?}", progress.details().unwrap());
            }
            SelectObjectContentEventStream::Cont(_) => {
                println!("Continuation Event");
            }
            SelectObjectContentEventStream::End(_) => {
                println!("End Event");
            }
            otherwise => panic!("Unknown event type: {:?}", otherwise),
        }
    }

    Ok(())
}
// snippet-end:[s3.rust.select-object-content]

/// Uses an SQL expression to retrieve content from an object in a bucket.
/// # Arguments
///
/// * `-b BUCKET` - The name of the bucket.
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
        object,
        name,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-east-2"));
    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    println!();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!("Region:            {}", shared_config.region().unwrap());
        println!("Bucket:            {}", &bucket);
        println!("Object:            {}", &object);
        println!("Name:              {}", &name);

        println!();
    }

    get_content(&client, &bucket, &object, &name).await
}
