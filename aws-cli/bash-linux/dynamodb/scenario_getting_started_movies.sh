#!/bin/bash
# bashsupport disable=BP2002

###############################################################################
#
#    Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#    SPDX-License-Identifier: Apache-2.0
#
###############################################################################

###############################################################################
#
#     Before running this AWS CLI example, set up your development environment, including your credentials.
#
#     For more information, see the following documentation topic:
#
#     https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
#
###############################################################################

#
# Purpose
#
# Demonstrates using the AWS Command Line Interface with Bash to create an Amazon DynamoDB table and
#    perform a series of operations on the table.
#
# 1. Create a table with partition: year and sort: title. (CreateTable)
# 2. Add a new movie. (PutItem)
# 3. Update the rating and plot of the movie by using an update expression. (UpdateItem)
# 4. Put movies in the table from the JSON files in the "movie_files" directory. (BatchWriteItem)
# 5. Get a movie by Key. (partition + sort) (GetItem)
# 6. Use Query with a key condition expression to return all movies released in a given
#    year. (Query)
# 7. Use Scan to return movies released within a range of years. (Scan)
# 8. Delete a movie. (DeleteItem)
# 9. Delete the table. (DeleteTable)
#

###############################################################################
# function get_input
#
# This function gets user input from the command line.
#
# Outputs:
#   User input to stdout.
#
# Returns:
#       0
###############################################################################
function get_input() {

  if [ -z "${mock_input+x}" ]; then
    read -r get_input_result
  else

    if [ -n "${mock_input_array[*]}" ]; then
      get_input_result="${mock_input_array[0]}"
      # bashsupport disable=BP2001
      # shellcheck disable=SC2206
      mock_input_array=(${mock_input_array[@]:1})
      export mock_input_array
      echo -n "$get_input_result"
    else
      get_input_result="y"
      echo "MOCK_INPUT_ARRAY is empty" 1>&2
      exit 1
    fi
  fi
}

###############################################################################
# function clean_up
#
# This function cleans up the created resources.
#
# Returns:
#       0 - If successful.
#       1 - If an error occurred.
###############################################################################
function clean_up() {
  local result=0
  local table_name=$1

  if [ -n "$table_name" ]; then
    if (dynamodb_delete_table -n "$table_name"); then
      echo "Deleted DynamoDB table named $table_name"
    else
      errecho "The table failed to delete."
      result=1
    fi
  fi

  if [ -e "$key_schema_json_file" ]; then
    rm "$key_schema_json_file"
  fi

  if [ -e "$attribute_definitions_json_file" ]; then
    rm $attribute_definitions_json_file""
  fi

  if [ -e "$item_json_file" ]; then
    rm "$item_json_file"
  fi

  if [ -e "$key_json_file" ]; then
    rm "$key_json_file"
  fi

  if [ -e "$batch_json_file" ]; then
    rm "$batch_json_file"
  fi

  if [ -e "$attribute_names_json_file" ]; then
    rm "$attribute_names_json_file"
  fi

  if [ -e "$attributes_values_json_file" ]; then
    rm "$attributes_values_json_file"
  fi

  return $result
}

###############################################################################
# function get_yes_no_input
#
# This function requests a yes/no answer from the user.
#
# Parameters:
#       $1 - The prompt.
#
# Returns:
#       0 - If yes.
#       1 - If no.
###############################################################################
function get_yes_no_input() {
  if [ -z "$1" ]; then
    echo "Internal error get_yes_no_input"
    return 1
  fi

  local index=0
  local response="N"
  while [[ $index -lt 10 ]]; do
    index=$((index + 1))
    echo -n "$1"
    get_input

    response=$(echo "$get_input_result" | tr '[:upper:]' '[:lower:]')
    if [ "$response" = "y" ] || [ "$response" = "n" ]; then
      break
    else
      echo -e "\nPlease enter or 'y' or 'n'."
    fi
  done

  echo

  if [ "$response" = "y" ]; then
    return 0
  else
    return 1
  fi
}

