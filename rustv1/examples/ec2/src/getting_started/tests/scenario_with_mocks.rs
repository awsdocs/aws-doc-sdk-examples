// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use std::{net::Ipv4Addr, path::PathBuf};

use aws_sdk_ec2::{
    operation::{
        allocate_address::AllocateAddressOutput, associate_address::AssociateAddressOutput,
    },
    types::{ArchitectureValues, Image, Instance, InstanceType, KeyPairInfo, SecurityGroup},
};
use aws_sdk_ssm::types::Parameter;
use mockall::predicate::{self, eq};

use crate::{
    ec2::{EC2Error, MockEC2Impl},
    getting_started::{
        scenario::{run, Ec2InstanceScenario},
        util::MockUtilImpl,
    },
    ssm::MockSSMImpl,
};

#[tokio::test]
async fn test_happy_path() {
    let mut mock_ec2 = MockEC2Impl::default();
    let mut mock_ssm = MockSSMImpl::default();
    let mut mock_util = MockUtilImpl::default();

    // create_and_list_key_pairs
    {
        mock_util
            .expect_prompt_key_name()
            .returning(|| Ok("test_key".into()));

        // create
        {
            mock_ec2
                .expect_create_key_pair()
                .with(eq("test_key".to_string()))
                .returning(|_| {
                    Ok((
                        KeyPairInfo::builder()
                            .key_name("test_key")
                            .key_pair_id("kp-12345")
                            .build(),
                        "PEM MATERIAL".into(),
                    ))
                });

            mock_util
                .expect_write_secure()
                .with(
                    eq("test_key"),
                    predicate::function(|path: &PathBuf| path.ends_with("test_key.pem")),
                    eq("PEM MATERIAL".to_string()),
                )
                .returning(|_, _, _| Ok(()));
        }

        mock_util
            .expect_should_list_key_pairs()
            .returning(|| Ok(false));
    }
    // create_security_group
    {
        mock_util
            .expect_prompt_security_group_name()
            .returning(|| Ok("test_group".into()));

        // security_group_manager.create
        {
            mock_ec2
                .expect_create_security_group()
                .with(
                    eq("test_group"),
                    eq("Security group for example: get started with instances."),
                )
                .returning(|_, _| {
                    Ok(SecurityGroup::builder()
                        .group_id("sg-0123")
                        .group_name("test_group")
                        .build())
                });
        }

        mock_util
            .expect_do_get()
            .with(eq("https://checkip.amazonaws.com"))
            .returning(|_| Ok("192.168.0.1".into()));

        mock_util
            .expect_should_add_to_security_group()
            .returning(|| true);

        // security_group_manager.authorize_ingress
        {
            mock_ec2
                .expect_authorize_security_group_ssh_ingress()
                .with(eq("sg-0123"), eq(vec![Ipv4Addr::new(192, 168, 0, 1)]))
                .returning(|_, _| Ok(()));
        }
    }

    // create_instance
    {
        // find_image
        {
            mock_ssm
                .expect_list_path()
                .with(eq("/aws/service/ami-amazon-linux-latest"))
                .returning(|_| {
                    Ok(vec![
                        Parameter::builder()
                            .name("amzn2-ami1")
                            .value("amzn2-ami1")
                            .build(),
                        Parameter::builder()
                            .name("amzn2-ami2")
                            .value("amzn2-ami2")
                            .build(),
                    ])
                });
            mock_ec2
                .expect_list_images()
                .with(eq(vec![
                    Parameter::builder()
                        .name("amzn2-ami1")
                        .value("amzn2-ami1")
                        .build(),
                    Parameter::builder()
                        .name("amzn2-ami2")
                        .value("amzn2-ami2")
                        .build(),
                ]))
                .returning(|_| {
                    Ok(vec![
                        aws_sdk_ec2::types::Image::builder()
                            .architecture(ArchitectureValues::X8664)
                            .image_id("img-1234_x64")
                            .build(),
                        aws_sdk_ec2::types::Image::builder()
                            .architecture(ArchitectureValues::Arm64)
                            .image_id("img-1234_arm")
                            .build(),
                    ])
                });

            mock_util
                .expect_select_scenario_image()
                .with(eq(vec![
                    Image::builder()
                        .architecture(ArchitectureValues::X8664)
                        .image_id("img-1234_x64")
                        .build()
                        .into(),
                    Image::builder()
                        .architecture(ArchitectureValues::Arm64)
                        .image_id("img-1234_arm")
                        .build()
                        .into(),
                ]))
                .returning(|_| {
                    Ok(Image::builder()
                        .architecture(ArchitectureValues::Arm64)
                        .image_id("img-1234_arm")
                        .build()
                        .into())
                });
        }

        mock_ec2
            .expect_list_instance_types()
            .withf(|image| {
                image.architecture == Some(ArchitectureValues::Arm64)
                    && image.image_id() == Some("img-1234_arm")
            })
            .returning(|_| Ok(vec![InstanceType::T1Micro, InstanceType::A1Medium]));

        mock_util
            .expect_select_instance_type()
            .with(eq(vec![InstanceType::T1Micro, InstanceType::A1Medium]))
            .returning(|_| Ok(InstanceType::T1Micro));

        // instance_manager.create
        {
            mock_ec2
                .expect_create_instance()
                .withf(|image_id, instance_type, keypair_info, sgs| {
                    image_id == "img-1234_arm"
                        && *instance_type == InstanceType::T1Micro
                        && keypair_info.key_name() == Some("test_key")
                        && sgs[0].group_id() == Some("sg-0123")
                })
                .returning(|_, _, _, _| Ok("i-01234567".into()));

            mock_ec2
                .expect_describe_instance()
                .with(eq("i-01234567"))
                .returning(|_| {
                    Ok(Instance::builder()
                        .architecture(ArchitectureValues::Arm64)
                        .instance_id("i-01234567")
                        .build())
                });
        }

        mock_ec2
            .expect_wait_for_instance_ready()
            .times(1)
            .with(eq("i-01234567"), eq(None))
            .returning(|_, _| {
                Err(EC2Error::new(
                    "Exceeded max time (60s) waiting for instance to start.",
                ))
            });

        mock_util
            .expect_should_continue_waiting()
            .returning(|| true);

        mock_ec2
            .expect_wait_for_instance_ready()
            .times(1)
            .with(eq("i-01234567"), eq(None))
            .returning(|_, _| Ok(()));

        // display_ssh_info
        {
            mock_util
                .expect_enter_to_continue()
                .returning(|| Ok("".into()));
        }
    }

    // stop_and_start_instance 1
    {
        // stop
        {
            mock_ec2
                .expect_stop_instance()
                .with(eq("i-01234567"))
                .returning(|_| Ok(()));
        }

        // start
        {
            mock_ec2
                .expect_start_instance()
                .with(eq("i-01234567"))
                .returning(|_| Ok(()));
        }

        // display_ssh_info
        {
            mock_util
                .expect_enter_to_continue()
                .returning(|| Ok("".into()));
        }
    }

    // associate_elastic_ip
    {
        // elastic_ip_manager.allocate
        {
            mock_ec2.expect_allocate_ip_address().returning(|| {
                Ok(AllocateAddressOutput::builder()
                    .allocation_id("eip-1234567")
                    .build())
            });
        }

        // elastic_ip_manager.associate
        {
            mock_ec2
                .expect_associate_ip_address()
                .with(eq("eip-1234567"), eq("i-01234567"))
                .returning(|_, _| {
                    Ok(AssociateAddressOutput::builder()
                        .association_id("aid-01234567")
                        .build())
                });
        }

        // display_ssh_info
        {
            mock_util
                .expect_enter_to_continue()
                .returning(|| Ok("".into()));
        }
    }

    // stop_and_start_instance 2
    {
        // stop
        {
            mock_ec2
                .expect_stop_instance()
                .with(eq("i-01234567"))
                .returning(|_| Ok(()));
        }

        // start
        {
            mock_ec2
                .expect_start_instance()
                .with(eq("i-01234567"))
                .returning(|_| Ok(()));
        }

        // display_ssh_info
        {
            mock_util
                .expect_enter_to_continue()
                .returning(|| Ok("".into()));
        }
    }

    // clean_up
    {
        mock_util.expect_should_clean_resources().returning(|| true);

        // elastic_ip_manager.remove
        {
            mock_ec2
                .expect_disassociate_ip_address()
                .with(eq("aid-01234567"))
                .returning(|_| Ok(()));

            mock_ec2
                .expect_deallocate_ip_address()
                .with(eq("eip-1234567"))
                .returning(|_| Ok(()));
        }

        // instance_manager.delete
        {
            mock_ec2
                .expect_delete_instance()
                .with(eq("i-01234567"))
                .returning(|_| Ok(()));
        }

        // security_group_manager.delete
        {
            mock_ec2
                .expect_delete_security_group()
                .with(eq("sg-0123"))
                .returning(|_| Ok(()));
        }

        // key_pair_manager.delete
        {
            mock_ec2
                .expect_delete_key_pair()
                .with(eq("test_key"))
                .returning(|_| Ok(()));

            mock_util
                .expect_remove()
                .withf(|p| format!("{p:?}").contains("test_key.pem"))
                .returning(|_| Ok(()));
        }
    }

    let scenario = Ec2InstanceScenario::new(mock_ec2, mock_ssm, mock_util);

    run(scenario).await;
}

