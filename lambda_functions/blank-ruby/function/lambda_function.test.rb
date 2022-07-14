# frozen_string_literal: true

require_relative 'lambda_function'
require 'test/unit'
require 'json'
require 'logger'
require 'aws-xray-sdk/lambda'

XRay.recorder.configure({ context_missing: 'LOG_ERROR' })

class TestFunction < Test::Unit::TestCase
  logger = Logger.new($stdout)

  def test_invoke
    file = File.read('event.json')
    event = JSON.parse(file)
    context = {}
    result = lambda_handler(event: event, context: context)
    assert_match('function_count', result.to_s, 'Should match')
  end
end
