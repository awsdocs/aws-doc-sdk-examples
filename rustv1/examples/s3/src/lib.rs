// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rust.example_code.s3.scenario_getting_started.lib]

use aws_config::Region;
use aws_sdk_s3::operation::create_bucket::CreateBucketOutput;
use aws_sdk_s3::types::{
    BucketLocationConstraint, CreateBucketConfiguration, Delete, ObjectIdentifier,
};
use aws_sdk_s3::{error::SdkError, primitives::ByteStream, Client};
use aws_sdk_s3::{
    operation::{
        get_object::{GetObjectError, GetObjectOutput},
        put_object::PutObjectOutput,
    },
    types::CopyObjectResult,
};
use error::S3ExampleError;
use std::path::Path;
use std::str;

pub mod error;

// snippet-start:[rust.copy-object]
/// Copy an object from one bucket to another.
pub async fn copy_object(
    client: &Client,
    source_bucket: &str,
    destination_bucket: &str,
    source_object: &str,
    destination_object: &str,
) -> Result<(), S3ExampleError> {
    let source_key = format!("{source_bucket}/{source_object}");
    let response = client
        .copy_object()
        .copy_source(&source_key)
        .bucket(destination_bucket)
        .key(destination_object)
        .send()
        .await?;

    println!(
        "Copied from {source_key} to {destination_bucket}/{destination_object} with etag {}",
        response
            .copy_object_result
            .unwrap_or_else(|| CopyObjectResult::builder().build())
            .e_tag()
            .unwrap_or("missing")
    );
    Ok(())
}
// snippet-end:[bin.rust.copy-object]

// snippet-start:[s3.rust.delete-object]
/// Delete an object from a bucket.
pub async fn remove_object(client: &Client, bucket: &str, key: &str) -> Result<(), S3ExampleError> {
    client
        .delete_object()
        .bucket(bucket)
        .key(key)
        .send()
        .await?;

    println!("Object deleted.");

    Ok(())
}
// snippet-end:[s3.rust.delete-object]

// snippet-start:[rust.example_code.s3.basics.download_object]
pub async fn download_object(
    client: &Client,
    bucket_name: &str,
    key: &str,
) -> Result<GetObjectOutput, SdkError<GetObjectError>> {
    client
        .get_object()
        .bucket(bucket_name)
        .key(key)
        .send()
        .await
}
// snippet-end:[rust.example_code.s3.basics.download_object]

// snippet-start:[rust.example_code.s3.basics.upload_object]
// snippet-start:[rust.example_code.s3.basics.put_object]
pub async fn upload_object(
    client: &Client,
    bucket_name: &str,
    file_name: &str,
    key: &str,
) -> Result<PutObjectOutput, S3ExampleError> {
    let body = ByteStream::from_path(Path::new(file_name)).await;
    client
        .put_object()
        .bucket(bucket_name)
        .key(key)
        .body(body.unwrap())
        .send()
        .await
        .map_err(S3ExampleError::from)
}
// snippet-end:[rust.example_code.s3.basics.put_object]
// snippet-end:[rust.example_code.s3.basics.upload_object]

// snippet-start:[rust.example_code.s3.basics.list_objects]
pub async fn list_objects(client: &Client, bucket: &str) -> Result<(), S3ExampleError> {
    let mut response = client
        .list_objects_v2()
        .bucket(bucket.to_owned())
        .max_keys(10) // In this example, go 10 at a time.
        .into_paginator()
        .send();

    while let Some(result) = response.next().await {
        match result {
            Ok(output) => {
                for object in output.contents() {
                    println!(" - {}", object.key().unwrap_or("Unknown"));
                }
            }
            Err(err) => {
                eprintln!("{err:?}")
            }
        }
    }

    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.list_objects]

// snippet-start:[rust.example_code.s3.basics.clear_bucket]
/// Given a bucket, remove all objects in the bucket, and then ensure no objects
/// remain in the bucket.
pub async fn clear_bucket(
    client: &Client,
    bucket_name: &str,
) -> Result<Vec<String>, S3ExampleError> {
    let objects = client.list_objects_v2().bucket(bucket_name).send().await?;

    // delete_objects no longer needs to be mutable.
    let objects_to_delete: Vec<String> = objects
        .contents()
        .into_iter()
        .filter_map(|obj| obj.key())
        .map(String::from)
        .collect();

    if objects_to_delete.is_empty() {
        return Ok(vec![]);
    }

    let return_keys = objects_to_delete.clone();

    delete_objects(client, bucket_name, objects_to_delete).await?;

    let objects = client.list_objects_v2().bucket(bucket_name).send().await?;

    eprintln!("{objects:?}");

    match objects.key_count {
        Some(0) => Ok(return_keys),
        _ => Err(S3ExampleError::new(
            "There were still objects left in the bucket.",
        )),
    }
}
// snippet-end:[rust.example_code.s3.basics.clear_bucket]

