# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

# To run this demo, you will need Ruby 2.6 or later, plus dependencies.
# For more information, see:
# https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md

require "aws-sdk-glue"
require "aws-sdk-iam"
require "aws-sdk-s3"
require "io/console"
require "pp"
require "logger"
require "optparse"
require "cli/ui"
require "yaml"
require_relative("../../../helpers/disclaimers")
require_relative("../../../helpers/decorators")
require_relative("../../../helpers/waiters")
require_relative("../glue_wrapper")
require_relative("../getting_started")

describe GlueWrapper do
  context "GlueWrapper" do
    resource_names = YAML.load_file("../resource_names.yaml")

    # Instantiate existing IAM role created with the CDK
    iam = Aws::IAM::Resource.new(region: "us-east-1")
    iam_role_name = resource_names["glue_service_role"]
    glue_service_role = iam.role(iam_role_name)

    # Instantiate existing S3 bucket created with the CDK.
    s3 = Aws::S3::Resource.new(region: "us-east-1")
    s3_bucket_name = resource_names["glue_bucket"]
    glue_bucket = s3.bucket(s3_bucket_name)

    random_int = rand(10 ** 4)
    crawler_name = "doc-example-crawler-#{random_int}"
    db_name = "doc-example-database-#{random_int}"
    db_prefix = "doc-example-#{random_int}-"
    data_source = "s3://crawler-public-us-east-1/flight/2016/csv"
    job_script = "test_job_script.py"
    job_name = "doc-example-job-#{random_int}"

    wrapper = GlueWrapper.new(Aws::Glue::Client.new(region: "us-east-1"), Logger.new($stdout))

    it "Checking for crawler", integ: true do
      crawler = wrapper.get_crawler(crawler_name)
      if crawler == false
        puts "Creating crawler #{crawler_name}."
        wrapper.create_crawler(crawler_name, glue_service_role.arn, db_name, db_prefix, data_source)
        puts "Successfully created #{crawler_name}:"
        crawler = wrapper.get_crawler(crawler_name)
        puts JSON.pretty_generate(crawler).yellow
      end
      print "\nDone!\n".green
    end

    it "Starts crawler", integ: false do
      wrapper.start_crawler(crawler_name)
      puts "Let's wait for the crawler to run. This typically takes a few minutes."
      crawler_state = nil
      while crawler_state != "READY"
        custom_wait(10)
        crawler = wrapper.get_crawler(crawler_name)
        crawler_state = crawler[0]["state"]
        print "Status check: #{crawler_state}.".yellow
      end
      print "\nDone!\n".green
    end

    it "Checks database created by crawler", integ: false do
      database = wrapper.get_database(db_name)
      puts "The crawler created database #{db_name}:"
      print "#{database}".yellow
      puts "\nThe database contains these tables:"
      tables = wrapper.get_tables(db_name)
      tables.each_with_index do |table, index|
        print "\t#{index + 1}. #{table['name']}".yellow
      end
      print "\nDone!\n".green
      puts "-" * 88
    end


    it "Creates a job definition that runs an ETL script", integ: false do
      puts "Uploading Python ETL script to S3..."
      wrapper.upload_job_script(job_script, glue_bucket)
      puts "Creating job definition #{job_name}:\n"
      response = wrapper.create_job(job_name, "Getting started example job.", glue_service_role.arn, "s3://#{glue_bucket.name}/#{job_script}")
      puts JSON.pretty_generate(response).yellow
      print "\nDone!\n".green
    end

    it "Starts a new job", integ: false do
      tables = wrapper.get_tables(db_name)
      job_run_status = nil
      job_run_id = wrapper.start_job_run(
        job_name,
        db_name,
        tables[0]["name"],
        glue_bucket.name
      )
      puts "Job #{job_name} started. Let's wait for it to run."
      until ["SUCCEEDED", "STOPPED", "FAILED", "TIMEOUT"].include?(job_run_status)
        custom_wait(10)
        job_run = wrapper.get_job_runs(job_name)
        job_run_status = job_run[0]["job_run_state"]
        print "Status check: #{job_name}/#{job_run_id} - #{job_run_status}.".yellow
      end
      puts "-" * 88
    end

    it "Views results from a successful job run", integ: false do
      job_run = wrapper.get_job_runs(job_name)
      job_run_status = job_run[0]["job_run_state"]
      if job_run_status == "SUCCEEDED"
        puts "Data from your job run is stored in your S3 bucket '#{glue_bucket.name}'. Files include:"
        begin

          # Print the key name of each object in the bucket.
          glue_bucket.objects.each do |object_summary|
            if object_summary.key.include?("run-")
              print "#{object_summary.key}".yellow
            end
          end

          # Print the first 256 bytes of a run file
          desired_sample_objects = 1
          glue_bucket.objects.each do |object_summary|
            if object_summary.key.include?("run-")
              if desired_sample_objects > 0
                sample_object = glue_bucket.object(object_summary.key)
                sample = sample_object.get(range: "bytes=0-255").body.read
                puts "\nSample run file contents:"
                print "#{sample}".yellow
                desired_sample_objects -= 1
              end
            end
          end
        rescue Aws::S3::Errors::ServiceError => e
          logger.error(
            "Couldn't get job run data. Here's why: %s: %s",
            e.response.error.code, e.response.error.message
          )
          raise
        end
      end
    end

    it "Deletes job definition and crawler.", integ: false do
      wrapper.delete_job(job_name)
      wrapper.delete_crawler(crawler_name)
    end
  end
end
