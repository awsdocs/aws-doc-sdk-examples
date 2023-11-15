use aws_config::BehaviorMajorVersion;
use aws_sdk_s3::Client;

#[tokio::main]
async fn main() -> Result<(), anyhow::Error> {
    let s3_client =
        Client::new(&aws_config::load_from_env_with_version(BehaviorMajorVersion::latest()).await);
    let part = 0;
    let bucket: String = "bucket".to_string();
    let key: String = "key".to_string();
    let upload_id: String = "abc123".to_string();
    let body = vec![1, 2, 3];

    let _upload_part_response = s3_client
        .upload_part()
        .bucket(&bucket)
        .key(key)
        .body(body.into())
        .part_number(part)
        .upload_id(upload_id.clone())
        .send()
        .await?;

    Ok(())
}
