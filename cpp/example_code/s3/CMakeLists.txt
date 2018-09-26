# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# http://aws.amazon.com/apache2.0/
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

cmake_minimum_required(VERSION 3.2)
project(s3-examples)

# Locate the aws sdk for c++ package.
find_package(AWSSDK REQUIRED COMPONENTS s3)

# Link to shared libraries.
add_definitions(-DUSE_IMPORT_EXPORT)

# The executables to build.
set(EXAMPLES "")
list(APPEND EXAMPLES "copy_object")
list(APPEND EXAMPLES "create_bucket")
list(APPEND EXAMPLES "delete_bucket")
list(APPEND EXAMPLES "delete_bucket_policy")
list(APPEND EXAMPLES "delete_object")
list(APPEND EXAMPLES "delete_website_config")
list(APPEND EXAMPLES "get_acl")
list(APPEND EXAMPLES "get_bucket_policy")
list(APPEND EXAMPLES "get_object")
list(APPEND EXAMPLES "get_website_config")
list(APPEND EXAMPLES "list_buckets")
list(APPEND EXAMPLES "list_objects")
list(APPEND EXAMPLES "put_bucket_policy")
list(APPEND EXAMPLES "put_object")
list(APPEND EXAMPLES "put_object_unicode")
list(APPEND EXAMPLES "put_website_config")
list(APPEND EXAMPLES "set_acl")
list(APPEND EXAMPLES "put_object_buffer")

# The executables to build
foreach(EXAMPLE IN LISTS EXAMPLES)
  add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
  target_link_libraries(${EXAMPLE} ${AWSSDK_LINK_LIBRARIES})
endforeach()
