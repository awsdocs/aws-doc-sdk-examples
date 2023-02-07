/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * Purpose
 *
 * Demonstrates creating
 *
 * 1.  Specify the name of an existing EC2 launch template.
 * 2.   Or create a new EC2 launch template.
 * 3.  Retrieve a list of EC2 Availability Zones.
 * 4.  Create an EC2 Auto Scaling group with the specified Availability Zone.
 * 5.  Retrieve a description of the EC2 Auto Scaling group.
 * 6.  Check lifecycle state of the EC2 instances using DescribeAutoScalingInstances.
 * 7.  Optionally enable metrics collection for the EC2 Auto Scaling group.
 * 8.  Update the EC2 Auto Scaling group, setting a new maximum size.
 * 9.  Update the EC2 Auto Scaling group, setting a new desired capacity.
 * 10. Terminate an EC2 instance in the EC2 Auto Scaling group.
 * 11. Get a description of activities for the EC2 Auto Scaling group.
 * 12. Optionally list the metrics for the EC2 Auto Scaling group.
 * 13. Disable metrics collection if enabled.
 * 14. Delete the EC2 Auto Scaling group.
 * 15. Delete the EC2 launch template.
 *
 */

#include <iostream>
#include <iomanip>
#include <aws/core/Aws.h>
#include <aws/rds/RDSClient.h>
#include <aws/rds/model/CreateDBInstanceRequest.h>
#include <aws/rds/model/CreateDBParameterGroupRequest.h>
#include <aws/rds/model/DeleteDBInstanceRequest.h>
#include <aws/rds/model/CreateDBSnapshotRequest.h>
#include <aws/rds/model/DeleteDBParameterGroupRequest.h>
#include <aws/rds/model/DescribeDBEngineVersionsRequest.h>
#include <aws/rds/model/DescribeDBInstancesRequest.h>
#include <aws/rds/model/DescribeDBSnapshotsRequest.h>
#include <aws/rds/model/DescribeOrderableDBInstanceOptionsRequest.h>
#include <aws/rds/model/DescribeDBParameterGroupsRequest.h>
#include <aws/rds/model/DescribeDBParametersRequest.h>
#include <aws/rds/model/ModifyDBParameterGroupRequest.h>
#include <aws/core/utils/UUID.h>
#include "rds_samples.h"

namespace AwsDoc {
    namespace RDS {
        const Aws::String DB_ENGINE("mysql");
        const int DB_ALLOCATED_STORAGE = 5;
        const Aws::String DB_STORAGE_TYPE("standard");
        const Aws::String PARAMETER_GROUP_NAME("doc-example-parameter-group");
        const Aws::String DB_INSTANCE_IDENTIFIER("doc-example-instance");
        const Aws::String DB_NAME("docexampledb");
        const Aws::String AUTO_INCREMENT_PREFIX("auto_increment");
        const Aws::String NO_NAME_PREFIX("");
        const Aws::String NO_SOURCE("");
        const Aws::String NO_PARAMETER_GROUP_FAMILY("");

        //! Routine which waits for EC2 instances in an EC2 Auto Scaling group to
        //! complete startup or shutdown.
        /*!
         \sa waitForInstances()
         \param groupName: An EC2 Auto Scaling group name.
         \param autoScalingGroups: Vector to receive 'AutoScalingGroup' records.
         \param client: 'AutoScalingClient' instance.
         \return bool: Successful completion.
         */
        bool getParameters(const Aws::String &parameterGroupName,
                           const Aws::String &namePrefix,
                           const Aws::String &source,
                           Aws::Vector<Aws::RDS::Model::Parameter> &parametersResult,
                           const Aws::RDS::RDSClient &client);

        bool getEngineVersions(const Aws::String &engineName,
                               const Aws::String &parameterGroupFamily,
                               Aws::Vector<Aws::RDS::Model::DBEngineVersion> &engineVersionsResult,
                               const Aws::RDS::RDSClient &client);

