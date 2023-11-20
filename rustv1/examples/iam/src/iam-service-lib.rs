/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

// snippet-start:[rust.example_code.iam.scenario_getting_started.lib]

use aws_sdk_iam::error::SdkError;
use aws_sdk_iam::operation::{
    attach_role_policy::*, create_access_key::*, create_role::*, create_service_linked_role::*,
    delete_user::*, delete_user_policy::*, get_account_password_policy::*, get_role::*,
    list_attached_role_policies::*, list_groups::*, list_policies::*, list_role_policies::*,
    list_roles::*, list_saml_providers::*, list_users::*,
};
use aws_sdk_iam::types::{AccessKey, Policy, PolicyScopeType, Role, User};
use aws_sdk_iam::Client as iamClient;
use aws_sdk_iam::{Client, Error as iamError};
use tokio::time::{sleep, Duration};

// snippet-start:[rust.example_code.iam.service.create_policy]
pub async fn create_policy(
    client: &iamClient,
    policy_name: &str,
    policy_document: &str,
) -> Result<Policy, iamError> {
    let policy = client
        .create_policy()
        .policy_name(policy_name)
        .policy_document(policy_document)
        .send()
        .await?;
    Ok(policy.policy.unwrap())
}
// snippet-end:[rust.example_code.iam.service.create_policy]

#[cfg(test)]
mod test_create_policy {
    use crate::create_policy;
    use http::StatusCode;
    use sdk_examples_test_utils::single_shot_client;

    #[tokio::test]
    async fn test_create_policy_success() {
        let client = single_shot_client!(
            sdk: aws_sdk_iam,
            status: StatusCode::OK,
            response: include_str!("../testing/test_create_policy_response_success.xml")
        );

        let response = create_policy(&client, "{}", "test_role").await;
        assert!(response.is_ok());
    }

    #[tokio::test]
    async fn test_create_policy_failed() {
        let client = single_shot_client!(
            sdk: aws_sdk_iam,
            status: StatusCode::BAD_REQUEST,
            response: include_str!("../testing/test_create_policy_response_malformed.xml")
        );

        let response = create_policy(&client, "{}", "test_role").await;
        assert!(response.is_err());
    }
}

// snippet-start:[rust.example_code.iam.service.create_role]
pub async fn create_role(
    client: &iamClient,
    role_name: &str,
    role_policy_document: &str,
) -> Result<Role, iamError> {
    let response: CreateRoleOutput = loop {
        if let Ok(response) = client
            .create_role()
            .role_name(role_name)
            .assume_role_policy_document(role_policy_document)
            .send()
            .await
        {
            break response;
        }
    };

    Ok(response.role.unwrap())
}
// snippet-end:[rust.example_code.iam.service.create_role]

// snippet-start:[rust.example_code.iam.service.create_user]
pub async fn create_user(client: &iamClient, user_name: &str) -> Result<User, iamError> {
    let response = client.create_user().user_name(user_name).send().await?;

    Ok(response.user.unwrap())
}
// snippet-end:[rust.example_code.iam.service.create_user]

// snippet-start:[rust.example_code.iam.service.list_roles]
pub async fn list_roles(
    client: &iamClient,
    path_prefix: Option<String>,
    marker: Option<String>,
    max_items: Option<i32>,
) -> Result<ListRolesOutput, SdkError<ListRolesError>> {
    let response = client
        .list_roles()
        .set_path_prefix(path_prefix)
        .set_marker(marker)
        .set_max_items(max_items)
        .send()
        .await?;
    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.list_roles]

// snippet-start:[rust.example_code.iam.service.get_role]
pub async fn get_role(
    client: &iamClient,
    role_name: String,
) -> Result<GetRoleOutput, SdkError<GetRoleError>> {
    let response = client.get_role().role_name(role_name).send().await?;
    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.get_role]

// snippet-start:[rust.example_code.iam.service.list_users]
pub async fn list_users(
    client: &iamClient,
    path_prefix: Option<String>,
    marker: Option<String>,
    max_items: Option<i32>,
) -> Result<ListUsersOutput, SdkError<ListUsersError>> {
    let response = client
        .list_users()
        .set_path_prefix(path_prefix)
        .set_marker(marker)
        .set_max_items(max_items)
        .send()
        .await?;
    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.list_users]

// snippet-start:[rust.example_code.iam.service.create_user_policy]
pub async fn create_user_policy(
    client: &iamClient,
    user: &User,
    policy_name: &str,
    policy_document: &str,
) -> Result<(), iamError> {
    client
        .put_user_policy()
        .user_name(user.user_name())
        .policy_name(policy_name)
        .policy_document(policy_document)
        .send()
        .await?;

    Ok(())
}
// snippet-end:[rust.example_code.iam.service.create_user_policy]

