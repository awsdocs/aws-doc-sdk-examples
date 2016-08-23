cmake_minimum_required(VERSION 2.8)
project(s3-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

# Link to shared libraries.
add_definitions(-DUSE_IMPORT_EXPORT)

# The executables to build.
add_executable(copy_object copy_object.cpp)
add_executable(create_bucket create_bucket.cpp)
add_executable(delete_bucket delete_bucket.cpp)
add_executable(delete_object delete_object.cpp)
add_executable(get_object get_object.cpp)
add_executable(list_buckets list_buckets.cpp)
add_executable(list_objects list_objects.cpp)
#add_executable(put_object put_object.cpp)

# Since find_package() was used, all dependencies, header files, and cflags
# necessary to build and link the executables are automatically resolved.
target_link_libraries(copy_object aws-cpp-sdk-s3)
target_link_libraries(create_bucket aws-cpp-sdk-s3)
target_link_libraries(delete_bucket aws-cpp-sdk-s3)
target_link_libraries(delete_object aws-cpp-sdk-s3)
target_link_libraries(get_object aws-cpp-sdk-s3)
target_link_libraries(list_buckets aws-cpp-sdk-s3)
target_link_libraries(list_objects aws-cpp-sdk-s3)
#target_link_libraries(put_object aws-cpp-sdk-s3)
