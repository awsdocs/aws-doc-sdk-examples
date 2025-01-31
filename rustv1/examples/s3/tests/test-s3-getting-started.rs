// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::{config::Region, Client};
use s3_code_examples::error::S3ExampleError;
use uuid::Uuid;

#[ignore]
#[tokio::test]
async fn test_it_runs() {
    let (region, client, bucket_name, file_name, key, target_key) = setup().await;
    let run = run_s3_operations(region, client, bucket_name, file_name, key, target_key).await;
    run.expect("Failed to perform s3 actions");
}

async fn run_s3_operations(
    region: Region,
    client: Client,
    bucket_name: String,
    file_name: String,
    key: String,
    target_key: String,
) -> Result<(), S3ExampleError> {
    s3_code_examples::create_bucket(&client, &bucket_name, &region).await?;
    s3_code_examples::upload_object(&client, &bucket_name, &file_name, &key).await?;
    s3_code_examples::download_object(&client, &bucket_name, &key).await?;
    s3_code_examples::copy_object(&client, &bucket_name, &bucket_name, &key, &target_key).await?;
    s3_code_examples::list_objects(&client, &bucket_name).await?;
    s3_code_examples::clear_bucket(&client, &bucket_name).await?;
    s3_code_examples::delete_bucket(&client, &bucket_name).await?;

    Ok(())
}

async fn setup() -> (Region, Client, String, String, String, String) {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));
    let region = region_provider.region().await.unwrap();

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    let bucket_name = format!("{}{}", "amzn-s3-demo-bucket-", Uuid::new_v4());
    let file_name = "../s3/testfile.txt".to_string();
    let key = "test file key name".to_string();
    let target_key = "target_key".to_string();

    (region, client, bucket_name, file_name, key, target_key)
}
