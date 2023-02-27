// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace IAMActions;

public class IAMWrapper
{
    private readonly IAmazonIdentityManagementService _IAMService;

    /// <summary>
    /// Constructor for the IAMWrapper class.
    /// </summary>
    /// <param name="IAMService">An IAM client object.</param>
    public IAMWrapper(IAmazonIdentityManagementService IAMService)
    {
        _IAMService = IAMService;
    }

    // snippet-start:[IAM.dotnetv3.AttachRolePolicy]
    /// <summary>
    /// Attach an AWS Identity Management policy to a role.
    /// </summary>
    /// <param name="policyArn">The policy to attach.</param>
    /// <param name="roleName">The role to which the policy will be attached.</param>
    /// <returns>A Boolean value indicating the success of the action.</returns>
    public async Task<bool> AttachRolePolicyAsync(string policyArn, string roleName)
    {
        var client = new AmazonIdentityManagementServiceClient();
        var response = await _IAMService.AttachRolePolicyAsync(new AttachRolePolicyRequest
        {
            PolicyArn = policyArn,
            RoleName = roleName,
        });

        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.AttachRolePolicy]

    // snippet-start:[IAM.dotnetv3.CreateAccessKey]
    public async Task<AccessKey> CreateAccessKeyAsync(string userName)
    {
        var response = await _IAMService.CreateAccessKeyAsync(new CreateAccessKeyRequest
        {
            UserName = userName,
        });

        return response.AccessKey;

    }

    // snippet-end:[IAM.dotnetv3.CreateAccessKey]

    // snippet-start:[IAM.dotnetv3.CreateGroup]
    public async Task<Group> CreateGroup(string groupName)
    {
        var response = await _IAMService.CreateGroupAsync(new CreateGroupRequest { GroupName = groupName });
        return response.Group;
    }

    // snippet-end:[IAM.dotnetv3.CreateGroup]

    // snippet-start:[IAM.dotnetv3.CreatePolicy]
    public async Task<ManagedPolicy> CreatePolicyAsync(string policyName, string policyDocument)
    {
        var response = await _IAMService.CreatePolicyAsync(new CreatePolicyRequest
        {
            PolicyDocument = policyDocument,
            PolicyName = policyName,
        });

        return response.Policy;
    }

    // snippet-end:[IAM.dotnetv3.CreatePolicy]

    // snippet-start:[IAM.dotnetv3.CreateRole]
    public async Task<string> CreateRoleAsync(string roleName, string rolePolicyDocument)
    {
        var request = new CreateRoleRequest
        {
            RoleName = roleName,
            AssumeRolePolicyDocument = rolePolicyDocument,
        };

        var response = await _IAMService.CreateRoleAsync(request);
        return response.Role.Arn;
    }

    // snippet-end:[IAM.dotnetv3.CreateRole]

    // snippet-start:[IAM.dotnetv3.CreateServiceLinkedRole]
    public async Task<Role> CreateServiceLinkedRoleAsync(string serviceName, string description)
    {
        var request = new CreateServiceLinkedRoleRequest
        {
            AWSServiceName = serviceName,
            Description = description
        };

        var response = await _IAMService.CreateServiceLinkedRoleAsync(request);
        return response.Role;
    }

    // snippet-start:[IAM.dotnetv3.CreateServiceLinkedRole]

    // snippet-start:[IAM.dotnetv3.CreateUser]
    public async Task<User> CreateUserAsync(string userName)
    {
        var response = await _IAMService.CreateUserAsync(new CreateUserRequest { UserName = userName });
        return response.User;
    }

    // snippet-end:[IAM.dotnetv3.CreateUser]

    // snippet-start:[IAM.dotnetv3.DeleteAccessKey]
    public async Task<bool> DeleteAccessKeyAsync(string accessKeyId, string userName)
    {
        var response = await _IAMService.DeleteAccessKeyAsync(new DeleteAccessKeyRequest
        {
            AccessKeyId = accessKeyId,
            UserName = userName,
        });

        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.DeleteAccessKey]

    // snippet-start:[IAM.dotnetv3.DeletePolicy]
    public async Task<bool> DeletePolicyAsync(string policyArn)
    {
        var response = await _IAMService.DeletePolicyAsync(new DeletePolicyRequest { PolicyArn = policyArn });
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.DeletePolicy]

    // snippet-start:[IAM.dotnetv3.DeleteRole]
    public async Task<bool> DeleteRoleAsync(string roleName)
    {
        var response = await _IAMService.DeleteRoleAsync(new DeleteRoleRequest {  RoleName = roleName });
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.DeleteRole]

    // snippet-start:[IAM.dotnetv3.DeleteRolePolicy]
    public async Task<bool> DeleteRolePolicyAsync(string roleName, string policyName)
    {
        var response = await _IAMService.DeleteRolePolicyAsync(new DeleteRolePolicyRequest
        {
            PolicyName = policyName,
            RoleName = roleName,
        });

        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.DeleteRolePolicy]

    // snippet-start:[IAM.dotnetv3.DeleteUser]
    public async Task<bool> DeleteUserAsync(string userName)
    {
        var response = await _IAMService.DeleteUserAsync(new DeleteUserRequest { UserName = userName });

        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.DeleteUser]

    // snippet-start:[IAM.dotnetv3.DeleteUserPolicy]
    public async Task<bool> DeleteUserPolicyAsync(string policyName, string userName)
    {
        var response = await _IAMService.DeleteUserPolicyAsync(new DeleteUserPolicyRequest { PolicyName = policyName, UserName = userName });

        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.DeleteUserPolicy]

