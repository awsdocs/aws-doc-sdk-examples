# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Shows how to get a list of work items from a storage object, render it in both
# HTML and text formats, and use Amazon Simple Email Service (Amazon SES) to send it as
# an email report.
#
# When the list of items is longer than a specified threshold, it is included as a CSV
# attachment to the email instead of in the body of the email itself.

require "logger"
require "erb"
require "json"
require "csv"
require "mail"
require_relative "db_wrapper"
require "mime"

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
    @timestamp = Time.now.strftime("%H:%M on %h %d %Y")
  end

  # Formats the report as a ready-to-send email message, including attachment
  def format_mime_message(email_recipient, text, html, attachment)
    @logger.info("Beginning to format message...")

    mail = Mail.new
    mail.sender = @email_sender
    mail.to = email_recipient
    mail.subject = "Work Items Report"
    mail.content_type = "multipart/mixed"

    html_part = Mail::Part.new do
      content_type  "text/html; charset=UTF-8"
      body          html
    end

    text_part = Mail::Part.new do
      body          text
    end

    mail.part content_type: "multipart/alternative" do |p|
      p.html_part = html_part
      p.text_part = text_part
    end

    mail.attachments[attachment] = File.read(attachment)

    mail.content_type = mail.content_type.gsub("alternative", "mixed")
    mail.charset= "UTF-8"
    mail.content_transfer_encoding = "quoted-printable"
    @logger.info(mail)
    mail
  end

  # Renders work items to CSV format, with the field names as a header row.
  #
  # @param work_items: The work items to include in the CSV output.
  # @return: Work items rendered to a string in CSV format.
  def render_csv(work_items, file_name)
    CSV.open(file_name, "w", headers: work_items.first.keys) do |csv|
      work_items.each do |h|
        csv << h.values
      end
    end
  end

  # @return html_part
  def render_template(template_file, work_items)
    erb = ERB.new(File.read(template_file), trim_mode: "%<>")
    @work_items = work_items
    @timestamp = Time.now.strftime("%H:%M on %h %d %Y")
    erb.result(binding)
  end

  # Gets a list of work items from the database and sends an email.
  # Two versions of the email are included:
  #    1. An HTML version that formats the report as an HTML table by using Flask's
  #       template rendering feature. Email clients that can render HTML receive this
  #       version.
  #    2. A text version that includes the report as a list of Ruby hashes. Email
  #       clients that cannot render HTML receive this version.
  # When your Amazon SES account is in the sandbox, both the sender and recipient
  # email addresses must be registered with Amazon SES.
  # @param recipient_email [String]
  # @return [Integer] An HTTP result code.
  def post_report(recipient_email)
    @logger.info("Getting work items for report.")
    work_items = @db_wrapper.get_work_items
    @logger.debug("Prepared the following items for a report:\n#{work_items}")

    file_name = File.join(File.dirname(__FILE__), "templates", "report.html")
    html_report = render_template(file_name, work_items)
    @logger.debug("HTML report: \n#{html_report}")

    text_report = ""
    work_items.each do |work_item|
      text_report += "\n#{work_item.to_json}"
    end
    @logger.debug("Text report: \n#{text_report}")

    @logger.info("Successfully rendered work_items into HTML & text.")

    csv_file = "data.csv"
    render_csv(work_items, csv_file)
    @logger.info("Successfully saved work items as CSV attachment: #{csv_file}")

    @logger.info("Sending report of #{work_items.count} items to #{recipient_email}")
    if work_items.count > 5
      mime_msg = format_mime_message(recipient_email, text_report, html_report, csv_file)
      @ses_client.send_raw_email({
        source: @email_sender,
        destinations: [recipient_email],
        raw_message: {
                       data: mime_msg.to_s
        }
      })
      204
    else
      @ses_client.send_email({
         source: @email_sender,
         destination: {
                        to_addresses: [recipient_email]
         },
         message: {
                    subject: {
                               data: "Work Items Report"
                    },
                    body: {
                            text: {
                              data: text_report
                            },
                            html: {
                              data: html_report
                            }
                    }
         }
       })
      204
    end
  rescue RDSClientError => e
    @logger.error("Couldn't get work items from storage:\n #{e}")
    500
  rescue StandardError => e
    @logger.error("Couldn't send email: #{e}")
    500
  end
end
