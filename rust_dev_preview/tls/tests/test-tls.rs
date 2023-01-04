/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#[ignore]
#[tokio::test]
async fn test_it_runs() {
    match tls::connect_via_tls_13().await {
        Err(_e) => assert!(false),
        _ => assert!(true),
    }
}
