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
        aws_smithy_runtime::client::http::test_util::ReplayEvent::new(
            http::Request::builder()
                .body(aws_smithy_types::body::SdkBody::from($req))
                .unwrap(),
            http::Response::builder()
                .status($status)
                .body(aws_smithy_types::body::SdkBody::from($res))
                .unwrap(),
        )
    }};
    (
        $req:expr,
        (
            $status:expr,
            $headers:expr,
            $res:expr
        )
    ) => {{
        aws_smithy_runtime::client::http::test_util::ReplayEvent::new(
            http::Request::builder()
                .body(aws_smithy_types::body::SdkBody::from($req))
                .unwrap(),
            {
                let mut builder = http::Response::builder().status($status);
                for (k, v) in $headers {
                    builder = builder.header(k, v);
                }
                builder
                    .body(aws_smithy_types::body::SdkBody::from($res))
                    .unwrap()
            },
        )
    }};
}

/// Create a single-shot Client for `sdk_crate`. The `req` and `res` will be the
/// body of the request and the response, respectively. The `status` is the HTTP
/// status code for the response. The credentials are hardcoded test values.
#[macro_export]
macro_rules! single_shot_client {
    (sdk: $sdk_crate:ident, status: $status:expr, response: $res:expr) => {{
        sdk_examples_test_utils::single_shot_client!($sdk_crate, "", $status, vec![], $res)
    }};
    (sdk: $sdk_crate:ident, request: $req:expr, status: $status:expr, response: $res:expr) => {{
        sdk_examples_test_utils::single_shot_client!($sdk_crate, $res, $status, vec![], $res)
    }};
    (sdk: $sdk_crate:ident, status: $status:expr, headers: $res_headers:expr, response: $res:expr) => {{
        sdk_examples_test_utils::single_shot_client!($sdk_crate, "", $status, $res_headers, $res)
    }};
    // "Private" internal root macro.
    ($sdk_crate:ident, $req:expr, $status:expr, $res_headers:expr, $res:expr) => {{
        $sdk_crate::Client::from_conf(
            sdk_examples_test_utils::client_config!($sdk_crate)
                .http_client(sdk_examples_test_utils::single_shot(
                    $req.into(),
                    ($status.try_into().unwrap(), $res_headers, $res.into()),
                ))
                .build(),
        )
    }};
}

/// Create a hard-coded testing config for an AWS SDK.
#[macro_export]
macro_rules! client_config {
    (
        $sdk_crate:ident
    ) => {
        /// TODO: remove after https://github.com/awslabs/smithy-rs/pull/2145
        $sdk_crate::Config::builder()
            .credentials_provider($sdk_crate::config::Credentials::new(
                "ATESTCLIENT",
                "atestsecretkey",
                Some("atestsessiontoken".to_string()),
                None,
                "",
            ))
            .region($sdk_crate::config::Region::new("us-east-1"))
    };
}
