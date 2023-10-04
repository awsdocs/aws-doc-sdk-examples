use std::io::Read;

use anyhow::anyhow;
use aws_sdk_dynamodb::primitives::DateTime;
use aws_sdk_s3::{operation::get_object::GetObjectOutput, types::CompletedPart};
use aws_smithy_types_convert::date_time::DateTimeExt;
use chrono::NaiveDateTime;
use pipe::{pipe, PipeReader, PipeWriter};
use streaming_zip::{Archive, CompressionMode};
use uuid::Uuid;

use crate::common::Common;

// Pipe will read up to 10MB at a time. Each multipart upload will therefore also
// be in the 10MB range. Multipart uploads have a maximum part count of 10,000,
// so this imposes an effective limit on the size of the upload. Increasing this
// limit uses more memory but allows larger files. JPEGs are typically 8MB, so this
// could be tuned but generally should allow ~10,000 images.
//
// (Multipart uploads also have a minimum size of 5MB.)
const READ_SIZE: usize = 1_048_578;

// ZipUploader is a struct to manage streaming a number of files into a single zip,
// that is itself streamed to an Amazon S3 object. It reads from a source bucket, to a
// bucket and key for the zip.
pub struct ZipUpload<'a> {
    part: i32,
    pipe: PipeReader,
    zip_writer: Archive<PipeWriter>,
    upload_parts: Vec<CompletedPart>,
    upload_id: String,
    key: String,
    bucket: String,
    source_bucket: String,
    s3_client: &'a aws_sdk_s3::Client,
}

impl<'a> std::fmt::Debug for ZipUpload<'a> {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.debug_struct("ZipUpload")
            .field("key", &self.key)
            .field("bucket", &self.bucket)
            .field("source_bucket", &self.source_bucket)
            .field("part", &self.part)
            .field("upload_id", &self.upload_id)
            .finish()
    }
}

pub struct ZipUploadBuilder<'a> {
    source_bucket: Option<String>,
    bucket: Option<String>,
    key: Option<String>,
    common: &'a Common,
}

impl<'a> ZipUploadBuilder<'a> {
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

    pub async fn build(self) -> Result<ZipUpload<'a>, anyhow::Error> {
        let s3_client = self.common.s3_client();
        let part = 0;
        let upload_parts: Vec<CompletedPart> = Vec::new();

        let pipe = pipe();
        let zip_writer = Archive::new(pipe.1);
        let pipe = pipe.0;

        let key = self.key.unwrap_or_else(|| Uuid::new_v4().to_string());
        let source_bucket = self
            .source_bucket
            .unwrap_or_else(|| self.common.storage_bucket().clone());
        let bucket = self
            .bucket
            .unwrap_or_else(|| self.common.working_bucket().clone());

        // Start the multipart upload...
        let upload = s3_client
            .create_multipart_upload()
            .bucket(&bucket)
            .key(&key)
            .content_type("application/zip")
            .send()
            .await?;
        // ... and keep its ID.
        let upload_id = upload
            .upload_id()
            .ok_or_else(|| anyhow!("Cannot start upload"))?
            .to_string();

        Ok(ZipUpload {
            part,
            pipe,
            zip_writer,
            upload_parts,
            key,
            bucket,
            source_bucket,
            upload_id,
            s3_client,
        })
    }
}

impl<'a> ZipUpload<'a> {
    // Start a builder for the ZipUpload.
    pub fn builder(common: &'a Common) -> ZipUploadBuilder {
        ZipUploadBuilder {
            key: None,
            bucket: None,
            source_bucket: None,
            common,
        }
    }

    // Read from the pipe until it has less than READ_SIZE, and write those to the
    // multipart upload in READ_SIZE chunks.
    async fn write_body_bytes(&mut self) -> Result<(), anyhow::Error> {
        let mut body = [0u8; READ_SIZE];
        while self.pipe.read(&mut body)? > 0 {
            let body = Vec::from(body);
            let upload_part_response = self
                .s3_client
                .upload_part()
                .bucket(&self.bucket)
                .key(self.key.to_string())
                .body(body.into())
                .part_number(self.part)
                .upload_id(self.upload_id.clone())
                .send()
                .await?;
            self.upload_parts.push(
                CompletedPart::builder()
                    .e_tag(upload_part_response.e_tag().unwrap_or_default())
                    .part_number(self.part)
                    .build(),
            );
            self.part += 1;
        }

        Ok(())
    }

    // Add an object to the archive. Reads the key from the source_bucket, passes it
    // through a new file entry in the archive, and writes the parts to the multipart
    // upload.
    pub async fn add_object(&mut self, key: String) -> Result<(), anyhow::Error> {
        let mut object = self.next_object(key).await?;
        while let Some(bytes) = object.body.try_next().await? {
            self.next_part(&bytes).await?;
        }
        self.finish_object().await?;

        Ok(())
    }

    // Move to the next object. This starts the object download from s3, as well as a new entry
    // in the Zip archive. It returns the GetObjectOutput to iterate the download's body.
    async fn next_object(&mut self, key: String) -> Result<GetObjectOutput, anyhow::Error> {
        let object = self
            .s3_client
            .get_object()
            .bucket(&self.source_bucket)
            .key(&key)
            .send()
            .await?;

        let last_modified: NaiveDateTime = object
            .last_modified
            .unwrap_or_else(|| DateTime::from_millis(0))
            .to_chrono_utc()?
            .naive_utc();

        self.zip_writer.start_new_file(
            key.into_bytes(),
            last_modified,
            CompressionMode::Deflate(8),
            false,
        )?;

        Ok(object)
    }

    // Write one sequence of bytes through the zip_writer and upload the part.
    async fn next_part(&mut self, bytes: &bytes::Bytes) -> Result<(), anyhow::Error> {
        self.zip_writer.append_data(bytes)?;

        self.write_body_bytes().await?;

        Ok(())
    }

    // Finish the current object and flush the part.
    async fn finish_object(&mut self) -> Result<(), anyhow::Error> {
        self.zip_writer.finish_file()?;
        self.write_body_bytes().await?;
        Ok(())
    }

    // Finish the entire operation. Takes ownership of itself to invalidate future operations
    // with this uploader.
    pub async fn finish(mut self) -> Result<(String, String), anyhow::Error> {
        // Swap out the Archive so that it can get finalized (and the new empty one dropped).
        // Without this, zip_writer.finish takes ownership of self, and effectively ends
        // the method early.
        let mut zip_writer = Archive::new(pipe().1);
        std::mem::swap(&mut self.zip_writer, &mut zip_writer);
        zip_writer.finish()?;
        self.write_body_bytes().await?;

        let upload = self
            .s3_client
            .complete_multipart_upload()
            .bucket(&self.bucket)
            .key(&self.key)
            .upload_id(self.upload_id.clone())
            .send()
            .await?;

        tracing::trace!(?upload, "Finished upload");

        // After taking ownership of `self`, return the owned bucket and key strings.
        Ok((self.bucket, self.key))
    }
}
