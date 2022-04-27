/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.

Purpose

Shows how to use the AWS SDK for PHP (v3) to get started using AWS Identity and Access Management (IAM).
Create an IAM user, assume a role, and perform AWS actions.
1. Create a user that has no permissions.
2. Create a role and policy that grant s3:ListAllMyBuckets permission.
3. Grant the user permission to assume the role.
4. Create an S3 client object as the user and try to list buckets (this should fail).
5. Get temporary credentials by assuming the role.
6. Create an S3 client object with the temporary credentials and list the buckets (this should succeed).
7. Delete all the resources.
To run the bin file directly, use the following command:
cargo --bin iam-getting-started
To run the service class tests run:
cargo test
*/

// snippet-start:[rust.example_code.iam.iam_basics.scenario]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_iam::Error as iamError;
use aws_sdk_iam::{Client as iamClient, Credentials as iamCredentials};
use aws_sdk_s3::Client as s3Client;
use aws_sdk_sts::Client as stsClient;
use aws_types::region::Region;
use std::borrow::Borrow;
use tokio::time::{sleep, Duration};
use uuid::Uuid;

#[tokio::main]
async fn main() -> Result<(), iamError> {
    let (client, uuid, list_all_buckets_policy_document, inline_policy_document) =
        initialize_variables().await;

    if let Err(e) = run_iam_operations(
        client,
        uuid,
        list_all_buckets_policy_document,
        inline_policy_document,
    )
    .await
    {
        println!("{:?}", e);
    };

    Ok(())
}

async fn initialize_variables() -> (iamClient, String, String, String) {
    // snippet-start:[rust.example_code.iam.iam_basics.start_service]
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);
    // snippet-end:[rust.example_code.iam.iam_basics.start_service]
    // snippet-start:[rust.example_code.iam.iam_basics.uuid]
    let uuid = Uuid::new_v4().to_string();
    // snippet-end:[rust.example_code.iam.iam_basics.uuid]

    // snippet-start:[rust.example_code.iam.iam_basics.setup_list_buckets_policy]
    let list_all_buckets_policy_document = "{
                \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Allow\",
                    \"Action\": \"s3:ListAllMyBuckets\",
                    \"Resource\": \"arn:aws:s3:::*\"}]
    }"
    .to_string();
    // snippet-end:[rust.example_code.iam.iam_basics.setup_list_buckets_policy]
    // snippet-start:[rust.example_code.iam.iam_basics.setup_inline_policy]
    let inline_policy_document = "{
                \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Allow\",
                    \"Action\": \"sts:AssumeRole\",
                    \"Resource\": \"{}\"}]
    }"
    .to_string();
    // snippet-end:[rust.example_code.iam.iam_basics.setup_inline_policy]

    (
        client,
        uuid,
        list_all_buckets_policy_document,
        inline_policy_document,
    )
}

