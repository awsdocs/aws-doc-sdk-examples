# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for iot_wrapper.py functions.
"""

import pytest
from botocore.exceptions import ClientError


@pytest.mark.parametrize("error_code", [None, "ResourceAlreadyExistsException"])
def test_create_thing(scenario_data, error_code):
    scenario_data.iot_stubber.stub_create_thing(
        "test-thing", "arn:aws:iot:us-east-1:123456789012:thing/test-thing", error_code
    )

    if error_code is None:
        response = scenario_data.wrapper.create_thing("test-thing")
        assert response["thingName"] == "test-thing"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_thing("test-thing")
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InternalFailureException"])
def test_list_things(scenario_data, error_code):
    things = [{"thingName": "thing1"}, {"thingName": "thing2"}]
    scenario_data.iot_stubber.stub_list_things(things, error_code)

    if error_code is None:
        result = scenario_data.wrapper.list_things()
        assert len(result) == 2
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_things()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ServiceUnavailableException"])
def test_create_keys_and_certificate(scenario_data, error_code):
    cert_id = "a" * 64
    scenario_data.iot_stubber.stub_create_keys_and_certificate(
        "arn:aws:iot:us-east-1:123456789012:cert/test-cert", cert_id, error_code
    )

    if error_code is None:
        response = scenario_data.wrapper.create_keys_and_certificate()
        assert response["certificateId"] == cert_id
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_keys_and_certificate()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_attach_thing_principal(scenario_data, error_code):
    scenario_data.iot_stubber.stub_attach_thing_principal(
        "test-thing", "arn:aws:iot:us-east-1:123456789012:cert/test-cert", error_code
    )

    if error_code is None:
        scenario_data.wrapper.attach_thing_principal(
            "test-thing", "arn:aws:iot:us-east-1:123456789012:cert/test-cert"
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.attach_thing_principal(
                "test-thing", "arn:aws:iot:us-east-1:123456789012:cert/test-cert"
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InternalFailureException"])
def test_describe_endpoint(scenario_data, error_code):
    scenario_data.iot_stubber.stub_describe_endpoint(
        "test-endpoint.iot.us-east-1.amazonaws.com", error_code
    )

    if error_code is None:
        endpoint = scenario_data.wrapper.describe_endpoint()
        assert "amazonaws.com" in endpoint
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.describe_endpoint()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ServiceUnavailableException"])
def test_list_certificates(scenario_data, error_code):
    certificates = [{"certificateId": "a" * 64, "certificateArn": "arn1", "status": "ACTIVE"}, {"certificateId": "b" * 64, "certificateArn": "arn2", "status": "ACTIVE"}]
    scenario_data.iot_stubber.stub_list_certificates(certificates, error_code)

    if error_code is None:
        result = scenario_data.wrapper.list_certificates()
        assert len(result) == 2
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_certificates()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_delete_certificate(scenario_data, error_code):
    cert_id = "a" * 64
    scenario_data.iot_stubber.stub_update_certificate(
        cert_id, "INACTIVE", error_code
    )
    if error_code is None:
        scenario_data.iot_stubber.stub_delete_certificate(cert_id, error_code)

    if error_code is None:
        scenario_data.wrapper.delete_certificate(cert_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.delete_certificate(cert_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceAlreadyExistsException"])
def test_create_topic_rule(scenario_data, error_code):
    scenario_data.iot_stubber.stub_create_topic_rule("test-rule", error_code)

    if error_code is None:
        scenario_data.wrapper.create_topic_rule(
            "test-rule",
            "device/test/data",
            "arn:aws:sns:us-east-1:123456789012:test-topic",
            "arn:aws:iam::123456789012:role/test-role",
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_topic_rule(
                "test-rule",
                "device/test/data",
                "arn:aws:sns:us-east-1:123456789012:test-topic",
                "arn:aws:iam::123456789012:role/test-role",
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InternalFailureException"])
def test_search_index(scenario_data, error_code):
    things = [{"thingName": "test-thing"}]
    scenario_data.iot_stubber.stub_search_index("thingName:test-thing", things, error_code)

    if error_code is None:
        result = scenario_data.wrapper.search_index("thingName:test-thing")
        assert len(result) == 1
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.search_index("thingName:test-thing")
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_delete_thing(scenario_data, error_code):
    scenario_data.iot_stubber.stub_delete_thing("test-thing", error_code)

    if error_code is None:
        scenario_data.wrapper.delete_thing("test-thing")
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.delete_thing("test-thing")
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_detach_thing_principal(scenario_data, error_code):
    scenario_data.iot_stubber.stub_detach_thing_principal(
        "test-thing", "arn:aws:iot:us-east-1:123456789012:cert/test-cert", error_code
    )

    if error_code is None:
        scenario_data.wrapper.detach_thing_principal(
            "test-thing", "arn:aws:iot:us-east-1:123456789012:cert/test-cert"
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.detach_thing_principal(
                "test-thing", "arn:aws:iot:us-east-1:123456789012:cert/test-cert"
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InternalFailureException"])
def test_list_topic_rules(scenario_data, error_code):
    rules = [{"ruleName": "rule1"}, {"ruleName": "rule2"}]
    scenario_data.iot_stubber.stub_list_topic_rules(rules, error_code)

    if error_code is None:
        result = scenario_data.wrapper.list_topic_rules()
        assert len(result) == 2
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_topic_rules()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InternalFailureException"])
def test_update_indexing_configuration(scenario_data, error_code):
    scenario_data.iot_stubber.stub_update_indexing_configuration(error_code)

    if error_code is None:
        scenario_data.wrapper.update_indexing_configuration()
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.update_indexing_configuration()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_delete_topic_rule(scenario_data, error_code):
    scenario_data.iot_stubber.stub_delete_topic_rule("test-rule", error_code)

    if error_code is None:
        scenario_data.wrapper.delete_topic_rule("test-rule")
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.delete_topic_rule("test-rule")
        assert exc_info.value.response["Error"]["Code"] == error_code
