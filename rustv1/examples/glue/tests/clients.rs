// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

use glue_code_examples::clients::GLUE_CLIENT;

#[tokio::test]
#[ignore]
async fn glue_client_loads() {
    let client = GLUE_CLIENT.get().await;

    let get_databases = client.get_databases().send().await;

    assert!(get_databases.is_ok());
}