###############################################################################
# function get_int_input
#
# This function requests a non-negative integer answer from the user.
#
# Parameters:
#       $1 - The prompt.
#       $2 - Optional inclusive low.
#       #3 - Optional inclusive high.
#
# Returns:
#       The input,
#    And
#       0 - If successful.
#       1 - If an error occurred.
###############################################################################
function get_int_input() {
  if [ -z "$1" ]; then
    echo "Internal error get_int_input"
    return 1
  fi

  local low=$2
  local high=$3

  local index=0
  local response=""
  local input_valid=false
  while [[ $input_valid == false ]]; do
    index=$((index + 1))
    if [[ index -gt 10 ]]; then
      return 1
    fi

    echo -n "$1"
    get_input

    # Remove leading integers.
    # shellcheck disable=SC2001
    response=$(echo "$get_input_result" | sed "s/[[:digit:]]*//")

    # Non-empty string indicates invalid input.
    if [[ -n "$response" ]]; then

      echo -e "Please enter an integer."
      continue
    fi

    response="$get_input_result"

    if [[ -z $low ]] || [[ -z $high ]]; then
      break
    fi

    if [[ $response -lt $low ]]; then
      echo -e "Response must be greater than or equal to $low."
      continue
    fi

    if [[ $response -gt $high ]]; then
      echo -e "Response must be less than or equal to $high."
      continue
    fi

    input_valid=true
  done

  get_input_result="$response"

  return 0
}

###############################################################################
# function get_float_input
#
# This function requests a non-negative float answer from the user.
#
# Parameters:
#       $1 - The prompt.
#       $2 - Optional inclusive low.
#       #3 - Optional inclusive high.
#
# Returns:
#       The input,
#    And
#       0 - If successful.
#       1 - If an error occurred.
###############################################################################
function get_float_input() {
  if [ -z "$1" ]; then
    echo "Internal error get_float_input"
    return 1
  fi

  local low=$2
  local high=$3

  local index=0
  local response=""
  local input_valid=false
  while [[ $input_valid == false ]]; do
    index=$((index + 1))
    if [[ index -gt 10 ]]; then
      return 1
    fi

    echo -n "$1"
    get_input

    # Remove leading integers.
    # shellcheck disable=SC2001
    response=$(echo "$get_input_result" | sed "s/[[:digit:]]*//")

    if [[ -n "$response" ]]; then # Remove decimal if present.
      # shellcheck disable=SC2001
      response=$(echo "$response" | sed "s/^\.[[:digit:]]*//")
    fi

    # Non-empty string indicates invalid input.
    if [[ -n "$response" ]]; then
      echo -e "Please enter a floating point number."
      continue
    fi

    response="$get_input_result"

    if [[ -z $low ]] || [[ -z $high ]]; then
      break
    fi

    if (($(echo "$response < $low" | bc -l))); then
      echo -e "Response must be greater than or equal to $low."
      continue
    fi

    if (($(echo "$response > $high" | bc -l))); then
      echo -e "Response must be less than or equal to $high."
      continue
    fi

    input_valid=true
  done

  get_input_result="$response"
  return 0
}

###############################################################################
# function echo_repeat
#
# This function prints a string 'n' times to stdout.
#
# Parameters:
#       $1 - The string.
#       $2 - Number of times to print the string.
#
# Outputs:
#   String 'n' times to stdout.
#
# Returns:
#       0
###############################################################################
function echo_repeat() {
  local end=$2 i
  for ((i = 0; i < end; i++)); do
    echo -n "$1"
  done
  echo
}

