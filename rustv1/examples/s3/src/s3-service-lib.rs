// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rust.example_code.s3.scenario_getting_started.lib]

use aws_sdk_s3::operation::{
    copy_object::{CopyObjectError, CopyObjectOutput},
    create_bucket::{CreateBucketError, CreateBucketOutput},
    get_object::{GetObjectError, GetObjectOutput},
    put_object::{PutObjectError, PutObjectOutput},
};
use aws_sdk_s3::types::{
    BucketLocationConstraint, CreateBucketConfiguration, Delete, ObjectIdentifier,
};
use aws_sdk_s3::{error::SdkError, primitives::ByteStream, Client};
use error::S3ExampleError;
use std::path::Path;
use std::str;

pub mod error;

// snippet-start:[rust.copy-object]
/// Copy an object from one bucket to another.
async fn copy_object(
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

// snippet-start:[rust.example_code.s3.basics.delete_bucket]
pub async fn delete_bucket(client: &Client, bucket_name: &str) -> Result<(), S3ExampleError> {
    client.delete_bucket().bucket(bucket_name).send().await?;
    println!("Bucket deleted");
    Ok(())
}
// snippet-end:[rust.example_code.s3.basics.delete_bucket]

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
) -> Result<PutObjectOutput, SdkError<PutObjectError>> {
    let body = ByteStream::from_path(Path::new(file_name)).await;
    client
        .put_object()
        .bucket(bucket_name)
        .key(key)
        .body(body.unwrap())
        .send()
        .await
}
// snippet-end:[rust.example_code.s3.basics.put_object]
// snippet-end:[rust.example_code.s3.basics.upload_object]

// snippet-start:[rust.example_code.s3.basics.create_bucket]
pub async fn create_bucket(
    client: &Client,
    bucket_name: &str,
    region: &str,
) -> Result<CreateBucketOutput, SdkError<CreateBucketError>> {
    let constraint = BucketLocationConstraint::from(region);
    let cfg = CreateBucketConfiguration::builder()
        .location_constraint(constraint)
        .build();
    client
        .create_bucket()
        .create_bucket_configuration(cfg)
        .bucket(bucket_name)
        .send()
        .await
}
// snippet-end:[rust.example_code.s3.basics.create_bucket]
// snippet-end:[rust.example_code.s3.scenario_getting_started.lib]

#[cfg(test)]
mod test {
    use std::env::temp_dir;

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

        let resp = copy_object(&client, "bucket_name", "object_key", "target_key").await;
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

        let resp = create_bucket(&client, "bucket_name", "region").await;
        assert!(resp.is_ok(), "{resp:?}");
        assert_eq!(resp.unwrap().location(), Some("test_location"));
    }
}
