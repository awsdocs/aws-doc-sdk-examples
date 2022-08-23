/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

extern crate exif;

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_dynamodb::model::AttributeValue;
use std::process;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The S3 bucket.
    #[structopt(short, long)]
    bucket: String,

    /// The filename.
    #[structopt(short, long)]
    filename: String,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The DynamoDB table.
    #[structopt(short, long)]
    table: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

#[derive(Clone, Debug)]
struct Litem {
    name: String,
    confidence: f32,
}

#[derive(Debug)]
struct Edata {
    height: String,
    width: String,
    created: String,
}

// snippet-start:[detect_labels-add_file_to_bucket.rust.main]
async fn add_file_to_bucket(client: &aws_sdk_s3::Client, bucket: &str, filename: &str) {
    let body = aws_sdk_s3::types::ByteStream::from_path(std::path::Path::new(filename)).await;

    match body {
        Ok(b) => {
            match client
                .put_object()
                .bucket(bucket)
                .key(filename)
                .body(b)
                .send()
                .await
            {
                Err(e) => {
                    println!("Got an error uploading file to bucket:");
                    println!("{}", e);
                    process::exit(1);
                }
                Ok(_) => {
                    println!("Uploaded file to bucket.");
                }
            }
        }
        Err(e) => {
            println!("Got an error uploading file to bucket:");
            println!("{}", e);
            process::exit(1);
        }
    }
}
// snippet-end:[detect_labels-add_file_to_bucket.rust.main]

// snippet-start:[detect_labels-get_exif_data.rust.main]
fn get_exif_data(filename: &str) -> Edata {
    let height: String = "".to_owned();
    let width: String = "".to_owned();
    let created: String = "".to_owned();
    let mut edata = Edata {
        height,
        width,
        created,
    };

    let file = std::fs::File::open(&filename).unwrap();
    let mut bufreader = std::io::BufReader::new(&file);
    let exifreader = exif::Reader::new();

    match exifreader.read_from_container(&mut bufreader) {
        Ok(exif) => {
            println!("{}", &filename);

            for f in exif.fields() {
                // Get EXIF values for image width, height, and when image was created.
                match &*f.tag.to_string() {
                    "ImageWidth" => edata.height.push_str(&*f.display_value().to_string()),
                    "ImageLength" => edata.width.push_str(&*f.display_value().to_string()),
                    "DateTimeOriginal" => edata.created.push_str(&*f.display_value().to_string()),
                    _ => {}
                }
            }
        }
        Err(_) => {
            println!();
            println!("File does not contain ELIF data");
        }
    };

    edata
}
// snippet-end:[detect_labels-get_exif_data.rust.main]

// snippet-start:[detect_labels-add_data_to_table.rust.main]
async fn add_data_to_table(
    verbose: bool,
    client: &aws_sdk_dynamodb::Client,
    table: &str,
    filename: &str,
    edata: Edata,
    labels: Vec<Litem>,
) {
    if verbose {
        println!("Added ")
    }
    let filename_string = AttributeValue::S(filename.to_string());
    let height_string = AttributeValue::S(edata.height);
    let created_string = AttributeValue::S(edata.created);
    let width_string = AttributeValue::S(edata.width);
    let label1_label = AttributeValue::S(labels[0].name.to_string());
    let label1_value = AttributeValue::S(labels[0].confidence.to_string());
    let label2_label = AttributeValue::S(labels[1].name.to_string());
    let label2_value = AttributeValue::S(labels[1].confidence.to_string());
    let label3_label = AttributeValue::S(labels[2].name.to_string());
    let label3_value = AttributeValue::S(labels[2].confidence.to_string());

    match client
        .put_item()
        .table_name(table)
        .item("filename", filename_string) // Table key.
        .item("height", height_string)
        .item("width", width_string)
        .item("created", created_string)
        .item("Label1", label1_label)
        .item("Value1", label1_value)
        .item("Label2", label2_label)
        .item("Value2", label2_value)
        .item("Label3", label3_label)
        .item("Value3", label3_value)
        .send()
        .await
    {
        Err(e) => {
            println!("Got an error adding data to table:");
            println!("{}", e);
            process::exit(1);
        }
        Ok(_) => {
            println!("Added info to table.");
        }
    }
}
// snippet-end:[detect_labels-add_data_to_table.rust.main]

