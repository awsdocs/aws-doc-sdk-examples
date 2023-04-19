use aws_sdk_iam::types::PolicyScopeType;
use aws_sdk_iam::error::SdkError;
use aws_sdk_iam::operation::list_policies::ListPoliciesError;

// snippet-start:[rust.example_code.iam.hello]
#[tokio::main]
async fn main() -> Result<(), SdkError<ListPoliciesError>> {
    let sdk_config = aws_config::load_from_env().await;
    let client = aws_sdk_iam::Client::new(&sdk_config);

    iam_service::list_policies(client, Some(PolicyScopeType::Local), None, None, None).await?;
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