// snippet-start:[rust.example_code.s3.delete_objects]
/// Delete the objects in a bucket.
pub async fn delete_objects(
    client: &Client,
    bucket_name: &str,
    objects_to_delete: Vec<String>,
) -> Result<(), S3ExampleError> {
    // Push into a mut vector to use `?` early return while building object keys.
    let mut delete_object_ids: Vec<ObjectIdentifier> = vec![];
    for obj in objects_to_delete {
        let obj_id = ObjectIdentifier::builder()
            .key(obj)
            .build()
            .map_err(|err| {
                S3ExampleError::new(format!("Failed to build key for delete_object: {err:?}"))
            })?;
        delete_object_ids.push(obj_id);
    }

    client
        .delete_objects()
        .bucket(bucket_name)
        .delete(
            Delete::builder()
                .set_objects(Some(delete_object_ids))
                .build()
                .map_err(|err| {
                    S3ExampleError::new(format!("Failed to build delete_object input {err:?}"))
                })?,
        )
        .send()
        .await?;
    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.delete_objects]

// snippet-start:[rust.example_code.s3.basics.create_bucket]
// snippet-start:[s3.rust.create-bucket]
pub async fn create_bucket(
    client: &Client,
    bucket_name: &str,
    region: &Region,
) -> Result<Option<CreateBucketOutput>, S3ExampleError> {
    let constraint = BucketLocationConstraint::from(region.to_string().as_str());
    let cfg = CreateBucketConfiguration::builder()
        .location_constraint(constraint)
        .build();
    let create = client
        .create_bucket()
        .create_bucket_configuration(cfg)
        .bucket(bucket_name)
        .send()
        .await;

    // BucketAlreadyExists and BucketAlreadyOwnedByYou are not problems for this task.
    create.map(Some).or_else(|err| {
        if err
            .as_service_error()
            .map(|se| se.is_bucket_already_exists() || se.is_bucket_already_owned_by_you())
            == Some(true)
        {
            Ok(None)
        } else {
            Err(S3ExampleError::from(err))
        }
    })
}
// snippet-end:[s3.rust.create-bucket]
// snippet-end:[rust.example_code.s3.basics.create_bucket]
// snippet-end:[rust.example_code.s3.scenario_getting_started.lib]

// snippet-start:[rust.example_code.s3.basics.delete_bucket]
pub async fn delete_bucket(client: &Client, bucket_name: &str) -> Result<(), S3ExampleError> {
    client.delete_bucket().bucket(bucket_name).send().await?;
    println!("Bucket deleted");
    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.delete_bucket]

#[cfg(test)]
mod test {
    use std::env::temp_dir;

    use aws_config::Region;
    use aws_smithy_runtime::client::http::test_util::StaticReplayClient;
    use sdk_examples_test_utils::{client_config, single_shot_client, test_event};
    use tokio::{fs::File, io::AsyncWriteExt};
    use uuid::Uuid;

    use crate::{
        clear_bucket, copy_object, create_bucket, delete_bucket, download_object, list_objects,
        upload_object,
    };

    #[tokio::test]
    async fn test_delete_bucket() {
        let client = single_shot_client!(
            sdk: aws_sdk_s3,
            status: 200,
            response: r#""#
        );

        let resp = delete_bucket(&client, "bucket_name").await;

        assert!(resp.is_ok(), "{resp:?}");
    }