// snippet-start:[rust.example_code.iam.service.delete_role]
pub async fn delete_role(client: &iamClient, role: &Role) -> Result<(), iamError> {
    let role = role.clone();
    while client
        .delete_role()
        .role_name(role.role_name())
        .send()
        .await
        .is_err()
    {
        sleep(Duration::from_secs(2)).await;
    }
    Ok(())
}
// snippet-end:[rust.example_code.iam.service.delete_role]

// snippet-start:[rust.example_code.iam.service.delete_service_linked_role]
pub async fn delete_service_linked_role(
    client: &iamClient,
    role_name: &str,
) -> Result<(), iamError> {
    client
        .delete_service_linked_role()
        .role_name(role_name)
        .send()
        .await?;

    Ok(())
}
// snippet-end:[rust.example_code.iam.service.delete_service_linked_role]

// snippet-start:[rust.example_code.iam.service.delete_user]
pub async fn delete_user(client: &iamClient, user: &User) -> Result<(), SdkError<DeleteUserError>> {
    let user = user.clone();
    let mut tries: i32 = 0;
    let max_tries: i32 = 10;

    let response: Result<(), SdkError<DeleteUserError>> = loop {
        match client
            .delete_user()
            .user_name(user.user_name())
            .send()
            .await
        {
            Ok(_) => {
                break Ok(());
            }
            Err(e) => {
                tries += 1;
                if tries > max_tries {
                    break Err(e);
                }
                sleep(Duration::from_secs(2)).await;
            }
        }
    };

    response
}
// snippet-end:[rust.example_code.iam.service.delete_user]

// snippet-start:[rust.example_code.iam.service.attach_role_policy]
pub async fn attach_role_policy(
    client: &iamClient,
    role: &Role,
    policy: &Policy,
) -> Result<AttachRolePolicyOutput, SdkError<AttachRolePolicyError>> {
    client
        .attach_role_policy()
        .role_name(role.role_name())
        .policy_arn(policy.arn().unwrap_or_default())
        .send()
        .await
}
// snippet-end:[rust.example_code.iam.service.attach_role_policy]

// snippet-start:[rust.example_code.iam.service.attach_user_policy]
pub async fn attach_user_policy(
    client: &iamClient,
    user_name: &str,
    policy_arn: &str,
) -> Result<(), iamError> {
    client
        .attach_user_policy()
        .user_name(user_name)
        .policy_arn(policy_arn)
        .send()
        .await?;

    Ok(())
}
// snippet-end:[rust.example_code.iam.service.attach_user_policy]

// snippet-start:[rust.example_code.iam.service.detach_user_policy]
pub async fn detach_user_policy(
    client: &iamClient,
    user_name: &str,
    policy_arn: &str,
) -> Result<(), iamError> {
    client
        .detach_user_policy()
        .user_name(user_name)
        .policy_arn(policy_arn)
        .send()
        .await?;

    Ok(())
}
// snippet-end:[rust.example_code.iam.service.detach_user_policy]

// snippet-start:[rust.example_code.iam.service.create_access_key]
pub async fn create_access_key(client: &iamClient, user_name: &str) -> Result<AccessKey, iamError> {
    let mut tries: i32 = 0;
    let max_tries: i32 = 10;

    let response: Result<CreateAccessKeyOutput, SdkError<CreateAccessKeyError>> = loop {
        match client.create_access_key().user_name(user_name).send().await {
            Ok(inner_response) => {
                break Ok(inner_response);
            }
            Err(e) => {
                tries += 1;
                if tries > max_tries {
                    break Err(e);
                }
                sleep(Duration::from_secs(2)).await;
            }
        }
    };

    Ok(response.unwrap().access_key.unwrap())
}
// snippet-end:[rust.example_code.iam.service.create_access_key]

// snippet-start:[rust.example_code.iam.service.delete_access_key]
pub async fn delete_access_key(
    client: &iamClient,
    user: &User,
    key: &AccessKey,
) -> Result<(), iamError> {
    loop {
        match client
            .delete_access_key()
            .user_name(user.user_name())
            .access_key_id(key.access_key_id())
            .send()
            .await
        {
            Ok(_) => {
                break;
            }
            Err(e) => {
                println!("Can't delete the access key: {:?}", e);
                sleep(Duration::from_secs(2)).await;
            }
        }
    }
    Ok(())
}
// snippet-end:[rust.example_code.iam.service.delete_access_key]

// snippet-start:[rust.example_code.iam.service.detach_role_policy]
pub async fn detach_role_policy(
    client: &iamClient,
    role_name: &str,
    policy_arn: &str,
) -> Result<(), iamError> {
    client
        .detach_role_policy()
        .role_name(role_name)
        .policy_arn(policy_arn)
        .send()
        .await?;

    Ok(())
}
// snippet-end:[rust.example_code.iam.service.detach_role_policy]

