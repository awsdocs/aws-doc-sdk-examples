use aws_sdk_dynamodb::primitives::DateTime;
use aws_sdk_s3::primitives::ByteStream;
use aws_smithy_types_convert::date_time::DateTimeExt;
use std::io::prelude::*;
use std::io::Write;
use tempfile::tempfile;
use zip_next::{write::FileOptions, ZipWriter};

use crate::common::Common;

pub struct ZipUpload<'a> {
    s3_client: &'a aws_sdk_s3::Client,
    source_bucket: String,
    bucket: String,
    key: String,
    zip: ZipWriter<std::fs::File>,
}

pub struct ZipUploadBuilder<'a> {
    common: &'a Common,
    source_bucket: Option<String>,
    bucket: Option<String>,
    key: Option<String>,
}

impl<'a> ZipUpload<'a> {
    pub fn builder(common: &'a Common) -> ZipUploadBuilder<'a> {
        ZipUploadBuilder {
            common,
            source_bucket: None,
            bucket: None,
            key: None,
        }
    }

    pub async fn add_object(&mut self, key: String) -> Result<(), anyhow::Error> {
        let mut object = self
            .s3_client
            .get_object()
            .bucket(&self.source_bucket)
            .key(&key)
            .send()
            .await?;

        let last_modified: zip_next::DateTime = object
            .last_modified
            .unwrap_or_else(|| DateTime::from_millis(0))
            .to_chrono_utc()?
            .naive_utc()
            .try_into()?;

        let length = object.content_length();
        tracing::info!(key, ?last_modified, length, "Adding bytes to zip");

        let options = FileOptions::default().last_modified_time(last_modified);
        self.zip.start_file(key, options)?;

        let mut byte_count = 0_usize;
        while let Some(bytes) = object.body.try_next().await? {
            let bytes_len = bytes.len();
            self.zip.write_all(&bytes)?;
            byte_count += bytes_len;
            tracing::trace!("Intermediate read of {bytes_len} (total {byte_count})");
        }

        Ok(())
    }

    pub async fn finish(mut self) -> Result<(String, String), anyhow::Error> {
        let mut zip = self.zip.finish()?;
        // Because this file is read immediately, seek it back to the start.
        zip.seek(std::io::SeekFrom::Start(0))?;

        let bucket = self.bucket;
        let key = self.key;

        let body = ByteStream::read_from()
            .file(zip.try_into()?)
            .buffer_size(4096 * 16)
            .build()
            .await?;

        self.s3_client
            .put_object()
            .bucket(bucket.clone())
            .key(format!("{}.zip", key))
            .body(body)
            .send()
            .await?;

        Ok((bucket, key))
    }
}

impl<'a> ZipUploadBuilder<'a> {
    pub async fn build(self) -> Result<ZipUpload<'a>, anyhow::Error> {
        let key = self.key.unwrap_or_else(|| uuid::Uuid::new_v4().to_string());
        let source_bucket = self
            .source_bucket
            .unwrap_or_else(|| self.common.storage_bucket().clone());
        let bucket = self
            .bucket
            .unwrap_or_else(|| self.common.working_bucket().clone());
        let zip = ZipWriter::new(tempfile()?);

        Ok(ZipUpload {
            s3_client: self.common.s3_client(),
            key,
            source_bucket,
            bucket,
            zip,
        })
    }

    pub fn key(&mut self, key: String) -> &Self {
        self.key = Some(key);
        self
    }

    pub fn source_bucket(&mut self, bucket: String) -> &Self {
        self.source_bucket = Some(bucket);
        self
    }

    pub fn bucket(&mut self, bucket: String) -> &Self {
        self.bucket = Some(bucket);
        self
    }
}
