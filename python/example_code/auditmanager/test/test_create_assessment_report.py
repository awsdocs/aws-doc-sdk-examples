# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for create_assessment_report.py.
"""

import dateutil.parser
import urllib.request
import uuid
import boto3
from botocore.exceptions import ClientError
import pytest

from create_assessment_report import AuditReport


@pytest.mark.parametrize('inputs, outputs, error_code', [
    (['bad-uuid'], (None, None), 'Nostub'),
    (['f66e7fc4-baf1-4661-85db-f6ff6ee76630', 'bad-date'], (None, None), 'Nostub'),
    (['f66e7fc4-baf1-4661-85db-f6ff6ee76630', '2021-01-01'],
     (uuid.UUID('f66e7fc4-baf1-4661-85db-f6ff6ee76630'), dateutil.parser.parse('2021-01-01').date()),
     None),
    (['f66e7fc4-baf1-4661-85db-f6ff6ee76630', '2021-01-01'], (None, None), 'TestException'),
])
def test_get_input(make_stubber, monkeypatch, inputs, outputs, error_code):
    auditmanager_client = boto3.client('auditmanager')
    auditmanager_stubber = make_stubber(auditmanager_client)
    report = AuditReport(auditmanager_client)

    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    if error_code != 'Nostub':
        auditmanager_stubber.stub_get_assessment(inputs[0], error_code=error_code)

    got_uuid, got_date = report.get_input()
    assert got_uuid == outputs[0]
    assert got_date == outputs[1]


@pytest.mark.parametrize('assessment_uuid, evidence_date, tokens, folders, stop_on_action, error_code', [
    (uuid.UUID('f66e7fc4-baf1-4661-85db-f6ff6ee76630'), None, [None, None], [], None, None),
    (uuid.UUID('f66e7fc4-baf1-4661-85db-f6ff6ee76630'), None, [None, None], [], 'stub_get_evidence_folders_by_assessment', 'TestException'),
    (uuid.UUID('f66e7fc4-baf1-4661-85db-f6ff6ee76630'), None, [None, '1', None], [], None, None),
    (uuid.UUID('f66e7fc4-baf1-4661-85db-f6ff6ee76630'), '2021-01-01', [None, None], [
        {'id': f'id-{"1"*36}', 'name': '2021-01-01', 'assessmentReportSelectionCount': 1,
         'totalEvidence': 1, 'controlId': f'ctl-{"1"*36}'}
    ], None, None),
    (uuid.UUID('f66e7fc4-baf1-4661-85db-f6ff6ee76630'), '2021-01-01', [None, None], [
        {'id': f'id-{"1" * 36}', 'name': '2021-01-01', 'assessmentReportSelectionCount': 2,
         'totalEvidence': 1, 'controlId': f'ctl-{"1" * 36}'}
    ], None, None),
])
def test_clear_staging(
        make_stubber, stub_runner, assessment_uuid, evidence_date, tokens, folders,
        stop_on_action, error_code):
    auditmanager_client = boto3.client('auditmanager')
    auditmanager_stubber = make_stubber(auditmanager_client)
    report = AuditReport(auditmanager_client)

    with stub_runner(error_code, stop_on_action) as runner:
        for i_token in range(len(tokens) - 1):
            runner.add(
                auditmanager_stubber.stub_get_evidence_folders_by_assessment,
                str(assessment_uuid), 1000, tokens[i_token:i_token+2], folders)
        if len(folders) > 0:
            if folders[0]['assessmentReportSelectionCount'] == folders[0]['totalEvidence']:
                runner.add(
                    auditmanager_stubber.stub_disassociate_assessment_report_evidence_folder,
                    str(assessment_uuid), folders[0]['id'])
            else:
                evidence_id = f'ev-{"1"*36}'
                runner.add(
                    auditmanager_stubber.stub_get_evidence_by_evidence_folder,
                    str(assessment_uuid), folders[0]['id'], 1000, [
                        {'id': evidence_id, 'assessmentReportSelection': 'Yes'}])
                runner.add(
                    auditmanager_stubber.stub_batch_disassociate_assessment_report_evidence,
                    str(assessment_uuid), folders[0]['id'], [evidence_id])

    if error_code is None:
        got_folder_ids = report.clear_staging(assessment_uuid, evidence_date)
        assert got_folder_ids == [folder['id'] for folder in folders]
    else:
        with pytest.raises(ClientError) as exc_info:
            report.clear_staging(assessment_uuid, evidence_date)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_add_folder_to_staging(make_stubber, error_code):
    auditmanager_client = boto3.client('auditmanager')
    auditmanager_stubber = make_stubber(auditmanager_client)
    report = AuditReport(auditmanager_client)
    assessment_uuid = uuid.UUID('f66e7fc4-baf1-4661-85db-f6ff6ee76630')
    folder_id = f'folder-{"1"*36}'

    auditmanager_stubber.stub_associate_assessment_report_evidence_folder(
        str(assessment_uuid), folder_id, error_code=error_code)

    if error_code is None:
        report.add_folder_to_staging(assessment_uuid, [folder_id])
    else:
        with pytest.raises(ClientError) as exc_info:
            report.add_folder_to_staging(assessment_uuid, [folder_id])
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code, stop_on_action', [
    (None, None),
    ('TestException', 'stub_create_assessment_report')])
def test_get_report(make_stubber, stub_runner, monkeypatch, error_code, stop_on_action):
    auditmanager_client = boto3.client('auditmanager')
    auditmanager_stubber = make_stubber(auditmanager_client)
    report = AuditReport(auditmanager_client)
    assessment_uuid = uuid.UUID('f66e7fc4-baf1-4661-85db-f6ff6ee76630')
    report_id = f'report-{"1"*36}'
    report_url = 'https://example.com/test-report'

    monkeypatch.setattr(urllib.request, 'urlretrieve', lambda x, y: None)

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(
            auditmanager_stubber.stub_create_assessment_report, 'ReportViaScript',
            'testing', str(assessment_uuid), report_id)
        runner.add(auditmanager_stubber.stub_list_assessment_reports, [report_id])
        runner.add(
            auditmanager_stubber.stub_get_assessment_report_url, report_id,
            str(assessment_uuid), report_url)

    if error_code is None:
        report.get_report(assessment_uuid)
    else:
        with pytest.raises(ClientError) as exc_info:
            report.get_report(assessment_uuid)
        assert exc_info.value.response['Error']['Code'] == error_code
