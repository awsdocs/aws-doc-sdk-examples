/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/// The SDK defaults to using RusTLS by default but you can also use [`native_tls`](https://github.com/sfackler/rust-native-tls)
/// which will choose a TLS implementation appropriate for your platform. This example looks much like
/// any other. Activating and deactivating `features` in your app's `Cargo.toml` is all that's needed.
#[tokio::main]
async fn main() -> Result<(), aws_sdk_s3::Error> {
    tracing_subscriber::fmt::init();
    list_buckets().await
}

async fn list_buckets() -> Result<(), aws_sdk_s3::Error> {
    let shared_config = aws_config::load_from_env().await;

    let s3_config = aws_sdk_s3::Config::from(&shared_config);
    let client = aws_sdk_s3::Client::from_conf(s3_config);

    let resp = client.list_buckets().send().await?;

    for bucket in resp.buckets().unwrap_or_default() {
        println!("bucket: {:?}", bucket.name().unwrap_or_default())
    }

    Ok(())
}

#[cfg(test)]
mod tests {
    use crate::list_buckets;

    /// You can run this test to ensure that this example is only using `native-tls`
    /// and that nothing is pulling in `rustls` as a dependency
    #[test]
    #[should_panic = "error: package ID specification `rustls` did not match any packages"]
    fn test_rustls_is_not_in_dependency_tree() {
        let cargo_location = std::env::var("CARGO").unwrap();
        let cargo_command = std::process::Command::new(&cargo_location)
            .arg("tree")
            .arg("--invert")
            .arg("rustls")
            .output()
            .expect("failed to run 'cargo tree'");

        let stderr = String::from_utf8_lossy(&cargo_command.stderr);

        // We expect the call to `cargo tree` to error out. If it did, we panic with the resulting
        // message here. In the case that no error message is set, that's bad.
        if !stderr.is_empty() {
            panic!("{}", stderr);
        }

        // Uh oh. We expected an error message but got none, likely because `cargo tree` found
        // `rustls` in our dependencies. We'll print out the message we got to see what went wrong.
        let stdout = String::from_utf8_lossy(&cargo_command.stdout);

        println!("{}", stdout)
    }

    // NOTE: not currently run in CI, separate PR will set up a with-creds CI runner
    #[tokio::test]
    #[ignore]
    async fn needs_creds_native_tls_works() {
        list_buckets().await.expect("should succeed")
    }
}
