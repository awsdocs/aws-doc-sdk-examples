cmake_minimum_required(VERSION 2.8)
project(cloudwatch-examples)

# Locate the aws sdk for c++ package.
find_package(aws-sdk-cpp)

set(EXAMPLES "")
list(APPEND EXAMPLES "describe_alarms")
list(APPEND EXAMPLES "delete_alarm")
list(APPEND EXAMPLES "put_metric_alarm")
list(APPEND EXAMPLES "enable_alarm_actions")
list(APPEND EXAMPLES "disable_alarm_actions")
list(APPEND EXAMPLES "list_metrics")
list(APPEND EXAMPLES "put_metric_data")
list(APPEND EXAMPLES "put_rule")
list(APPEND EXAMPLES "put_targets")
list(APPEND EXAMPLES "put_events")
list(APPEND EXAMPLES "describe_subscription_filters")
list(APPEND EXAMPLES "delete_subscription_filter")
list(APPEND EXAMPLES "put_subscription_filter")

# The executables to build.
foreach(EXAMPLE IN LISTS EXAMPLES)
    add_executable(${EXAMPLE} ${EXAMPLE}.cpp)
    target_link_libraries(${EXAMPLE} aws-cpp-sdk-monitoring aws-cpp-sdk-events aws-cpp-sdk-logs)
endforeach()


