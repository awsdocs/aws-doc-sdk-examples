require 'sinatra'
require 'sinatra-contrib'
require "sinatra/config_file"

require 'rds_actions'

config = demo ? "spec/config.yml" : "config.yml"
config_file config
# dev reminder: config available in settings attribute

get '/api/items' do
  # list_items()
end

post '/api/items' do
  # add_item()
end

get '/api/items/:item_id' do
  # get_item()
  # #{params['item_id']}
end

put '/api/items/:item_id' do
  # update_item()
end

delete '/api/items/:item_id' do
  # delete_item()
end

post '/api/items/report' do
  # add_report()
end