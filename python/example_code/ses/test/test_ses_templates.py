# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for ses_templates.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from ses_templates import SesTemplate


def make_template(name):
    return {
        'TemplateName': name,
        'SubjectPart': 'test-subject {{subject}}',
        'TextPart': 'test {{text}}',
        'HtmlPart': 'test {{html}}'
    }


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_create_template(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_template = SesTemplate(ses_client)
    template = make_template('test-template')

    ses_stubber.stub_create_template(*template.values(), error_code=error_code)

    if error_code is None:
        ses_template.create_template(*template.values())
        assert template == ses_template.template
        assert {'subject', 'text', 'html'} == ses_template.template_tags
        assert ses_template.verify_tags({'text': 'hi', 'html': '<p>hi</p>'})
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_template.create_template(*template.values())
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_delete_template(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_template = SesTemplate(ses_client)
    template_name = 'test-template'
    ses_template.template = {'TemplateName': template_name}

    ses_stubber.stub_delete_template(template_name, error_code=error_code)

    if error_code is None:
        ses_template.delete_template()
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_template.delete_template()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_get_template(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_template = SesTemplate(ses_client)
    template = make_template('test-template')

    ses_stubber.stub_get_template(*template.values(), error_code=error_code)

    if error_code is None:
        got_template = ses_template.get_template(template['TemplateName'])
        assert got_template == template
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_template.get_template(template['TemplateName'])
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_list_templates(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_template = SesTemplate(ses_client)
    template_names = [f'test-template-{index}' for index in range(4)]

    ses_stubber.stub_list_templates(template_names, error_code=error_code)

    if error_code is None:
        got_template_metas = ses_template.list_templates()
        assert got_template_metas == [
            {'Name': meta['Name']} for meta in got_template_metas]
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_template.list_templates()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_update_template(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_template = SesTemplate(ses_client)
    template = make_template('test-template')

    ses_stubber.stub_update_template(*template.values(), error_code=error_code)

    if error_code is None:
        ses_template.update_template(*template.values())
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_template.update_template(*template.values())
        assert exc_info.value.response['Error']['Code'] == error_code
