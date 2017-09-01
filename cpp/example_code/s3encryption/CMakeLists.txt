#   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

#   This file is licensed under the Apache License, Version 2.0 (the "License").
#   You may not use this file except in compliance with the License. A copy of
#   the License is located at

#   http://aws.amazon.com/apache2.0/

#   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
#   CONDITIONS OF ANY KIND, either express or implied. See the License for the
#   specific language governing permissions and limitations under the License.

# On Windows making sure you use the correct comipler. Such as -G"Visual Studio 14 2015 Win64"
cmake_minimum_required(VERSION 3.2)
project(s3Encryption)
SET(CMAKE_CXX_STANDARD 11)

# On Windows, AWSSDK_DIR should be provided to Cmake
if (WIN32)
	SET(AWSSDK_DIR "C:/Program Files/aws-cpp-sdk-all/lib/cmake/AWSSDK")
endif()

find_package(AWSSDK REQUIRED)

SET(SERVICE s3-encryption)
AWSSDK_DETERMINE_LIBS_TO_LINK(SERVICE OUTPUT)
Message(STATUS "All linked libs: ${OUTPUT}")

if (APPLE)
    add_definitions(-DUNDER_MACOS)
endif()

link_directories("${AWSSDK_LIB_DIR}")
add_executable(s3Encryption s3Encryption.cpp)

if (WIN32)
    add_definitions(-DUSE_IMPORT_EXPORT)
    AWSSDK_CPY_DYN_LIBS(SERVICE "" ./)
endif()

target_link_libraries(s3Encryption ${OUTPUT})
target_include_directories(s3Encryption PRIVATE ${AWSSDK_INCLUDE_DIRS})
