/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

// use aws_sdk_dynamodb::model::AttributeValue;
use aws_types::region;
use aws_types::region::ProvideRegion;
use std::error::Error;
use std::path::Path;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The Amazon S3 bucket where we upload the picture.
    #[structopt(short, long)]
    bucket: String,

    /// The name of the picture, JPG, JPEG, or PNG.
    #[structopt(short, long)]
    filename: String,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /*
    /// The DynamoDB table.
    #[structopt(short, long)]
    table: String,
    */
    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// This code example:
/// - Saves the image in an Amazon S3 bucket with an "uploads/" prefix.
/// - Displays facial details,
///   such as age range, gender, and emotion (smiling, etc.).
/// # Arguments
///
/// * `[-b BUCKET]` - The Amazon S3 bucket where we upload the picture.
/// * `[-f FILENAME]` - The name of the picture, JPG, JPEG, or PNG.
/// * `[-t TABLE]` - The DynamoDB table in which the information is stored.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    tracing_subscriber::fmt::init();

    let Opt {
        bucket,
        filename,
        region,
        // table,
        verbose,
    } = Opt::from_args();

    // Make sure filename ends with .jpg, .jpeg, or .png
    let mut content_type = String::new();

    let path = Path::new(&filename);
    let extension: &str = path.extension().unwrap().to_str().unwrap();

    match extension {
        "jpg" => content_type.push_str("image/jpg"),
        "jpeg" => content_type.push_str("image/jpg"),
        "png" => content_type.push_str("image/png"),
        _ => {
            println!();
            println!("{} is not a JPG, JPEG, or PNG file!", filename);
            println!();
            return Ok(());
        }
    }

    let region = region::ChainProvider::first_try(region.map(aws_sdk_s3::Region::new))
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
            aws_sdk_rekognition::PKG_VERSION
        );
        println!("S3 client version:          {}", aws_sdk_s3::PKG_VERSION);
        println!("Bucket:                     {}", &bucket);
        println!("Filename:                   {}", &filename);
        // println!("Table:                      {}", &table);
        println!(
            "Region:                     {}",
            region.region().unwrap().as_ref()
        );
        println!();
    }

    /*
    let s3_region = region.region();
    // let rek_region = region.region();
    // let dyn_region = region.region();

    let s3_conf = aws_sdk_s3::Config::builder().region(s3_region).build();
    let s3_client = aws_sdk_s3::Client::from_conf(s3_conf);

    let body = aws_sdk_s3::ByteStream::from_path(path).await;

    let key: String = String::from("uploads/") + &filename;

    s3_client
        .put_object()
        .body(body.unwrap())
        .bucket(&bucket)
        .content_type(content_type)
        .key(&key)
        .send()
        .await?;

    println!("Added file to bucket.");
    println!();
    let dyn_conf = aws_sdk_dynamodb::Config::builder()
        .region(dyn_region)
        .build();
    let dyn_client = aws_sdk_dynamodb::Client::from_conf(dyn_conf);
    */

    // Get EXIF information from file.
    println!("Retrieving ELIF information from file.");

    for path in &[filename] {
        let file = std::fs::File::open(path)?;
        let mut bufreader = std::io::BufReader::new(&file);
        let exifreader = exif::Reader::new();
        let exif = exifreader.read_from_container(&mut bufreader)?;
        for f in exif.fields() {
            println!(
                "{} {} {}",
                f.tag,
                f.ifd_num,
                f.display_value().with_unit(&exif)
            );
        }
        /*
        let n = AttributeValue::S(filename);
        let t = AttributeValue::S(String::from(f.tag.to_string()));
        let i = AttributeValue::S(f.ifd_num.to_string());
        let v = AttributeValue::S(f.display_value().with_unit(&exif));

        dyn_client
            .put_item()
            .table_name(&table)
            .item("filename", n)
            .item("tag", t)
            .item("id", i)
            .item("value", v)
            .send()
            .await?;
            */
    }

    println!("Added EXIF information to table.");
    println!();

    /*
    let rek_conf = aws_sdk_rekognition::Config::builder()
        .region(rek_region)
        .build();
    let rek_client = aws_sdk_rekognition::Client::from_conf(rek_conf);

    let s3_obj = aws_sdk_rekognition::model::S3Object::builder()
        .bucket(bucket)
        .name(key)
        .build();

    let s3_img = aws_sdk_rekognition::model::Image::builder()
        .s3_object(s3_obj)
        .build();

    let resp = rek_client
        .detect_labels()
        .image(s3_img)
        .attributes(aws_sdk_rekognition::model::Attribute::All)
        .send()
        .await?;
        */

    Ok(())
}