// snippet-start:[detect_labels-get_label_data.rust.main]
async fn get_label_data(
    rekog_client: &aws_sdk_rekognition::Client,
    bucket: &str,
    key: &str,
) -> Vec<Litem> {
    let s3_obj = aws_sdk_rekognition::model::S3Object::builder()
        .bucket(bucket)
        .name(key)
        .build();

    let s3_img = aws_sdk_rekognition::model::Image::builder()
        .s3_object(s3_obj)
        .build();

    let resp = rekog_client.detect_labels().image(s3_img).send().await;

    let labels = resp.unwrap().labels.unwrap_or_default();

    // Create vector of Labels.
    let mut label_vec: Vec<Litem> = vec![];

    for label in labels {
        let name = label.name.as_deref().unwrap_or_default();
        let confidence = label.confidence.unwrap();

        let label = Litem {
            name: name.to_string(),
            confidence,
        };
        label_vec.push(label);
    }

    // Sort label items by confidence.
    label_vec.sort_by(|b, a| a.confidence.partial_cmp(&b.confidence).unwrap());

    // Return the first three items.
    label_vec[0..3].to_vec()
}
// snippet-end:[detect_labels-get_label_data.rust.main]

/// Gets EXIF information from a a JPG, JPEG, or PNG file,
/// uploads it to an S3 bucket,
/// uses Rekognition to identify the three top attributes (labels in Rekognition) in the file,
/// and adds the EXIF and label information to a DynamoDB table in the Region.
///
/// # Arguments
///
/// * `-b BUCKET` - The S3 bucket to which the file is uploaded.aws_sdk_dynamodb
/// * `-t TABLE` - The DynamoDB table in which the EXIF and label information is stored.
///   It must use the primary key `filename`.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), exif::Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        bucket,
        filename,
        region,
        table,
        verbose,
    } = Opt::from_args();

    let dynamo_region = region.clone();
    let s3_region = region.clone();
    let rek_region = region.clone();

    let dynamo_region_provider =
        RegionProviderChain::first_try(dynamo_region.map(aws_sdk_dynamodb::Region::new))
            .or_default_provider()
            .or_else(aws_sdk_dynamodb::Region::new("us-west-2"));

    let rek_region_provider =
        RegionProviderChain::first_try(rek_region.map(aws_sdk_rekognition::Region::new))
            .or_default_provider()
            .or_else(aws_sdk_rekognition::Region::new("us-west-2"));

    let s3_region_provider = RegionProviderChain::first_try(s3_region.map(aws_sdk_s3::Region::new))
        .or_default_provider()
        .or_else(aws_sdk_s3::Region::new("us-west-2"));

    println!();

    if verbose {
        println!(
            "DynamoDB client version:    {}",
            aws_sdk_dynamodb::PKG_VERSION
        );
        println!(
            "Rekognition client version: {}",
            aws_sdk_dynamodb::PKG_VERSION
        );
        println!("S3 client version:          {}", aws_sdk_s3::PKG_VERSION);
        println!("Filename:                   {}", &filename);
        println!("Bucket:                     {}", &bucket);
        println!("Table:                      {}", &table);
        println!();
        println!(
            "Region:                     {}",
            dynamo_region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let s3_shared_config = aws_config::from_env()
        .region(s3_region_provider)
        .load()
        .await;
    let s3_client = aws_sdk_s3::Client::new(&s3_shared_config);

    add_file_to_bucket(&s3_client, &bucket, &filename).await;

    let edata = get_exif_data(&filename);

    let rek_shared_config = aws_config::from_env()
        .region(rek_region_provider)
        .load()
        .await;
    let rek_client = aws_sdk_rekognition::Client::new(&rek_shared_config);

    let labels = get_label_data(&rek_client, &bucket, &filename).await;

    let dynamo_shared_config = aws_config::from_env()
        .region(dynamo_region_provider)
        .load()
        .await;
    let dynamo_client = aws_sdk_dynamodb::Client::new(&dynamo_shared_config);

    // Add data to table.
    add_data_to_table(verbose, &dynamo_client, &table, &filename, edata, labels).await;

    Ok(())
}
