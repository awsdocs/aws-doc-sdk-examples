use aws_smithy_http::body::SdkBody;

pub mod macros;

/// Create a single-shot test connection. The arguments are the same as test_event,
/// but the expanded macro creates a TestConnection. The `TestConnection` can be
/// provided directly to a `Client::from_conf_conn`.
pub fn single_shot(
    req: SdkBody,
    res: (http::StatusCode, Vec<(&str, &str)>, SdkBody),
) -> aws_smithy_client::test_connection::TestConnection<SdkBody> {
    aws_smithy_client::test_connection::TestConnection::new(vec![
        (test_event!(req, (res.0, res.1, res.2))),
    ])
}