        bool getDBInstance(const Aws::String &dbInstanceIdentifier,
                           Aws::RDS::Model::DBInstance &instanceResult,
                           const Aws::RDS::RDSClient &client);

        bool chooseMicroDBInstanceClass(const Aws::String &engine,
                                        const Aws::String &engineVersion,
                                        Aws::String &dbInstanceClass,
                                        const Aws::RDS::RDSClient &client);

        void displayConnection(const Aws::RDS::Model::DBInstance& dbInstance);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa testForEmptyString()
         \param string: A string to test.
         \return bool: True if empty.
         */
        bool testForEmptyString(const Aws::String &string);

        //! Test routine passed as argument to askQuestion routine.
        /*!
         \sa alwaysTrueTest()
         \return bool: Always true.
         */
        bool alwaysTrueTest(const Aws::String &) { return true; }

        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        Aws::String askQuestion(const Aws::String &string,
                                const std::function<bool(
                                        Aws::String)> &test = testForEmptyString);

        //! Command line prompt/response for yes/no question.
        /*!
         \\sa askYesNoQuestion()
         \param string: A question prompt expecting a 'y' or 'n' response.
         \return bool: True if yes.
         */
        bool askYesNoQuestion(const Aws::String &string);

        //! Command line prompt/response utility function for an int result confined to
        //! a range.
        /*!
         \sa askQuestionForIntRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return int: User's response.
         */
        int askQuestionForIntRange(const Aws::String &string, int low,
                                   int high);

        std::vector<int> splitToInts(const Aws::String &string,
                                     char delimiter) {
            std::vector<int> result;
            std::stringstream stringStream(string);
            Aws::String split;
            while (std::getline(stringStream, split, delimiter)) {
                try {
                    result.push_back(std::stoi(split));
                }
                catch (std::exception e) {
                    std::cerr << "askQuestionForIntRange error " << e.what()
                              << std::endl;
                }

            }

            return result;
        }

    }
}

