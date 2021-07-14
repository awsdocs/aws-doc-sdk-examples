/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/// Lists your AWS Elemental MediaLive input names and ARNs.
#[tokio::main]
async fn main() -> Result<(), medialive::Error> {
    let client = medialive::Client::from_env();
    let input_list = client.list_inputs().send().await?;

    for i in input_list.inputs.unwrap_or_default() {
        let input_arn = i.arn.as_deref().unwrap_or_default();
        let input_name = i.name.as_deref().unwrap_or_default();

        println!("Input Name : {}, Input ARN : {}", input_name, input_arn);
    }

    Ok(())
}
