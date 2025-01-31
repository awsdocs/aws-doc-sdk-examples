#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#// snippet-start:[ec2.bash.change-instance-type.complete]
###############################################################################
#
# function change_ec2_instance_type
#
# This function changes the instance type of the specified Amazon EC2 instance.
#
# Parameters:
#   -i   [string, mandatory] The instance ID of the instance whose type you
#                            want to change.
#   -t   [string, mandatory] The instance type to switch the instance to.
#   -f   [switch, optional]  If set, the function doesn't pause and ask before
#                            stopping the instance.
#   -r   [switch, optional]  If set, the function restarts the instance after
#                            changing the type.
#   -v   [switch, optional]  Enable verbose logging.
#   -h   [switch, optional]  Displays this help.
#
# Example:
#      The following example converts the specified instance to type "t2.micro"
#      without pausing to ask permission. It automatically restarts the
#      instance after changing the type.
#
#      change_ec2_instance_type -i i-123456789012 -t t2.micro -f -r
#
# Returns:
#      0 if successful
#      1 if it fails
###############################################################################

# Import the general_purpose functions.
source awsdocs_general.sh

###############################################################################
# function instance-exists
#
# This function checks to see if the specified instance already exists. If it
# does, it sets two global parameters to return the running state and the
# instance type.
#
# Input parameters:
#       $1 - The id of the instance to check
#
# Returns:
#       0 if the instance already exists
#       1 if the instance doesn't exist
#     AND:
#       Sets two global variables:
#            existing_state - Contains the running/stopped state of the instance.
#            existing_type  - Contains the current type of the instance.
###############################################################################
function get_instance_info() {

  # Declare local variables.
  local instance_id response

  # This function accepts a single parameter.
  instance_id=$1

  # The following --filters parameter causes server-side filtering to limit
  # results to only the records that match the specified ID. The --query
  # parameter causes CLI client-side filtering to include only the values of
  # the InstanceType and State.Code fields.

  response=$(
    aws ec2 describe-instances \
      --query 'Reservations[*].Instances[*].[State.Name, InstanceType]' \
      --filters Name=instance-id,Values="$instance_id" \
      --output text
  )

  # shellcheck disable=SC2181
  if [[ $? -ne 0 ]] || [[ -z "$response" ]]; then
    # There was no response, so no such instance.
    return 1 # 1 in Bash script means error/false
  fi

  # If we got a response, the instance exists.
  # Retrieve the values of interest and set them as global variables.
  existing_state=$(echo "$response" | cut -f 1)
  existing_type=$(echo "$response" | cut -f 2)

  return 0 # 0 in Bash script means no error/true
}

