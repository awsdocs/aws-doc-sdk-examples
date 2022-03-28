using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;
using System;

var client = new AmazonIdentityManagementServiceClient();
var request = new CreateServiceLinkedRoleRequest
{
    AWSServiceName = "",
}