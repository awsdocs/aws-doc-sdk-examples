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
project(ses-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "create_template")
list(APPEND EXAMPLES "create_receipt_filter")
list(APPEND EXAMPLES "create_receipt_rule")
list(APPEND EXAMPLES "create_receipt_rule_set")
list(APPEND EXAMPLES "delete_identity")
list(APPEND EXAMPLES "delete_receipt_filter")
list(APPEND EXAMPLES "delete_receipt_rule")
list(APPEND EXAMPLES "delete_receipt_rule_set")
list(APPEND EXAMPLES "delete_template")
list(APPEND EXAMPLES "get_template")
list(APPEND EXAMPLES "list_identities")
list(APPEND EXAMPLES "send_email")
list(APPEND EXAMPLES "send_templated_email")
list(APPEND EXAMPLES "update_template")
list(APPEND EXAMPLES "verify_email_identity")


# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
  add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
  target_link_libraries(${EXAMPLE} aws-cpp-sdk-email aws-cpp-sdk-core)
endforeach()