######################################
#
#  See header at top of this file
#
######################################
function change_ec2_instance_type() {

  # bashsupport disable=BP5008
  function usage() (
    echo ""
    echo "This function changes the instance type of the specified instance."
    echo "Parameter:"
    echo "  -i  Specify the instance ID whose type you want to modify."
    echo "  -t  Specify the instance type to convert the instance to."
    echo "  -f  If the instance was originally running, this option prevents"
    echo "      the script from asking permission before stopping the instance."
    echo "  -r  Start instance after changing the type."
    echo "  -v  Enable verbose logging."
    echo ""
  )

  local force restart requested_type instance_id verbose option response answer
  local OPTIND OPTARG # Required to use getopts command in a function.

  # Set default values.
  force=false
  restart=false
  requested_type=""
  instance_id=""
  verbose=false

  # Retrieve the calling parameters.
  while getopts "i:t:frvh" option; do
    case "${option}" in
      i) instance_id="${OPTARG}" ;;
      t) requested_type="${OPTARG}" ;;
      f) force=true ;;
      r) restart=true ;;
      v) verbose=true ;;
      h)
        usage
        return 0
        ;;
      \?)
        echo "Invalid parameter"
        usage
        return 1
        ;;
    esac
  done

  if [[ -z "$instance_id" ]]; then
    errecho "ERROR: You must provide an instance ID with the -i parameter."
    usage
    return 1
  fi

  if [[ -z "$requested_type" ]]; then
    errecho "ERROR: You must provide an instance type with the -t parameter."
    usage
    return 1
  fi

  iecho "Parameters:\n"
  iecho "    Instance ID:   $instance_id"
  iecho "    Requests type: $requested_type"
  iecho "    Force stop:    $force"
  iecho "    Restart:       $restart"
  iecho "    Verbose:       $verbose"
  iecho ""

  # Check that the specified instance exists.
  iecho -n "Confirming that instance $instance_id exists..."
  if ! get_instance_info "$instance_id"; then
    errecho "ERROR: I can't find the instance \"$instance_id\" in the current AWS account."
    return 1
  fi

  # Function get_instance_info has returned two global values:
  #   $existing_type  -- The instance type of the specified instance
  #   $existing_state -- Whether the specified instance is running

  iecho "confirmed $instance_id exists."
  iecho "      Current type: $existing_type"
  iecho "      Current state code: $existing_state"

  # Are we trying to change the instance to the same type?
  if [[ "$existing_type" == "$requested_type" ]]; then
    errecho "ERROR: Can't change instance type to the same type: $requested_type."
    return 1
  fi

  # Check if the instance is currently running.
  # 16="running"
  if [[ "$existing_state" == "running" ]]; then
    # If it is, we need to stop it.
    # Do we have permission to stop it?
    # If -f (force) was set, we do.
    # If not, we need to ask the user.
    if [[ $force == false ]]; then
      while true; do
        echo ""
        echo "The instance $instance_id is currently running. It must be stopped to change the type."
        read -r -p "ARE YOU SURE YOU WANT TO STOP THE INSTANCE? (Y or N) " answer
        case $answer in
          [yY]*)
            break
            ;;
          [nN]*)
            echo "Aborting."
            exit
            ;;
          *)
            echo "Please answer Y or N."
            ;;
        esac
      done
    else
      iecho "Forcing stop of instance without prompt because of -f."
    fi

    # stop the instance
    iecho -n "Attempting to stop instance $instance_id..."
    response=$(aws ec2 stop-instances \
      --instance-ids "$instance_id") || {
      echo "ERROR - AWS reports that it's unable to stop instance $instance_id."
      echo "$response"
      return 1
    }
    iecho "request accepted."
  else
    iecho "Instance is not in running state, so not requesting a stop."
  fi

  # Wait until stopped.
  iecho "Waiting for $instance_id to report 'stopped' state..."
  aws ec2 wait instance-stopped \
    --instance-ids "$instance_id" || {
    echo
    echo "ERROR - AWS reports that Wait command failed."
    echo "$response"
    return 1
  }
  iecho "stopped.\n"

  # Change the type - command produces no output.
  iecho "Attempting to change type from $existing_type to $requested_type..."
  response=$(
    aws ec2 modify-instance-attribute \
      --instance-id "$instance_id" \
      --instance-type "{\"Value\":\"$requested_type\"}"
  ) || {
    errecho "ERROR - AWS reports that it's unable to change the instance type for instance $instance_id from $existing_type to $requested_type.\n$response"
    return 1
  }
  iecho "changed.\n"

  # Restart if asked
  if [[ "$restart" == "true" ]]; then

    iecho "Requesting to restart instance $instance_id..."
    response=$(
      aws ec2 start-instances \
        --instance-ids "$instance_id"
    ) || {
      errecho "ERROR - AWS reports that it's unable to restart instance $instance_id.\n$response"
      return 1
    }

    iecho "started.\n"
    iecho "Waiting for instance $instance_id to report 'running' state..."
    response=$(aws ec2 wait instance-running \
      --instance-ids "$instance_id") || {

      errecho "ERROR - AWS reports that Wait command failed.\n$response"
    }

    iecho "running.\n"

  else
    iecho "Restart was not requested with -r.\n"
  fi
}

#// snippet-end:[ec2.bash.change-instance-type.complete]
