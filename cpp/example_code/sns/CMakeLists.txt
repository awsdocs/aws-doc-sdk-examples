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
list(APPEND EXAMPLES "set_topic_attributes")
list(APPEND EXAMPLES "subscribe_app")
list(APPEND EXAMPLES "subscribe_email")
list(APPEND EXAMPLES "subscribe_lambda")
list(APPEND EXAMPLES "unsubscribe")



# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
  add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
	target_link_libraries(${EXAMPLE} aws-cpp-sdk-sns)
endforeach()

