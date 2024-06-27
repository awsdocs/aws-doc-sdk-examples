# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[ruby.glue.HelloGlue]

require 'aws-sdk-glue'

def hello_glue
  # Create a Glue client using the default AWS credentials and configuration
  glue_client = Aws::Glue::Client.new

  # List the jobs in your account
  jobs = []
  next_token = nil
  loop do
    response = glue_client.get_jobs(
      max_results: 10, # Limit the number of results to 10
      next_token: next_token
    )
    jobs.concat(response.jobs)
    next_token = response.next_token
    break if next_token.nil?
  end

  # Print the job names
  if jobs.empty?
    puts "You don't have any Glue jobs."
  else
    puts "Here are the Glue jobs in your account:"
    jobs.each do |job|
      puts "- #{job.name}"
    end
  end
end

hello_glue

# snippet-end:[ruby.glue.HelloGlue]
