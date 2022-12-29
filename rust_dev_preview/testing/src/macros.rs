#[macro_export]
macro_rules! test_event {
    (
        $req:expr,
        (
            $status:expr,
            $res:expr
        )
    ) => {{
        (
            http::Request::builder()
                .body(aws_smithy_http::body::SdkBody::from($req))
                .unwrap(),
            http::Response::builder()
                .status($status)
                .body(aws_smithy_http::body::SdkBody::from($res))
                .unwrap(),
        )
    }};
}

#[macro_export]
macro_rules! single_shot {
    (
            $req:expr,
            (
                $status:expr,
                $res:expr
            )
        ) => {{
        (aws_smithy_client::test_connection::TestConnection::new(vec![
            testing_examples::test_event!($req, ($status, $res)),
        ]))
    }};
}

#[macro_export]
macro_rules! single_shot_client {
    (
            $sdk_crate:ident,
            $req:expr,
            $status:expr,
            $res:expr
        ) => {{
        ($sdk_crate::Client::from_conf_conn(
            $sdk_crate::Config::builder()
                .credentials_provider($sdk_crate::Credentials::new(
                    "ATESTCLIENT",
                    "atestsecretkey",
                    Some("atestsessiontoken".to_string()),
                    None,
                    "",
                ))
                .region($sdk_crate::Region::new("us-east-1"))
                .build(),
            testing_examples::single_shot!($req, ($status, $res)),
        ))
    }};
}
