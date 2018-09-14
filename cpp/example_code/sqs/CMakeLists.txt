# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# http://aws.amazon.com/apache2.0/
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

set (CMAKE_CXX_STANDARD 11)

cmake_minimum_required(VERSION 3.2)
project(sqs-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "list_queues")
list(APPEND EXAMPLES "create_queue")
list(APPEND EXAMPLES "get_queue_url")
list(APPEND EXAMPLES "delete_queue")
list(APPEND EXAMPLES "send_message")
list(APPEND EXAMPLES "receive_message")
list(APPEND EXAMPLES "change_message_visibility")
list(APPEND EXAMPLES "long_polling_on_create_queue")
list(APPEND EXAMPLES "long_polling_on_existing_queue")
list(APPEND EXAMPLES "long_polling_on_message_receipt")
list(APPEND EXAMPLES "dead_letter_queue")
list(APPEND EXAMPLES "purge_queue")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-sqs aws-cpp-sdk-core)
endforeach()


