# frozen_string_literal: true

require 'sinatra'
require 'sinatra/cors'
require_relative 'db_wrapper'
require_relative 'report'
require 'aws-sdk-rdsdataservice'
require 'aws-sdk-ses'
require 'logger'

logger = Logger.new($stdout)
client = Aws::RDSDataService::Client.new
ses_client = Aws::SES::Client.new
config = YAML.load_file('helpers/config.yml')
wrapper = DBWrapper.new(config, client)
reporter = Report.new(wrapper, 'fprior@amazon.com', ses_client)

# set :port, 8080
# set :allow_origin, '*'

configure do
  set :port, 8080
  set :allow_origin, '*'
  enable :cross_origin
end

before do
  response.headers['Access-Control-Allow-Origin'] = '*'
  response.headers["Access-Control-Allow-Methods"] = "HEAD,GET,PUT,POST,DELETE,OPTIONS"
  response.headers["Access-Control-Allow-Headers"] = "Content-Type"
end

get '/api/items' do
  items = wrapper.get_work_items(nil, params[:archived])
  items.to_json
end

post '/api/items' do
  payload = MultiJson.load(request.body.read)
  id = wrapper.add_work_item(payload)
  [204, id]
end

get '/api/items/:item_id' do
  item = wrapper.get_work_items(:item_id)
  item
end

put %r{/api/items/([\w]+):archive} do |id|
  is_added = wrapper.archive_work_item(id)
  if is_added
    204
  end
end

post %r{/api/items:report} do
  reporter.post_report('fprior@amazon.com')
  204
end

options "*" do
  response.headers["Access-Control-Allow-Methods"] = "HEAD,GET,PUT,POST,DELETE,OPTIONS"
  response.headers["Access-Control-Allow-Headers"] = "Content-Type"
  204
end
