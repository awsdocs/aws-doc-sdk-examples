use std::{
    fmt::Display,
    time::{Duration, SystemTime},
};

use anyhow::anyhow;
use aws_sdk_autoscaling::{
    error::DisplayErrorContext,
    types::{Activity, AutoScalingGroup, LaunchTemplateSpecification},
};
use aws_sdk_ec2::types::RequestLaunchTemplateData;
use tokio_stream::StreamExt;

const LAUNCH_TEMPLATE_NAME: &str = "SDK_Code_Examples_EC2_Autoscaling_template_from_Rust_SDK";
const AUTOSCALING_GROUP_NAME: &str = "SDK_Code_Examples_EC2_Autoscaling_Group_from_Rust_SDK";
const MAX_WAIT: Duration = Duration::from_secs(5);

pub struct AutoScalingScenario {
    ec2: aws_sdk_ec2::Client,
    autoscaling: aws_sdk_autoscaling::Client,
    launch_template_arn: String,
    auto_scaling_group_name: String,
}

impl Display for AutoScalingScenario {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.write_fmt(format_args!(
            "\tLaunch Template ID: {}\n",
            self.launch_template_arn
        ))?;
        f.write_fmt(format_args!(
            "\tScaling Group Name: {}\n",
            self.auto_scaling_group_name
        ))?;

        Ok(())
    }
}

pub struct AutoScalingScenarioDescription {
    instances: Result<Vec<String>, anyhow::Error>,
    activities: Result<Vec<Activity>, anyhow::Error>,
}

impl Display for AutoScalingScenarioDescription {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.write_str("\t         Instances:\n")?;
        match &self.instances {
            Ok(instances) => {
                for instance in instances {
                    f.write_fmt(format_args!("\t\t- {instance}\n"))?;
                }
            }
            Err(e) => f.write_fmt(format_args!("\t\t! {e}"))?,
        }

        f.write_str("\t        Activities:\n")?;
        match &self.activities {
            Ok(activities) => {
                for activity in activities {
                    f.write_fmt(format_args!(
                        "\t\t- {} {}\n",
                        activity.cause().unwrap_or("(Cause unknown)"),
                        activity.status_message().unwrap_or_default()
                    ))?;
                }
            }
            Err(e) => f.write_fmt(format_args!("\t\t! {e}"))?,
        }

        Ok(())
    }
}

macro_rules! sdk_err {
    ($context:expr) => {
        Err(anyhow!("{}", $context))
    };
    ($context:expr, $err:ident) => {
        Err(anyhow!("{}: {}", $context, DisplayErrorContext(&$err)))
    };
    ($c1:expr, $e1:ident, $c2:expr, $e2:ident) => {
        Err(anyhow!(
            "{}: {}\n{}: {}",
            $c1,
            DisplayErrorContext(&$e1),
            $c2,
            DisplayErrorContext(&$e2)
        ))
    };
}

