cmake_minimum_required(VERSION 2.8)
set (CMAKE_CXX_STANDARD 11)
project(s3-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

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
  target_link_libraries(${EXAMPLE} aws-cpp-sdk-s3 aws-cpp-sdk-core)
endforeach()
