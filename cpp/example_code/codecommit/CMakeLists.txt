# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# http://aws.amazon.com/apache2.0/
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

cmake_minimum_required(VERSION 3.2)
project(codecommit-examples)
set (CMAKE_CXX_STANDARD 11)

# Locate the aws sdk for c++ package.
find_package(AWSSDK REQUIRED COMPONENTS codecommit)

set(EXAMPLES "")
list(APPEND EXAMPLES "create_pull_request")
list(APPEND EXAMPLES "create_repository")
list(APPEND EXAMPLES "delete_branch")
list(APPEND EXAMPLES "delete_repository")
list(APPEND EXAMPLES "list_pull_requests")
list(APPEND EXAMPLES "update_pull_request")
list(APPEND EXAMPLES "update_repository")
list(APPEND EXAMPLES "list_branches")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
  add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
  target_link_libraries(${EXAMPLE} ${AWSSDK_LINK_LIBRARIES})
endforeach()
