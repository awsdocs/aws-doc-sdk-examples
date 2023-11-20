use aws_smithy_types::body::SdkBody;

pub mod macros;
pub mod waiter;

/// Create a single-shot test connection. The arguments are the same as test_event,
/// but the expanded macro creates a TestConnection. The `TestConnection` can be
/// provided directly to a `Client::from_conf_conn`.
pub fn single_shot(
    req: SdkBody,
    res: (http::StatusCode, Vec<(&str, &str)>, SdkBody),
) -> aws_smithy_runtime::client::http::test_util::StaticReplayClient {
    aws_smithy_runtime::client::http::test_util::StaticReplayClient::new(vec![
        (test_event!(req, (res.0, res.1, res.2))),
    ])
}
