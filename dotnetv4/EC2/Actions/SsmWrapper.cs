// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace EC2Actions;

// snippet-start:[SSM.dotnetv4.SSMActionsClass]
/// <summary>
/// Methods that perform actions from the Simple Systems Management Service.
/// </summary>
public class SsmWrapper
{
    private readonly IAmazonSimpleSystemsManagement _amazonSSM;

    public SsmWrapper(IAmazonSimpleSystemsManagement amazonService)
    {
        _amazonSSM = amazonService;
    }

    /// <summary>
    /// Get a list of parameter values based on the service path.
    /// </summary>
    /// <param name="path">The path used to retrieve parameters.</param>
    /// <returns>Async task.</returns>
    public async Task<List<Parameter>> GetParametersByPath(string path)
    {
        var parameters = new List<Parameter>();
        var request = new GetParametersByPathRequest { Path = path };

        // Get the whole list with a paginator.
        var paginatedParametersByPath = _amazonSSM.Paginators.GetParametersByPath(request);

        await foreach (var parametersPage in paginatedParametersByPath.Responses)
        {
            parameters.AddRange(parametersPage.Parameters);
        }

        // Filter out everything except items that
        // have "amzn2" in the name property.
        var paramList =
            from parameter in parameters
            where parameter.Name.Contains("amzn2")
            select parameter;
        return paramList.ToList();
    }
}

// snippet-end:[SSM.dotnetv4.SSMActionsClass]