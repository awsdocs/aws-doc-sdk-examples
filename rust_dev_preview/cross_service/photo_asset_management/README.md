# Create a photo asset management application with the SDK for Rust

## Overview

The Photo Asset Management (PAM) example app uses Amazon Rekognition to categorize images, which are stored with Amazon S3 Intelligent-Tiering for cost savings. Users can upload new images. Those images are analyzed with label detection and the labels are stored in an Amazon DynamoDB table. Users can later request a bundle of images matching those labels. When images are requested, they will be retrieved from Amazon Simple Storage Service (Amazon S3), zipped, and the user is sent a link to the zip.

This Rust implementation shows a number of techniques to run Rust binaries in AWS Lambda. The `src/bin/pam.rs` binary uses the lambda `_HANDLER` environment variable to choose which handler to use for requests. This is a trade off - while it's a single binary in Amazon S3, each handler only uses a subset of the features. Because there's a lot of overlap in which features are used, and the entire binary is under 5MB, the convenience of a single Code Asset for the AWS Cloud Development Kit (AWS CDK) outweighs managing multiple binaries _for this application_. Other applications should apply their best judgment for this tradeoff. Individual binaries are available for testing in isolation.

The `Common` struct loads a number of clients and environment data a single time during Lambda initialization. This is then used for every invocation of the handler. `uploader.rs` and `chunked_uploader.rs` export a `ZipWriter`, which manages reading a number of files from Amazon S3, and then streams them in a .zip to another bucket.

The `handlers` module includes the specific handler logic.

## Compile using Cargo Lambda

https://www.cargo-lambda.info/

```
cargo lambda build --release --arm64
zip ../../target/lambda/pam/bootstrap{.zip,}
```

## Test using Cargo Lambda

Set appropriate environment variables from the AWS CDK in `Cargo.toml`.

In one terminal, run `cargo lambda` and watch for changes.

```
cargo lambda watch
```

In another terminal, run `cargo lambda invoke &lt;name-of-bin&gt; --data-file examples/&lt;name-of-bin&gt;.json`.

## Deploy

Follow the instructions in the [PAM application CDK README](../../../applications/photo-asset-manager/cdk/README.md), using `PAM_LANG=rust`.