# snippet-start:[aws-cli.bash-linux.dynamodb.scenario_getting_started_movies]
###############################################################################
# function dynamodb_getting_started_movies
#
# Scenario to create an Amazon DynamoDB table and perform a series of operations on the table.
#
# Returns:
#       0 - If successful.
#       1 - If an error occurred.
###############################################################################
function dynamodb_getting_started_movies() {
  source ./dynamodb_operations.sh

  key_schema_json_file="dynamodb_key_schema.json"
  attribute_definitions_json_file="dynamodb_attr_def.json"
  item_json_file="movie_item.json"
  key_json_file="movie_key.json"
  batch_json_file="batch.json"
  attribute_names_json_file="attribute_names.json"
  attributes_values_json_file="attribute_values.json"

  echo_repeat "*" 88
  echo
  echo "Welcome to the Amazon DynamoDB getting started demo."
  echo
  echo_repeat "*" 88
  echo

  local table_name
  echo -n "Enter a name for a new DynamoDB table: "
  get_input
  table_name=$get_input_result

  local provisioned_throughput="ReadCapacityUnits=5,WriteCapacityUnits=5"

  echo '[
  {"AttributeName": "year", "KeyType": "HASH"},
   {"AttributeName": "title", "KeyType": "RANGE"}
  ]' >"$key_schema_json_file"

  echo '[
  {"AttributeName": "year", "AttributeType": "N"},
   {"AttributeName": "title", "AttributeType": "S"}
  ]' >"$attribute_definitions_json_file"

  if dynamodb_create_table -n "$table_name" -a "$attribute_definitions_json_file" \
    -k "$key_schema_json_file" -p "$provisioned_throughput" 1>/dev/null; then
    echo "Created a DynamoDB table named $table_name"
  else
    errecho "The table failed to create. This demo will exit."
    clean_up
    return 1
  fi

  echo "Waiting for the table to become active...."

  if dynamodb_wait_table_active -n "$table_name"; then
    echo "The table is now active."
  else
    errecho "The table failed to become active. This demo will exit."
    cleanup "$table_name"
    return 1
  fi

  echo
  echo_repeat "*" 88
  echo

  echo -n "Enter the title of a movie you want to add to the table: "
  get_input
  local added_title
  added_title=$get_input_result

  local added_year
  get_int_input "What year was it released? "
  added_year=$get_input_result

  local rating
  get_float_input "On a scale of 1 - 10, how do you rate it? " "1" "10"
  rating=$get_input_result

  local plot
  echo -n "Summarize the plot for me: "
  get_input
  plot=$get_input_result

  echo '{
    "year": {"N" :"'"$added_year"'"},
    "title": {"S" :  "'"$added_title"'"},
    "info": {"M" : {"plot": {"S" : "'"$plot"'"}, "rating": {"N" :"'"$rating"'"} } }
   }' >"$item_json_file"

  if dynamodb_put_item -n "$table_name" -i "$item_json_file"; then
    echo "The movie '$added_title' was successfully added to the table '$table_name'."
  else
    errecho "Put item failed. This demo will exit."
    clean_up "$table_name"
    return 1
  fi

  echo
  echo_repeat "*" 88
  echo

  echo "Let's update your movie '$added_title'."
  get_float_input "You rated it $rating, what new rating would you give it? " "1" "10"
  rating=$get_input_result

  echo -n "You summarized the plot as '$plot'."
  echo "What would you say now? "
  get_input
  plot=$get_input_result

  echo '{
    "year": {"N" :"'"$added_year"'"},
    "title": {"S" :  "'"$added_title"'"}
    }' >"$key_json_file"

  echo '{
    ":r": {"N" :"'"$rating"'"},
    ":p": {"S" : "'"$plot"'"}
   }' >"$item_json_file"

  local update_expression="SET info.rating = :r, info.plot = :p"

  if dynamodb_update_item -n "$table_name" -k "$key_json_file" -e "$update_expression" -v "$item_json_file"; then
    echo "Updated '$added_title' with new attributes."
  else
    errecho "Update item failed. This demo will exit."
    clean_up "$table_name"
    return 1
  fi

  echo
  echo_repeat "*" 88
  echo

  echo "We will now use batch write to upload 150 movie entries into the table."

  local batch_json
  for batch_json in movie_files/movies_*.json; do
    echo "{ \"$table_name\" : $(<"$batch_json") }" >"$batch_json_file"
    if dynamodb_batch_write_item -i "$batch_json_file" 1>/dev/null; then
      echo "Entries in $batch_json added to table."
    else
      errecho "Batch write failed. This demo will exit."
      clean_up "$table_name"
      return 1
    fi
  done

  local title="The Lord of the Rings: The Fellowship of the Ring"
  local year="2001"

  if get_yes_no_input "Let's move on...do you want to get info about '$title'? (y/n) "; then
    echo '{
  "year": {"N" :"'"$year"'"},
  "title": {"S" :  "'"$title"'"}
  }' >"$key_json_file"
    local info
    info=$(dynamodb_get_item -n "$table_name" -k "$key_json_file")

    # shellcheck disable=SC2181
    if [[ ${?} -ne 0 ]]; then
      errecho "Get item failed. This demo will exit."
      clean_up "$table_name"
      return 1
    fi

    echo "Here is what I found:"
    echo "$info"
  fi

  local ask_for_year=true
  while [[ "$ask_for_year" == true ]]; do
    echo "Let's get a list of movies released in a given year."
    get_int_input "Enter a year between 1972 and 2018: " "1972" "2018"
    year=$get_input_result
    echo '{
    "#n": "year"
    }' >"$attribute_names_json_file"

    echo '{
    ":v": {"N" :"'"$year"'"}
    }' >"$attributes_values_json_file"

    response=$(dynamodb_query -n "$table_name" -k "#n=:v" -a "$attribute_names_json_file" -v "$attributes_values_json_file")

    # shellcheck disable=SC2181
    if [[ ${?} -ne 0 ]]; then
      errecho "Query table failed. This demo will exit."
      clean_up "$table_name"
      return 1
    fi

    echo "Here is what I found:"
    echo "$response"

    if ! get_yes_no_input "Try another year? (y/n) "; then
      ask_for_year=false
    fi
  done

  echo "Now let's scan for movies released in a range of years. Enter a year: "
  get_int_input "Enter a year between 1972 and 2018: " "1972" "2018"
  local start=$get_input_result

  get_int_input "Enter another year: " "1972" "2018"
  local end=$get_input_result

  echo '{
    "#n": "year"
    }' >"$attribute_names_json_file"

  echo '{
    ":v1": {"N" : "'"$start"'"},
    ":v2": {"N" : "'"$end"'"}
    }' >"$attributes_values_json_file"

  response=$(dynamodb_scan -n "$table_name" -f "#n BETWEEN :v1 AND :v2" -a "$attribute_names_json_file" -v "$attributes_values_json_file")

  # shellcheck disable=SC2181
  if [[ ${?} -ne 0 ]]; then
    errecho "Scan table failed. This demo will exit."
    clean_up "$table_name"
    return 1
  fi

  echo "Here is what I found:"
  echo "$response"

  echo
  echo_repeat "*" 88
  echo

  echo "Let's remove your movie '$added_title' from the table."

  if get_yes_no_input "Do you want to remove '$added_title'? (y/n) "; then
    echo '{
  "year": {"N" :"'"$added_year"'"},
  "title": {"S" :  "'"$added_title"'"}
  }' >"$key_json_file"

    if ! dynamodb_delete_item -n "$table_name" -k "$key_json_file"; then
      errecho "Delete item failed. This demo will exit."
      clean_up "$table_name"
      return 1
    fi
  fi

  if get_yes_no_input "Do you want to delete the table '$table_name'? (y/n) "; then
    if ! clean_up "$table_name"; then
      return 1
    fi
  else
    if ! clean_up; then
      return 1
    fi
  fi

  return 0
}
# snippet-end:[aws-cli.bash-linux.dynamodb.scenario_getting_started_movies]

###############################################################################
# function main
#
###############################################################################
function main() {
  get_input_result=""
  mock_input_array=""

  dynamodb_getting_started_movies
}

# bashsupport disable=BP5001
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  main
fi
