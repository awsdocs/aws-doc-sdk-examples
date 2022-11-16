# frozen_string_literal: true

require 'sinatra'
require 'sinatra/cors'
require_relative 'db_wrapper'
require 'aws-sdk-rdsdataservice'


client = Aws::RDSDataService::Client.new
config = YAML.load_file('helpers/config.yml')
wrapper = DBWrapper.new(config, client)

set :port, 8080

# set :allow_headers, "Authorization, Content-Type, Accept, X-User-Email, X-Auth-Token"
set :allow_origin, 'http://localhost:3000'
set :allow_methods, "HEAD,GET,PUT,POST,DELETE,OPTIONS"
set :allow_headers, "X-Requested-With, X-HTTP-Method-Override, Content-Type, Cache-Control, Accept"
set :allow_credentials, true
set :max_age, "1728000"
set :expose_headers, ['Content-Type']

get '/api/items' do
  wrapper.get_work_items(nil, params[:archived])
end

post '/api/items' do
  payload = MultiJson.load(request.body.read)
  wrapper.add_work_item(payload)
end

get '/api/items/:item_id' do
  wrapper.get_work_items(:item_id, params[:archived])
  # #{params['item_id']}
end

delete '/api/items/:item_id' do
  wrapper.archive_work_item(:item_id)
end

post '/api/items/report' do
  # @wrapper.create_report({}.json)
end

# options "*" do
#   response.headers["Allow"] = "HEAD,GET,PUT,POST,DELETE,OPTIONS"
#   response.headers["Access-Control-Allow-Headers"] = "X-Requested-With, X-HTTP-Method-Override, Content-Type, Cache-Control, Accept"
#   200
# end