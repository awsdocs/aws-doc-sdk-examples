/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_iam::{Client as iamClient, Region};
use uuid::Uuid;

#[ignore]
#[tokio::test]
async fn test_create_role() {
    let uuid = Uuid::new_v4().to_string();

    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let user = iam_service::create_user(&client, &format!("{}{}", "iam_test_user_", uuid)).await;
    let role_policy_document = "{\"Version\": \"2012-10-17\",
        \"Statement\": [{
            \"Effect\": \"Allow\",
            \"Principal\": {\"AWS\": \"{}\"},
            \"Action\": \"sts:AssumeRole\"
        }]}"
    .to_string();
    let role_policy_document =
        role_policy_document.replace("{}", user.unwrap().arn.as_ref().unwrap());
    let role_name = &format!("{}{}", "iam_test_role_", uuid);
    let role = iam_service::create_role(&client, role_name, &role_policy_document)
        .await
        .unwrap();

    println!("Role: {}", role.role_name.unwrap());

    //test_list_role_policies
    let list = iam_service::list_role_policies(&client, role_name, None, None).await;
    println!("Role Policies: ");
    for item in list.unwrap().policy_names.unwrap() {
        println!("RP: {}", item);
    }
}

#[ignore]
#[tokio::test]
async fn test_create_service_linked_role() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let uuid = Uuid::new_v4().to_string().replace("-", "");
    let role = iam_service::create_service_linked_role(
        &client,
        "autoscaling.amazonaws.com".parse().unwrap(),
        Some(uuid),
        None,
    )
    .await
    .unwrap();
    println!(
        "{}",
        role.role.as_ref().unwrap().role_name.as_ref().unwrap()
    );
}

#[ignore]
#[tokio::test]
async fn test_get_account_password_policy() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let password_policy = iam_service::get_account_password_policy(&client)
        .await
        .unwrap();
    println!("{:?}", password_policy);
}

#[ignore]
#[tokio::test]
async fn test_list_and_get_roles() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let roles = iam_service::list_roles(&client, None, None, None)
        .await
        .unwrap();
    println!("All roles:");
    for role in &roles.roles.unwrap() {
        assert_eq!(&role.role_name, &role.role_name);
        println!("{}", role.role_name.as_ref().unwrap());
    }
    println!("\nPathed roles:");
    let roles = iam_service::list_roles(&client, Some("/iam".parse().unwrap()), None, None)
        .await
        .unwrap();
    for role in &roles.roles.unwrap() {
        assert_eq!(&role.role_name, &role.role_name);
        println!("{}", role.role_name.as_ref().unwrap());
    }
    println!("\nLimited roles:");
    let roles = iam_service::list_roles(&client, None, None, Some(5))
        .await
        .unwrap();
    for role in &roles.roles.unwrap() {
        assert_eq!(&role.role_name, &role.role_name);
        println!("{}", role.role_name.as_ref().unwrap());
    }
    let marker = roles.marker.unwrap();
    println!("\nPaginated roles:");
    let mut get_role_name: String = "".parse().unwrap();
    let roles = iam_service::list_roles(&client, None, Some(marker), Some(5))
        .await
        .unwrap();
    for role in &roles.roles.unwrap() {
        assert_eq!(&role.role_name, &role.role_name);
        println!("{}", role.role_name.as_ref().unwrap());
        get_role_name = role.role_name.clone().unwrap();
    }

    let get_role = iam_service::get_role(&client, get_role_name.parse().unwrap())
        .await
        .unwrap();
    assert_eq!(get_role_name, get_role.role.unwrap().role_name.unwrap());
    println!("{}", get_role_name);
}

#[ignore]
#[tokio::test]
async fn test_list_attached_role_policies() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let attached_role_policies = iam_service::list_attached_role_policies(
        &client,
        "cfn-lint-referee-plugin-DO-NOT-DELETE".parse().unwrap(),
        None,
        None,
        None,
    )
    .await
    .unwrap();
    println!("Arp: {:?}", attached_role_policies);
}

#[ignore]
#[tokio::test]
async fn test_list_groups() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let groups = iam_service::list_groups(&client, None, None, None)
        .await
        .unwrap();
    for group in groups.groups.unwrap() {
        println!("{}", group.group_name.unwrap());
    }
}

#[ignore]
#[tokio::test]
async fn test_list_policies() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let policies = iam_service::list_policies(&client, None, None, None)
        .await
        .unwrap();
    for policy in policies.policies.unwrap() {
        println!("{}", policy.policy_name.unwrap());
    }
}

#[ignore]
#[tokio::test]
async fn test_list_roles() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let roles = iam_service::list_roles(&client, None, None, None).await;
    for role in roles.unwrap().roles.unwrap() {
        println!("Role: {}", role.role_name.unwrap());
    }
}

#[ignore]
#[tokio::test]
async fn test_list_saml_providers() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let saml_providers = iam_service::list_saml_providers(&client).await.unwrap();
    println!("SAML Providers: {:?}", saml_providers);
}

#[ignore]
#[tokio::test]
async fn test_list_users() {
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = iamClient::new(&shared_config);

    let users = iam_service::list_users(&client, None, None, None)
        .await
        .unwrap();
    for user in users.users.unwrap() {
        println!("{}", user.user_name.unwrap());
    }
}
