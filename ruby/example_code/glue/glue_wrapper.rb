# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-glue'
require 'aws-sdk-s3'
require 'logger'

# snippet-start:[ruby.example_code.glue.GlueWrapper.full]
# snippet-start:[ruby.example_code.glue.GlueWrapper.decl]

# The `GlueWrapper` class serves as a wrapper around the AWS Glue API,
# providing a simplified interface for common operations.
# It encapsulates the functionality of the AWS SDK for Glue and provides methods for interacting with Glue crawlers,
# databases, tables, jobs, and S3 resources.
# The class initializes with a Glue client and a logger, allowing it to make API calls and log any errors or informational messages.
class GlueWrapper
  def initialize(glue_client, logger)
    @glue_client = glue_client
    @logger = logger
  end
  # snippet-end:[ruby.example_code.glue.GlueWrapper.decl]

  # snippet-start:[ruby.example_code.glue.GetCrawler]
  # Retrieves information about a specific crawler.
  #
  # @param name [String] The name of the crawler to retrieve information about.
  # @return [Aws::Glue::Types::Crawler, nil] The crawler object if found, or nil if not found.
  def get_crawler(name)
    @glue_client.get_crawler(name: name)
  rescue Aws::Glue::Errors::EntityNotFoundException
    @logger.info("Crawler #{name} doesn't exist.")
    false
  rescue Aws::Glue::Errors::GlueException => e
    @logger.error("Glue could not get crawler #{name}: \n#{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.glue.GetCrawler]

  # snippet-start:[ruby.example_code.glue.CreateCrawler]
  # Creates a new crawler with the specified configuration.
  #
  # @param name [String] The name of the crawler.
  # @param role_arn [String] The ARN of the IAM role to be used by the crawler.
  # @param db_name [String] The name of the database where the crawler stores its metadata.
  # @param db_prefix [String] The prefix to be added to the names of tables that the crawler creates.
  # @param s3_target [String] The S3 path that the crawler will crawl.
  # @return [void]
  def create_crawler(name, role_arn, db_name, _db_prefix, s3_target)
    @glue_client.create_crawler(
      name: name,
      role: role_arn,
      database_name: db_name,
      targets: {
        s3_targets: [
          {
            path: s3_target
          }
        ]
      }
    )
  rescue Aws::Glue::Errors::GlueException => e
    @logger.error("Glue could not create crawler: \n#{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.glue.CreateCrawler]

  # snippet-start:[ruby.example_code.glue.StartCrawler]
  # Starts a crawler with the specified name.
  #
  # @param name [String] The name of the crawler to start.
  # @return [void]
  def start_crawler(name)
    @glue_client.start_crawler(name: name)
  rescue Aws::Glue::Errors::ServiceError => e
    @logger.error("Glue could not start crawler #{name}: \n#{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.glue.StartCrawler]

  # Additional methods omitted for brevity...

  # Uploads a job script file to an S3 bucket.
  #
  # @param file_path [String] The local path of the job script file.
  # @param bucket_resource [Aws::S3::Bucket] The S3 bucket resource to upload the file to.
  # @return [void]
  def upload_job_script(file_path, bucket_resource)
    File.open(file_path) do |file|
      bucket_resource.client.put_object({
                                          body: file,
                                          bucket: bucket_resource.name,
                                          key: file_path
                                        })
    end
  rescue Aws::S3::Errors::S3UploadFailedError => e
    @logger.error("S3 could not upload job script: \n#{e.message}")
    raise
  end
end
# snippet-end:[ruby.example_code.glue.GlueWrapper.full]
