# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for AWS IoT Greengrass code example snippets.
"""

import importlib
import os
import sys
from unittest.mock import MagicMock, ANY, patch, mock_open, call

import pytest

gg_mock = MagicMock()
gg_ml_mock = MagicMock()
boto3_mock = MagicMock()

sys.modules['threading'] = MagicMock()
sys.modules['greengrasssdk'] = gg_mock
sys.modules['greengrass_machine_learning_sdk'] = gg_ml_mock
sys.modules['boto3'] = boto3_mock


@pytest.mark.parametrize('module_name, topic', [
    ('snippets.connector_cloudwatch_metrics_usage', 'cloudwatch/metric/put'),
    ('snippets.connector_kinesis_firehose_usage', 'kinesisfirehose/message'),
    ('snippets.connector_modbus_rtu_usage', 'modbus/adapter/request'),
    ('snippets.connector_raspberrypi_gpio_usage', 'gpio/test-iot/22/read'),
    ('snippets.connector_serial_stream_usage', 'serial/CORE_THING_NAME/write/dev/serial1'),
    ('snippets.connector_servicenow_metricsbase_integration_usage', 'servicenow/metricbase/metric'),
    ('snippets.connector_sns_usage', 'sns/message'),
    ('snippets.connector_splunk_integration_usage', 'splunk/logs/put'),
    ('snippets.connector_twilio_notifications_usage', 'twilio/txt')
])
def test_connector(module_name, topic, monkeypatch):
    monkeypatch.setenv('AWS_IOT_THING_NAME', 'test-iot')
    conn_module = importlib.import_module(module_name)
    conn_module.publish_basic_message()
    gg_mock.client.assert_called_with('iot-data')
    gg_mock.client().publish.assert_called_with(
        topic=topic, payload=ANY)


def test_ml_connector():
    with patch('builtins.open', mock_open()):
        with patch.object(
                gg_ml_mock.client(), 'invoke_inference_service') as invoke_mock:
            mock_body = MagicMock()
            mock_body.read.return_value = '[1,2,3]'
            invoke_mock.return_value = {'Body': mock_body}
            import snippets.connector_image_classification_usage as ml_conn
            ml_conn.infer()
            gg_ml_mock.client.assert_called_with('inference')
            gg_ml_mock.client().invoke_inference_service.assert_called_with(
                AlgoType='image-classification',
                ServiceName='imageClassification',
                ContentType='image/jpeg',
                Body=ANY)


def test_getting_started_connector():
    conn_module = importlib.import_module('snippets.getting_started_connectors')
    event = {'to_name': 'test name', 'to_number': '555-0101', 'temperature': 100}
    conn_module.function_handler(event, None)
    gg_mock.client.assert_called_with('iot-data')
    gg_mock.client().publish.assert_called_with(
        topic='twilio/txt', payload=ANY)


@pytest.mark.parametrize("module_name, sdk_mock", [
    ('snippets.iot_data_client_boto3', boto3_mock),
    ('snippets.iot_data_client_greengrasssdk', gg_mock)
])
def test_client(module_name, sdk_mock):
    importlib.import_module(module_name)
    sdk_mock.client.assert_called_with('iot-data')
    sdk_mock.client().publish.assert_called_with(
        topic='some/topic', qos=0, payload='Some payload'.encode())


def test_local_resource_access(monkeypatch):
    lra_module = importlib.import_module('snippets.local_resource_access_volume')
    monkeypatch.setattr(os, 'stat', lambda x: f"Stat info for {x}")
    with patch('builtins.open', mock_open(read_data="data")) as mock_file:
        lra_module.function_handler(None, None)
        mock_file.assert_called()
    gg_mock.client.assert_called_with('iot-data')
    gg_mock.client().publish.assert_has_calls([
        call(topic='LRA/test', payload=ANY),
        call(topic='LRA/test', payload=ANY)])


@pytest.mark.parametrize('module_name,check_publish,use_version', [
    ('snippets.secret_resource_access', True, False),
    ('snippets.secret_resource_access_default_value', False, False),
    ('snippets.secret_resource_access_staging_label', False, True)
])
def test_secret_resource_access(module_name, check_publish, use_version):
    secret_module = importlib.import_module(module_name)
    secret_module.function_handler(None, None)
    gg_mock.client.assert_any_call('secretsmanager')
    kwargs = {'SecretId': secret_module.secret_name}
    if use_version:
        kwargs['VersionStage'] = secret_module.secret_version
    gg_mock.client().get_secret_value.assert_called_with(**kwargs)
    if check_publish:
        gg_mock.client().publish.assert_any_call(
            topic=secret_module.send_topic, payload=ANY)
