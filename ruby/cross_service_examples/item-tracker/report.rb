# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Shows how to get a list of work items from a storage object, render it in both
# HTML and text formats, and use Amazon Simple Email Service (Amazon SES) to send it as
# an email report.
#
# When the list of items is longer than a specified threshold, it is included as a CSV
# attachment to the email instead of in the body of the email itself.

require 'logger'
require 'mime'
require 'json'
require 'csv'
require 'mail'
require_relative 'db_wrapper'

# Encapsulates a report resource that gets work items from an
# Amazon Aurora Serverless database and uses Amazon SES to send emails about them.
class Report
  # @param db_wrapper: An object that manages moving data in and out of the underlying database.
  # @param email_sender: The email address from which the email report is sent.
  # @param ses_client: A Amazon SES client.
  def initialize(db_wrapper, email_sender, ses_client)
    @logger = Logger.new($stdout)
    @db_wrapper = db_wrapper
    @email_sender = email_sender
    @ses_client = ses_client
  end

  # Formats the report as a ready-to-send email message, including attachment
  def format_message(_recipient, _text, _html, _attachment, _charset = 'utf-8')
    logger = Logger.new($stdout)
    mail = Mail.new do
      from 'info@yourrubyapp.com'
      to 'your@bestuserever.com'
      subject 'Email with HTML and an attachment'
      text_part do
        body 'Put your plain text here'
      end
      html_part do
        content_type 'text/html; charset=UTF-8'
        body '<h1>And here is the place for HTML</h1>'
      end
      add_file '/path/to/Attachment.pdf'
    end
  end

  #   msg = MIME::Multipart::Mixed.new
  #   msg['Subject'] = 'Work items'
  #   msg['From'] = @email_sender
  #   msg['To'] = recipient
  #   msg_body = MIMEMultipart('alternative')
  #
  #   text = MIMEText(text.encode(charset), 'plain', charset)
  #   html = MIMEText(html.encode(charset), 'html', charset)
  #   msg_body.attach(text)
  #   msg_body.attach(html)
  #
  #   att = MIMEApplication(attachment.encode(charset))
  #   att.add_header('Content-Disposition', 'attachment', filename = 'work_items.csv')
  #   msg.attach(msg_body)
  #   msg.attach(att)
  #   msg
  # end

  # Renders work items to CSV format, with the field names as a header row.
  #
  # @param work_items: The work items to include in the CSV output.
  # @return: Work items rendered to a string in CSV format.
  def render_csv(work_items)
    CSV.open('data.csv', 'w', headers: work_items.first.keys) do |csv|
      work_items.each do |h|
        csv << h.values
      end
    end
  end

  # Gets a list of work items from storage, makes a report of them, and
  # sends an email. The email is sent in both HTML and text format.
  #
  # When ten or fewer items are in the report, the items are included in the body
  # of the email. Otherwise, the items are included as an attachment in CSV format.
  #
  # When your Amazon SES account is in the sandbox, both the sender and recipient
  # email addresses must be registered with Amazon SES.
  #
  # @param recipient_email: The recipient's email address.
  # @return: An error message and an HTTP result code.
  def post(recipient_email)
    response = None
    result = 200
    work_items = @db_wrapper.get_work_items(item_id = nil, archive = false)
    snap_time = Time.now
    @logger.info("Sending report of #{work_items.count} items to #{recipient_email}")

    html_report = render_template(
      'report.html',
      work_items,
      item_count = work_items.count.to_s,
      snap_time = snap_time
    )
    @logger.info('HTML successfully rendered.')
    csv_items = render_csv(work_items)

    text_report = render_template(
      'report.txt',
      work_items = csv_items,
      item_count = work_items.count.to_s,
      snap_time = snap_time
    )
    @logger.info('CSV successfully rendered.')

    if work_items.count > 10
      mime_msg = format_message(recipient_email, text_report, html_report, csv_items)
      response = @ses_client.send_raw_email(
        source = @email_sender,
        destinations = [recipient_email],
        raw_message = {
          data: mime_msg.to_s
        }
      )
    else
      @ses_client.send_email(
        source = @email_sender,
        destination = { to_addresses: [recipient_email] },
        message = {
          subject: { data: 'Work items' },
          body: {
            html: { data: html_report },
            text: { data: text_report }
          }
        }
      )
    end
  rescue RDSClientError => e
    @logger.exception(
      "Couldn't get work items from storage. Here's why: %s", e
    )
    response = 'A storage error occurred.'
    result = 500
  rescue ClientError => e
    @logger.exception(
      "Couldn't send email. Here's why: %s: %s",
      e.response['Error']['Code'], e.response['Error']['Message']
    )
    response = 'An email error occurred.'
    result = 500
  ensure
    return response.to_json, result
  end
end
