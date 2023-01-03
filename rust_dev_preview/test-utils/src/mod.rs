use aws_smithy_client::test_connection::TestConnection;
use aws_smithy_http::body::SdkBody;
use http::StatusCode;

pub mod macros;

/// Create a single-shot test connection. The arguments are the same as test_event,
/// but the expanded macro creates a TestConnection. The `TestConnection` can be
/// provided directly to a `Client::from_conf_conn`.
pub fn single_shot(req: SdkBody, res: (StatusCode, SdkBody)) -> TestConnection<SdkBody> {
    aws_smithy_client::test_connection::TestConnection::new(vec![
        (test_event!(req, (res.0, res.1))),
    ])
}
