// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::types::{
    CompressionType, CsvInput, ExpressionType, FileHeaderInfo, InputSerialization, JsonOutput,
    OutputSerialization, SelectObjectContentEventStream,
};
use aws_sdk_s3::{config::Region, meta::PKG_VERSION, Client};
use clap::Parser;
use serde::de::IgnoredAny;
use serde::Deserialize;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the bucket containing the object (CSV file).
    #[structopt(short, long)]
    bucket: String,

    /// The object key to scan. This example expects the object to be an uncompressed CSV file with:

    /// Name,PhoneNumber,City,Occupation
    /// Person1,(nnn) nnn-nnnn,City1,Occupation1,Comment
    /// ...
    /// PersonN,(nnn) nnn-nnnn,CityN,OccupationN,Comment
    #[structopt(short, long)]
    object: String,

    /// The name of the person to scan for. This used as a prefix search
    #[structopt(short, long)]
    name: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "PascalCase")]
pub struct Record {
    pub name: String,
    pub phone_number: String,
    pub city: String,
    pub occupation: String,
    pub description: String,
}

fn is_valid_json(data: impl AsRef<str>) -> bool {
    serde_json::from_str::<IgnoredAny>(data.as_ref()).is_ok()
}

// Get object content.
// snippet-start:[s3.rust.select-object-content]
async fn get_content(
    client: &Client,
    bucket: &str,
    object: &str,
    name: &str,
) -> Result<(), anyhow::Error> {
    // To escape a single quote, use two single quotes.
    let name = name.replace('\'', "''");
    let person: String = format!("SELECT * FROM s3object s where s.Name like '{name}%'");
    tracing::info!(query = %person);

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
                // By default, the output delimiter is `\n`
                .json(JsonOutput::builder().build())
                .build(),
        )
        .send()
        .await?;
    let mut processed_records: Vec<Record> = vec![];
    let mut buf: String = String::new();

    while let Some(event) = output.payload.recv().await? {
        match event {
            SelectObjectContentEventStream::Records(records) => {
                let records_str = records.payload().map(|p| p.as_ref()).unwrap_or_default();
                let records_str = std::str::from_utf8(records_str).expect("invalid utf8 from s3");
                for line in records_str.lines() {
                    // It's possible for one record to be split onto multiple lines
                    if let Some(record) = parse_line_buffered(&mut buf, line)? {
                        processed_records.push(record);
                    }
                }
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
    println!("Found the following records:\n{:#?}", processed_records);

    Ok(())
}

/// Parse a new line &str, potentially using content from the previous line
fn parse_line_buffered(buf: &mut String, line: &str) -> Result<Option<Record>, anyhow::Error> {
    if buf.is_empty() && is_valid_json(line) {
        Ok(Some(serde_json::from_str(line)?))
    } else {
        buf.push_str(line);
        if is_valid_json(&buf) {
            let result = serde_json::from_str(buf);
            buf.clear();
            Ok(Some(result?))
        } else {
            Ok(None)
        }
    }
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
async fn main() -> Result<(), anyhow::Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        bucket,
        object,
        name,
        verbose,
    } = Opt::parse();

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
