// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

use async_once::AsyncOnce;
use aws_config::SdkConfig;
use lazy_static::lazy_static;

lazy_static! {
    pub static ref SDK_CONFIG: AsyncOnce<SdkConfig> =
        AsyncOnce::new(async { aws_config::load_from_env().await });
    pub static ref IAM_CLIENT: AsyncOnce<aws_sdk_iam::Client> = AsyncOnce::new(async {
        let config = SDK_CONFIG.get().await;
        aws_sdk_iam::Client::new(config)
    });
}
