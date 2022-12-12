// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

// snippet-start:[s3.rust.if-modified-since]
use aws_sdk_s3::{
    error::HeadObjectError,
    types::{ByteStream, DateTime, SdkError},
    Client, Error,
};
use aws_smithy_types::date_time::Format;
use http::StatusCode;
use tracing::{error, warn};

const KEY: &str = "key";
const BODY: &str = "Hello, world!";

/// Lists your tables in DynamoDB local.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let uuid = uuid::Uuid::new_v4();
    let client = Client::new(&aws_config::load_from_env().await);

    // Create bucket.
    // Put an object in the bucket.
    // Get the bucket.
    // Get the bucket again, but it hasn't been modified.
    // Delete the bucket.

    let bucket = format!("if-modified-since-{uuid}");

    client.create_bucket().bucket(bucket.clone()).send().await?;

    let put_object = client
        .put_object()
        .bucket(bucket.as_str())
        .key(KEY)
        .body(ByteStream::from_static(BODY.as_bytes()))
        .send()
        .await;

    // A normal successful 200, which we can grab the etag from.
    let e_tag = match put_object {
        Ok(output) => output.e_tag,
        Err(err) => {
            error!("{err:?}");
            None
        }
    };

    let head_object = client
        .head_object()
        .bucket(bucket.as_str())
        .key(KEY)
        .send()
        .await;

    // Get Object will also 200, and again let us us politely ask for the last_modified and the etag.
    let (last_modified, e_tag_2) = match head_object {
        Ok(output) => (Ok(output.last_modified().cloned()), output.e_tag),
        Err(err) => (Err(err), None),
    };

    warn!("last modified: {last_modified:?}");
    assert_eq!(e_tag, e_tag_2, "Put and first Get had differing etags");

    let head_object = client
        .head_object()
        .bucket(bucket.as_str())
        .key(KEY)
        .if_modified_since(last_modified.unwrap().unwrap())
        .send()
        .await;

    // And then.
    // The second Get Object, with the set `id_modified_since`, is a 304, which is an SdkError::ServiceError.
    // Go through it and pull the parts out (when it's actually a 304).
    let (last_modified, e_tag_2): (
        Result<Option<DateTime>, SdkError<HeadObjectError>>,
        Option<String>,
    ) = match head_object {
        Ok(output) => (Ok(output.last_modified().cloned()), output.e_tag),
        Err(err) => match err {
            aws_sdk_s3::types::SdkError::ServiceError(err) => {
                let http = err.raw().http();
                match http.status() {
                    StatusCode::NOT_MODIFIED => (
                        Ok(Some(
                            DateTime::from_str(
                                http.headers()
                                    .get("last-modified")
                                    .map(|t| t.to_str().unwrap())
                                    .unwrap(),
                                Format::HttpDate,
                            )
                            .unwrap(),
                        )),
                        http.headers()
                            .get("etag")
                            .map(|t| t.to_str().unwrap().into()),
                    ),
                    _ => (Err(aws_sdk_s3::types::SdkError::ServiceError(err)), None),
                }
            }
            _ => (Err(err), None),
        },
    };

    warn!("last modified: {last_modified:?}");
    assert_eq!(e_tag, e_tag_2, "Put and second Get had differing etags");

    client
        .delete_object()
        .bucket(bucket.as_str())
        .key(KEY)
        .send()
        .await?;

    client
        .delete_bucket()
        .bucket(bucket.as_str())
        .send()
        .await?;

    Ok(())
}
// snippet-end:[s3.rust.if-modified-since]
