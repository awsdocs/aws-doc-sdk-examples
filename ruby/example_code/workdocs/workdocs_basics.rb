# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-workdocs'
require 'logger'

class WorkDocsManager
  def initialize(client, logger: Logger.new($stdout))
    @client = client
    @logger = logger
  end

  # snippet-start:[workdocs.Ruby.DescribeUsers]
  # Describes users within an organization
  # @param [String] org_id: The ID of the org.
  def describe_users(org_id)
    resp = @client.describe_users({
                                    organization_id: org_id,
                                    include: 'ALL', # accepts ALL, ACTIVE_PENDING
                                    order: 'ASCENDING', # accepts ASCENDING, DESCENDING
                                    sort: 'USER_NAME', # accepts USER_NAME
                                    fields: %w[FULL_NAME STORAGE_LIMIT USER_STATUS STORAGE_USED] # Corrected field names
                                  })
    resp.users.each do |user|
      @logger.info "First name:  #{user.given_name}"
      @logger.info "Last name:   #{user.surname}"
      @logger.info "Email:       #{user.email_address}"
      @logger.info "Root folder: #{user.root_folder_id}"
      @logger.info ''
    end
    resp.users
  rescue Aws::WorkDocs::Errors::ServiceError => e
    @logger.error "AWS WorkDocs Service Error: #{e.message}"
    exit(1)
  end
  # snippet-end:[workdocs.Ruby.DescribeUsers]

  # snippet-start:[workdocs.Ruby.DescribeRootFolders]
  # Retrieves the root folder for a user by email
  # @param users [Array<Types::User>] A list of users selected from API response
  # @param user_email [String] The email of the user.
  def get_user_folder(users, user_email)
    user = users.find { |user| user.email_address == user_email }
    if user
      user.root_folder_id
    else
      @logger.error "Could not get root folder for user with email address #{user_email}"
      exit(1)
    end
  end

  # Describes the contents of a folder
  # @param [String] folder_id - The Id of the folder to describe.
  def describe_folder_contents(folder_id)
    resp = @client.describe_folder_contents({
                                              folder_id: folder_id, # required
                                              sort: 'NAME', # accepts DATE, NAME
                                              order: 'ASCENDING' # accepts ASCENDING, DESCENDING
                                            })
    resp.documents.each do |doc|
      md = doc.latest_version_metadata
      @logger.info "Name:          #{md.name}"
      @logger.info "Size (bytes):  #{md.size}"
      @logger.info "Last modified: #{doc.modified_timestamp}"
      @logger.info "Doc ID:        #{doc.id}"
      @logger.info "Version ID:    #{md.id}"
      @logger.info ''
    end
  rescue Aws::WorkDocs::Errors::ServiceError => e
    @logger.error "Error listing folder contents: #{e.message}"
    exit(1)
  end
  # snippet-end:[workdocs.Ruby.DescribeRootFolders]
end

# Example usage:
if $PROGRAM_NAME == __FILE__
  user_email = 'someone@somewhere'
  org_id = 'd-123456789c'
  client = Aws::WorkDocs::Client.new
  manager = WorkDocsManager.new(client)
  users = manager.describe_users(org_id)
  user_folder = manager.get_user_folder(users, user_email)
  manager.describe_folder_contents(user_folder) if user_folder
end
