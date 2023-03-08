/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef EC2_EXAMPLES_EC2_SAMPLES_H
#define EC2_EXAMPLES_EC2_SAMPLES_H

#include <aws/core/Aws.h>


namespace AwsDoc {
    namespace EC2 {
        //! Allocate an Elastic IP address and associate it with an Amazon Elastic Compute Cloud
        //! (Amazon EC2) instance.
        /*!
          \sa AllocateAndAssociateAddress()
          \param instanceID: An EC2 instance ID.
          \param allocationId: String to return the allocation ID of the address.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool AllocateAndAssociateAddress(const Aws::String &instanceId,
                                         Aws::String &allocationId,
                                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an EC2 instance key pair.
        /*!
          \sa CreateKeyPair()
          \param keyPairName: A name for a key pair.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool CreateKeyPair(const Aws::String &keyPairName,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create a security group.
        /*!
          \sa CreateSecurityGroup()
          \param groupName: A security group name.
          \param description: A description.
          \param vpcID: A virtual private cloud (VPC) ID.
          \param groupIDResult: A string to receive the group ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool CreateSecurityGroup(const Aws::String &groupName,
                                 const Aws::String &description,
                                 const Aws::String &vpcID,
                                 Aws::String &groupIDResult,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon EC2 key pair.
        /*!
          \sa DeleteKeyPair()
          \param keyPairName: A name for a key pair.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DeleteKeyPair(const Aws::String &keyPairName,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete a security group.
        /*!
          \sa DeleteSecurityGroup()
          \param securityGroupID: A security group ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DeleteSecurityGroup(const Aws::String &securityGroupID,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all Elastic IP addresses.
        /*!
          \sa DescribeAddresses()
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        DescribeAddresses(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all EC2 instances associated with an account.
        /*!
          \sa DescribeInstances()
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        DescribeInstances(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all EC2 instance key pairs.
        /*!
          \sa DescribeKeyPairs()
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        DescribeKeyPairs(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all EC2 Regions and Availability Zones.
        /*!
          \sa DescribeRegionsAndZones()
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DescribeRegionsAndZones(
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all EC2 security groups, or a specific group.
        /*!
          \sa DescribeSecurityGroups()
          \param groupID: A group name, ignored if empty.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DescribeSecurityGroups(const Aws::String &groupID,
                                    const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Enable detailed monitoring for an EC2 instance.
        /*!
          \sa EnableMonitoring()
          \param instanceId: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool EnableMonitoring(const Aws::String &instanceId,
                              const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Disable monitoring for an EC2 instance.
        /*!
          \sa DisableMonitoring()
          \param instanceId: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool DisableMonitoring(const Aws::String &instanceId,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Reboot an EC2 instance.
        /*!
          \sa RebootInstance()
          \param instanceID: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool RebootInstance(const Aws::String &instanceId,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Release an Elastic IP address.
        /*!
          \sa ReleaseAddress()
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool ReleaseAddress(const Aws::String &allocationID,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Launch an EC2 instance.
        /*!
          \sa RunInstance()
          \param instanceName: A name for the EC2 instance.
          \param amiId: An Amazon Machine Image (AMI) identifier.
          \param instanceID: String to return the instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool RunInstance(const Aws::String &instanceName,
                         const Aws::String &amiId,
                         Aws::String &instanceID,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Start an EC2 instance.
        /*!
          \sa StartInstance()
          \param instanceID: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool StartInstance(const Aws::String &instanceId,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Stop an EC2 instance.
        /*!
          \sa StopInstance()
          \param instanceID: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool StopInstance(const Aws::String &instanceId,
                          const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Terminate an EC2 instance.
        /*!
          \sa TerminateInstances()
          \param instanceID: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */

        bool TerminateInstances(const Aws::String &instanceID,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

    } // EC2
} // AwsDoc
#endif //EC2_EXAMPLES_EC2_SAMPLES_H
