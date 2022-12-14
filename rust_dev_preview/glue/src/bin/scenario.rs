// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

/// This scenario follows the [AWS Glue Tutorial](https://docs.aws.amazon.com/glue/latest/ug/tutorial-add-crawler.html).
use glue_code_examples::{GlueMvpError, GlueScenario};
use tracing::{error, warn};
use tracing_bunyan_formatter::{BunyanFormattingLayer, JsonStorageLayer};
use tracing_subscriber::{layer::SubscriberExt, EnvFilter, Registry};

fn init_logging() -> Result<(), GlueMvpError> {
    tracing::subscriber::set_global_default(
        Registry::default()
            .with(EnvFilter::try_from_default_env().unwrap_or_else(|_| "scenario".into()))
            .with(JsonStorageLayer)
            .with(BunyanFormattingLayer::new(
                "GlueMvpScenario".into(),
                std::io::stdout,
            )),
    )
    .map_err(|err| GlueMvpError::Unknown(format!("{err}")))
}

#[tokio::main(flavor = "current_thread")]
async fn main() {
    init_logging().expect("Failed to initialize tracing");

    let mut scenario = GlueScenario::parse();

    let prepare = scenario.prepare().await;

    if let Err(err) = prepare {
        error!(?err, "Failed to prepare crawler")
    }

    let run = scenario.run().await;

    // Actively call cleanup to report errors.
    // This could also be a Drop impl on `GlueScenario`.
    let cleanup = scenario.cleanup().await;

    if let Err(err) = cleanup {
        error!(?err, "Cleanup error");
    }

    if let Err(err) = run {
        error!(?err, "Job run failed");
    }

    warn!(
        "Glue scenario complete! See details in {}",
        scenario.bucket()
    );
}
