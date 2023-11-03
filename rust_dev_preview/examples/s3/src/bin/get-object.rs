use aws_config::meta::region::RegionProviderChain;
use std::{fs::File, io::Write, path::PathBuf, process::exit};

use aws_sdk_s3::Client;
use clap::Parser;
use tracing::log::trace;

#[derive(Debug, Parser)]
struct Opt {
    #[structopt(long)]
    bucket: String,
    #[structopt(long)]
    object: String,
    #[structopt(long)]
    destination: PathBuf,
}

// snippet-start:[s3.rust.get_object]
async fn get_object(client: Client, opt: Opt) -> Result<usize, anyhow::Error> {
    trace!("bucket:      {}", opt.bucket);
    trace!("object:      {}", opt.object);
    trace!("destination: {}", opt.destination.display());

    let mut file = File::create(opt.destination.clone())?;

    let mut object = client
        .get_object()
        .bucket(opt.bucket)
        .key(opt.object)
        .send()
        .await?;

    let mut byte_count = 0_usize;
    while let Some(bytes) = object.body.try_next().await? {
        let bytes_len = bytes.len();
        file.write_all(&bytes)?;
        trace!("Intermediate write of {bytes_len}");
        byte_count += bytes_len;
    }

    Ok(byte_count)
}
// snippet-end:[s3.rust.get_object]

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();

    let region_provider = RegionProviderChain::default_provider().or_else("us-east-1");
    let config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&config);

    match get_object(client, Opt::parse()).await {
        Ok(bytes) => {
            println!("Wrote {bytes}");
        }
        Err(err) => {
            eprintln!("Error: {}", err);
            exit(1);
        }
    }
}
