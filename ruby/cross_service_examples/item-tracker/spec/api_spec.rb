ENV["APP_ENV"] = "test"

require "hello_world"  # <-- your sinatra app
require "rspec"
require "rack/test"
require_relative "../src/app"

RSpec.describe "The HelloWorld App" do
  include Rack::Test::Methods

  def app
    app
  end

  it "says hello" do
    get "/api/items/:item_id"
    expect(last_response).to be_ok
    expect(last_response.body).to eq("Hello World")
  end
end
