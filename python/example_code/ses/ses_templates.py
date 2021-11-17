# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Simple Email Service
(Amazon SES) to manage email templates that contain replaceable tags.
"""

import logging
from pprint import pprint
import re
import boto3
from botocore.exceptions import ClientError

# Defines template tags, which are enclosed in two curly braces, such as {{tag}}.
TEMPLATE_REGEX = r'(?<={{).+?(?=}})'

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ses.SesTemplate]
class SesTemplate:
    """Encapsulates Amazon SES template functions."""
    def __init__(self, ses_client):
        """
        :param ses_client: A Boto3 Amazon SES client.
        """
        self.ses_client = ses_client
        self.template = None
        self.template_tags = set()

    def _extract_tags(self, subject, text, html):
        """
        Extracts tags from a template as a set of unique values.

        :param subject: The subject of the email.
        :param text: The text version of the email.
        :param html: The html version of the email.
        """
        self.template_tags = set(re.findall(TEMPLATE_REGEX, subject + text + html))
        logger.info("Extracted template tags: %s", self.template_tags)
# snippet-end:[python.example_code.ses.SesTemplate]

    def verify_tags(self, template_data):
        """
        Verifies that the tags in the template data are part of the template.

        :param template_data: Template data formed of key-value pairs of tags and
                              replacement text.
        :return: True when all of the tags in the template data are usable with the
                 template; otherwise, False.
        """
        diff = set(template_data) - self.template_tags
        if diff:
            logger.warning(
                "Template data contains tags that aren't in the template: %s", diff)
            return False
        else:
            return True

    def name(self):
        """
        :return: Gets the name of the template, if a template has been loaded.
        """
        return self.template['TemplateName'] if self.template is not None else None

# snippet-start:[python.example_code.ses.CreateTemplate]
    def create_template(self, name, subject, text, html):
        """
        Creates an email template.

        :param name: The name of the template.
        :param subject: The subject of the email.
        :param text: The plain text version of the email.
        :param html: The HTML version of the email.
        """
        try:
            template = {
                'TemplateName': name,
                'SubjectPart': subject,
                'TextPart': text,
                'HtmlPart': html}
            self.ses_client.create_template(Template=template)
            logger.info("Created template %s.", name)
            self.template = template
            self._extract_tags(subject, text, html)
        except ClientError:
            logger.exception("Couldn't create template %s.", name)
            raise
# snippet-end:[python.example_code.ses.CreateTemplate]

# snippet-start:[python.example_code.ses.DeleteTemplate]
    def delete_template(self):
        """
        Deletes an email template.
        """
        try:
            self.ses_client.delete_template(TemplateName=self.template['TemplateName'])
            logger.info("Deleted template %s.", self.template['TemplateName'])
            self.template = None
            self.template_tags = None
        except ClientError:
            logger.exception(
                "Couldn't delete template %s.", self.template['TemplateName'])
            raise
# snippet-end:[python.example_code.ses.DeleteTemplate]

# snippet-start:[python.example_code.ses.GetTemplate]
    def get_template(self, name):
        """
        Gets a previously created email template.

        :param name: The name of the template to retrieve.
        :return: The retrieved email template.
        """
        try:
            response = self.ses_client.get_template(TemplateName=name)
            self.template = response['Template']
            logger.info("Got template %s.", name)
            self._extract_tags(
                self.template['SubjectPart'], self.template['TextPart'],
                self.template['HtmlPart'])
        except ClientError:
            logger.exception("Couldn't get template %s.", name)
            raise
        else:
            return self.template
# snippet-end:[python.example_code.ses.GetTemplate]

# snippet-start:[python.example_code.ses.ListTemplates]
    def list_templates(self):
        """
        Gets a list of all email templates for the current account.

        :return: The list of retrieved email templates.
        """
        try:
            response = self.ses_client.list_templates()
            templates = response['TemplatesMetadata']
            logger.info("Got %s templates.", len(templates))
        except ClientError:
            logger.exception("Couldn't get templates.")
            raise
        else:
            return templates
# snippet-end:[python.example_code.ses.ListTemplates]

# snippet-start:[python.example_code.ses.UpdateTemplate]
    def update_template(self, name, subject, text, html):
        """
        Updates a previously created email template.

        :param name: The name of the template.
        :param subject: The subject of the email.
        :param text: The plain text version of the email.
        :param html: The HTML version of the email.
        """
        try:
            template = {
                'TemplateName': name,
                'SubjectPart': subject,
                'TextPart': text,
                'HtmlPart': html}
            self.ses_client.update_template(Template=template)
            logger.info("Updated template %s.", name)
            self.template = template
            self._extract_tags(subject, text, html)
        except ClientError:
            logger.exception("Couldn't update template %s.", name)
            raise
# snippet-end:[python.example_code.ses.UpdateTemplate]


# snippet-start:[python.example_code.ses.Scenario_Templates]
def usage_demo():
    print('-'*88)
    print("Welcome to the Amazon Simple Email Service (Amazon SES) email template "
          "demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    ses_template = SesTemplate(boto3.client('ses'))
    template = {
        'name': 'doc-example-template',
        'subject': 'Example of an email template.',
        'text': "This is what {{name}} will {{action}} if {{name}} can't display HTML.",
        'html': "<p><i>This</i> is what {{name}} will {{action}} if {{name}} "
                "<b>can</b> display HTML.</p>"}
    print("Creating an email template.")
    ses_template.create_template(**template)
    print("Getting the list of template metadata.")
    template_metas = ses_template.list_templates()
    for temp_meta in template_metas:
        print(f"Got template {temp_meta['Name']}:")
        temp_data = ses_template.get_template(temp_meta['Name'])
        pprint(temp_data)
    print(f"Deleting template {template['name']}.")
    ses_template.delete_template()

    print("Thanks for watching!")
    print('-'*88)
# snippet-end:[python.example_code.ses.Scenario_Templates]


if __name__ == '__main__':
    usage_demo()