    #[tokio::test]
    async fn test_delete_objects() {
        let client = aws_sdk_s3::Client::from_conf(
            client_config!(aws_sdk_s3)
                .http_client(StaticReplayClient::new(vec![
                    // client.list_objects_v2().bucket(bucket_name)
                    test_event!(
                        r#""#,
                        (
                            200,
                            r#"<?xml version="1.0" encoding="UTF-8"?><ListBucketResult>
                            <Name>test</Name>
                            <Contents><Key>obj1</Key></Contents>
                            <Contents><Key>obj2</Key></Contents>
                            <KeyCount>2</KeyCount>
                            </ListBucketResult>"#
                        )
                    ),
                    // client.delete_objects().delete(...(delete_objects)...))
                    test_event!(r#""#, (200, r#"<?xml version="1.0" encoding="UTF-8"?>
                    <DeleteResult>
                        <Deleted>
                            <DeleteMarker>true</DeleteMarker>
                            <Key>obj1</Key>
                        </Deleted>
                        <Deleted>
                            <DeleteMarker>true</DeleteMarker>
                            <Key>obj2</Key>
                        </Deleted>
                    </DeleteResult>
                    "#)),
                    // client.list_objects_v2().bucket(bucket_name)
                    test_event!(
                        r#""#,
                        (
                            200,
                            r#"<?xml version="1.0" encoding="UTF-8"?><ListBucketResult><Name>test</Name>
                            <KeyCount>0</KeyCount>
                            </ListBucketResult>"#
                        )
                    ),
                ]))
                .build(),
        );

        let resp = clear_bucket(&client, "bucket_name").await;

        assert!(resp.is_ok(), "{resp:?}");
    }

    #[tokio::test]
    async fn test_delete_objects_failed() {
        let client = aws_sdk_s3::Client::from_conf(
            client_config!(aws_sdk_s3)
                .http_client(StaticReplayClient::new(vec![
                    // client.list_objects_v2().bucket(bucket_name)
                    test_event!(
                        r#""#,
                        (
                            200,
                            r#"<?xml version="1.0" encoding="UTF-8"?><ListBucketResult>
                            <Name>test</Name>
                            <Contents><Key>obj1</Key></Contents>
                            <Contents><Key>obj2</Key></Contents>
                            <KeyCount>2</KeyCount>
                            </ListBucketResult>"#
                        )
                    ),
                    // client.delete_objects().delete(...(delete_objects)...))
                    test_event!(
                        r#""#,
                        (
                            200,
                            r#"<?xml version="1.0" encoding="UTF-8"?>
                    <DeleteResult>
                        <Deleted>
                            <DeleteMarker>true</DeleteMarker>
                            <Key>obj1</Key>
                        </Deleted>
                        <Deleted>
                            <DeleteMarker>true</DeleteMarker>
                            <Key>obj2</Key>
                        </Deleted>
                    </DeleteResult>
                    "#
                        )
                    ),
                    // client.list_objects_v2().bucket(bucket_name)
                    test_event!(
                        r#""#,
                        (
                            200,
                            r#"<?xml version="1.0" encoding="UTF-8"?><ListBucketResult>
                            <Name>test</Name>
                            <Contents><Key>obj3</Key></Contents>
                            <KeyCount>1</KeyCount>
                            </ListBucketResult>"#
                        )
                    ),
                ]))
                .build(),
        );

        let resp = clear_bucket(&client, "bucket_name").await;

        assert!(resp.is_err(), "{resp:?}");
    }

    #[tokio::test]
    async fn test_list_objects() {
        let client = single_shot_client!(
            sdk: aws_sdk_s3,
            status: 200,
            response: r#"<?xml version="1.0" encoding="UTF-8"?>
<ListBucketResult>
   <Name>test</Name>
</ListBucketResult>"#
        );

        let resp = list_objects(&client, "bucket_name").await;
        assert!(resp.is_ok(), "{resp:?}");
    }

    #[tokio::test]
    async fn test_copy_object() {
        let client = single_shot_client!(
            sdk: aws_sdk_s3,
            status: 200,
            response: r#""#
        );

        let bucket = "bucket_name";
        let resp = copy_object(&client, bucket, bucket, "object_key", "target_key").await;
        assert!(resp.is_ok(), "{resp:?}");
    }

    #[tokio::test]
    async fn test_download_object() {
        let client = single_shot_client!(
            sdk: aws_sdk_s3,
            status: 200,
            response: r#""#
        );

        let resp = download_object(&client, "bucket_name", "key").await;
        assert!(resp.is_ok(), "{resp:?}");
    }

    #[tokio::test]
    async fn test_upload_object() {
        let client = single_shot_client!(
            sdk: aws_sdk_s3,
            status: 200,
            response: r#""#
        );

        let file_name = {
            let mut dir = temp_dir();
            let file_name = format!("{}.txt", Uuid::new_v4());
            dir.push(file_name);
            let file_name = dir.clone();
            let file_name = file_name.to_str().unwrap().to_string();

            let mut file = File::create(dir).await.unwrap();
            let written_bytes = file.write("test file".as_bytes()).await.unwrap();

            println!("Wrote bytes to s3 {written_bytes}");

            file_name
        };

        let resp = upload_object(&client, "bucket_name", file_name.as_str(), "key").await;

        assert!(resp.is_ok(), "{resp:?}");
    }

    #[tokio::test]
    async fn test_create_bucket() {
        let client = single_shot_client!(
            sdk: aws_sdk_s3,
            status: 200,
            headers: vec![("Location", "test_location")],
            response: r#""#
        );

        let resp = create_bucket(&client, "bucket_name", &Region::from_static("us-esst-1")).await;
        assert!(resp.is_ok(), "{resp:?}");
        let output = resp.unwrap();
        assert!(output.is_some());
        assert_eq!(output.unwrap().location(), Some("test_location"));
    }
}
