# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam_ruby_example_show_admins'

describe '#user_has_admin_policy?' do
  let(:admin_access) { 'AdministratorAccess' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        get_account_authorization_details: {
          user_detail_list: [
            {
              user_policy_list: [
                {
                  policy_name: admin_access
                }
              ]
            }
          ]
        }
      }
    )
  end
  let(:user) { iam_client.get_account_authorization_details.user_detail_list[0] }

  it 'checks whether the user is associated with an administrator policy' do
    expect(user_has_admin_policy?(user, admin_access)).to be(true)
  end
end

describe '#user_has_attached_policy?' do
  let(:admin_access) { 'AdministratorAccess' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        get_account_authorization_details: {
          user_detail_list: [
            {
              user_name: 'my-user',
              attached_managed_policies: [
                {
                  policy_name: admin_access
                }
              ]
            }
          ]
        }
      }
    )
  end
  let(:user) { iam_client.get_account_authorization_details.user_detail_list[0] }

  it 'checks whether the user has an attached administrator policy' do
    expect(user_has_attached_policy?(user, admin_access)).to be(true)
  end
end

describe '#group_has_admin_policy?' do
  let(:admin_access) { 'AdministratorAccess' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_groups_for_user: {
          groups: [
            {
              group_name: 'my-group',
              path: '/',
              group_id: 'AGPAIFFQAVRFFEXAMPLE',
              arn: 'arn:aws:iam::111111111111:group/my-group',
              create_date: Time.now
            }
          ]
        },
        list_group_policies: {
          policy_names: [
            admin_access
          ]
        }
      }
    )
  end
  let(:group) { iam_client.list_groups_for_user(user_name: 'my-user').groups[0] }

  it 'checks whether the group is associated with an administrator policy' do
    expect(group_has_admin_policy?(iam_client, group, admin_access)).to be(true)
  end
end

describe '#group_has_attached_policy?' do
  let(:admin_access) { 'AdministratorAccess' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_groups_for_user: {
          groups: [
            {
              group_name: 'my-group',
              path: '/',
              group_id: 'AGPAIFFQAVRFFEXAMPLE',
              arn: 'arn:aws:iam::111111111111:group/my-group',
              create_date: Time.now
            }
          ]
        },
        list_attached_group_policies: {
          attached_policies: [
            policy_name: admin_access
          ]
        }
      }
    )
  end
  let(:group) { iam_client.list_groups_for_user(user_name: 'my-user').groups[0] }

  it 'checks whether the group has an attached administrator policy' do
    expect(group_has_attached_policy?(iam_client, group, admin_access)).to be(true)
  end
end

describe '#user_has_admin_from_group?' do
  let(:admin_access) { 'AdministratorAccess' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        get_account_authorization_details: {
          user_detail_list: [
            {
              user_name: 'my-user',
            }
          ]
        },
        list_groups_for_user: {
          groups: [
            {
              group_name: 'my-group',
              path: '/',
              group_id: 'AGPAIFFQAVRFFEXAMPLE',
              arn: 'arn:aws:iam::111111111111:group/my-group',
              create_date: Time.now
            }
          ]
        },
        list_group_policies: {
          policy_names: [
            admin_access
          ]
        },
        list_attached_group_policies: {
          attached_policies: [
            policy_name: admin_access
          ]
        }
      }
    )
  end
  let(:user) { iam_client.get_account_authorization_details.user_detail_list[0] }

  it 'checks whether the user\'s groups are associated with an administrator policy' do
    expect(user_has_admin_from_group?(iam_client, user, admin_access)).to be(true)
  end
end

describe '#is_user_admin?' do
  let(:admin_access) { 'AdministratorAccess' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        get_account_authorization_details: {
          user_detail_list: [
            {
              user_policy_list: [
                {
                  policy_name: admin_access
                }
              ],
              attached_managed_policies: [
                {
                  policy_name: admin_access
                }
              ]
            }
          ]
        },
        list_groups_for_user: {
          groups: [
            {
              group_name: 'my-group',
              path: '/',
              group_id: 'AGPAIFFQAVRFFEXAMPLE',
              arn: 'arn:aws:iam::111111111111:group/my-group',
              create_date: Time.now
            }
          ]
        },
        list_group_policies: {
          policy_names: [
            admin_access
          ]
        },
        list_attached_group_policies: {
          attached_policies: [
            policy_name: admin_access
          ]
        }
      }
    )
  end
  let(:user) { iam_client.get_account_authorization_details.user_detail_list[0] }

  it 'checks whether the user is associated with an administrator policy' do
    expect(is_user_admin?(iam_client, user, admin_access)).to be(true)
  end
end

describe '#get_admin_count' do
  let(:admin_access) { 'AdministratorAccess' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        get_account_authorization_details: {
          user_detail_list: [
            {
              user_policy_list: [
                {
                  policy_name: admin_access
                }
              ],
              attached_managed_policies: [
                {
                  policy_name: admin_access
                }
              ]
            }
          ]
        },
        list_groups_for_user: {
          groups: [
            {
              group_name: 'my-group',
              path: '/',
              group_id: 'AGPAIFFQAVRFFEXAMPLE',
              arn: 'arn:aws:iam::111111111111:group/my-group',
              create_date: Time.now
            }
          ]
        },
        list_group_policies: {
          policy_names: [
            admin_access
          ]
        },
        list_attached_group_policies: {
          attached_policies: [
            policy_name: admin_access
          ]
        }
      }
    )
  end
  let(:users) { iam_client.get_account_authorization_details.user_detail_list }

  it 'counts how many users are associated with an administrator policy' do
    expect(get_admin_count(iam_client, users, admin_access)).to eq(1)
  end
end