#[tokio::test]
async fn test_unhappy_path_instance_takes_too_long() {
    let mut mock_ec2 = MockEC2Impl::default();
    let mut mock_ssm = MockSSMImpl::default();
    let mut mock_util = MockUtilImpl::default();

    // create_and_list_key_pairs
    {
        mock_util
            .expect_prompt_key_name()
            .returning(|| Ok("test_key".into()));

        // create
        {
            mock_ec2
                .expect_create_key_pair()
                .with(eq("test_key".to_string()))
                .returning(|_| {
                    Ok((
                        KeyPairInfo::builder()
                            .key_name("test_key")
                            .key_pair_id("kp-12345")
                            .build(),
                        "PEM MATERIAL".into(),
                    ))
                });

            mock_util
                .expect_write_secure()
                .with(
                    eq("test_key"),
                    predicate::function(|path: &PathBuf| path.ends_with("test_key.pem")),
                    eq("PEM MATERIAL".to_string()),
                )
                .returning(|_, _, _| Ok(()));
        }

        mock_util
            .expect_should_list_key_pairs()
            .returning(|| Ok(false));
    }
    // create_security_group
    {
        mock_util
            .expect_prompt_security_group_name()
            .returning(|| Ok("test_group".into()));

        // security_group_manager.create
        {
            mock_ec2
                .expect_create_security_group()
                .with(
                    eq("test_group"),
                    eq("Security group for example: get started with instances."),
                )
                .returning(|_, _| {
                    Ok(SecurityGroup::builder()
                        .group_id("sg-0123")
                        .group_name("test_group")
                        .build())
                });
        }

        mock_util
            .expect_do_get()
            .with(eq("https://checkip.amazonaws.com"))
            .returning(|_| Ok("192.168.0.1".into()));

        mock_util
            .expect_should_add_to_security_group()
            .returning(|| true);

        // security_group_manager.authorize_ingress
        {
            mock_ec2
                .expect_authorize_security_group_ssh_ingress()
                .with(eq("sg-0123"), eq(vec![Ipv4Addr::new(192, 168, 0, 1)]))
                .returning(|_, _| Ok(()));
        }
    }

    // create_instance
    {
        // find_image
        {
            mock_ssm
                .expect_list_path()
                .with(eq("/aws/service/ami-amazon-linux-latest"))
                .returning(|_| {
                    Ok(vec![
                        Parameter::builder()
                            .name("amzn2-ami1")
                            .value("amzn2-ami1")
                            .build(),
                        Parameter::builder()
                            .name("amzn2-ami2")
                            .value("amzn2-ami2")
                            .build(),
                    ])
                });
            mock_ec2
                .expect_list_images()
                .with(eq(vec![
                    Parameter::builder()
                        .name("amzn2-ami1")
                        .value("amzn2-ami1")
                        .build(),
                    Parameter::builder()
                        .name("amzn2-ami2")
                        .value("amzn2-ami2")
                        .build(),
                ]))
                .returning(|_| {
                    Ok(vec![
                        aws_sdk_ec2::types::Image::builder()
                            .architecture(ArchitectureValues::X8664)
                            .image_id("img-1234_x64")
                            .build(),
                        aws_sdk_ec2::types::Image::builder()
                            .architecture(ArchitectureValues::Arm64)
                            .image_id("img-1234_arm")
                            .build(),
                    ])
                });

            mock_util
                .expect_select_scenario_image()
                .with(eq(vec![
                    Image::builder()
                        .architecture(ArchitectureValues::X8664)
                        .image_id("img-1234_x64")
                        .build()
                        .into(),
                    Image::builder()
                        .architecture(ArchitectureValues::Arm64)
                        .image_id("img-1234_arm")
                        .build()
                        .into(),
                ]))
                .returning(|_| {
                    Ok(Image::builder()
                        .architecture(ArchitectureValues::Arm64)
                        .image_id("img-1234_arm")
                        .build()
                        .into())
                });
        }

        mock_ec2
            .expect_list_instance_types()
            .withf(|image| {
                image.architecture == Some(ArchitectureValues::Arm64)
                    && image.image_id() == Some("img-1234_arm")
            })
            .returning(|_| Ok(vec![InstanceType::T1Micro, InstanceType::A1Medium]));

        mock_util
            .expect_select_instance_type()
            .with(eq(vec![InstanceType::T1Micro, InstanceType::A1Medium]))
            .returning(|_| Ok(InstanceType::T1Micro));

        // instance_manager.create
        {
            mock_ec2
                .expect_create_instance()
                .withf(|image_id, instance_type, keypair_info, sgs| {
                    image_id == "img-1234_arm"
                        && *instance_type == InstanceType::T1Micro
                        && keypair_info.key_name() == Some("test_key")
                        && sgs[0].group_id() == Some("sg-0123")
                })
                .returning(|_, _, _, _| Ok("i-01234567".into()));

            mock_ec2
                .expect_describe_instance()
                .with(eq("i-01234567"))
                .returning(|_| {
                    Ok(Instance::builder()
                        .architecture(ArchitectureValues::Arm64)
                        .instance_id("i-01234567")
                        .build())
                });
        }

        mock_ec2
            .expect_wait_for_instance_ready()
            .times(1)
            .with(eq("i-01234567"), eq(None))
            .returning(|_, _| {
                Err(EC2Error::new(
                    "Exceeded max time (60s) waiting for instance to start.",
                ))
            });

        mock_util
            .expect_should_continue_waiting()
            .returning(|| false);
    }

    // clean_up
    {
        mock_util.expect_should_clean_resources().returning(|| true);

        // instance_manager.delete
        {
            mock_ec2
                .expect_delete_instance()
                .with(eq("i-01234567"))
                .returning(|_| Ok(()));
        }

        // security_group_manager.delete
        {
            mock_ec2
                .expect_delete_security_group()
                .with(eq("sg-0123"))
                .returning(|_| Ok(()));
        }

        // key_pair_manager.delete
        {
            mock_ec2
                .expect_delete_key_pair()
                .with(eq("test_key"))
                .returning(|_| Ok(()));

            mock_util
                .expect_remove()
                .withf(|p| format!("{p:?}").contains("test_key.pem"))
                .returning(|_| Ok(()));
        }
    }

    let scenario = Ec2InstanceScenario::new(mock_ec2, mock_ssm, mock_util);

    run(scenario).await;
}
