use std::{
    fs::File,
    path::PathBuf,
    pin::Pin,
    process::exit,
    task::{Context, Poll},
};

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::{
    primitives::{ByteStream, SdkBody},
    Client,
};
use clap::Parser;
use tracing::trace;

#[derive(Debug, Parser)]
struct Opt {
    #[structopt(long)]
    bucket: String,
    #[structopt(long)]
    object: String,
    #[structopt(long)]
    source: PathBuf,
}

#[pin_project::pin_project]
pub struct ProgressBody<InnerBody> {
    #[pin]
    inner: InnerBody,
    bytes_written: u64,
    content_length: u64,
}

impl ProgressBody<SdkBody> {
    /// Given an `SdkBody`, a `Box<dyn HttpChecksum>`, and a precalculated checksum represented
    /// as `Bytes`, create a new `ChecksumBody<SdkBody>`.
    pub fn new(body: SdkBody, content_length: u64) -> Self {
        Self {
            inner: body,
            content_length,
            bytes_written: 0,
        }
    }

    fn poll_inner(
        self: Pin<&mut Self>,
        cx: &mut Context<'_>,
    ) -> Poll<Option<Result<(), aws_smithy_http::body::Error>>> {
        use http_body::Body;

        let this = self.project();

        match this.inner.poll_data(cx) {
            Poll::Ready(Some(Ok(data))) => {
                *this.bytes_written += data.len() as u64;
                let progress = *this.bytes_written as f64 / *this.content_length as f64;
                tracing::trace!(
                    "Read {} bytes, progress: {:.2}%",
                    data.len(),
                    progress * 100.0
                );

                Poll::Ready(Some(Ok(data)))
            }
            Poll::Ready(None) => {
                tracing::trace!("done");
                Poll::Ready(None)
            }
            Poll::Ready(Some(Err(e))) => Poll::Ready(Some(Err(e))),
            Poll::Pending => Poll::Pending,
        }
    }
}

// snippet-start:[s3.rust.put-object]
// Uploads a local file to a bucket.
async fn put_object(client: &Client, opts: &Opt) -> Result<(), anyhow::Error> {
    trace!("bucket: {}", opts.bucket);
    trace!("object: {}", opts.object);
    trace!("source: {}", opts.source.display());

    let file = File::open(opts.source.clone())?;

    let body = ByteStream::from(SdkBody::from(file).map(move |body| {
        let body = ProgressBody::new(
            body,
            file.metadata().expect("Could not load file metadata").len(),
        );
        aws_smithy_http::body::SdkBody::from_dyn(aws_smithy_http::body::BoxBody::new(body))
    }));

    Ok(())
}
// snippet-end:[s3.rust.put-object-presigned]

#[tokio::main]
async fn main() -> () {
    tracing_subscriber::fmt::init();

    let region_provider = RegionProviderChain::default_provider().or_else("us-east-1");
    let config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&config);

    match put_object(&client, &Opt::parse()).await {
        Ok(()) => {
            println!("Uploaded file");
        }
        Err(err) => {
            eprintln!("Error: {}", err);
            exit(1);
        }
    }
}
