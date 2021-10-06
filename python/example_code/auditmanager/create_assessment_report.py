# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[auditmanager.Python.assessment_report.create]
"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Audit Manager to create an
assessment report that consists of only one day of evidence.
"""

import time
import urllib.request
import boto3
import re
from botocore.exceptions import ClientError

auditmanager_client = boto3.client('auditmanager')


def get_input():
    print('-' * 40)
    assessment_id = input('Provide assessment id [uuid]: ').lower()
    if not re.match("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$", assessment_id):
        print("Assessment Id is not a valid UUID : ", assessment_id)
        return
    evidence_folder = input('Provide evidence date [yyyy-mm-dd] : ')
    if not re.match("^([0-9]{4})\-(0[1-9]|1[0-2])\-(0[1-9]|1[0-9]|2[0-9]|3[0-1])$", evidence_folder):
        print("[ERROR] Invalid date : ", evidence_folder)
        return
    try:
        auditmanager_client.get_assessment(assessmentId=assessment_id)
    except ClientError as ex:
        print(ex)
        return
    return assessment_id, evidence_folder


def clear_staging(assessment_id, evidence_folder):
    # find all the evidence in the report and clear it
    next_token = None
    page = 1
    interested_folder_id_list = []
    while True:
        print(" Page [" + str(page) + "]")
        if next_token is None:
            folder_list = auditmanager_client.get_evidence_folders_by_assessment(
                assessmentId=assessment_id,
                maxResults=1000
            )
        else:
            folder_list = auditmanager_client.get_evidence_folders_by_assessment(
                assessmentId=assessment_id,
                nextToken=next_token,
                maxResults=1000
            )
            print('got folders: ', len(folder_list.get('evidenceFolders')))
        for folder in folder_list.get('evidenceFolders'):
            if folder.get('name') == evidence_folder:
                interested_folder_id_list.append(folder.get('id'))
            if folder.get('assessmentReportSelectionCount') == folder.get('totalEvidence'):
                print("Removing folder from report selection : ", folder.get('name'), " ", folder.get('id'), " ", folder.get('controlId'))
                auditmanager_client.disassociate_assessment_report_evidence_folder(
                    assessmentId=assessment_id,
                    evidenceFolderId=folder.get('id')
                )
            elif folder.get('assessmentReportSelectionCount') > 0:
                # get all evidence in the folder
                # add selected evidence in the selected_evidence_list
                evidence_list = auditmanager_client.get_evidence_by_evidence_folder(assessmentId=assessment_id,
                                                                                    controlSetId=folder.get('id'),
                                                                                    evidenceFolderId=folder.get('id'),
                                                                                    maxResults=1000)
                selected_evidence_list = []
                for evidence in evidence_list.get('evidence'):
                    if evidence.get('assessmentReportSelection') == 'Yes':
                        selected_evidence_list.append(evidence.get('id'))
                print("Removing evidence report selection : ", folder.get('name'), "  ", len(selected_evidence_list))
                auditmanager_client.batch_disassociate_assessment_report_evidence(
                    assessmentId=assessment_id,
                    evidenceFolderId=folder.get('id'),
                    evidenceIds=selected_evidence_list
                )
        next_token = folder_list.get('nextToken')
        if not next_token:
            break
        page += 1
    return interested_folder_id_list


def add_folder_to_staging(assessment_id, folder_id_list):
    print("Adding folders to report : ", folder_id_list)
    for folder in folder_id_list:
        auditmanager_client.associate_assessment_report_evidence_folder(
            assessmentId=assessment_id,
            evidenceFolderId=folder
        )


def get_report(assessment_id):
    report = auditmanager_client.create_assessment_report(
        name='ReportViaScript',
        description='testing',
        assessmentId=assessment_id,
    )
    if is_report_generated(report.get('assessmentReport').get('id')):
        report_url = auditmanager_client.get_assessment_report_url(
            assessmentReportId=report.get('assessmentReport').get('id'),
            assessmentId=assessment_id,
        )
        print(report_url.get('preSignedUrl'))
        urllib.request.urlretrieve(report_url.get('preSignedUrl').get('link'),
                                   report_url.get('preSignedUrl').get('hyperlinkName'))
    else:
        print('Report generation did not finish in 15 mins')
        print('Failed to download report. Go to the console and manually download the report.')


def is_report_generated(assessment_report_id):
    max_wait_time = 0
    while max_wait_time < 900:
        print('Checking status of the report ', assessment_report_id)
        report_list = auditmanager_client.list_assessment_reports(
            maxResults=1
        )
        if report_list.get('assessmentReports')[0].get('id') == assessment_report_id \
                and report_list.get('assessmentReports')[0].get('status') == 'COMPLETE':
            return True
        print('Sleeping for 5 secs')
        time.sleep(5)
        max_wait_time += 5


if __name__ == '__main__':
    print('-' * 88)
    print("     Welcome to the AWS Audit Manager Samples Demo!")
    print('-' * 88)
    print(" This script creates an assessment report for an assessment with all the evidence collected on the provided date.")
    print('-' * 88)
    assessment_id, evidence_folder = get_input()
    folder_id_list = clear_staging(assessment_id, evidence_folder)
    add_folder_to_staging(assessment_id, folder_id_list)
    get_report(assessment_id)
# snippet-end:[auditmanager.Python.assessment_report.create]