// snippet-start:[rust.example_code.iam.service.delete_policy]
pub async fn delete_policy(client: &iamClient, policy: Policy) -> Result<(), iamError> {
    client
        .delete_policy()
        .policy_arn(policy.arn.unwrap())
        .send()
        .await?;
    Ok(())
}
// snippet-end:[rust.example_code.iam.service.delete_policy]

// snippet-start:[rust.example_code.iam.service.delete_user_policy]
pub async fn delete_user_policy(
    client: &iamClient,
    user: &User,
    policy_name: &str,
) -> Result<(), SdkError<DeleteUserPolicyError>> {
    client
        .delete_user_policy()
        .user_name(user.user_name())
        .policy_name(policy_name)
        .send()
        .await?;

    Ok(())
}
// snippet-end:[rust.example_code.iam.service.delete_user_policy]

// snippet-start:[rust.example_code.iam.service.list_policies]
// snippet-start:[rust.example_code.iam.hello_lib]
pub async fn list_policies(
    client: iamClient,
    path_prefix: String,
) -> Result<Vec<String>, SdkError<ListPoliciesError>> {
    let list_policies = client
        .list_policies()
        .path_prefix(path_prefix)
        .scope(PolicyScopeType::Local)
        .into_paginator()
        .items()
        .send()
        .try_collect()
        .await?;

    let policy_names = list_policies
        .into_iter()
        .map(|p| {
            let name = p
                .policy_name
                .unwrap_or_else(|| "Missing Policy Name".to_string());
            println!("{}", name);
            name
        })
        .collect();

    Ok(policy_names)
}
// snippet-end:[rust.example_code.iam.hello_lib]
// snippet-end:[rust.example_code.iam.service.list_policies]

// snippet-start:[rust.example_code.iam.service.list_groups]
pub async fn list_groups(
    client: &iamClient,
    path_prefix: Option<String>,
    marker: Option<String>,
    max_items: Option<i32>,
) -> Result<ListGroupsOutput, SdkError<ListGroupsError>> {
    let response = client
        .list_groups()
        .set_path_prefix(path_prefix)
        .set_marker(marker)
        .set_max_items(max_items)
        .send()
        .await?;

    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.list_groups]

// snippet-start:[rust.example_code.iam.service.create_service_linked_role]
pub async fn create_service_linked_role(
    client: &iamClient,
    aws_service_name: String,
    custom_suffix: Option<String>,
    description: Option<String>,
) -> Result<CreateServiceLinkedRoleOutput, SdkError<CreateServiceLinkedRoleError>> {
    let response = client
        .create_service_linked_role()
        .aws_service_name(aws_service_name)
        .set_custom_suffix(custom_suffix)
        .set_description(description)
        .send()
        .await?;

    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.create_service_linked_role]

// snippet-start:[rust.example_code.iam.service.get_account_password_policy]
pub async fn get_account_password_policy(
    client: &iamClient,
) -> Result<GetAccountPasswordPolicyOutput, SdkError<GetAccountPasswordPolicyError>> {
    let response = client.get_account_password_policy().send().await?;

    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.get_account_password_policy]

// snippet-start:[rust.example_code.iam.service.list_attached_role_policies]
pub async fn list_attached_role_policies(
    client: &iamClient,
    role_name: String,
    path_prefix: Option<String>,
    marker: Option<String>,
    max_items: Option<i32>,
) -> Result<ListAttachedRolePoliciesOutput, SdkError<ListAttachedRolePoliciesError>> {
    let response = client
        .list_attached_role_policies()
        .role_name(role_name)
        .set_path_prefix(path_prefix)
        .set_marker(marker)
        .set_max_items(max_items)
        .send()
        .await?;

    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.list_attached_role_policies]

// snippet-start:[rust.example_code.iam.service.list_role_policies]
pub async fn list_role_policies(
    client: &iamClient,
    role_name: &str,
    marker: Option<String>,
    max_items: Option<i32>,
) -> Result<ListRolePoliciesOutput, SdkError<ListRolePoliciesError>> {
    let response = client
        .list_role_policies()
        .role_name(role_name)
        .set_marker(marker)
        .set_max_items(max_items)
        .send()
        .await?;

    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.list_role_policies]

// snippet-start:[rust.example_code.iam.service.list_saml_providers]
pub async fn list_saml_providers(
    client: &Client,
) -> Result<ListSamlProvidersOutput, SdkError<ListSAMLProvidersError>> {
    let response = client.list_saml_providers().send().await?;

    Ok(response)
}
// snippet-end:[rust.example_code.iam.service.list_saml_providers]

// snippet-end:[rust.example_code.iam.scenario_getting_started.lib]
