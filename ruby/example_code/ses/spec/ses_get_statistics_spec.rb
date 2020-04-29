require_relative 'spec_helper'
sns = Aws::SES::Resource.new(region: 'us-west-2')

module Aws
  describe SimpleEmailServices do
    let(:simpleemailservices) { SimpleEmailServices.simpleemailservices }

# making sure the dp percentages fall within 0.0 and 1.0
    describe '#attempts' do
      it 'confirms a valid dp range' do
        expect(dp).to be >= 0.0 && <= 1.0
        end
# testing to confirm whether 10% is accurately recognized as a valid percentage, and 110% as an invalid one
      it 'recognizes a valid dp percentage' do
        expect(dp(0.1)).to be_valid
        end
      it 'recognizes an invalid dp percentage' do
        expect(dp(1.1)).not_to be_valid
        end
        end

# testing to confirm the correct message is given for a service error
     describe 'an error is returned if get statistics cannot be reached' do
          it 'returns the correct service error and message if we are missing a timestamp' do
          dp.delete('timestamp')
          result = ses.record(dp)
              expect(result).not_to be_success &
              expect(result.dp_id).to eq(nil) &
              expect(result.error_message).to include('timestamp is required')
          end

# testing to make sure the data point API saves and displays the desired data points
     describe 'Send Data Points API' do
          it 'records submitted data points' do
            dps = {
                'timestamp' => '2020-03-13T22:22:00Z',
                'attempts'=> '4',
                'bounces' => '0',
                'complaints' => '0',
                'rejects' => '0'
            }
            post '/dps', JSON.generate(simpleemailsevice.dps)
          end
        end
      end
    end
  end
  end

