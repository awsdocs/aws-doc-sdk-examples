// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

use async_once::AsyncOnce;
use aws_config::SdkConfig;
use lazy_static::lazy_static;

lazy_static! {
    pub static ref SDK_CONFIG: AsyncOnce<SdkConfig> =
        AsyncOnce::new(async { aws_config::load_from_env().await });
    pub static ref S3_CLIENT: AsyncOnce<aws_sdk_s3::Client> = AsyncOnce::new(async {
        let config = SDK_CONFIG.get().await;
        aws_sdk_s3::Client::new(config)
    });
    pub static ref GLUE_CLIENT: AsyncOnce<aws_sdk_glue::Client> = AsyncOnce::new(async {
        let config = SDK_CONFIG.get().await;
        aws_sdk_glue::Client::new(config)
    });
}
