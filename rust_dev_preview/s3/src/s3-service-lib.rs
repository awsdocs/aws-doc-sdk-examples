/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

// snippet-start:[rust.example_code.s3.scenario_getting_started.lib]

use aws_sdk_s3::model::{
    BucketLocationConstraint, CreateBucketConfiguration, Delete, ObjectIdentifier,
};
use aws_sdk_s3::output::{GetObjectOutput, ListObjectsV2Output};
use aws_sdk_s3::types::ByteStream;
use aws_sdk_s3::Client;
use error::Error;
use std::path::Path;
use std::str;

pub mod error;

// snippet-start:[rust.example_code.s3.basics.delete_bucket]
pub async fn delete_bucket(client: &Client, bucket_name: &str) -> Result<(), Error> {
    client.delete_bucket().bucket(bucket_name).send().await?;
    println!("Bucket deleted");
    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.delete_bucket]

// snippet-start:[rust.example_code.s3.basics.delete_objects]
pub async fn delete_objects(client: &Client, bucket_name: &str) -> Result<(), Error> {
    let objects = client.list_objects_v2().bucket(bucket_name).send().await?;

    let mut delete_objects: Vec<ObjectIdentifier> = vec![];
    for obj in objects.contents().unwrap_or_default() {
        let obj_id = ObjectIdentifier::builder()
            .set_key(Some(obj.key().unwrap().to_string()))
            .build();
        delete_objects.push(obj_id);
    }
    client
        .delete_objects()
        .bucket(bucket_name)
        .delete(Delete::builder().set_objects(Some(delete_objects)).build())
        .send()
        .await?;

    let objects: ListObjectsV2Output = client.list_objects_v2().bucket(bucket_name).send().await?;
    match objects.key_count {
        0 => Ok(()),
        _ => Err(Error::unhandled(
            "There were still objects left in the bucket.",
        )),
    }
}
// snippet-end:[rust.example_code.s3.basics.delete_objects]

// snippet-start:[rust.example_code.s3.basics.list_objects]
pub async fn list_objects(client: &Client, bucket_name: &str) -> Result<(), Error> {
    let objects = client.list_objects_v2().bucket(bucket_name).send().await?;
    println!("Objects in bucket:");
    for obj in objects.contents().unwrap_or_default() {
        println!("{:?}", obj.key().unwrap());
    }

    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.list_objects]

// snippet-start:[rust.example_code.s3.basics.copy_object]
pub async fn copy_object(
    client: &Client,
    bucket_name: &str,
    object_key: &str,
    target_key: &str,
) -> Result<(), Error> {
    let mut source_bucket_and_object: String = "".to_owned();
    source_bucket_and_object.push_str(bucket_name);
    source_bucket_and_object.push('/');
    source_bucket_and_object.push_str(object_key);

    client
        .copy_object()
        .copy_source(source_bucket_and_object)
        .bucket(bucket_name)
        .key(target_key)
        .send()
        .await?;

    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.copy_object]

// snippet-start:[rust.example_code.s3.basics.download_object]
// snippet-start:[rust.example_code.s3.basics.get_object]
pub async fn download_object(client: &Client, bucket_name: &str, key: &str) -> GetObjectOutput {
    let resp = client
        .get_object()
        .bucket(bucket_name)
        .key(key)
        .send()
        .await;
    resp.unwrap()
}
// snippet-end:[rust.example_code.s3.basics.get_object]
// snippet-end:[rust.example_code.s3.basics.download_object]

// snippet-start:[rust.example_code.s3.basics.upload_object]
// snippet-start:[rust.example_code.s3.basics.put_object]
pub async fn upload_object(
    client: &Client,
    bucket_name: &str,
    file_name: &str,
    key: &str,
) -> Result<(), Error> {
    let body = ByteStream::from_path(Path::new(file_name)).await;
    client
        .put_object()
        .bucket(bucket_name)
        .key(key)
        .body(body.unwrap())
        .send()
        .await?;

    println!("Uploaded file: {}", file_name);
    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.put_object]
// snippet-end:[rust.example_code.s3.basics.upload_object]

// snippet-start:[rust.example_code.s3.basics.create_bucket]
pub async fn create_bucket(client: &Client, bucket_name: &str, region: &str) -> Result<(), Error> {
    let constraint = BucketLocationConstraint::from(region);
    let cfg = CreateBucketConfiguration::builder()
        .location_constraint(constraint)
        .build();
    client
        .create_bucket()
        .create_bucket_configuration(cfg)
        .bucket(bucket_name)
        .send()
        .await?;
    println!("Creating bucket named: {bucket_name}");
    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.create_bucket]
// snippet-end:[rust.example_code.s3.scenario_getting_started.lib]
