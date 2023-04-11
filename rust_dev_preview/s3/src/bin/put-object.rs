use std::{
    convert::Infallible,
    mem,
    path::PathBuf,
    pin::Pin,
    process::exit,
    task::{Context, Poll},
};

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::{
    error::DisplayErrorContext,
    primitives::{ByteStream, SdkBody},
    Client,
};
use aws_smithy_http::body::BoxBody;
use bytes::Bytes;
use clap::Parser;
use http_body::{Body, SizeHint};
use tracing::debug;

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
    pub fn new(body: SdkBody, content_length: u64) -> Self {
        Self {
            inner: body,
            content_length,
            bytes_written: 0,
        }
    }
}

impl<InnerBody> Body for ProgressBody<InnerBody>
where
    InnerBody: Body<Data = Bytes, Error = aws_smithy_http::body::Error>,
{
    type Data = Bytes;

    type Error = aws_smithy_http::body::Error;

    fn poll_data(
        self: Pin<&mut Self>,
        cx: &mut Context<'_>,
    ) -> Poll<Option<Result<Self::Data, Self::Error>>> {
        let this = self.project();

        match this.inner.poll_data(cx) {
            Poll::Ready(Some(Ok(data))) => {
                *this.bytes_written += data.len() as u64;
                let progress = *this.bytes_written as f64 / *this.content_length as f64;
                println!(
                    "Read {} bytes, progress: {:.2}%",
                    data.len(),
                    progress * 100.0
                );

                Poll::Ready(Some(Ok(data)))
            }
            Poll::Ready(None) => {
                tracing::debug!("done");
                Poll::Ready(None)
            }
            Poll::Ready(Some(Err(e))) => Poll::Ready(Some(Err(e))),
            Poll::Pending => Poll::Pending,
        }
    }

    fn poll_trailers(
        self: Pin<&mut Self>,
        cx: &mut Context<'_>,
    ) -> Poll<Result<Option<http::HeaderMap>, Self::Error>> {
        self.project().inner.poll_trailers(cx)
    }

    fn size_hint(&self) -> http_body::SizeHint {
        SizeHint::with_exact(self.content_length)
    }
}

// snippet-start:[s3.rust.put-object]
// Uploads a local file to a bucket.
async fn put_object(client: &Client, opts: &Opt) -> Result<(), anyhow::Error> {
    debug!("bucket: {}", opts.bucket);
    debug!("object: {}", opts.object);
    debug!("source: {}", opts.source.display());

    let body = ByteStream::read_from()
        .path(opts.source.clone())
        .buffer_size(2048)
        .build()
        .await?;

    let request = client
        .put_object()
        .bucket(opts.bucket.clone())
        .key(opts.object.clone())
        .body(body);

    let customized = request.customize().await?.map_request(|mut req| {
        let body = mem::replace(req.body_mut(), SdkBody::taken()).map(|body| {
            let len = body.content_length().expect("upload body sized"); // TODO - panics
            let body = ProgressBody::new(body, len);
            // Warning: A from_dyn loses `Once`, so it can't be sigv4'd. There is an upcoming
            // special body map to handle this block's operation while keeping signing.
            // See https://github.com/awslabs/smithy-rs/pull/2567
            SdkBody::from_dyn(BoxBody::new(body))
        });
        let _ = mem::replace(req.body_mut(), body);
        Ok::<http::Request<SdkBody>, Infallible>(req)
    })?;

    match customized.send().await {
        Ok(_) => Ok(()),
        Err(e) => {
            eprintln!("{}", DisplayErrorContext(e));
            Ok(())
        }
    }
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
