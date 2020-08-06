# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the requests package to call the REST API created by this demo.
"""

import argparse
import datetime
import logging
import pprint
import random
import urllib.parse
import boto3
import requests


def find_api_url(stack_name):
    """
    Find the API URL from the AWS CloudFormation stack that was used to create
    the resources for this demo.

    :param stack_name: The name of the stack.
    :return: The endpoint URL found in the stack description.
    """
    cloudformation = boto3.resource('cloudformation')
    stack = cloudformation.Stack(name=stack_name)
    try:
        api_url = next(
            output['OutputValue'] for output in stack.outputs
            if output['OutputKey'] == 'EndpointURL')
        print(f"Found API URL in {stack_name} AWS CloudFormation stack: {api_url}")
    except StopIteration:
        print("Couldn't find the REST URL for your API. Try running the following "
              "at the command prompt:\n")
        print(f"\taws cloudformation describe-stacks --stack-name {stack_name} "
              f"--query \"Stacks[0].Outputs[?OutputKey=='EndpointURL'].OutputValue\" "
              f"--output text")
    else:
        return api_url


def demo():
    """
    Calls the REST API in various ways, using the requests package.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    stack_name = 'ChaliceRestDemo'

    parser = argparse.ArgumentParser()
    parser.add_argument(
        '--api_url',
        help=f"The base URL of the REST API for the demo. If not specified, the demo "
             f"looks it up in the {stack_name} AWS CloudFormation stack.")
    args = parser.parse_args()
    if args.api_url is None:
        api_url = find_api_url(stack_name)
    else:
        api_url = args.api_url
        print(f"Using supplied API URL: {api_url}")
    if api_url is not None:
        print('-' * 88)
        print("Welcome to the AWS Chalice REST API client demo.")
        print('-' * 88)

        states_url = urllib.parse.urljoin(api_url, 'states')
        print(f"Sending GET request to {states_url}")
        states_response = requests.get(states_url)
        print(f"Response: {states_response.status_code}\n\t{states_response.json()}")

        states = states_response.json()['states'].split(', ')
        random_state = random.choice(states)
        state_url = urllib.parse.urljoin(api_url, f'states/{random_state}')
        print(f"Sending GET request to {state_url}")
        state_response = requests.get(state_url)
        print(f"Response: {state_response.status_code}")
        pprint.pprint(state_response.json())

        historical_data = [{
            'state': random_state,
            'date': (datetime.date.today() -
                     datetime.timedelta(days=index)).isoformat(),
            'cases': index*random.randint(1, 100),
            'deaths': index*random.randint(0, 20)
        } for index in range(1, 11)]
        print(f"Put {len(historical_data)} historical records for {random_state}.")
        for record in historical_data:
            requests.put(state_url, json=record)
        print(f"Send GET request again to {state_url}")
        state_response = requests.get(state_url)
        print(f"Response: {state_response.status_code}")
        pprint.pprint(state_response.json())

        past_date = (datetime.date.today() -
                     datetime.timedelta(days=random.randint(2, 10))).isoformat()
        date_url = urllib.parse.urljoin(api_url, f'states/{random_state}/{past_date}')
        print(f"Sending GET request to {date_url}")
        date_response = requests.get(date_url)
        print(f"Response: {date_response.status_code}\n\t{date_response.json()}")

        print(f"Sending DELETE request to {date_url}")
        date_del_response = requests.delete(date_url)
        print(f"Response: {date_del_response.status_code}")

        print(f"Sending GET request to {date_url} (expect 404).")
        date_response = requests.get(date_url)
        print(f"Response: {date_response.status_code}")

        print(f"Sending DELETE request to remove all historical data from "
              f"{random_state}.")
        state_response = requests.delete(state_url)
        print(f"Response: {state_response.status_code}")

        print(f"Send GET to {state_url} (expect new random data for today).")
        state_response = requests.get(state_url)
        print(f"Response: {state_response.status_code}\n\t{state_response.json()}")

        print("You can remove all resources created for this demo by running "
              "the following at a command prompt:")
        print(f"\taws cloudformation delete-stack --stack-name {stack_name}")
        print("Thanks for watching!")


if __name__ == '__main__':
    demo()
