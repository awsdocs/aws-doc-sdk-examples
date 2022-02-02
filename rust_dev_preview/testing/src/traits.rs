/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(dead_code)]

use async_trait::async_trait;
use aws_sdk_s3 as s3;
use std::error::Error;
use std::str::FromStr;

// snippet-start:[testing.rust.traits-trait]
pub struct ListObjectsResult {
    pub objects: Vec<s3::model::Object>,
    pub continuation_token: Option<String>,
    pub has_more: bool,
}

#[async_trait]
pub trait ListObjects {
    async fn list_objects(
        &self,
        bucket: &str,
        prefix: &str,
        continuation_token: Option<String>,
    ) -> Result<ListObjectsResult, Box<dyn Error + Send + Sync + 'static>>;
}
// snippet-end:[testing.rust.traits-trait]

// snippet-start:[testing.rust.traits-implementation]
#[derive(Clone, Debug)]
pub struct S3ListObjects {
    s3: s3::Client,
}

impl S3ListObjects {
    pub fn new(s3: s3::Client) -> Self {
        Self { s3 }
    }
}

#[async_trait]
impl ListObjects for S3ListObjects {
    async fn list_objects(
        &self,
        bucket: &str,
        prefix: &str,
        continuation_token: Option<String>,
    ) -> Result<ListObjectsResult, Box<dyn Error + Send + Sync + 'static>> {
        let response = self
            .s3
            .list_objects_v2()
            .bucket(bucket)
            .prefix(prefix)
            .set_continuation_token(continuation_token)
            .send()
            .await?;
        Ok(ListObjectsResult {
            objects: response.contents().unwrap_or_default().to_vec(),
            continuation_token: response.continuation_token().map(|t| t.to_string()),
            has_more: response.is_truncated(),
        })
    }
}
// snippet-end:[testing.rust.traits-implementation]

// snippet-start:[testing.rust.traits-fake]
#[derive(Clone, Debug)]
pub struct TestListObjects {
    expected_bucket: String,
    expected_prefix: String,
    pages: Vec<Vec<s3::model::Object>>,
}

#[async_trait]
impl ListObjects for TestListObjects {
    async fn list_objects(
        &self,
        bucket: &str,
        prefix: &str,
        continuation_token: Option<String>,
    ) -> Result<ListObjectsResult, Box<dyn Error + Send + Sync + 'static>> {
        assert_eq!(self.expected_bucket, bucket);
        assert_eq!(self.expected_prefix, prefix);

        let index = continuation_token
            .map(|t| usize::from_str(&t).expect("valid token"))
            .unwrap_or_default();
        if self.pages.is_empty() {
            Ok(ListObjectsResult {
                objects: Vec::new(),
                continuation_token: None,
                has_more: false,
            })
        } else {
            Ok(ListObjectsResult {
                objects: self.pages[index].clone(),
                continuation_token: Some(format!("{}", index + 1)),
                has_more: index + 1 < self.pages.len(),
            })
        }
    }
}
// snippet-end:[testing.rust.traits-fake]

// snippet-start:[testing.rust.traits-function]
async fn determine_prefix_file_size(
    // Now we take a reference to our trait object instead of the S3 client
    list_objects_impl: &dyn ListObjects,
    bucket: &str,
    prefix: &str,
) -> Result<usize, Box<dyn Error + Send + Sync + 'static>> {
    let mut next_token: Option<String> = None;
    let mut total_size_bytes = 0;
    loop {
        let result = list_objects_impl
            .list_objects(bucket, prefix, next_token.take())
            .await?;

        // Add up the file sizes we got back
        for object in result.objects {
            total_size_bytes += object.size() as usize;
        }

        // Handle pagination, and break the loop if there are no more pages
        next_token = result.continuation_token;
        if !result.has_more {
            break;
        }
    }
    Ok(total_size_bytes)
}
// snippet-end:[testing.rust.traits-function]

// snippet-start:[testing.rust.traits-tests]
#[tokio::test]
async fn test_single_page() {
    use s3::model::Object;

    // Create a TestListObjects instance with just one page of two objects in it
    let fake = TestListObjects {
        expected_bucket: "some-bucket".into(),
        expected_prefix: "some-prefix".into(),
        pages: vec![[5, 2i64]
            .iter()
            .map(|size| Object::builder().size(*size).build())
            .collect()],
    };

    // Run the code we want to test with it
    let size = determine_prefix_file_size(&fake, "some-bucket", "some-prefix")
        .await
        .unwrap();

    // Verify we got the correct total size back
    assert_eq!(7, size);
}

#[tokio::test]
async fn test_multiple_pages() {
    use s3::model::Object;

    // This time, we add a helper function for making pages
    fn make_page(sizes: &[i64]) -> Vec<Object> {
        sizes
            .iter()
            .map(|size| Object::builder().size(*size).build())
            .collect()
    }

    // Create the TestListObjects instance with two pages of objects now
    let fake = TestListObjects {
        expected_bucket: "some-bucket".into(),
        expected_prefix: "some-prefix".into(),
        pages: vec![make_page(&[5, 2]), make_page(&[3, 9])],
    };

    // And now test and verify
    let size = determine_prefix_file_size(&fake, "some-bucket", "some-prefix")
        .await
        .unwrap();
    assert_eq!(19, size);
}
// snippet-end:[testing.rust.traits-tests]
