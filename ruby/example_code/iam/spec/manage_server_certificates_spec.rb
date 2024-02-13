require "aws-sdk-iam"
require_relative("../manage_server_certificates")
require "rspec"

describe ServerCertificateManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:manager) { ServerCertificateManager.new(iam_client) }

  describe "#list_server_certificate_names", :integ do
    it "logs server certificate names" do
      expect { manager.list_server_certificate_names }.not_to raise_error
      # Further expectations can be added based on the logging output or mocked responses.
    end
  end

  describe "#update_server_certificate_name", :integ do
    it "updates the server certificate name and returns true" do
      current_name = "existing_certificate_name"
      new_name = "new_certificate_name"

      # Assuming the server certificate `existing_certificate_name` exists in the AWS account for this test.
      expect(manager.update_server_certificate_name(current_name, new_name)).to be true
    end
  end

  describe "#delete_server_certificate", :integ do
    it "deletes the server certificate and returns true" do
      name = "certificate_to_delete"

      # Assuming the server certificate `certificate_to_delete` exists in the AWS account for this test.
      expect(manager.delete_server_certificate(name)).to be true
    end
  end
end
