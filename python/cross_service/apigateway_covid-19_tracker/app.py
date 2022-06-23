# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement a REST API using AWS Chalice. This REST API stores and
retrieves fictional data representing COVID-19 cases in the United States.

You can find actual COVID-19 data in Amazon's public data lake.
    * https://aws.amazon.com/covid-19-data-lake/
"""

import datetime
import decimal
import json
import logging
import urllib.parse
import chalice
import chalicelib.covid_data

app = chalice.Chalice(app_name='fictional-covid')
app.debug = True  # Set this to False for production use.

logger = logging.getLogger()
logger.setLevel(logging.INFO)

storage = chalicelib.covid_data.Storage.from_env()


def convert_decimal_to_int(obj):
    """
    Amazon DynamoDB returns numbers in Python Decimal format, which are converted to
    float by the JSON serializer. This function instead converts them to int.
    Pass this function to the `json.dumps` function for custom conversion.

    :param obj: The current object being deserialized.
    :return: When obj is a Decimal, return it as an int.
    """
    if isinstance(obj, decimal.Decimal):
        return int(obj)


def verify_input(state, date=None, data=None):
    """
    Verifies that user inputs to the API are within expected bounds. Unacceptable
    input raises a BadRequestError with status code 400.

    :param state: The state specified for the current request.
    :param date: The date specified for the current request.
    :param data: The body data specified for the current request.
    """
    if state not in storage.STATES:
        raise chalice.BadRequestError(
            f"Unknown state '{state}'.\nYour choices are: "
            f"{', '.join(sorted(storage.STATES))}")

    if data is not None and state != data['state']:
        raise chalice.BadRequestError(
            f"The state '{data['state']}' specified in the request body does not "
            f"match the state '{state}' specified in the URL.")

    test_date = date if date is not None else None if data is None else data['date']
    if test_date is not None:
        try:
            iso_date = datetime.date.fromisoformat(test_date)
            earliest_date = datetime.date(year=2020, month=1, day=1)
            today = datetime.date.today()
            if iso_date < earliest_date or iso_date > today:
                raise chalice.BadRequestError(
                    f"Date must be between {earliest_date} and {today}. "
                    f"{test_date} is outside that range.")
        except ValueError as error:
            raise chalice.BadRequestError from error


@app.route('/states', methods=['GET'])
def list_states():
    """
    Lists the allowed states.

    :return: The list of states allowed by the Storage class.
    """
    # Chalice automatically serializes the returned dict to JSON.
    return {'states': ', '.join(sorted(storage.STATES))}


@app.route('/states/{state}', methods=['DELETE', 'GET', 'POST', 'PUT'])
def state_cases(state):
    """
    Handles requests for a specific state.

    For a PUT or POST request, the body of the request must contain a single record
    for the specified state, in JSON format. For both PUT and POST, the record is
    added to the table if it does not exist and any existing data is overwritten.

    :param state: The state of the current request.
    :return: For a GET request, all records for the specified state are returned in
             the response body in JSON format.
             For other requests, only a status code is returned.
    """
    logger.info("Got %s to /states/%s.", app.current_request.method, state)
    logger.info("JSON body: %s", app.current_request.json_body)

    state = urllib.parse.unquote(state)
    verify_input(state, data=app.current_request.json_body)

    response = None
    if app.current_request.method == 'GET':
        # To use a custom converter, serialize the response to JSON here.
        response = json.dumps(
            storage.get_state_data(state), default=convert_decimal_to_int)
    elif app.current_request.method == 'PUT':
        storage.put_state_data(state, app.current_request.json_body)
    elif app.current_request.method == 'DELETE':
        storage.delete_state_data(state)
    elif app.current_request.method == 'POST':
        storage.post_state_data(state, app.current_request.json_body)

    return response


@app.route('/states/{state}/{date}', methods=['DELETE', 'GET'])
def state_date_cases(state, date):
    """
    Handles requests for a specific state and date. Dates must be in ISO format and
    be between 20202-01-01 and today.

    For GET requests, the single record for the specified date is returned. If no
    record exists, a 404 NotFound error is returned.

    For DELETE requests, if the record does not exist, the request has no effect
    and 200 is returned.

    :param state: The state of the current request.
    :param date: The date of the current request.
    :return: For GET requests, the specified data record is returned in the response
             body in JSON format.
             For DELETE requests, only the status code is returned.
    """
    logger.info("Got %s to /states/%s/%s.", app.current_request.method, state, date)

    state = urllib.parse.unquote(state)
    date = urllib.parse.unquote(date)
    verify_input(state, date=date)

    response = None
    if app.current_request.method == 'GET':
        response = storage.get_state_date_data(state, date)
        if response is not None:
            response = json.dumps(response, default=convert_decimal_to_int)
        else:
            raise chalice.NotFoundError(f"No data found for {state} on {date}.")
    elif app.current_request.method == 'DELETE':
        storage.delete_state_date_data(state, date)

    return response
