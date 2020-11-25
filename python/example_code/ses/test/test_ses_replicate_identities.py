# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for ses_replicate_identities.py.
"""

import json
import random
import string
import time
import uuid
import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY
import pytest

import ses_replicate_identities


def generate_random_string(char_set, length):
    return ''.join(random.choices(char_set + string.digits, k=length))


def get_zone(name):
    return {
        'CallerReference': str(uuid.uuid4()),
        'Config': {'Comment': name, 'PrivateZone': True},
        'Id': f'/hostedzone/{generate_random_string(string.ascii_letters, 21)}',
        'Name': f'{name}.'}


def get_record_sets(domain, rec_name, dkim_count, existing_tokens):
    record_sets = [{
        'Name': domain,
        'ResourceRecords': [{'Value': 'ns-1234.exampledns.com.'}],
        'TTL': 172800,
        'Type': 'NS'
    }, {
        'Name': domain,
        'ResourceRecords': [{
            'Value': 'ns-1234.exampledns.com. awsdns-hostmaster.amazon.com. '
                     '1 7200 900 1209600 86400'}],
        'TTL': 900,
        'Type': 'SOA'
    }]
    for _ in range(dkim_count):
        cname_string = generate_random_string(string.ascii_lowercase, 32)
        record_sets.append({
            'Name': f'{cname_string}._domainkey.{rec_name}.',
            'ResourceRecords': [{'Value': f'{cname_string}.dkim.amazonses.com'}],
            'TTL': 1800,
            'Type': 'CNAME'})
    for ex_domain, token in existing_tokens.items():
        record_sets.append({
            'Name': f'_amazonses.{ex_domain}.',
            'ResourceRecords': [{'Value': json.dumps(token)}],
            'TTL': 1800,
            'Type': 'TXT'
        })
    return record_sets


def get_record_set_changes(domain, new_token, existing_tokens):
    records = [{'Value': json.dumps(new_token)}]
    if domain in existing_tokens:
        records.insert(0, {'Value': json.dumps(existing_tokens[domain])})
    changes = [{
        'Action': 'UPSERT',
        'ResourceRecordSet': {
            'Name': f'_amazonses.{domain}',
            'Type': 'TXT',
            'TTL': 1800,
            'ResourceRecords': records}}]
    return changes


def get_record_set_dkim_changes(domain, tokens):
    changes = [{
        'Action': 'UPSERT',
        'ResourceRecordSet': {
            'Name': f'{token}._domainkey.{domain}',
            'Type': 'CNAME',
            'TTL': 1800,
            'ResourceRecords': [{'Value': f'{token}.dkim.amazonses.com'}]}}
        for token in tokens]
    return changes


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_identities(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    emails = ['bill@example1.com', 'bob@example3.com']
    domains = ['example1.com', 'example2.com']

    ses_stubber.stub_list_identities(
        None, 20, emails + domains, error_code=error_code)

    if error_code is None:
        got_emails, got_domains = ses_replicate_identities.get_identities(ses_client)
        assert got_emails == emails
        assert got_domains == domains
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_replicate_identities.get_identities(ses_client)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_verify_emails(make_stubber):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    in_emails = ['tester@example.com', 'failer@example-2.com', 'tryer@example-1.com']
    out_emails = ['tester@example.com', 'tryer@example-1.com']

    ses_stubber.stub_verify_email_identity(in_emails[0])
    ses_stubber.stub_verify_email_identity(in_emails[1], error_code='TestException')
    ses_stubber.stub_verify_email_identity(in_emails[2])

    got_emails = ses_replicate_identities.verify_emails(in_emails, ses_client)
    assert got_emails == out_emails


def test_verify_domains(make_stubber):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    in_domains = ['example.com', 'example-fail.com', 'example-1.com']
    out_domain_tokens = {'example.com': 'test-token', 'example-1.com': 'test-token-1'}

    ses_stubber.stub_verify_domain_identity(
        in_domains[0], out_domain_tokens[in_domains[0]])
    ses_stubber.stub_verify_domain_identity(
        in_domains[1], 'no-token', error_code='TestException')
    ses_stubber.stub_verify_domain_identity(
        in_domains[2], out_domain_tokens[in_domains[2]])

    got_domain_tokens = ses_replicate_identities.verify_domains(in_domains, ses_client)
    assert got_domain_tokens == out_domain_tokens


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_hosted_zones(make_stubber, error_code):
    route53_client = boto3.client('route53')
    route53_stubber = make_stubber(route53_client)
    zones = [get_zone(f'example-{index}') for index in range(3)]

    route53_stubber.stub_list_hosted_zones(zones, '20', error_code=error_code)

    if error_code is None:
        got_zones = ses_replicate_identities.get_hosted_zones(route53_client)
        assert got_zones == zones
    else:
        got_zones = ses_replicate_identities.get_hosted_zones(route53_client)
        assert not got_zones


def test_find_domain_zone_matches(make_stubber):
    domains = [
        'example1.com', 'test.example1.com', 'sub.test.example1.com',
        'example2.com', 'test.example2.com', 'example3.com']
    zones = [
        {'Name': 'example1.com.'},
        {'Name': 'test.example1.com.'},
        {'Name': 'example2.com.'}]
    domain_zones = {
        'example1.com': zones[0],
        'test.example1.com': zones[1],
        'sub.test.example1.com': zones[1],
        'example2.com': zones[2],
        'test.example2.com': zones[2],
        'example3.com': None}

    got_domain_zones = ses_replicate_identities.find_domain_zone_matches(
        domains, zones)
    assert got_domain_zones == domain_zones


@pytest.mark.parametrize("domain,rec_name,error_code,stop_on_method", [
    ('example.com', 'example.com', None, None),
    ('test.example.com', 'example.com', None, None),
    ('test.example.com', 'test.example.com', None, None),
    ('test.example.com', 'example2.com', None, None),
    ('example.com', 'example.com', 'TestException', 'stub_list_resource_record_sets')])
def test_add_route_53_verification_record(
        make_stubber, stub_runner, domain, rec_name, error_code, stop_on_method):
    route53_client = boto3.client('route53')
    route53_stubber = make_stubber(route53_client)
    zone = get_zone(domain)
    new_token = f'new-token-{uuid.uuid4()}'
    existing_domain_tokens = {
        f'{sub}{rec_name}': f'old-token-{uuid.uuid4()}' for sub in ['', 'test', 'dev']}
    record_sets = get_record_sets(domain, rec_name, 3, existing_domain_tokens)
    changes = get_record_set_changes(domain, new_token, existing_domain_tokens)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            route53_stubber.stub_list_resource_record_sets,
            zone['Id'], '20', record_sets)
        runner.add(
            route53_stubber.stub_change_resource_record_sets, zone['Id'], changes)

    if error_code is None:
        ses_replicate_identities.add_route53_verification_record(
            domain, new_token, zone, route53_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_replicate_identities.add_route53_verification_record(
                domain, new_token, zone, route53_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_generate_dkim_tokens(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    domain = 'example.com'
    tokens = ['test-token-1', 'test-token-2']

    ses_stubber.stub_verify_domain_dkim(domain, tokens, error_code=error_code)

    got_tokens = ses_replicate_identities.generate_dkim_tokens(domain, ses_client)
    if error_code is None:
        assert got_tokens == tokens
    else:
        assert not got_tokens


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_add_dkim_domain_tokens(make_stubber, stub_runner, error_code):
    route53_client = boto3.client('route53')
    route53_stubber = make_stubber(route53_client)
    domain = 'example.com'
    zone = get_zone(domain)
    tokens = [f'test-dkim-token-{index}' for index in range(3)]
    changes = get_record_set_dkim_changes(domain, tokens)

    route53_stubber.stub_change_resource_record_sets(zone['Id'], changes)

    ses_replicate_identities.add_dkim_domain_tokens(
        zone, domain, tokens, route53_client)


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_configure_sns_topics(make_stubber, monkeypatch, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    identity = 'tester@example.com'
    topics = ['Funny', 'Critical', 'Boing']
    topic_arns = [f'arn:aws:sns::123456789012:{topic}Topic' for topic in topics]

    monkeypatch.setattr('builtins.input', lambda x: topic_arns.pop(0))

    for topic, topic_arn in zip(topics, topic_arns):
        ses_stubber.stub_set_identity_notification_topic(
            identity, topic, topic_arn, error_code=error_code)

    ses_replicate_identities.configure_sns_topics(identity, topics, ses_client)
