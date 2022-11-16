# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


# Shows how to use the AWS SDK for Python (Boto3) to create a REST web service that
# stores work items in an Amazon Aurora Serverless database and uses Amazon Simple
# Email Service (Amazon SES) to let client applications do the following:

# * Get a list of active or archived work items.
# * Add new work items to the database.
# * Archive a currently active work item.
# * Send a report of work items to an email recipient.

# This web service is intended to be used in conjunction with the Elwing React
# client found in the resources/clients/react/elwing folder of this repository.
# For more information on how to set up resources, run the web service, and run the
# React client, see the accompanying README.



# import cors?
require 'sinatra'
require 'item_list'
require 'models/item'
require 'report'
require 'db_wrapper'
require 'sinatra'

# Creates the Sinatra service, which responds to HTTP requests through its routes.
class MyApp < Sinatra::Base

  def initialize
    @wrapper = DBWrapper.new(config, client)
    @client = Aws::RDSDataService::Client.new
    @config = YAML.load_file('helpers/config.yml')
  end

  set :port, 8080

  before do
    response.headers['Access-Control-Allow-Origin'] = 'http://example.com'
  end

  options "*" do
    response.headers["Allow"] = "GET, PUT, POST, DELETE, OPTIONS"
    response.headers["Access-Control-Allow-Headers"] = "Authorization, Content-Type, Accept, X-User-Email, X-Auth-Token"
    response.headers["Access-Control-Allow-Origin"] = "*"
    200
  end
