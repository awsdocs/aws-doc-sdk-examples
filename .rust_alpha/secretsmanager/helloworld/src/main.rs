/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
use aws_hyper::conn::Standard;
use secretsmanager::Client;
use secretsmanager::Region;
use secretsmanager::SdkError;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[tokio::main]
async fn main() {
    let secret_name = "some-secret-id";
    let secret_value = "testsecret";
    SubscriberBuilder::default()
        .with_env_filter("info")
        .with_span_events(FmtSpan::CLOSE)
        .init();
    let config = secretsmanager::Config::builder()
        // region can also be loaded from AWS_DEFAULT_REGION, just remove this line.
        .region(Region::new("us-east-1"))
        // creds loaded from environment variables, or they can be hard coded.
        // Other credential providers not currently supported
        .build();
    let conn = Standard::https();
    let client = Client::from_conf_conn(config, conn);

    // attempt to create a secret,
    // need to find a better way to handle failure such as ResourceExistsException
    let data = match client
        .create_secret()
        .name(secret_name)
        .secret_string(secret_value)
        .send()
        .await
    {
        Ok(secret) => secret,
        Err(SdkError::ServiceError { err, .. }) => match err.kind {
            secretsmanager::error::CreateSecretErrorKind::ResourceExistsError(_) => {
                panic!("This secret already exists!")
            }
            _ => panic!("Secretsmanager Error: {}", err),
        },
        Err(other) => panic!("Failed to create secret: {}", other),
    };
    println!(
        "Created secret {:?} with ARN {:?}",
        secret_name,
        data.arn.unwrap()
    );

    //  try and retrieve the secret value we just created
    let retrieved_secret = client
        .get_secret_value()
        .secret_id(secret_name)
        .send()
        .await
        .expect("unable to retrieve secret");

    assert_eq!(retrieved_secret.secret_string.unwrap(), secret_value);
    println!(
        "successfully retrieved secret string that matches the original one we created earlier"
    );
}
