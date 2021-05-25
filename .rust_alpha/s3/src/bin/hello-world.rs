use s3::Region;
use smithy_http::body::SdkBody;
use smithy_http::byte_stream::ByteStream;
use std::error::Error;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    SubscriberBuilder::default()
        .with_env_filter("info")
        .with_span_events(FmtSpan::CLOSE)
        .init();
    let conf = s3::Config::builder()
        .region(Region::new("us-east-2"))
        .build();
    let client = s3::Client::from_conf(conf);
    let resp = client.list_buckets().send().await?;
    for bucket in resp.buckets.unwrap_or_default() {
        println!("bucket: {:?}", bucket.name.expect("buckets have names"))
    }
    // not the best pattern but fine for now because it reads a file all the way into memory
    let f = tokio::fs::read("Cargo.toml").await?;
    let body = ByteStream::new(SdkBody::from(f));
    let resp = client
        .put_object()
        .bucket("aws-rust-sdk")
        .key("demo")
        .body(body)
        .send();
    let resp = resp.await?;
    println!("version: {:?}", resp.version_id);

    let downloaded = client
        .get_object()
        .bucket("aws-rust-sdk")
        .key("demo")
        .send()
        .await?;
    let data = downloaded.body.collect().await?;
    println!(
        "data: {}",
        String::from_utf8(data.into_bytes().to_vec()).unwrap()
    );
    Ok(())
}
