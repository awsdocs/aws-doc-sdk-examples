use aws_smithy_http::body::SdkBody;
use aws_smithy_runtime::client::http::test_util::{ReplayEvent, StaticReplayClient};

use aws_sdk_s3 as s3;

// snippet-start:[testing.rust.replay]
pub async fn determine_prefix_file_size(
    // Now we take a reference to our trait object instead of the S3 client
    // s3_list: ListObjectsService,
    s3: s3::Client,
    bucket: &str,
    prefix: &str,
) -> Result<usize, s3::Error> {
    let mut next_token: Option<String> = None;
    let mut total_size_bytes = 0;
    loop {
        let result = s3
            .list_objects_v2()
            .prefix(prefix)
            .bucket(bucket)
            .set_continuation_token(next_token.take())
            .send()
            .await?;

        // Add up the file sizes we got back
        for object in result.contents() {
            total_size_bytes += object.size() as usize;
        }

        // Handle pagination, and break the loop if there are no more pages
        next_token = result.continuation_token.clone();
        if next_token.is_none() {
            break;
        }
    }
    Ok(total_size_bytes)
}
// snippet-end:[testing.rust.replay]

// snippet-start:[testing.rust.replay-tests]
fn make_s3_test_credentials() -> s3::config::Credentials {
    s3::config::Credentials::new(
        "ATESTCLIENT",
        "astestsecretkey",
        Some("atestsessiontoken".to_string()),
        None,
        "",
    )
}

#[tokio::test]
async fn test_single_page() {
    let client: s3::Client = s3::Client::from_conf(
        s3::Config::builder()
            .credentials_provider(make_s3_test_credentials())
            .region(s3::config::Region::new("us-east-1"))
            .http_client(StaticReplayClient::new(vec![
              ReplayEvent::new(
                http::Request::builder()
                    .method("GET")
                    .uri("https://test-bucket-us-east-1.s3.us-east-1.amazonaws.com/?list-type=2&prefix=test-prefix")
                    .header("user-agent", "aws-sdk-rust/0.56.1 os/macos lang/rust/1.70.0")
                    .header("x-amz-user-agent", "aws-sdk-rust/0.56.1 api/s3/0.0.0-local os/macos lang/rust/1.70.0")
                    .body(())
                    .unwrap(),
                http::Response::builder()
                    .status(200)
                    .body(SdkBody::from(include_str!("./testing/response_1.xml")))
                    .unwrap(),
            )
            ]))
            .build(),
    );

    // Run the code we want to test with it
    let size = determine_prefix_file_size(client, "some-bucket", "some-prefix")
        .await
        .unwrap();

    // Verify we got the correct total size back
    assert_eq!(7, size);
}

#[tokio::test]
async fn test_multiple_pages() {
    let client: s3::Client = s3::Client::from_conf(
        s3::Config::builder()
            .credentials_provider(make_s3_test_credentials())
            .region(s3::config::Region::new("us-east-1"))
            .http_client(StaticReplayClient::new(vec![
              ReplayEvent::new(
                http::Request::builder()
                    .method("GET")
                    .uri("https://test-bucket-us-east-1.s3.us-east-1.amazonaws.com/?list-type=2&prefix=test-prefix")
                    .header("user-agent", "aws-sdk-rust/0.56.1 os/macos lang/rust/1.70.0")
                    .header("x-amz-user-agent", "aws-sdk-rust/0.56.1 api/s3/0.0.0-local os/macos lang/rust/1.70.0")
                    .body(())
                    .unwrap(),
                http::Response::builder()
                    .status(200)
                    .body(SdkBody::from(include_str!("./testing/response_multi_1.xml")))
                    .unwrap(),
            ),
              ReplayEvent::new(
                http::Request::builder()
                    .method("GET")
                    .uri("https://test-bucket-us-east-1.s3.us-east-1.amazonaws.com/?list-type=2&prefix=test-prefix&continuation-token=next")
                    .header("user-agent", "aws-sdk-rust/0.56.1 os/macos lang/rust/1.70.0")
                    .header("x-amz-user-agent", "aws-sdk-rust/0.56.1 api/s3/0.0.0-local os/macos lang/rust/1.70.0")
                    .body(())
                    .unwrap(),
                http::Response::builder()
                    .status(200)
                    .body(SdkBody::from(include_str!("./testing/response_multi_2.xml")))
                    .unwrap(),
            )
            ]))
            .build(),
    );

    // Run the code we want to test with it
    let size = determine_prefix_file_size(mock, "some-bucket", "some-prefix")
        .await
        .unwrap();

    assert_eq!(19, size);
}
// snippet-end:[testing.rust.replay-tests]
