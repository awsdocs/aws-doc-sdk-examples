# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true
require_relative 'colorizer'

def banner
  Dir.chdir(File.dirname(__FILE__))
  text = File.read('banner.txt')
  puts text.red
end

def confirm_begin
  # not actually a password
  reply = CLI::UI::Prompt.ask_password('Press any key to continue.')
  exit unless reply
end

def new_step(number, title)
  puts "\n=== STEP #{number.to_s} === #{title}".blue
end
