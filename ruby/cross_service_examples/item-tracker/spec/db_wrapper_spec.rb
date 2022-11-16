# frozen_string_literal: true

require 'multi_json'
require 'sinatra'
require 'sinatra/config_file'
require 'yaml'
require 'json'
require 'rspec'
require 'aws-sdk-rdsdataservice'
require 'aws-sdk-ses'

require_relative('../db_wrapper')
require_relative('../report')

describe 'CRUD commands on Aurora' do
  client = Aws::RDSDataService::Client.new
  config = YAML.safe_load(File.open(File.join(File.dirname(__FILE__), './../helpers', 'config.yml')))
  let(:wrapper) { DBWrapper.new(config, client) }

  it 'gets the table name' do
    table = wrapper.get_table_name
    expect(table).to be_an_instance_of(String)
  end

  it 'adds a new item' do
    item_data = {
      description: 'This is a first item',
      guide: 'Foo Guide',
      status: 'Active',
      username: 'FooUser'
    }
    id = wrapper.add_work_item(item_data)
    expect(id).to be_an_instance_of(Integer)
  end

  it 'gets a specific item' do
    data = wrapper.get_work_items(1, false)
    expect(data[0]).to be_an_instance_of(Hash)
  end

  it 'gets multiple items' do
    item_data = {
      description: 'This is a second item',
      guide: 'Bar Guide',
      status: 'Active',
      username: 'BarUser'
    }
    wrapper.add_work_item(item_data)
    data = wrapper.get_work_items(nil, false)
    expect(data[0]).to be_an_instance_of(Hash)
  end

  it 'archives a specific item' do
    id = wrapper.archive_work_item(5)
    expect(id).to be_an_instance_of(TrueClass)
  end

  it 'make report' do
    # Generate report
    report = Report.new(wrapper, 'fprior@amazon.com', Aws::SES::Client.new)
    response = report.post('fprior@amazon.com')
    puts "RESPONSE: " + response.to_s
  end
end

