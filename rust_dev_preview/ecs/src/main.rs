/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#[tokio::main]
async fn main() -> Result<(), aws_sdk_ecs::Error> {
    let shared_config = aws_config::load_from_env().await;
    let client = aws_sdk_ecs::Client::new(&shared_config);
    let cluster = client
        .create_cluster()
        .cluster_name("test_cluster")
        .send()
        .await?;
    println!("cluster created: {:?}", cluster);

    let cluster_deleted = client
        .delete_cluster()
        .cluster("test_cluster")
        .send()
        .await?;
    println!("cluster deleted: {:?}", cluster_deleted);
    Ok(())
}