impl AutoScalingScenario {
    pub async fn prepare_scenario(
        sdk_config: &aws_types::sdk_config::SdkConfig,
    ) -> Result<Self, anyhow::Error> {
        let ec2 = aws_sdk_ec2::Client::new(sdk_config);
        let autoscaling = aws_sdk_autoscaling::Client::new(sdk_config);

        let auto_scaling_group_name = String::from(AUTOSCALING_GROUP_NAME);

        // Before creating any resources, prepare the list of AZs
        let availablity_zones = ec2.describe_availability_zones().send().await;
        if let Err(err) = availablity_zones {
            return sdk_err!("Failed to find AZs", err);
        }

        let availability_zones: Vec<String> = availablity_zones
            .unwrap()
            .availability_zones
            .unwrap_or_default()
            .iter()
            .take(3)
            .map(|z| z.zone_name.clone().unwrap())
            .collect();

        // 1. Create an EC2 launch template that you'll use to create an auto scaling group. Bonus: use SDK with EC2.CreateLaunchTemplate to create the launch template.
        //   * Recommended: InstanceType='t1.micro', ImageId='ami-0ca285d4c2cda3300'
        let create_launch_template = ec2
            .create_launch_template()
            .launch_template_name(LAUNCH_TEMPLATE_NAME)
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
                return sdk_err!("Failed to load launch template");
            }
        };

        // 2. CreateAutoScalingGroup: pass it the launch template you created in step 0. Give it min/max of 1 instance.
        //   You can use EC2.describe_availability_zones() to get a list of AZs (you have to specify an AZ when you create the group).
        //   Wait for instance to launch. Use a waiter if you have one, otherwise DescribeAutoScalingInstances until LifecycleState='InService'
        let create_autoscaling_group = autoscaling
            .create_auto_scaling_group()
            .auto_scaling_group_name(auto_scaling_group_name.as_str())
            .launch_template(
                LaunchTemplateSpecification::builder()
                    .launch_template_id(launch_template_arn.clone())
                    .version("$Latest")
                    .build(),
            )
            .max_size(1)
            .min_size(1)
            .set_availability_zones(Some(availability_zones))
            .send()
            .await;

        if let Err(err) = create_autoscaling_group {
            let delete_launch_template = ec2
                .delete_launch_template()
                .launch_template_id(launch_template_arn.clone())
                .send()
                .await;
            return match delete_launch_template {
                Ok(_) => sdk_err!("Failed to create autoscaling group", err),
                Err(dlt_err) => sdk_err!(
                    "Failed to create autoscaling group",
                    err,
                    "Failed to clean up launch template",
                    dlt_err
                ),
            };
        }

        let scenario = AutoScalingScenario {
            ec2,
            autoscaling: autoscaling.clone(), // Clients are cheap so cloning here to prevent a move is ok.
            auto_scaling_group_name: auto_scaling_group_name.clone(),
            launch_template_arn,
        };

        // snippet-start:[rust.autoscaling.scenario.enable_metrics_collection]
        let enable_metrics_collection = autoscaling
            .enable_metrics_collection()
            .auto_scaling_group_name(auto_scaling_group_name.as_str())
            .granularity("1Minute")
            .set_metrics(Some(vec![
                String::from("GroupMinSize"),
                String::from("GroupMaxSize"),
                String::from("GroupDesiredCapacity"),
                String::from("GroupInServiceInstances"),
                String::from("GroupTotalInstances"),
            ]))
            .send()
            .await;
        // snippet-end:[rust.autoscaling.scenario.enable_metrics_collection]

        match enable_metrics_collection {
            Ok(_) => Ok(scenario),
            Err(err) => {
                scenario.clean_scenario().await?;
                sdk_err!("Failed to enable metrics collections for group", err)
            }
        }
    }

    pub async fn clean_scenario(self) -> Result<(), anyhow::Error> {
        let delete_group = self
            .autoscaling
            .delete_auto_scaling_group()
            .auto_scaling_group_name(self.auto_scaling_group_name.clone())
            .send()
            .await;

        // 14. Delete LaunchTemplate.
        let delete_launch_template = self
            .ec2
            .delete_launch_template()
            .launch_template_id(self.launch_template_arn.clone())
            .send()
            .await;

        match (delete_group, delete_launch_template) {
            (Ok(_), Ok(_)) => Ok(()),
            (Ok(_), Err(e)) => sdk_err!("There was an error cleaning the launch template", e),
            (Err(e), Ok(_)) => sdk_err!("There was an error cleaning the scale group", e),
            (Err(e1), Err(e2)) => sdk_err!(
                "There was an error cleaning the scenario\nScale Group",
                e1,
                "Delete Launch Template",
                e2
            ),
        }
    }

    // snippet-start:[rust.autoscaling.scenario.describe_scenario]
    pub async fn describe_scenario(&self) -> Result<AutoScalingScenarioDescription, anyhow::Error> {
        let instances = self
            .list_instances()
            .await
            .map_err(|e| anyhow!("There was an error listing instances: {e}",));

        // 10. DescribeScalingActivities: list the scaling activities that have occurred for the group so far.
        //   Bonus: use CloudWatch API to get and show some metrics collected for the group.
        //   CW.ListMetrics with Namespace='AWS/AutoScaling' and Dimensions=[{'Name': 'AutoScalingGroupName', 'Value': }]
        //   CW.GetMetricStatistics with Statistics='Sum'. Start and End times must be in UTC!
        let activities = self
            .autoscaling
            .describe_scaling_activities()
            .auto_scaling_group_name(self.auto_scaling_group_name.clone())
            .into_paginator()
            .items()
            .send()
            .collect::<Result<Vec<_>, _>>()
            .await
            .map_err(|e| {
                anyhow!(
                    "There was an error retrieving scaling activities: {}",
                    DisplayErrorContext(&e)
                )
            });

        Ok(AutoScalingScenarioDescription {
            instances,
            activities,
        })
    }
    // snippet-end:[rust.autoscaling.scenario.describe_scenario]

    // snippet-start:[rust.autoscaling.scenario.get_group]
    async fn get_group(&self) -> Result<AutoScalingGroup, anyhow::Error> {
        let describe_auto_scaling_groups = self
            .autoscaling
            .describe_auto_scaling_groups()
            .auto_scaling_group_names(self.auto_scaling_group_name.clone())
            .send()
            .await;

        if let Err(err) = describe_auto_scaling_groups {
            return sdk_err!(
                format!(
                    "Failed to get status of autoscaling group {}",
                    self.auto_scaling_group_name.clone()
                ),
                err
            );
        }

        let auto_scaling_groups = describe_auto_scaling_groups
            .unwrap()
            .auto_scaling_groups
            .unwrap_or_default();
        let auto_scaling_group = auto_scaling_groups.first();

        if auto_scaling_group.is_none() {
            return Err(anyhow!(
                "Could not find autoscaling group {}",
                self.auto_scaling_group_name.clone()
            ));
        }

        Ok(auto_scaling_group.unwrap().clone())
    }
    // snippet-end:[rust.autoscaling.scenario.get_group]

    pub async fn wait_for_stable(&self, size: usize) -> Result<(), anyhow::Error> {
        let mut group = self.get_group().await?;
        let start = SystemTime::now();

        while !group
            .instances
            .as_ref()
            .map(|i| i.len())
            .unwrap_or_default()
            == size
        {
            if SystemTime::now()
                .duration_since(start)
                .unwrap_or(Duration::MAX)
                > MAX_WAIT
            {
                return Err(anyhow!("Exceeded maximum wait duration for stable group"));
            }
            tokio::time::sleep(tokio::time::Duration::from_millis(100)).await;
            group = self.get_group().await?;
        }

        Ok(())
    }

    // snippet-start:[rust.autoscaling.scenario.list_instances]
    pub async fn list_instances(&self) -> Result<Vec<String>, anyhow::Error> {
        // The direct way to list instances is by using DescribeAutoScalingGroup's instances property. However, this returns a Vec<Instance>, as opposed to a Vec<AutoScalingInstanceDetails>.
        // Ok(self.get_group().await?.instances.unwrap_or_default().map(|i| i.instance_id.clone().unwrap_or_default()).filter(|id| !id.is_empty()).collect())

        // Alternatively, and for the sake of example, DescribeAutoScalingInstances returns a list that can be filtered by the client.
        Ok(vec![])
    }
    // snippet-end:[rust.autoscaling.scenario.list_instances]

    pub async fn scale_min_size(&self, size: i32) -> Result<(), anyhow::Error> {
        let update_group = self
            .autoscaling
            .update_auto_scaling_group()
            .auto_scaling_group_name(self.auto_scaling_group_name.clone())
            .min_size(size)
            .send()
            .await;
        if let Err(err) = update_group {
            return sdk_err!(format!("Failed to update group to min size ({size}))"), err);
        }
        Ok(())
    }

    pub async fn scale_max_size(&self, size: i32) -> Result<(), anyhow::Error> {
        // 5. UpdateAutoScalingGroup: update max size to 3.
        let update_group = self
            .autoscaling
            .update_auto_scaling_group()
            .auto_scaling_group_name(self.auto_scaling_group_name.clone())
            .max_size(size)
            .send()
            .await;
        if let Err(err) = update_group {
            return sdk_err!(format!("Failed to update group to max size ({size})"), err);
        }
        Ok(())
    }

    // snippet-start:[rust.autoscaling.scenario.scale_desired_capacity]
    pub async fn scale_desired_capacity(&self, capacity: i32) -> Result<(), anyhow::Error> {
        // 7. SetDesiredCapacity: set desired capacity to 2.
        //   Wait for a second instance to launch.
        let update_group = self
            .autoscaling
            .set_desired_capacity()
            .auto_scaling_group_name(self.auto_scaling_group_name.clone())
            .desired_capacity(capacity)
            .send()
            .await;
        if let Err(err) = update_group {
            return sdk_err!(
                format!("Failed to update group to desired capacity ({capacity}))"),
                err
            );
        }
        Ok(())
    }
    // snippet-end:[rust.autoscaling.scenario.scale_desired_capacity]

    pub async fn scale_group_to_zero(&self) -> Result<(), anyhow::Error> {
        // snippet-start:[rust.autoscaling.scenario.disable_metrics_collection]
        // If this fails it's fine, just means there are extra cloudwatch metrics events for the scale-down.
        let _ = self
            .autoscaling
            .disable_metrics_collection()
            .auto_scaling_group_name(self.auto_scaling_group_name.clone())
            .send()
            .await;
        // snippet-end:[rust.autoscaling.scenario.disable_metrics_collection]

        // 12. DeleteAutoScalingGroup (to delete the group you must stop all instances):
        //   UpdateAutoScalingGroup with MinSize=0
        let update_group = self
            .autoscaling
            .update_auto_scaling_group()
            .auto_scaling_group_name(self.auto_scaling_group_name.clone())
            .min_size(0)
            .desired_capacity(0)
            .send()
            .await;
        if let Err(err) = update_group {
            return sdk_err!("Failed to update group for scaling down", err);
        }

        let stable = self.wait_for_stable(0).await;
        if let Err(err) = stable {
            return Err(anyhow!(
                "Error while waiting for group to be stable on scale down: {err}"
            ));
        }

        Ok(())
    }

    // snippet-start:[rust.autoscaling.scenario.terminate_some_instance]
    pub async fn terminate_some_instance(&self) -> Result<(), anyhow::Error> {
        // Retrieve a list of instances in the auto scaling group.
        let instances = self.get_group().await?.instances.unwrap_or_default();
        // Or use other logic to find an instance to terminate.
        let instance = instances.first();
        if let Some(instance) = instance {
            let termination = self
                .ec2
                .terminate_instances()
                .instance_ids(instance.instance_id().unwrap_or_default())
                .send()
                .await;
            if let Err(err) = termination {
                sdk_err!("There was a problem terminating an instance", err)
            } else {
                Ok(())
            }
        } else {
            Err(anyhow!("There was no instance to terminate"))
        }
    }
    // snippet-end:[rust.autoscaling.scenario.terminate_some_instance]
}
