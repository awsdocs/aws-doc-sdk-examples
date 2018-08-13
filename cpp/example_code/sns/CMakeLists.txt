# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# http://aws.amazon.com/apache2.0/
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.


cmake_minimum_required(VERSION 2.8)
project(sns-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "list_topics")
list(APPEND EXAMPLES "create_topic")
list(APPEND EXAMPLES "delete_topic")
list(APPEND EXAMPLES "get_topic_attributes")
list(APPEND EXAMPLES "get_sms_type")
list(APPEND EXAMPLES "list_subscriptions")
list(APPEND EXAMPLES "publish_sms")
list(APPEND EXAMPLES "publish_to_topic")
list(APPEND EXAMPLES "set_sms_type")
list(APPEND EXAMPLES "subscribe_app")
list(APPEND EXAMPLES "subscribe_email")
list(APPEND EXAMPLES "subscribe_lambda")
list(APPEND EXAMPLES "unsubscribe")



# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
  add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
	target_link_libraries(${EXAMPLE} aws-cpp-sdk-sns)
endforeach()