    // snippet-start:[IAM.dotnetv3.DetachRolePolicy]
    public async Task<bool> DetachRolePolicyAsync(string policyArn, string roleName)
    {
        var response = await _IAMService.DetachRolePolicyAsync(new DetachRolePolicyRequest
        {
            PolicyArn = policyArn,
            RoleName = roleName,
        });

        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.DetachRolePolicy]

    // snippet-start:[IAM.dotnetv3.GetAccountPasswordPolicy]
    public async Task<PasswordPolicy> GetAccountPasswordPolicyAsync()
    {
        var response = await _IAMService.GetAccountPasswordPolicyAsync(new GetAccountPasswordPolicyRequest());
        return response.PasswordPolicy;
    }

    // snippet-end:[IAM.dotnetv3.GetAccountPasswordPolicy]

    // snippet-start:[IAM.dotnetv3.GetPolicy]
    public async Task<ManagedPolicy> GetPolicyAsync(string policyArn)
    {

        var response = await _IAMService.GetPolicyAsync(new GetPolicyRequest { PolicyArn = policyArn });
        return response.Policy;
    }

    // snippet-end:[IAM.dotnetv3.GetPolicy]

    // snippet-start:[IAM.dotnetv3.GetRole]
    public async Task<Role> GetRoleAsync(string roleName)
    {
        var response = await _IAMService.GetRoleAsync(new GetRoleRequest
        {
            RoleName = roleName,
        });

        return response.Role;
    }

    // snippet-end:[IAM.dotnetv3.GetRole]

    // snippet-start:[IAM.dotnetv3.ListAttachedRolePolicies]
    public async Task<List<AttachedPolicyType>> ListAttachedRolePoliciesAsync(string roleName)
    {
        var attachedPolicies = new List<AttachedPolicyType>();
        var attachedRolePoliciesPaginator = _IAMService.Paginators.ListAttachedRolePolicies(new ListAttachedRolePoliciesRequest { RoleName = roleName });

        await foreach(var response in attachedRolePoliciesPaginator.Responses)
        {
            attachedPolicies.AddRange(response.AttachedPolicies);
        }

        return attachedPolicies;
    }

    // snippet-end:[IAM.dotnetv3.ListAttachedRolePolicies]

    // snippet-start:[IAM.dotnetv3.ListGroups]
    public async Task<List<Group>> ListGroupsAsync()
    {
        var groupsPaginator = _IAMService.Paginators.ListGroups(new ListGroupsRequest());
        var groups = new List<Group>();

        await foreach(var response in groupsPaginator.Responses)
        {
            groups.AddRange(response.Groups);
        }

        return groups;
    }

    // snippet-end:[IAM.dotnetv3.ListGroups]

    // snippet-start:[IAM.dotnetv3.ListPolicies]
    public async Task<List<ManagedPolicy>> ListPoliciesAsync()
    {
        var listPoliciesPaginator = _IAMService.Paginators.ListPolicies(new ListPoliciesRequest());
        var policies = new List<ManagedPolicy>();

        await foreach(var response in listPoliciesPaginator.Responses)
        {
            policies.AddRange(response.Policies);
        }

        return policies;
    }

    // snippet-end:[IAM.dotnetv3.ListPolicies]

    // snippet-start:[IAM.dotnetv3.ListRolePolicies]
    public async Task<List<string>> ListRolePoliciesAsync(string roleName)
    {
        var listRolePoliciesPaginator = _IAMService.Paginators.ListRolePolicies(new ListRolePoliciesRequest { RoleName = roleName });
        var policyNames = new List<string>();

        await foreach(var response in listRolePoliciesPaginator.Responses)
        {
            policyNames.AddRange(response.PolicyNames);
        }

        return policyNames;
    }

    // snippet-end:[IAM.dotnetv3.ListRolePolicies]

    // snippet-start:[IAM.dotnetv3.ListRoles]
    public async Task<List<Role>> ListRolesAsync()
    {
        var listRolesPaginator = _IAMService.Paginators.ListRoles(new ListRolesRequest());
        var roles = new List<Role>();

        await foreach (var response in listRolesPaginator.Responses)
        {
            roles.AddRange(response.Roles);
        }

        return roles;
    }

    // snippet-end:[IAM.dotnetv3.ListRoles]

    // snippet-start:[IAM.dotnetv3.ListSAMLProviders]
    public async Task<List<SAMLProviderListEntry>> ListSAMLProvidersAsync()
    {
        var response = await _IAMService.ListSAMLProvidersAsync(new ListSAMLProvidersRequest());
        return response.SAMLProviderList;
    }

    // snippet-end:[IAM.dotnetv3.ListSAMLProviders]

    // snippet-start:[IAM.dotnetv3.ListUsers]
    public async Task<List<User>> ListUsersAsync()
    {
        var listUsersPaginator = _IAMService.Paginators.ListUsers(new ListUsersRequest());
        var users = new List<User>();

        await foreach(var response in listUsersPaginator.Responses)
        {
            users.AddRange(response.Users);
        }

        return users;
    }

    // snippet-end:[IAM.dotnetv3.ListUsers]

    // snippet-start:[IAM.dotnetv3.PutGroupPolicy]
    public async Task<bool> PutGroupPolicyAsync(string groupName, string policyName, string policyDocument)
    {
        var request = new PutGroupPolicyRequest
        {
            GroupName = groupName,
            PolicyName = policyName,
            PolicyDocument = policyDocument
        };

        var response = await _IAMService.PutGroupPolicyAsync(request);
        return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
    }

    // snippet-end:[IAM.dotnetv3.PutGroupPolicy]
}