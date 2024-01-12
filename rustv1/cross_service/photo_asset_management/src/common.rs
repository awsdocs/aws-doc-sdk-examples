// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use aws_config::{BehaviorVersion, SdkConfig};

#[cfg(not(debug_assertions))]
pub fn init_tracing_subscriber() {
    tracing_subscriber::fmt()
        .with_target(true)
        .with_ansi(false)
        .without_time()
        .init();
}

#[cfg(debug_assertions)]
pub fn init_tracing_subscriber() {
    use tracing_subscriber::EnvFilter;

    tracing_subscriber::fmt()
        .with_env_filter(EnvFilter::from_default_env())
        .with_target(true)
        .init();
}

// Common fields are loaded during the Lambda init phase. These include reading
// several environment variables to know which buckets and tables to work from,
// and also preparing the SDK Config (expensive) and several clients from that
// config (cheap).
pub struct Common {
    sdk_config: SdkConfig,
    dynamodb_client: aws_sdk_dynamodb::Client,
    rekognition_client: aws_sdk_rekognition::Client,
    s3_client: aws_sdk_s3::Client,
    sns_client: aws_sdk_sns::Client,
    storage_bucket: String,
    working_bucket: String,
    labels_table: String,
    notification_topic: String,
}

impl Common {
    pub fn new(
        sdk_config: SdkConfig,
        storage_bucket: String,
        working_bucket: String,
        labels_table: String,
        notification_topic: String,
    ) -> Self {
        let dynamodb_client = aws_sdk_dynamodb::Client::new(&sdk_config);
        let rekognition_client = aws_sdk_rekognition::Client::new(&sdk_config);
        let s3_client = aws_sdk_s3::Client::new(&sdk_config);
        let sns_client = aws_sdk_sns::Client::new(&sdk_config);

        Common {
            sdk_config,
            rekognition_client,
            dynamodb_client,
            s3_client,
            sns_client,
            storage_bucket,
            working_bucket,
            labels_table,
            notification_topic,
        }
    }

    pub async fn load_from_env() -> Self {
        let sdk_config = aws_config::load_defaults(BehaviorVersion::latest()).await;
        // PAM environment is declared in the cdk, in lib/backend/lambdas.ts
        let storage_bucket =
            std::env::var("STORAGE_BUCKET_NAME").expect("storage bucket in environment");
        let working_bucket =
            std::env::var("WORKING_BUCKET_NAME").expect("working bucket in environment");
        let labels_table = std::env::var("LABELS_TABLE_NAME").expect("labels table in environment");
        let notification_topic =
            std::env::var("NOTIFICATION_TOPIC").expect("notification topic in environment");

        Common::new(
            sdk_config,
            storage_bucket,
            working_bucket,
            labels_table,
            notification_topic,
        )
    }

    pub fn sdk_config(&self) -> &SdkConfig {
        &self.sdk_config
    }

    pub fn dynamodb_client(&self) -> &aws_sdk_dynamodb::Client {
        &self.dynamodb_client
    }

    pub fn rekognition_client(&self) -> &aws_sdk_rekognition::Client {
        &self.rekognition_client
    }

    pub fn s3_client(&self) -> &aws_sdk_s3::Client {
        &self.s3_client
    }

    pub fn sns_client(&self) -> &aws_sdk_sns::Client {
        &self.sns_client
    }

    pub fn storage_bucket(&self) -> &String {
        &self.storage_bucket
    }

    pub fn working_bucket(&self) -> &String {
        &self.working_bucket
    }

    pub fn labels_table(&self) -> &String {
        &self.labels_table
    }

    pub fn notification_topic(&self) -> &String {
        &self.notification_topic
    }
}

#[macro_export]
macro_rules! apig_response(
  ($body:expr) => {{
    let mut headers = http::header::HeaderMap::new();
    headers.insert("Access-Control-Allow-Origin", http::header::HeaderValue::from_static("*"));
    aws_lambda_events::apigw::ApiGatewayProxyResponse {
      status_code: 200,
      headers,
      multi_value_headers: http::header::HeaderMap::new(),
      body: Some(aws_lambda_events::encodings::Body::Text(serde_json::json!($body).to_string())),
      is_base64_encoded: false
    }
  }}
);
