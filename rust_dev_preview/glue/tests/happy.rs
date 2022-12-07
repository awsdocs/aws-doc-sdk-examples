// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

use aws_sdk_glue::model::JobRunState;
use glue_code_examples::{
    clients::{GLUE_CLIENT, S3_CLIENT},
    GlueScenario, GlueScenarioArgs,
};

fn scenario_for_test() -> GlueScenario {
    let mut args: GlueScenarioArgs = Default::default();
    let iam_role = std::env::var("RUST_SDK_GLUE_TEST_ROLE").expect("Missing env variable RUST_SDK_GLUE_TEST_ROLE. You must supply this role for the AWS Glue tests to run correctly.");
    args.iam_role = secrecy::Secret::new(iam_role);
    GlueScenario::from_args(args)
}

async fn clean_up_and_verify(scenario: &mut GlueScenario) {
    scenario.cleanup().await.expect("Failed to clean up");
}

#[tokio::test]
#[ignore]
async fn test_prepare() {
    let client = GLUE_CLIENT.get().await;
    let mut scenario = scenario_for_test();

    scenario
        .prepare()
        .await
        .expect("Failed to prepare scenario");

    client
        .get_crawler()
        .name(scenario.crawler())
        .send()
        .await
        .expect("Failed to retrieve crawlers")
        .crawler()
        .unwrap_or_else(|| panic!("Failed to retrieve crawler {}", scenario.crawler()));

    clean_up_and_verify(&mut scenario).await;
}

#[tokio::test]
#[ignore]
async fn test_run() {
    let client = GLUE_CLIENT.get().await;
    let mut scenario = scenario_for_test();

    scenario
        .prepare()
        .await
        .expect("Failed to prepare scenario");

    scenario.run().await.expect("Failed to run scenario");

    let get_job_run = client
        .get_job_run()
        .job_name(scenario.job())
        .run_id(scenario.job_run_id())
        .send()
        .await
        .expect("Failed to retrieve job runs");

    let job_run = get_job_run.job_run().unwrap_or_else(|| {
        panic!(
            "Failed to find job run {} {}",
            scenario.job(),
            scenario.job_run_id()
        )
    });

    assert_eq!(job_run.job_run_state(), Some(&JobRunState::Succeeded));

    let s3_client = S3_CLIENT.get().await;
    let list_objects = s3_client
        .list_objects()
        .bucket(scenario.bucket())
        .send()
        .await
        .expect("Failed to get bucket items");

    println!("{:?}", list_objects);

    clean_up_and_verify(&mut scenario).await;
}
