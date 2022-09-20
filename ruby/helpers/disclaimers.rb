# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "cli/ui"
require_relative("colorizer")

def billing
  puts "\e[H\e[2J"
  puts "========================== BILLING DISCLAIMER =============================".yellow
  puts "Please note that running this code will create actual resources that may"
  puts "incur charges. While the code attempts to destroy all resources it creates,"
  puts "unexpected errors may leave orphaned resources that must be manually deleted."
  puts ""
  puts "For more information on how AWS pricing works, please see:"
  puts "    https://aws.amazon.com/pricing/?aws-products-pricing"
  puts ""
  puts "==========================================================================".yellow
  reply = CLI::UI::Prompt.confirm("Do you understand the above BILLING DISCLAIMER and wish to continue?")
  exit unless reply
end

def security
  puts "\e[H\e[2J"
  puts "========================== SECURITY DISCLAIMER =============================".yellow
  puts "Per Amazon Web Service's Shared Responsibility Model, you, the customer,"
  puts "assume a certain level of responsibility and should carefully consider"
  puts "the security impact of the services used in this demo, the integration"
  puts "of these services into your IT environment, and applicable laws and regulations."
  puts ""
  puts "For more information on the Shared Repsonsibility Model, please see:      "
  puts "    https://aws.amazon.com/compliance/shared-responsibility-model/"
  puts ""
  puts "==========================================================================".yellow
  reply = CLI::UI::Prompt.confirm("Do you understand the above SECURITY DISCLAIMER and wish to continue?")
  exit unless reply
end
