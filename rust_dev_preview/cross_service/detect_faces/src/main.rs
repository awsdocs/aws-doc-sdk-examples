/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
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

// Saves a file to a bucket.
// snippet-start:[detect_faces-save_bucket.rust.main]
async fn save_bucket(
    client: &aws_sdk_s3::Client,
    body: aws_sdk_s3::types::ByteStream,
    bucket: &str,
    content_type: &str,
    key: &str,
) -> Result<(), aws_sdk_s3::Error> {
    client
        .put_object()
        .body(body)
        .bucket(bucket)
        .content_type(content_type)
        .key(key)
        .send()
        .await?;

    println!("Added file to bucket.");
    println!();

    Ok(())
}
// snippet-end:[detect_faces-save_bucket.rust.main]

// Displays information about faces in a picture.
// snippet-start:[detect_faces-describe_faces.rust.main]
async fn describe_faces(
    verbose: bool,
    client: &aws_sdk_rekognition::Client,
    image: aws_sdk_rekognition::model::Image,
) -> Result<(), aws_sdk_rekognition::Error> {
    let resp = client
        .detect_faces()
        .image(image)
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

    // Sort vector by from_left value.
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
// snippet-end:[detect_faces-describe_faces.rust.main]

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

    let s3_region = region.clone();
    let rek_region = region.clone();

    let rek_region_provider =
        RegionProviderChain::first_try(s3_region.map(aws_sdk_rekognition::Region::new))
            .or_default_provider()
            .or_else(aws_sdk_rekognition::Region::new("us-west-2"));

    let s3_region_provider =
        RegionProviderChain::first_try(rek_region.map(aws_sdk_s3::Region::new))
            .or_default_provider()
            .or_else(aws_sdk_s3::Region::new("us-west-2"));
    println!();

    if verbose {
        println!(
            "Rekognition client version: {}",
            aws_sdk_rekognition::PKG_VERSION
        );
        println!("S3 client version:          {}", aws_sdk_s3::PKG_VERSION);
        println!("Bucket:                     {}", bucket);
        println!("Filename:                   {}", filename);

        println!(
            "Region:               {}",
            s3_region_provider.region().await.unwrap().as_ref()
        );

        println!();
    }

    let s3_shared_config = aws_config::from_env()
        .region(s3_region_provider)
        .load()
        .await;
    let s3_client = aws_sdk_s3::Client::new(&s3_shared_config);

    let rek_shared_config = aws_config::from_env()
        .region(rek_region_provider)
        .load()
        .await;
    let rek_client = aws_sdk_rekognition::Client::new(&rek_shared_config);

    let body = aws_sdk_s3::types::ByteStream::from_path(path).await;

    let key: String = String::from("uploads/") + &filename;

    save_bucket(&s3_client, body.unwrap(), &bucket, &content_type, &filename)
        .await
        .unwrap();
    let s3_obj = aws_sdk_rekognition::model::S3Object::builder()
        .bucket(bucket)
        .name(key)
        .build();

    let s3_img = aws_sdk_rekognition::model::Image::builder()
        .s3_object(s3_obj)
        .build();

    describe_faces(verbose, &rek_client, s3_img).await.unwrap();

    Ok(())
}
