package scenarios

import (
	"fmt"
	"strconv"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/rds/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/rds/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// TestRunScenario runs the scenario multiple times. The first time, it runs with no
// errors. In subsequent runs, it specifies that each stub in the sequence should
// raise an error and verifies the results.
func TestRunScenario(t *testing.T) {
	scenTest := GetStartedInstancesTest{}
	testtools.RunScenarioTests(&scenTest, t)
}

// GetStartedInstancesTest encapsulates data for a scenario test.
type GetStartedInstancesTest struct {
	Answers            []string
	helper             instancesTestHelper
	dbEngine           string
	parameterGroupName string
	instanceName       string
	dbName             string
}

// SetupDataAndStubs sets up test data and builds the stubs that are used to return
// mocked data.
func (scenTest *GetStartedInstancesTest) SetupDataAndStubs() []testtools.Stub {
	scenTest.dbEngine = "test-engine"
	scenTest.parameterGroupName = "test-parameter-group"
	scenTest.instanceName = "test-instance"
	scenTest.dbName = "test-database"

	families := []string{"family-1", "family-2", "family-3"}
	familyChoice := 1
	params := []types.Parameter{{
		ParameterName: aws.String("auto_increment_param1"), ParameterValue: aws.String("1"),
		AllowedValues: aws.String("1-10"), Description: aws.String("Test desc"), IsModifiable: true,
		DataType: aws.String("integer"),
	}, {
		ParameterName: aws.String("auto_increment_param2"), ParameterValue: aws.String("2"),
		AllowedValues: aws.String("1-10"), Description: aws.String("Test desc"), IsModifiable: true,
		DataType: aws.String("integer"),
	}, {
		ParameterName: aws.String("another_param"), ParameterValue: aws.String("3"),
		AllowedValues: aws.String("1-10"), Description: aws.String("Test desc"), IsModifiable: true,
		DataType: aws.String("integer"),
	}}
	updateParams := make([]types.Parameter, 2)
	copy(updateParams, params[:2])
	updateParams[0].ParameterValue = aws.String("4")
	updateParams[1].ParameterValue = aws.String("5")
	adminName := "admin"
	adminPassword := "password"
	engineVersionChoice := 1
	engineVersion := fmt.Sprintf("%v-%v", scenTest.dbEngine, engineVersionChoice)
	instanceClasses := []string{"micro-1", "micro-2", "micro-3"}
	instanceChoice := 1
	snapshotId := fmt.Sprintf("%v-%v", scenTest.instanceName, scenTest.helper.UniqueId())

	scenTest.helper = instancesTestHelper{}
	scenTest.Answers = []string{
		// CreateParameterGroup
		strconv.Itoa(familyChoice),
		// SetUserParameters
		*updateParams[0].ParameterValue, *updateParams[1].ParameterValue,
		// CreateInstance
		adminName, adminPassword, strconv.Itoa(engineVersionChoice), strconv.Itoa(instanceChoice),
		// CreateSnapshot
		"y",
		// Cleanup
		"y",
	}

	var stubList []testtools.Stub

	// CreateParameterGroup
	stubList = append(stubList, stubs.StubGetParameterGroup(scenTest.parameterGroupName, families[familyChoice],
		&testtools.StubError{Err: &types.DBParameterGroupNotFoundFault{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubGetEngineVersions(scenTest.dbEngine, "", families, nil))
	stubList = append(stubList, stubs.StubCreateParameterGroup(scenTest.parameterGroupName,
		families[familyChoice], "Example parameter group.", nil))
	stubList = append(stubList, stubs.StubGetParameterGroup(scenTest.parameterGroupName, families[familyChoice], nil))

	// SetUserParameters
	stubList = append(stubList, stubs.StubGetParameters(scenTest.parameterGroupName, "", params, nil))
	stubList = append(stubList, stubs.StubUpdateParameters(scenTest.parameterGroupName, updateParams, nil))
	stubList = append(stubList, stubs.StubGetParameters(scenTest.parameterGroupName, "user", updateParams, nil))

	// CreateInstance
	stubList = append(stubList, stubs.StubGetInstance(scenTest.dbName, "",
		&testtools.StubError{Err: &types.DBInstanceNotFoundFault{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubGetEngineVersions(scenTest.dbEngine, families[familyChoice], families, nil))
	stubList = append(stubList, stubs.StubGetOrderableInstances(scenTest.dbEngine, engineVersion, instanceClasses, nil))
	stubList = append(stubList, stubs.StubCreateInstance(scenTest.instanceName, scenTest.dbName,
		scenTest.dbEngine, engineVersion, scenTest.parameterGroupName, instanceClasses[instanceChoice],
		"standard", int32(5), adminName, adminPassword, nil))
	stubList = append(stubList, stubs.StubGetInstance(scenTest.instanceName, "available", nil))

	// DisplayConnection - No stubs needed.

	// CreateSnapshot
	stubList = append(stubList, stubs.StubCreateSnapshot(scenTest.instanceName, snapshotId, nil))
	stubList = append(stubList, stubs.StubGetSnapshot(snapshotId, "available", nil))

	// Cleanup
	stubList = append(stubList, stubs.StubDeleteInstance(scenTest.instanceName, nil))
	stubList = append(stubList, stubs.StubGetInstance(scenTest.instanceName, "",
		&testtools.StubError{Err: &types.DBInstanceNotFoundFault{}, ContinueAfter: true}))
	stubList = append(stubList, stubs.StubDeleteParameterGroup(scenTest.parameterGroupName, nil))

	return stubList
}

// RunSubTest performs a single test run with a set of stubs set up to run with
// or without errors.
func (scenTest *GetStartedInstancesTest) RunSubTest(stubber *testtools.AwsmStubber) {
	mockQuestioner := demotools.MockQuestioner{Answers: scenTest.Answers}
	scenario := NewGetStartedInstances(*stubber.SdkConfig, &mockQuestioner, &scenTest.helper)
	scenario.isTestRun = true
	scenario.Run(scenTest.dbEngine, scenTest.parameterGroupName, scenTest.instanceName, scenTest.dbName)
}

func (scenTest *GetStartedInstancesTest) Cleanup() {}

// instancesTestHelper implements IScenarioHelper for unit testing.
type instancesTestHelper struct {
}

// Pause does nothing during unit testing.
func (helper *instancesTestHelper) Pause(secs int) {}

// UniqueId returns a known unique ID for unit testing.
func (helper *instancesTestHelper) UniqueId() string {
	return "test-id"
}
