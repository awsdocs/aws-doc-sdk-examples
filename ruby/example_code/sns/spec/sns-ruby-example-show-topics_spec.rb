# frozen_string_literal: true

require_relative 'spec_helper'
SNS = Aws::SNS::Resource.new(region: 'us-west-2')

describe '#endpoint' do
  it 'returns the correct ARN corresponding to each topic' do
    topic = simplenotificationservices.simplenotifictionservice('aws')
                .arn('arn:aws:sns:us-west-2:123456789012:MyGroovyTopic')
    expect(topic).to be_kind_of(SimpleNotificationServices::ARN) &
    expect(topic.name).to eq('MyGroovyTopic')

    it 'does not return an ARN not corresponding to the topic' do
      topic = simplenotificationservices.simplenotifictionservice('aws').arn('T#W(*YEHGUIVW#)UGE*JIOVG(WEU)VJP')
      expect(topic).to be_kind_of(SimpleNotificationServices::ARN) &
      expect(topic.name).not_to eq('MyGroovyTopic')
    end
  end
end
