use futures::StreamExt;

use aws_sdk_iam::error::SdkError;
use aws_sdk_iam::operation::list_policies::ListPoliciesError;
use iam_service::clients::IAM_CLIENT;

#[tokio::main]
async fn main() -> Result<(), SdkError<ListPoliciesError>> {
    let client = IAM_CLIENT.get().await;

    let mut list_policies = client.list_policies().into_paginator().send();

    while let Some(list_policies_output) = list_policies.next().await {
        match list_policies_output {
            Ok(list_policies) => {
                if let Some(policies) = list_policies.policies() {
                    for policy in policies.iter().enumerate() {
                        println!("{:?}", policy.policy_name())
                    }
                }
            }

            Err(err) => return Err(err),
        }
    }

    Ok(())
}

#[cfg(test)]
mod test {
    use crate::main;

    #[test]
    fn test_hello() {
        assert!(main().is_err(), "main should not throw an error")
    }
}
