#!/usr/bin/ruby

class WorkItem
  def initialize(attributes)
    @work_item_id = attributes['work_item_id']  # The ID of the item.
    @created_date = attributes['created_date']  # The item's description.
    @guide = attributes['guide']                # The SDK guide the item is associated with.
    @status = attributes['status']              # The current status of the item.
    @username = attributes['username']          # The user assigned to the item.
    @archive = attributes['archive']            # Whether the item is active or archived.
  end
end

# Explore adding the following validations:
# work_item_id INT AUTO_INCREMENT PRIMARY KEY
# created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
# description TEXT,
# guide TEXT,
# status TEXT,
# username VARCHAR(45),
# archive BOOL DEFAULT 0

# The table in question is based on the following setup SQL
# aws rds-data execute-statement ^
#   --resource-arn "arn:aws:rds:us-west-2:0123456789012:cluster:doc-example-aurora-app-docexampleauroraappcluster-1bqmf5EXAMPLE" \
#   --database "auroraappdb" \
#   --secret-arn "arn:aws:secretsmanager:us-west-2:0123456789012:secret:docexampleauroraappsecret8B-6N2njEXAMPLE-111222" \
#   --sql "CREATE TABLE Persons (PersonID int, LastName varchar(255), FirstName varchar(255));"