async fn run_iam_operations(
    client: iamClient,
    uuid: String,
    list_all_buckets_policy_document: String,
    inline_policy_document: String,
) -> Result<(), iamError> {
    // snippet-start:[rust.example_code.iam.iam_basics.create_user]
    let user = iam_service::create_user(&client, &format!("{}{}", "iam_demo_user_", uuid)).await?;
    println!(
        "Created the user with the name: {}",
        user.user_name.as_ref().unwrap()
    );
    // snippet-end:[rust.example_code.iam.iam_basics.create_user]
    let key = iam_service::create_access_key(&client, user.user_name.as_ref().unwrap()).await?;

    // snippet-start:[rust.example_code.iam.iam_basics.setup_role_policy_document]
    let assume_role_policy_document = "{
        \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Allow\",
                    \"Principal\": {\"AWS\": \"{}\"},
                    \"Action\": \"sts:AssumeRole\"
                }]
            }"
    .to_string()
    .replace("{}", user.arn.as_ref().unwrap());
    // snippet-end:[rust.example_code.iam.iam_basics.setup_role_policy_document]

    // snippet-start:[rust.example_code.iam.iam_basics.create_role]
    let assume_role_role = iam_service::create_role(
        &client,
        &format!("{}{}", "iam_demo_role_", uuid),
        &assume_role_policy_document,
    )
    .await?;
    println!(
        "Created the role with the ARN: {}",
        assume_role_role.arn.as_ref().unwrap()
    );
    // snippet-end:[rust.example_code.iam.iam_basics.create_role]

    // snippet-start:[rust.example_code.iam.iam_basics.create_policy]
    let list_all_buckets_policy = iam_service::create_policy(
        &client,
        &format!("{}{}", "iam_demo_policy_", uuid),
        &list_all_buckets_policy_document,
    )
    .await?;
    println!(
        "Created policy: {}",
        list_all_buckets_policy.policy_name.as_ref().unwrap()
    );
    // snippet-end:[rust.example_code.iam.iam_basics.create_policy]

    // snippet-start:[rust.example_code.iam.iam_basics.attach_role_policy]
    let attach_role_policy_result =
        iam_service::attach_role_policy(&client, &assume_role_role, &list_all_buckets_policy)
            .await?;
    println!(
        "Attached the policy to the role: {:?}",
        attach_role_policy_result
    );
    // snippet-end:[rust.example_code.iam.iam_basics.attach_role_policy]

    let inline_policy_name = &format!("{}{}", "iam_demo_inline_policy_", uuid);
    let inline_policy_document =
        inline_policy_document.replace("{}", assume_role_role.arn.as_ref().unwrap());
    iam_service::create_user_policy(&client, &user, &inline_policy_name, &inline_policy_document)
        .await?;
    println!("Created inline policy.");

    //First, fail to list the buckets with the user.
    let creds = iamCredentials::from_keys(
        key.access_key_id.as_ref().unwrap(),
        key.secret_access_key.as_ref().unwrap(),
        None,
    );
    let fail_config = aws_config::from_env()
        .credentials_provider(creds.clone())
        .load()
        .await;
    println!("Fail config: {:?}", fail_config);
    let fail_client: s3Client = s3Client::new(&fail_config);
    match fail_client.list_buckets().send().await {
        Ok(e) => {
            println!("This should not run. {:?}", e);
        }
        Err(e) => {
            println!("Successfully failed with error: {:?}", e)
        }
    }

    let sts_config = aws_config::from_env()
        .credentials_provider(creds.clone())
        .load()
        .await;
    let sts_client: stsClient = stsClient::new(&sts_config);
    sleep(Duration::from_secs(10)).await;
    let assumed_role = sts_client
        .assume_role()
        .role_arn(assume_role_role.arn.as_ref().unwrap())
        .role_session_name(&format!("{}{}", "iam_demo_assumerole_session_", uuid))
        .send()
        .await;
    println!("Assumed role: {:?}", assumed_role);
    sleep(Duration::from_secs(10)).await;

    let assumed_credentials = iamCredentials::from_keys(
        assumed_role
            .as_ref()
            .unwrap()
            .credentials
            .as_ref()
            .unwrap()
            .access_key_id
            .as_ref()
            .unwrap(),
        assumed_role
            .as_ref()
            .unwrap()
            .credentials
            .as_ref()
            .unwrap()
            .secret_access_key
            .as_ref()
            .unwrap(),
        assumed_role
            .as_ref()
            .unwrap()
            .credentials
            .as_ref()
            .unwrap()
            .session_token
            .borrow()
            .clone(),
    );

    let succeed_config = aws_config::from_env()
        .credentials_provider(assumed_credentials)
        .load()
        .await;
    println!("succeed config: {:?}", succeed_config);
    let succeed_client: s3Client = s3Client::new(&succeed_config);
    sleep(Duration::from_secs(10)).await;
    match succeed_client.list_buckets().send().await {
        Ok(_) => {
            println!("This should now run successfully.")
        }
        Err(e) => {
            println!("This should not run. {:?}", e);
            panic!()
        }
    }

    //Clean up.
    iam_service::detach_role_policy(
        &client,
        assume_role_role.role_name.as_ref().unwrap(),
        list_all_buckets_policy.arn.as_ref().unwrap(),
    )
    .await?;
    iam_service::delete_policy(&client, list_all_buckets_policy).await?;
    iam_service::delete_role(&client, &assume_role_role).await?;
    println!(
        "Deleted role {}",
        assume_role_role.role_name.as_ref().unwrap()
    );
    iam_service::delete_access_key(&client, &user, &key).await?;
    println!("Deleted key for {}", key.user_name.as_ref().unwrap());
    iam_service::delete_user_policy(&client, &user, &inline_policy_name).await?;
    println!("Deleted inline user policy: {}", inline_policy_name);
    iam_service::delete_user(&client, &user).await?;
    println!("Deleted user {}", user.user_name.as_ref().unwrap());

    Ok(())
}
// snippet-end:[rust.example_code.iam.iam_basics.scenario]
