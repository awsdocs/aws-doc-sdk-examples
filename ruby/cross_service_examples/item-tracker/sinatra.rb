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
require "sinatra/config_file"

# Creates the Sinatra service, which responds to HTTP requests through its routes.
# @param demo [bool] Is this a test?
def create_app(demo=false)
  config = demo ? "spec/config.yml" : "config.yml"
  config_file config
  # Sinatra run command
end