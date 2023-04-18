use aws_sdk_iam::types::PolicyScopeType;
use futures::StreamExt;

use aws_sdk_iam::error::SdkError;
use aws_sdk_iam::operation::list_policies::ListPoliciesError;
use iam_service::clients::IAM_CLIENT;

// snippet-start:[rust.example_code.iam.hello]
#[tokio::main]
async fn main() -> Result<(), SdkError<ListPoliciesError>> {
    let client = IAM_CLIENT.get().await;

    let mut list_policies = client
        .list_policies()
        // Get policies created by the user.
        .set_scope(Some(PolicyScopeType::Local))
        .into_paginator()
        .send();

    while let Some(list_policies_output) = list_policies.next().await {
        match list_policies_output {
            Ok(list_policies) => {
                if let Some(policies) = list_policies.policies() {
                    for policy in policies.iter() {
                        println!("{:?}", policy.policy_name().unwrap())
                    }
                }
            }

            Err(err) => return Err(err),
        }
    }

    Ok(())
}
// snippet-end:[rust.example_code.iam.hello]

#[cfg(test)]
mod test {
    use crate::main;

    #[test]
    fn test_hello() {
        assert_eq!(main().is_err(), false, "don't panic")
    }
}
