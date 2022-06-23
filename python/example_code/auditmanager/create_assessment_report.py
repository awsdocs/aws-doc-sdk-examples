# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Audit Manager to create an
assessment report that consists of only one day of evidence.
"""

# snippet-start:[python.example_code.auditmanager.Scenario_CreateAssessmentReport]
import dateutil.parser
import logging
import time
import urllib.request
import uuid
import boto3
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


class AuditReport:
    def __init__(self, auditmanager_client):
        self.auditmanager_client = auditmanager_client

    def get_input(self):
        print('-' * 40)
        try:
            assessment_id = input('Provide assessment id [uuid]: ').lower()
            try:
                assessment_uuid = uuid.UUID(assessment_id)
            except ValueError:
                logger.error("Assessment Id is not a valid UUID: %s", assessment_id)
                raise
            evidence_folder = input('Provide evidence date [yyyy-mm-dd]: ')
            try:
                evidence_date = dateutil.parser.parse(evidence_folder).date()
            except ValueError:
                logger.error("Invalid date : %s", evidence_folder)
                raise
            try:
                self.auditmanager_client.get_assessment(assessmentId=str(assessment_uuid))
            except ClientError:
                logger.exception("Couldn't get assessment %s.", assessment_uuid)
                raise
        except (ValueError, ClientError):
            return None, None
        else:
            return assessment_uuid, evidence_date

    def clear_staging(self, assessment_uuid, evidence_date):
        """
        Find all the evidence in the report and clear it.
        """
        next_token = None
        page = 1
        interested_folder_id_list = []
        while True:
            print(f"Page [{page}]")
            if next_token is None:
                folder_list = self.auditmanager_client.get_evidence_folders_by_assessment(
                    assessmentId=str(assessment_uuid),
                    maxResults=1000)
            else:
                folder_list = self.auditmanager_client.get_evidence_folders_by_assessment(
                    assessmentId=str(assessment_uuid),
                    nextToken=next_token,
                    maxResults=1000)
            folders = folder_list.get('evidenceFolders')
            print(f"Got {len(folders)} folders.")
            for folder in folders:
                folder_id = folder.get('id')
                if folder.get('name') == str(evidence_date):
                    interested_folder_id_list.append(folder_id)
                if folder.get('assessmentReportSelectionCount') == folder.get('totalEvidence'):
                    print(
                        f"Removing folder from report selection : {folder.get('name')} "
                        f"{folder_id} {folder.get('controlId')}")
                    self.auditmanager_client.disassociate_assessment_report_evidence_folder(
                        assessmentId=str(assessment_uuid),
                        evidenceFolderId=folder_id)
                elif folder.get('assessmentReportSelectionCount') > 0:
                    # Get all evidence in the folder and
                    # add selected evidence in the selected_evidence_list.
                    evidence_list = self.auditmanager_client.get_evidence_by_evidence_folder(
                        assessmentId=str(assessment_uuid),
                        controlSetId=folder_id,
                        evidenceFolderId=folder_id,
                        maxResults=1000)
                    selected_evidence_list = []
                    for evidence in evidence_list.get('evidence'):
                        if evidence.get('assessmentReportSelection') == 'Yes':
                            selected_evidence_list.append(evidence.get('id'))
                    print(f"Removing evidence report selection : {folder.get('name')} "
                          f"{len(selected_evidence_list)}")
                    self.auditmanager_client.batch_disassociate_assessment_report_evidence(
                        assessmentId=str(assessment_uuid),
                        evidenceFolderId=folder_id,
                        evidenceIds=selected_evidence_list)
            next_token = folder_list.get('nextToken')
            if not next_token:
                break
            page += 1
        return interested_folder_id_list

    def add_folder_to_staging(self, assessment_uuid, folder_id_list):
        print(f"Adding folders to report : {folder_id_list}")
        for folder in folder_id_list:
            self.auditmanager_client.associate_assessment_report_evidence_folder(
                assessmentId=str(assessment_uuid),
                evidenceFolderId=folder)

    def get_report(self, assessment_uuid):
        report = self.auditmanager_client.create_assessment_report(
            name='ReportViaScript',
            description='testing',
            assessmentId=str(assessment_uuid))
        if self._is_report_generated(report.get('assessmentReport').get('id')):
            report_url = self.auditmanager_client.get_assessment_report_url(
                assessmentReportId=report.get('assessmentReport').get('id'),
                assessmentId=str(assessment_uuid))
            print(report_url.get('preSignedUrl'))
            urllib.request.urlretrieve(
                report_url.get('preSignedUrl').get('link'),
                report_url.get('preSignedUrl').get('hyperlinkName'))
            print(f"Report saved as {report_url.get('preSignedUrl').get('hyperlinkName')}.")
        else:
            print("Report generation did not finish in 15 minutes.")
            print("Failed to download report. Go to the console and manually download "
                  "the report.")

    def _is_report_generated(self, assessment_report_id):
        max_wait_time = 0
        while max_wait_time < 900:
            print(f"Checking status of the report {assessment_report_id}")
            report_list = self.auditmanager_client.list_assessment_reports(maxResults=1)
            if (report_list.get('assessmentReports')[0].get('id') == assessment_report_id
                    and report_list.get('assessmentReports')[0].get('status') == 'COMPLETE'):
                return True
            print('Sleeping for 5 seconds...')
            time.sleep(5)
            max_wait_time += 5


def run_demo():
    print('-' * 88)
    print("Welcome to the AWS Audit Manager samples demo!")
    print('-' * 88)
    print("This script creates an assessment report for an assessment with all the "
          "evidence collected on the provided date.")
    print('-' * 88)

    report = AuditReport(boto3.client('auditmanager'))
    assessment_uuid, evidence_date = report.get_input()
    if assessment_uuid is not None and evidence_date is not None:
        folder_id_list = report.clear_staging(assessment_uuid, evidence_date)
        report.add_folder_to_staging(assessment_uuid, folder_id_list)
        report.get_report(assessment_uuid)


if __name__ == '__main__':
    run_demo()
# snippet-end:[python.example_code.auditmanager.Scenario_CreateAssessmentReport]
