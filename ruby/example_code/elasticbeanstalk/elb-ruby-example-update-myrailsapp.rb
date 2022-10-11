# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# eb_list_stacks.rb demonstrates how to update a Ruby on Rails application the AWS SDK for Ruby.

# snippet-start:[eb.Ruby.updateMyRailsApp]
require "aws-sdk-elasticbeanstalk"  # v2: require 'aws-sdk'
require "aws-sdk-s3"
# Replace us-west-2 with the AWS Region you're using for Elastic Beanstalk.
region = "us-east-1"
Aws.config.update({region: region})

eb = Aws::ElasticBeanstalk::Client.new
s3 = Aws::S3::Client.new

app_name = "test"

# Get S3 bucket containing app
app_versions = eb.describe_application_versions({ application_name: app_name })

if app_versions.application_versions.empty?
  puts "Could not find an existing application in #{region} by the name #{app_name}"
else
  av = app_versions.application_versions[0]

  bucket = av.source_bundle.s3_bucket
  s3_key = av.source_bundle.s3_key

  # Get info on environment
  envs = eb.describe_environments({ application_name: app_name })
  env = envs.environments[0]
  env_name = env.environment_name

  # Create new storage location
  resp = eb.create_storage_location

  puts "Created storage location in bucket #{resp.s3_bucket}"

  s3.list_objects({
                    prefix: s3_key,
                    bucket: bucket
                  })

  # Create ZIP file
  zip_file_basename = SecureRandom.urlsafe_base64.to_s
  zip_file_name = zip_file_basename + ".zip"

  # Call out to OS to produce ZIP file
  cmd = "git archive --format=zip -o #{zip_file_name} HEAD"
  %x[ #{cmd} ]

  # Get ZIP file contents
  zip_contents = File.read(zip_file_name)

  key = app_name + "\\" + zip_file_name

  s3.put_object({
                  body: zip_contents,
                  bucket: bucket,
                  key: key
                })

  date = Time.new
  today = date.day.to_s + "/" + date.month.to_s + "/" + date.year.to_s

  eb.create_application_version({
                                  process: false,
                                  application_name: app_name,
                                  version_label: zip_file_basename,
                                  source_bundle: {
                                    s3_bucket: bucket,
                                    s3_key: key
                                  },
                                  description: "Updated #{today}"
                                })

  eb.update_environment({
                          environment_name: env_name,
                          version_label: zip_file_basename
                        })
  # snippet-end:[eb.Ruby.updateMyRailsApp]
end
