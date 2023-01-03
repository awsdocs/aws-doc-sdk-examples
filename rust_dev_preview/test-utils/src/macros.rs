/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

//! Macros and functions that simplify making AWS SDK clients with a mocked request/response pair.

/// Generate a single http::{Request, Response} pair. The first argument
/// is the expression to use as the value for SdkBody::from. The second
/// argument with the HTTP Status code and the response body.
///
/// To create a number of events to use for a test client, create a vec![]
/// of many of these test_event pairs.
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

/// Create a single-shot Client for `sdk_crate`. The `req` and `res` will be the
/// body of the request and the response, respectiecly. The `status` is the HTTP
/// status code for the response. The credentials are hardcoded test values.
#[macro_export]
macro_rules! single_shot_client {
    ( sdk: $sdk_crate:ident, status: $status:expr, response: $res:expr) => {{
        ($sdk_crate::Client::from_conf_conn(
            sdk_examples_test_utils::client_config!($sdk_crate),
            sdk_examples_test_utils::single_shot("".into(), ($status.into(), $res.into())),
        ))
    }};
    ( sdk: $sdk_crate:ident, request: $req:expr, status: $status:expr, response: $res:expr) => {{
        ($sdk_crate::Client::from_conf_conn(
            sdk_examples_test_utils::client_config!($sdk_crate),
            sdk_examples_test_utils::single_shot($req.into(), ($status.into(), $res.into())),
        ))
    }};
}

/// Create a hard-coded testing config for an AWS SDK.
/// TODO: remove after https://github.com/awslabs/smithy-rs/pull/2145
#[macro_export]
macro_rules! client_config {
    (
        $sdk_crate:ident
    ) => {
        $sdk_crate::Config::builder()
            .credentials_provider($sdk_crate::Credentials::new(
                "ATESTCLIENT",
                "atestsecretkey",
                Some("atestsessiontoken".to_string()),
                None,
                "",
            ))
            .region($sdk_crate::Region::new("us-east-1"))
            .build()
    };
}