bool AwsDoc::RDS::gettingStartedWithDBInstances(
        const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::RDS::RDSClient client(clientConfig);

    std::cout << "Checking for an existing DB instance parameter group named '" <<
              PARAMETER_GROUP_NAME << "'." << std::endl;
    Aws::String dbParameterGroupFamily;
    bool parameterGroupFound = true;
    {
        Aws::RDS::Model::DescribeDBParameterGroupsRequest request;
        request.SetDBParameterGroupName(PARAMETER_GROUP_NAME);

        Aws::RDS::Model::DescribeDBParameterGroupsOutcome outcome = client.DescribeDBParameterGroups(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "RDS::DescribeDBParameterGroups was successful." << std::endl;
            dbParameterGroupFamily = outcome.GetResult().GetDBParameterGroups()[0].GetDBParameterGroupFamily();
        }
        else if (outcome.GetError().GetErrorType() ==
                 Aws::RDS::RDSErrors::D_B_PARAMETER_GROUP_NOT_FOUND_FAULT) {
            parameterGroupFound = false;
        }
        else {
            std::cerr << "Error with RDS::DescribeDBParameterGroups. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }

    if (!parameterGroupFound) {
        Aws::Vector<Aws::RDS::Model::DBEngineVersion> engineVersions;

        if (!getEngineVersions(DB_ENGINE, NO_PARAMETER_GROUP_FAMILY,
                               engineVersions, client)) {
            return false;
        }

        std::cout << "Here are the available DB parameter group families."
                  << std::endl;
        std::vector<Aws::String> families;
        for (const Aws::RDS::Model::DBEngineVersion &version: engineVersions) {
            Aws::String family = version.GetDBParameterGroupFamily();
            if (std::find(families.begin(), families.end(), family) ==
                families.end()) {
                families.push_back(family);
                std::cout << "  " << families.size() << ": " << family << std::endl;
            }
        }

        int choice = askQuestionForIntRange("Choose a family index: ", 1,
                                            families.size());
        dbParameterGroupFamily = families[choice - 1];
    }
    if (!parameterGroupFound) {
        Aws::RDS::Model::CreateDBParameterGroupRequest request;
        request.SetDBParameterGroupName(PARAMETER_GROUP_NAME);
        request.SetDBParameterGroupFamily(dbParameterGroupFamily);
        request.SetDescription("Example parameter group.");

        Aws::RDS::Model::CreateDBParameterGroupOutcome outcome = client.CreateDBParameterGroup(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "RDS::CreateDBParameterGroup was successful." << std::endl;
        }
        else {
            std::cerr << "Error with RDS::CreateDBParameterGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }

    std::cout << "Let's set some parameter values in your parameter group."
              << std::endl;

    Aws::String marker;
    Aws::Vector<Aws::RDS::Model::Parameter> autoIncrementParameters;
    if (!getParameters(PARAMETER_GROUP_NAME, AUTO_INCREMENT_PREFIX, NO_SOURCE,
                       autoIncrementParameters,
                       client)) {
        return false;
    }

    Aws::Vector<Aws::RDS::Model::Parameter> updateParameters;

    for (Aws::RDS::Model::Parameter &autoIncParameter: autoIncrementParameters) {
        if (autoIncParameter.GetIsModifiable() &&
            (autoIncParameter.GetDataType() == "integer")) {
            std::cout << "The " << autoIncParameter.GetParameterName()
                      << " is described as: " <<
                      autoIncParameter.GetDescription() << "." << std::endl;
            if (autoIncParameter.ParameterValueHasBeenSet()) {
                std::cout << "The current value is "
                          << autoIncParameter.GetParameterValue()
                          << "." << std::endl;
            }
            std::vector<int> splitValues = splitToInts(
                    autoIncParameter.GetAllowedValues(), '-');
            if (splitValues.size() == 2) {
                int newValue = askQuestionForIntRange(
                        Aws::String("Enter a new value int the range ") +
                        autoIncParameter.GetAllowedValues() + ": ",
                        splitValues[0], splitValues[1]);
                autoIncParameter.SetParameterValue(std::to_string(newValue));
                updateParameters.push_back(autoIncParameter);

            }
            else {
                std::cerr << "Error parsing " << autoIncParameter.GetAllowedValues()
                          << std::endl;
            }
        }
    }

    {
        Aws::RDS::Model::ModifyDBParameterGroupRequest request;
        request.SetDBParameterGroupName(PARAMETER_GROUP_NAME);
        request.SetParameters(updateParameters);

        Aws::RDS::Model::ModifyDBParameterGroupOutcome outcome = client.ModifyDBParameterGroup(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "RDS::ModifyDBClusterParameterGroup was successful."
                      << std::endl;
        }
        else {
            std::cerr << "Error with RDS::ModifyDBClusterParameterGroup. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
        }
    }

    std::cout
            << "You can get a list of parameters you've set by specifying a source of 'user'."
            << std::endl;

    Aws::Vector<Aws::RDS::Model::Parameter> userParamaters;
    if (!getParameters(PARAMETER_GROUP_NAME, NO_NAME_PREFIX, "user", userParamaters,
                       client)) {
        return false;
    }

    for (const auto &userParameter: userParamaters) {
        std::cout << "  " << userParameter.GetParameterName() << ", " <<
                  userParameter.GetDescription() << ", parameter value - "
                  << userParameter.GetParameterValue() << std::endl;
    }

    std::cout << "Checking for an existing DB instance." << std::endl;

    Aws::RDS::Model::DBInstance dbInstance;
    if (!getDBInstance(DB_INSTANCE_IDENTIFIER, dbInstance, client)) {
        return false;
    }

    if (!dbInstance.DbInstancePortHasBeenSet()) {
        std::cout << "Let's create a DB instance." << std::endl;
        const Aws::String administratorName = askQuestion(
                "Enter an administrator user name for the database: ");
        const Aws::String administratorPassword = askQuestion(
                "Enter a password for the administrator (at least 8 characters): ");
        Aws::Vector<Aws::RDS::Model::DBEngineVersion> engineVersions;

        if (!getEngineVersions(DB_ENGINE, dbParameterGroupFamily, engineVersions,
                               client)) {
            return false;
        }

        std::cout << "The available engines for your parameter group are:" << std::endl;

        int index = 1;
        for (const Aws::RDS::Model::DBEngineVersion &engineVersion: engineVersions) {
            std::cout << "  " << index << ": " << engineVersion.GetEngineVersion()
                      << std::endl;
            ++index;
        }
        int choice = askQuestionForIntRange("Which engine do you want to use? ", 1,
                                            static_cast<int>(engineVersions.size()));
        const Aws::RDS::Model::DBEngineVersion engineVersion = engineVersions[choice -
                                                                              1];

        Aws::String dbInstanceClass;
        if (!chooseMicroDBInstanceClass(engineVersion.GetEngine(),
                                        engineVersion.GetEngineVersion(),
                                        dbInstanceClass,
                                        client)) {
            return false;
        }

        std::cout << "Creating a DB instance named '" << DB_INSTANCE_IDENTIFIER
                  << "' and database '" << DB_NAME << "'.\n"
                  << "The DB instance is configured to use your custom parameter group '"
                  << PARAMETER_GROUP_NAME << "',\n"
                  << "selected engine version " << engineVersion.GetEngineVersion()
                  << ",\n"
                  << "selected DB instance class '" << dbInstanceClass << "',"
                  << " and " << DB_ALLOCATED_STORAGE << " GiB of " << DB_STORAGE_TYPE
                  << " storage.\nThis typically takes several minutes." << std::endl;

        Aws::RDS::Model::CreateDBInstanceRequest request;
        request.SetDBName(DB_NAME);
        request.SetDBInstanceIdentifier(DB_INSTANCE_IDENTIFIER);
        request.SetDBParameterGroupName(PARAMETER_GROUP_NAME);
        request.SetEngine(engineVersion.GetEngine());
        request.SetEngineVersion(engineVersion.GetEngineVersion());
        request.SetDBInstanceClass(dbInstanceClass);
        request.SetStorageType(DB_STORAGE_TYPE);
        request.SetAllocatedStorage(DB_ALLOCATED_STORAGE);
        request.SetMasterUsername(administratorName);
        request.SetMasterUserPassword(administratorPassword);

        Aws::RDS::Model::CreateDBInstanceOutcome outcome = client.CreateDBInstance(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "RDS::CreateDBInstance was successful." << std::endl;
        }
        else {
            std::cerr << "Error with RDS::CreateDBInstance. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }
    int counter = 0;
    do {
        std::this_thread::sleep_for(std::chrono::seconds(1));
        ++counter;
        if (counter > 800) {
            std::cerr << "Wait for instance to become available timed out ofter "
                      << counter
                      << " seconds." << std::endl;
            return false;
        }

        dbInstance = Aws::RDS::Model::DBInstance();
        if (!getDBInstance(DB_INSTANCE_IDENTIFIER, dbInstance, client)) {
            return false;
        }

        if ((counter % 20) == 0) {
            std::cout << "Current DB instance status is '"
                      << dbInstance.GetDBInstanceStatus()
                      << "' after " << counter << " seconds." << std::endl;
        }
    } while (dbInstance.GetDBInstanceStatus() != "available");

    displayConnection(dbInstance);

    if (askYesNoQuestion("Do you want to create a snapshot of your DB instance (y/n)? "))
    {
        Aws::String snapshotID(DB_INSTANCE_IDENTIFIER + "-" +
                               Aws::String(Aws::Utils::UUID::RandomUUID()).substr(0, 12));
        {
            Aws::RDS::Model::CreateDBSnapshotRequest request;
            request.SetDBInstanceIdentifier(DB_INSTANCE_IDENTIFIER);
            request.SetDBSnapshotIdentifier(snapshotID);

            Aws::RDS::Model::CreateDBSnapshotOutcome outcome = client.CreateDBSnapshot(request);

            if (outcome.IsSuccess()) {
                std::cout << "RDS::CreateDBSnapshot was successful." << std::endl;
            }
            else {
                std::cerr << "Error with RDS::CreateDBSnapshot. " << outcome.GetError().GetMessage()
                          << std::endl;
            }
        }

        std::cout << "Waiting for snapshot to become available." << std::endl;
        std::cout << "This may take a while." << std::endl;

        Aws::RDS::Model::DBSnapshot snapshot;
        counter = 0;
        do{
            std::this_thread::sleep_for(std::chrono::seconds(1));
            ++counter;
            if (counter > 600) {
                std::cerr << "Wait for snapshot to be available timed out ofter " << counter
                          << " seconds." << std::endl;
                return false;
            }
            Aws::RDS::Model::DescribeDBSnapshotsRequest request;
            request.SetDBSnapshotIdentifier(snapshotID);

            Aws::RDS::Model::DescribeDBSnapshotsOutcome outcome = client.DescribeDBSnapshots(request);

            if (outcome.IsSuccess()) {
                snapshot = outcome.GetResult().GetDBSnapshots()[0];
            }
            else {
                std::cerr << "Error with RDS::DescribeDBSnapshots. " << outcome.GetError().GetMessage()
                          << std::endl;
                return false;
            }

            if ((counter % 20) == 0) {
                std::cout << "Current snapshot status is '"
                          << snapshot.GetStatus()
                          << "' after " << counter << " seconds." << std::endl;
            }
        }while (snapshot.GetStatus() != "available");
    }

    bool result = true;
    if (askYesNoQuestion(
            "Do you want to delete the DB instance and parameter group (y/n)? ")) {
        {
            Aws::RDS::Model::DeleteDBInstanceRequest request;
            request.SetDBInstanceIdentifier(DB_INSTANCE_IDENTIFIER);
            request.SetSkipFinalSnapshot(true);
            request.SetDeleteAutomatedBackups(true);

            Aws::RDS::Model::DeleteDBInstanceOutcome outcome = client.DeleteDBInstance(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "RDS::DeleteDBInstance was successful." << std::endl;
            }
            else {
                std::cerr << "Error with RDS::DeleteDBInstance. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                result = false;
            }
        }

        counter = 0;
        do {
            std::this_thread::sleep_for(std::chrono::seconds(1));
            ++counter;
            if (counter > 600) {
                std::cerr << "Wait for instance to delete timed out ofter " << counter
                          << " seconds." << std::endl;
                return false;
            }

            dbInstance = Aws::RDS::Model::DBInstance();
            if (!getDBInstance(DB_INSTANCE_IDENTIFIER, dbInstance, client)) {
                return false;
            }

            if (dbInstance.DBInstanceIdentifierHasBeenSet() && (counter % 20) == 0) {
                std::cout << "Current DB instance status is '"
                          << dbInstance.GetDBInstanceStatus()
                          << "' after " << counter << " seconds." << std::endl;
            }
        } while (dbInstance.DBInstanceIdentifierHasBeenSet());

        {
            Aws::RDS::Model::DeleteDBParameterGroupRequest request;
            request.SetDBParameterGroupName(PARAMETER_GROUP_NAME);

            Aws::RDS::Model::DeleteDBParameterGroupOutcome outcome = client.DeleteDBParameterGroup(
                    request);

            if (outcome.IsSuccess()) {
                std::cout << "RDS::DeleteDBParameterGroup was successful." << std::endl;
            }
            else {
                std::cerr << "Error with RDS::DeleteDBParameterGroup. "
                          << outcome.GetError().GetMessage()
                          << std::endl;
                result = false;
            }
        }
    }

    return result;
}

#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {
    Aws::SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::RDS::gettingStartedWithDBInstances(clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

//! Test routine passed as argument to askQuestion routine.
/*!
 \sa testForEmptyString()
 \param string: A string to test.
 \return bool: True if empty.
 */
bool AwsDoc::RDS::testForEmptyString(const Aws::String &string) {
    if (string.empty()) {
        std::cout << "Enter some text." << std::endl;
        return false;
    }

    return true;
}

//! Command line prompt/response utility function.
/*!
 \\sa askQuestion()
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::RDS::askQuestion(const Aws::String &string,
                                     const std::function<bool(
                                             Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
    } while (!test(result));

    return result;
}

//! Command line prompt/response for yes/no question.
/*!
 \\sa askYesNoQuestion()
 \param string: A question prompt expecting a 'y' or 'n' response.
 \return bool: True if yes.
 */
bool AwsDoc::RDS::askYesNoQuestion(const Aws::String &string) {
    Aws::String resultString = askQuestion(string, [](
            const Aws::String &string1) -> bool {
            bool result = false;
            if (string1.length() == 1) {
                int answer = std::tolower(string1[0]);
                result = (answer == 'y') || (answer == 'n');
            }

            if (!result) {
                std::cout << "Answer 'y' or 'n'." << std::endl;
            }

            return result;
    });

    return std::tolower(resultString[0]) == 'y';
}

//! Command line prompt/response utility function for an int result confined to
//! a range.
/*!
 \sa askQuestionForIntRange()
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return int: User's response.
 */
int AwsDoc::RDS::askQuestionForIntRange(const Aws::String &string, int low,
                                        int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cout << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cout << "\nNot a valid number." << std::endl;
                return false;
            }
    });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}

bool AwsDoc::RDS::getParameters(const Aws::String &parameterGroupName,
                                const Aws::String &namePrefix,
                                const Aws::String &source,
                                Aws::Vector<Aws::RDS::Model::Parameter> &parametersResult,
                                const Aws::RDS::RDSClient &client) {
    Aws::String marker;
    do {
        Aws::RDS::Model::DescribeDBParametersRequest request;
        request.SetDBParameterGroupName(PARAMETER_GROUP_NAME);
        if (!marker.empty()) {
            request.SetMarker(marker);
        }
        if (!source.empty()) {
            request.SetSource(source);
        }

        Aws::RDS::Model::DescribeDBParametersOutcome outcome = client.DescribeDBParameters(
                request);

        if (outcome.IsSuccess()) {
            const Aws::Vector<Aws::RDS::Model::Parameter> &parameters =
                    outcome.GetResult().GetParameters();
            for (const Aws::RDS::Model::Parameter &parameter: parameters) {
                if (!namePrefix.empty()) {
                    if (parameter.GetParameterName().find(AUTO_INCREMENT_PREFIX) == 0) {
                        parametersResult.push_back(parameter);
                    }
                }
                else {
                    parametersResult.push_back(parameter);
                }

            }

            marker = outcome.GetResult().GetMarker();
        }
        else {
            std::cerr << "Error with RDS::DescribeDBParameters. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    } while (!marker.empty());

    return true;
}

bool AwsDoc::RDS::getEngineVersions(const Aws::String &engineName,
                                    const Aws::String &parameterGroupFamily,
                                    Aws::Vector<Aws::RDS::Model::DBEngineVersion> &engineVersionsResult,
                                    const Aws::RDS::RDSClient &client) {
    Aws::RDS::Model::DescribeDBEngineVersionsRequest request;
    request.SetEngine(engineName);
    if (!parameterGroupFamily.empty()) {
        request.SetDBParameterGroupFamily(parameterGroupFamily);
    }

    Aws::RDS::Model::DescribeDBEngineVersionsOutcome outcome = client.DescribeDBEngineVersions(
            request);

    if (outcome.IsSuccess()) {
        engineVersionsResult = outcome.GetResult().GetDBEngineVersions();
    }
    else {
        std::cerr << "Error with RDS::DescribeDBEngineVersionsRequest. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDoc::RDS::getDBInstance(const Aws::String &dbInstanceIdentifier,
                                Aws::RDS::Model::DBInstance &instanceResult,
                                const Aws::RDS::RDSClient &client) {
    Aws::RDS::Model::DescribeDBInstancesRequest request;
    request.SetDBInstanceIdentifier(dbInstanceIdentifier);

    Aws::RDS::Model::DescribeDBInstancesOutcome outcome = client.DescribeDBInstances(
            request);

    bool result = true;
    if (outcome.IsSuccess()) {
        instanceResult = outcome.GetResult().GetDBInstances()[0];
    }
    else if (outcome.GetError().GetErrorType() !=
             Aws::RDS::RDSErrors::D_B_INSTANCE_NOT_FOUND_FAULT) {
        std::cerr << "Error with RDS::DescribeDBInstances. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
        result = false;
    }
    return result;
}

bool AwsDoc::RDS::chooseMicroDBInstanceClass(const Aws::String &engine,
                                             const Aws::String &engineVersion,
                                             Aws::String &dbInstanceClass,
                                             const Aws::RDS::RDSClient &client) {
    std::vector<Aws::String> instanceClasses;
    Aws::String marker;
    do {
        Aws::RDS::Model::DescribeOrderableDBInstanceOptionsRequest request;
        request.SetEngine(engine);
        request.SetEngineVersion(engineVersion);
        if (!marker.empty()) {
            request.SetMarker(marker);
        }

        Aws::RDS::Model::DescribeOrderableDBInstanceOptionsOutcome outcome =
                client.DescribeOrderableDBInstanceOptions(request);

        if (outcome.IsSuccess()) {
            const Aws::Vector<Aws::RDS::Model::OrderableDBInstanceOption> &options =
                    outcome.GetResult().GetOrderableDBInstanceOptions();
            for (const Aws::RDS::Model::OrderableDBInstanceOption &option: options) {
                Aws::String instanceClass = option.GetDBInstanceClass();
                if (instanceClass.find("micro") != std::string::npos) {
                    instanceClasses.push_back(option.GetDBInstanceClass());
                }
            }
            marker = outcome.GetResult().GetMarker();
        }
        else {
            std::cerr << "Error with RDS::DescribeOrderableDBInstanceOptions. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    } while (!marker.empty());

    std::cout << "The available micro DB instance classes for your database engine are:"
              << std::endl;
    for (int i = 0; i < instanceClasses.size(); ++i) {
        std::cout << "   " << i + 1 << ": " << instanceClasses[i] << std::endl;
    }

    int choice = askQuestionForIntRange(
            "Which micro DB instance class do you want to use? ",
            1, static_cast<int>(instanceClasses.size()));
    dbInstanceClass = instanceClasses[choice - 1];
    return true;
}

void AwsDoc::RDS::displayConnection(const Aws::RDS::Model::DBInstance &dbInstance) {
    std::cout << R"(You can now connect to your database using your favorite MySql client.
One way to connect is by using the 'mysql' shell on an Amazon EC2 instance
that is running in the same VPC as your DB instance. Pass the endpoint,
port, and administrator user name to 'mysql' and enter your password
when prompted:)" << std::endl;

    std::cout << "  mysql -h " << dbInstance.GetEndpoint().GetAddress()  << " -P "
    << dbInstance.GetEndpoint().GetPort() << " - u " << dbInstance.GetMasterUsername()
    << " -p" << std::endl;

    std::cout << "For more information, see the User Guide for Amazon RDS:\n"
              <<   "  https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_GettingStarted.CreatingConnecting.MySQL.html#CHAP_GettingStarted.Connecting.MySQL"
                 << std::endl;
}
