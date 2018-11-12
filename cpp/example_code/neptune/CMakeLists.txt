# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# http://aws.amazon.com/apache2.0/
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

cmake_minimum_required(VERSION 3.2)
project(neptune-examples)
set (CMAKE_CXX_STANDARD 11)

# Locate the aws sdk for c++ package.
find_package(AWSSDK REQUIRED COMPONENTS neptune)

set(EXAMPLES "")
list(APPEND EXAMPLES "create_db_cluster")
list(APPEND EXAMPLES "delete_db_cluster")
list(APPEND EXAMPLES "modify_db_cluster")
list(APPEND EXAMPLES "describe_db_clusters")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
  add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
  target_link_libraries(${EXAMPLE} ${AWSSDK_LINK_LIBRARIES})
endforeach()
