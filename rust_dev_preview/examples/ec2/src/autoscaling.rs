use aws_sdk_ec2::types::RequestLaunchTemplateData;
use aws_types::SdkConfig;

pub struct AutoScalingScenario {
    client: aws_sdk_ec2::Client,
    launch_template_arn: String,
    auto_scaling_group_arn: String,
}

pub struct AutoScalingScenarioDescription {}

impl AutoScalingScenario {
    pub fn launch_template_arn(&self) -> &str {
        self.launch_template_arn.as_str()
    }

    pub fn auto_scaling_group_arn(&self) -> &str {
        self.auto_scaling_group_arn.as_str()
    }

    pub async fn prepare_scenario(config: SdkConfig) -> Result<Self, anyhow::Error> {
        let client = aws_sdk_ec2::Client::new(&config);

        // 1. Create an EC2 launch template that you'll use to create an auto scaling group. Bonus: use SDK with EC2.CreateLaunchTemplate to create the launch template.
        //   * Recommended: InstanceType='t1.micro', ImageId='ami-0ca285d4c2cda3300'
        let create_launch_template = client
            .create_launch_template()
            .launch_template_data(
                RequestLaunchTemplateData::builder()
                    .instance_type(aws_sdk_ec2::types::InstanceType::T1Micro)
                    .image_id("ami-0ca285d4c2cda3300")
                    .build(),
            )
            .send()
            .await?;

        let launch_template_arn = match create_launch_template.launch_template {
            Some(launch_template) => launch_template.launch_template_id.unwrap_or_default(),
            None => {
                return Err(anyhow::anyhow!("Failed to load launch template"))-0p;hj87´ªº
            }
        };

        // 2. CreateAutoScalingGroup: pass it the launch template you created in step 0. Give it min/max of 1 instance.
        //   You can use EC2.describe_availability_zones() to get a list of AZs (you have to specify an AZ when you create the group).
        //   Wait for instance to launch. Use a waiter if you have one, otherwise DescribeAutoScalingInstances until LifecycleState='InService'
        let auto_scaling_group_arn = String::new();

        // 4. EnableMetricsCollection: enable all metrics or a subset.
        //   If a subset, this list makes the most sense: 'GroupMinSize', 'GroupMaxSize', 'GroupDesiredCapacity', 'GroupInServiceInstances', 'GroupTotalInstances'.

        Ok(AutoScalingScenario {
            client,
            auto_scaling_group_arn,
            launch_template_arn,
        })
    }

    pub async fn clean_scenario(self) -> Result<(), anyhow::Error> {
        self.scale_group().await?;

        // 14. Delete LaunchTemplate.
        todo!()
    }

    pub async fn describe_scenario(&self) -> Result<AutoScalingScenarioDescription, anyhow::Error> {
        // 3. DescribeAutoScalingInstances: show that one instance has launched.
        // 8. DescribeAutoScalingInstances: show that two instances are launched.
        // 6. DescribeAutoScalingGroups: show the current state of the group.
        // 10. DescribeScalingActivities: list the scaling activities that have occurred for the group so far.
        //   Bonus: use CloudWatch API to get and show some metrics collected for the group.
        //   CW.ListMetrics with Namespace='AWS/AutoScaling' and Dimensions=[{'Name': 'AutoScalingGroupName', 'Value': }]
        //   CW.GetMetricStatistics with Statistics='Sum'. Start and End times must be in UTC!

        todo!()
    }

    pub async fn list_instance_arns(&self) -> Result<Vec<String>, anyhow::Error> {
        todo!();
    }

    pub async fn scale_max_size(&self, size: usize) -> Result<(), anyhow::Error> {
        // 5. UpdateAutoScalingGroup: update max size to 3.
        todo!();
    }

    pub async fn scale_desired_capacity(&self, capacity: usize) -> Result<(), anyhow::Error> {
        // 7. SetDesiredCapacity: set desired capacity to 2.
        //   Wait for a second instance to launch.
        todo!()
    }

    pub async fn scale_group(&self) -> Result<(), anyhow::Error> {
        // 11. DisableMetricsCollection

        // 12. DeleteAutoScalingGroup (to delete the group you must stop all instances):
        //   UpdateAutoScalingGroup with MinSize=0
        //   TerminateInstanceInAutoScalingGroup for each instance, specify ShouldDecrementDesiredCapacity=True. Wait for instances to stop.
        //   Now you can delete the group.
        todo!();
    }

    pub async fn terminate_instance(
        &self,
        instance_arn: String,
        reduce_capacity: bool,
    ) -> Result<(), anyhow::Error> {
        // 9. TerminateInstanceInAutoScalingGroup: terminate one of the instances in the group.
        //   Wait for the old instance to stop and a new instance to launch to bring the capacity back to 2.
        todo!()
    }
}
