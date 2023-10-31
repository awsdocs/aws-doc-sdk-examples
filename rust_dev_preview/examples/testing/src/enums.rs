/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_s3 as s3;

#[allow(dead_code)]
// snippet-start:[testing.rust.enums-struct]
pub struct ListObjectsResult {
    pub objects: Vec<s3::types::Object>,
    pub next_continuation_token: Option<String>,
    pub has_more: bool,
}
// snippet-end:[testing.rust.enums-struct]

#[allow(dead_code)]
// snippet-start:[testing.rust.enums-enum]
pub enum ListObjects {
    Real(s3::Client),
    #[cfg(test)]
    Test {
        expected_bucket: String,
        expected_prefix: String,
        pages: Vec<Vec<s3::types::Object>>,
    },
}
// snippet-end:[testing.rust.enums-enum]

// snippet-start:[testing.rust.enums-list_objects]
impl ListObjects {
    #[allow(dead_code)]
    pub async fn list_objects(
        &self,
        bucket: &str,
        prefix: &str,
        continuation_token: Option<String>,
    ) -> Result<ListObjectsResult, s3::Error> {
        match self {
            Self::Real(s3) => {
                Self::real_list_objects(s3.clone(), bucket, prefix, continuation_token).await
            }
            #[cfg(test)]
            Self::Test {
                expected_bucket,
                expected_prefix,
                pages,
            } => {
                assert_eq!(expected_bucket, bucket);
                assert_eq!(expected_prefix, prefix);
                Self::test_list_objects(pages, continuation_token)
            }
        }
    }
    // snippet-end:[testing.rust.enums-list_objects]

    #[allow(dead_code)]
    // snippet-start:[testing.rust.enums-real-list-objects]
    async fn real_list_objects(
        s3: s3::Client,
        bucket: &str,
        prefix: &str,
        continuation_token: Option<String>,
    ) -> Result<ListObjectsResult, s3::Error> {
        let response = s3
            .list_objects_v2()
            .bucket(bucket)
            .prefix(prefix)
            .set_continuation_token(continuation_token)
            .send()
            .await?;
        Ok(ListObjectsResult {
            objects: response.contents().to_vec(),
            next_continuation_token: response.next_continuation_token.clone(),
            has_more: response.is_truncated(),
        })
    }
    // snippet-end:[testing.rust.enums-real-list-objects]

    #[allow(clippy::result_large_err)]
    // snippet-start:[testing.rust.enums-test]
    #[cfg(test)]
    fn test_list_objects(
        pages: &[Vec<s3::types::Object>],
        continuation_token: Option<String>,
    ) -> Result<ListObjectsResult, s3::Error> {
        use std::str::FromStr;
        let index = continuation_token
            .map(|t| usize::from_str(&t).expect("valid token"))
            .unwrap_or_default();
        if pages.is_empty() {
            Ok(ListObjectsResult {
                objects: Vec::new(),
                next_continuation_token: None,
                has_more: false,
            })
        } else {
            Ok(ListObjectsResult {
                objects: pages[index].to_vec(),
                next_continuation_token: Some(format!("{}", index + 1)),
                has_more: index + 1 < pages.len(),
            })
        }
    }
}
// snippet-end:[testing.rust.enums-test]

#[allow(dead_code)]
// snippet-start:[testing.rust.enums-function]
async fn determine_prefix_file_size(
    // Now we take an instance of our enum rather than the S3 client
    list_objects_impl: ListObjects,
    bucket: &str,
    prefix: &str,
) -> Result<usize, s3::Error> {
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
        next_token = result.next_continuation_token;
        if !result.has_more {
            break;
        }
    }
    Ok(total_size_bytes)
}
// snippet-end:[testing.rust.enums-function]

#[cfg(test)]
mod test {
    use super::*;
    use aws_sdk_s3 as s3;
    // snippet-start:[testing.rust.enums-tests]
    #[tokio::test]
    async fn test_single_page() {
        use s3::types::Object;

        // Create a TestListObjects instance with just one page of two objects in it
        let fake = ListObjects::Test {
            expected_bucket: "test-bucket".into(),
            expected_prefix: "test-prefix".into(),
            pages: vec![[5, 2i64]
                .iter()
                .map(|size| Object::builder().size(*size).build())
                .collect()],
        };

        // Run the code we want to test with it
        let size = determine_prefix_file_size(fake, "test-bucket", "test-prefix")
            .await
            .unwrap();

        // Verify we got the correct total size back
        assert_eq!(7, size);
    }

    #[tokio::test]
    async fn test_multiple_pages() {
        use s3::types::Object;

        // This time, we add a helper function for making pages
        fn make_page(sizes: &[i64]) -> Vec<Object> {
            sizes
                .iter()
                .map(|size| Object::builder().size(*size).build())
                .collect()
        }

        // Create the TestListObjects instance with two pages of objects now
        let fake = ListObjects::Test {
            expected_bucket: "test-bucket".into(),
            expected_prefix: "test-prefix".into(),
            pages: vec![make_page(&[5, 2]), make_page(&[3, 9])],
        };

        // And now test and verify
        let size = determine_prefix_file_size(fake, "test-bucket", "test-prefix")
            .await
            .unwrap();
        assert_eq!(19, size);
    }
    // snippet-end:[testing.rust.enums-tests]
}
