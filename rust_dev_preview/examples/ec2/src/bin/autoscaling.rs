use anyhow::anyhow;
use ec2_code_examples::autoscaling::AutoScalingScenario;
use tracing::info;

async fn show_scenario_description(scenario: &AutoScalingScenario, event: &str) {
    let auto_scaling_scenario_description = scenario.describe_scenario().await;
    match auto_scaling_scenario_description {
        Ok(description) => info!("DescribeAutoScalingInstances: {event}\n{description}"),
        Err(err) => info!("Error in DescribeAutoScalingInstances: {event}\n{err:?}"),
    }
}

#[tokio::main]
async fn main() -> Result<(), anyhow::Error> {
    tracing_subscriber::fmt::init();

    let shared_config = aws_config::from_env().load().await;

    // 1. Create an EC2 launch template that you'll use to create an auto scaling group. Bonus: use SDK with EC2.CreateLaunchTemplate to create the launch template.
    // 2. CreateAutoScalingGroup: pass it the launch template you created in step 0. Give it min/max of 1 instance.
    // 4. EnableMetricsCollection: enable all metrics or a subset.
    let scenario = match AutoScalingScenario::prepare_scenario(&shared_config).await {
        Ok(scenario) => scenario,
        Err(err) => return Err(anyhow!("Failed to initialize scenario: {err:?}")),
    };

    info!("Prepared autoscaling scenario:\n{scenario}");

    // 3. DescribeAutoScalingInstances: show that one instance has launched.
    show_scenario_description(
        &scenario,
        "show that the group was created and one instance has launched",
    )
    .await;

    // 5. UpdateAutoScalingGroup: update max size to 3.
    let scale_max_size = scenario.scale_max_size(3).await;
    if let Err(err) = scale_max_size {
        info!("There was a problem scaling max size\n{err:?}");
    }

    // 6. DescribeAutoScalingGroups: the current state of the group
    show_scenario_description(
        &scenario,
        "show the current state of the group after setting max size",
    )
    .await;

    // 7. SetDesiredCapacity: set desired capacity to 2.
    let scale_desired_capacity = scenario.scale_desired_capacity(2).await;
    if let Err(err) = scale_desired_capacity {
        info!("There was a problem setting desired capacity\n{err:?}");
    }
    //   Wait for a second instance to launch.

    // 8. DescribeAutoScalingInstances: show that two instances are launched.
    show_scenario_description(
        &scenario,
        "show that two instances are launched after setting desired capacity",
    )
    .await;

    // 9. TerminateInstanceInAutoScalingGroup: terminate one of the instances in the group.
    let terminate_and_wait = scenario.terminate_instance_and_wait().await;
    if let Err(err) = terminate_and_wait {
        info!("There was a problem replacing an instance\n{err:?}");
    }

    // 10. DescribeScalingActivities: list the scaling activities that have occurred for the group so far.
    show_scenario_description(
        &scenario,
        "list the scaling activities that have occurred for the group so far",
    )
    .await;

    // 11. DisableMetricsCollection
    // 12. DeleteAutoScalingGroup (to delete the group you must stop all instances):
    // 13. TerminateInstanceInAutoScalingGroup for each instance, specify ShouldDecrementDesiredCapacity=True. Wait for instances to stop.
    // 14. Delete LaunchTemplate.
    scenario.clean_scenario().await?;

    info!("The scenario has been cleaned up!");

    Ok(())
}
