# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "sinatra"
require "sinatra/cors"
require_relative "db_wrapper"
require_relative "report"
require "aws-sdk-rdsdataservice"
require "aws-sdk-ses"

client = Aws::RDSDataService::Client.new
ses_client = Aws::SES::Client.new
config = YAML.load_file("helpers/config.yml")
wrapper = DBWrapper.new(config, client)
reporter = Report.new(wrapper, config["recipient_email"], ses_client)

configure do
  set :port, 8080
  set :allow_origin, "*"
  enable :cross_origin
end

before do
  response.headers["Access-Control-Allow-Origin"] = "*"
  response.headers["Access-Control-Allow-Methods"] = "HEAD,GET,PUT,POST,DELETE,OPTIONS"
  response.headers["Access-Control-Allow-Headers"] = "Content-Type"
end

get "/api/items" do
  items = wrapper.get_work_items(nil, params[:archived])
  items.to_json
  halt 204
end

post "/api/items" do
  payload = MultiJson.load(request.body.read)
  wrapper.add_work_item(payload)
  halt 204
end

get "/api/items/:item_id" do
  wrapper.get_work_items(:item_id, nil)
  halt 204
end

put %r{/api/items/([\w]+):archive} do |id|
  body wrapper.archive_work_item(id) ? "true" : "false"
  halt 200
end

post %r{/api/items:report} do
  reporter.post_report(config["recipient_email"])
  halt 204
end

options "*" do
  response.headers["Access-Control-Allow-Methods"] = "HEAD,GET,PUT,POST,DELETE,OPTIONS"
  response.headers["Access-Control-Allow-Headers"] = "Content-Type"
  halt 204
end
