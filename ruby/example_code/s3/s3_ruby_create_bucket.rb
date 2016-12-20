require 'aws-sdk'
require 'json'

profile_name = 'david'
region = "us-east-1"
bucket = 'doc-sample-bucket'
my_bucket = 'david-cloud'

# S3

# Configure SDK
s3 = Aws::S3::Client.new(profile: profile_name, region: region)

# Display a List of Amazon S3 Buckets
resp = s3.list_buckets
resp.buckets.each do |bucket|
  puts bucket.name
end

# Create a S3 bucket from S3::client
s3.create_bucket(bucket: bucket)

# Upload a file to s3 bucket, directly putting string data
s3.put_object(bucket: bucket, key: "file1", body: "My first s3 object")

# Check the file exists
resp = s3.list_objects_v2(bucket: bucket)
resp.contents.each do |obj|
  puts obj.key
end

# Copy files from bucket to bucket
s3.copy_object(bucket: bucket,
               copy_source: "#{my_bucket}/test_file",
               key: 'file2')
s3.copy_object(bucket: bucket,
               copy_source: "#{my_bucket}/test_file1",
               key: 'file3')

# Delete multiple objects in a single HTTP request
s3.delete_objects(
  bucket: 'doc-sample-bucket',
  delete: {
    objects: [
      {
        key: 'file2'
      },
      {
        key: 'file3'
      }
    ]
  }
)

# Verify objects now have been deleted
resp = s3.list_objects_v2(bucket: bucket)
resp.contents.each do |obj|
  puts obj.key
end
