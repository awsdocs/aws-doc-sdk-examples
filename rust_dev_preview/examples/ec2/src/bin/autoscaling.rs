use aws_config::meta::region::RegionProviderChain;
use aws_types::region::Region;
use ec2_code_examples::autoscaling::AutoScalingScenario;
use tracing::info;

async fn show_scenario_description(scenario: &AutoScalingScenario, event: &str) {
    match scenario.describe_scenario().await {
        Ok(description) => info!(description, "DescribeAutoScalingInstances: {event}"),
        Err(err) => info!(err, "Error in DescribeAutoScalingInstances: {event}"),
    }
}

#[tokio::main]
async fn main() -> Result<(), anyhow::Error> {
    tracing_subscriber::fmt::init();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    let shared_config = aws_config::from_env().region(region_provider).load().await;

    // 1. Create an EC2 launch template that you'll use to create an auto scaling group. Bonus: use SDK with EC2.CreateLaunchTemplate to create the launch template.
    // 2. CreateAutoScalingGroup: pass it the launch template you created in step 0. Give it min/max of 1 instance.
    // 4. EnableMetricsCollection: enable all metrics or a subset.
    let scenario = AutoScalingScenario::prepare_scenario(shared_config).await?;

    // 3. DescribeAutoScalingInstances: show that one instance has launched.
    show_scenario_description(&scenario, &"show that one instance has launched").await;

    // 5. UpdateAutoScalingGroup: update max size to 3.
    if let Err(err) = scenario.scale_max_size(3).await {
        info!(err, "there was a problem scaling max size");
    }

    // 6. DescribeAutoScalingGroups: the current state of the group
    show_scenario_description(&scenario, &"show the current state of the group").await;

    // 7. SetDesiredCapacity: set desired capacity to 2.
    if let Err(err) = scenario.scale_desired_capacity(2).await {
        info!(err, "there was a problem setting desired capacity");
    }
    //   Wait for a second instance to launch.

    // 8. DescribeAutoScalingInstances: show that two instances are launched.
    show_scenario_description(&scenario, &"show that two instances are launched").await;

    // 9. TerminateInstanceInAutoScalingGroup: terminate one of the instances in the group.
    match scenario.list_instance_arns().await {
        Ok(instances) => {
            if let Some(instance) = instances.iter().next() {
                scenario.terminate_instance(instance, false).await;
            }
            // Wait for the old instance to stop and a new instance to launch to bring the capacity back to 2.
        }
        Err(err) => {
            info!(err, "error getting Autoscaling Group Instances");
        }
    }

    // 10. DescribeScalingActivities: list the scaling activities that have occurred for the group so far.
    show_scenario_description(
        &scenario,
        &"list the scaling activities that have occurred for the group so far",
    )
    .await;

    // 11. DisableMetricsCollection
    // 12. DeleteAutoScalingGroup (to delete the group you must stop all instances):
    // 13. TerminateInstanceInAutoScalingGroup for each instance, specify ShouldDecrementDesiredCapacity=True. Wait for instances to stop.
    // 14. Delete LaunchTemplate.
    scenario.clean_scenario().await?;

    Ok(())
}
