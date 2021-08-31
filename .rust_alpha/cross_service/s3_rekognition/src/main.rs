/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_types::region;
use aws_types::region::ProvideRegion;
use std::error::Error;
use std::path::Path;
use structopt::StructOpt;

#[derive(Debug)]
struct Person {
    from_left: f32,
    age_range: String,
    gender: String,
    emotion: String,
}

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
            "Rekognition client version: {}",
            aws_sdk_rekognition::PKG_VERSION
        );
        println!("S3 client version:          {}", aws_sdk_s3::PKG_VERSION);
        println!("Bucket:                     {}", &bucket);
        println!("Filename:                   {}", &filename);
        println!(
            "Region:                     {}",
            region.region().unwrap().as_ref()
        );
        println!();
    }

    let s3_region = region.region();
    let rek_region = region.region();

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

    println!("Added file to bucket");
    println!();

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
        .detect_faces()
        .image(s3_img)
        .attributes(aws_sdk_rekognition::model::Attribute::All)
        .send()
        .await?;

    // Create vector of persons
    let mut persons: Vec<Person> = vec![];

    for detail in resp.face_details.unwrap_or_default() {
        if verbose {
            println!("{:?}", detail);
            println!();
        }

        let age = detail.age_range.unwrap();
        let mut range: String = age.low.unwrap_or_default().to_string().to_owned();
        range.push('-');
        range.push_str(&age.high.unwrap_or_default().to_string());

        // Get the emotion with the greatest value
        let mut e: String = String::from("");

        let mut confidence = 0.0;

        for emotion in detail.emotions.unwrap_or_default() {
            let c = emotion.confidence.unwrap_or_default();
            if c > confidence {
                confidence = c;
                e = String::from(emotion.r#type.unwrap().as_ref());
            }
        }

        let p = Person {
            from_left: detail.bounding_box.unwrap().left.unwrap_or_default(),
            age_range: range,
            gender: String::from(detail.gender.unwrap().value.unwrap().as_ref()),
            emotion: e,
        };

        persons.push(p);
    }

    // Sort vector by from_left value
    persons.sort_by(|a, b| a.from_left.partial_cmp(&b.from_left).unwrap());

    if !verbose {
        println!("Face details (from left):");
        println!();

        for p in persons {
            println!("From left: {}", p.from_left);
            println!("Age range: {}", p.age_range);
            println!("Gender:    {}", p.gender);
            println!("Emotion:   {}", p.emotion);
            println!();
        }
    }

    Ok(())
}
