// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef EC2_EXAMPLES_EC2_SAMPLES_H
#define EC2_EXAMPLES_EC2_SAMPLES_H

#include <aws/core/Aws.h>
#include <aws/ec2/model/Tag.h>


namespace AwsDoc {
    namespace EC2 {
        //! Allocate an Elastic IP address and associate it with an Amazon Elastic Compute Cloud
        //! (Amazon EC2) instance.
        /*!
          \param instanceID: An EC2 instance ID.
          \param publicIPAddress[out]: String to return the public IP address.
          \param[out] allocationID: String to return the allocation ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool allocateAndAssociateAddress(const Aws::String &instanceId, Aws::String &publicIPAddress,
                                         Aws::String &allocationID,
                                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Associate an Elastic IP address with an EC2 instance.
        /*!
          \param instanceId: An EC2 instance ID.
          \param allocationId: An Elastic IP allocation ID.
          \param[out] associationID: String to receive the association ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: True if the address was associated with the instance; otherwise, false.
         */
        bool
        associateAddress(const Aws::String &instanceId, const Aws::String &allocationId, Aws::String &associationID,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Authorize ingress to an Amazon Elastic Compute Cloud (Amazon EC2) group.
        /*!
          \param groupID: The EC2 group ID.
          \param clientConfiguration: The ClientConfiguration object.
          \return bool: True if the operation was successful, false otherwise.
         */
        bool
        authorizeSecurityGroupIngress(const Aws::String &groupID,
                                      const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an EC2 instance key pair.
        /*!
          \param keyPairName: A name for a key pair.
          \param keyFilePath: File path where the credentials are stored. Ignored if it is an empty string;
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createKeyPair(const Aws::String &keyPairName, const Aws::String &keyFilePath,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create a security group.
        /*!
          \param groupName: A security group name.
          \param description: A description.
          \param vpcID: A virtual private cloud (VPC) ID.
          \param[out] groupIDResult: A string to receive the group ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createSecurityGroup(const Aws::String &groupName,
                                 const Aws::String &description,
                                 const Aws::String &vpcID,
                                 Aws::String &groupIDResult,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Add or overwrite only the specified tags for the specified Amazon Elastic Compute Cloud (Amazon EC2) resource or resources.
        /*!
          \param resources: The resources for the tags.
          \param tags: Map of tag keys and values.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createTags(const Aws::Vector<Aws::String> &resources,
                        const Aws::Vector<Aws::EC2::Model::Tag> &tags,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an Amazon EC2 key pair.
        /*!
          \param keyPairName: A name for a key pair.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteKeyPair(const Aws::String &keyPairName,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete a security group.
        /*!
          \param securityGroupID: A security group ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteSecurityGroup(const Aws::String &securityGroupID,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all Elastic IP addresses.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        describeAddresses(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! DescribeAvailabilityZones
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
        */
        int describeAvailabilityZones(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all EC2 instances associated with an account.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        describeInstances(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all EC2 instance key pairs.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        describeKeyPairs(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all EC2 Regions and Availability Zones.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool describeRegions(
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe all EC2 security groups, or a specific group.
        /*!
          \param groupID: A group name, ignored if empty.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool describeSecurityGroups(const Aws::String &groupID,
                                    const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Enable detailed monitoring for an EC2 instance.
        /*!
          \param instanceId: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool enableMonitoring(const Aws::String &instanceId,
                              const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Disable monitoring for an EC2 instance.
        /*!
          \param instanceId: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool disableMonitoring(const Aws::String &instanceId,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Reboot an EC2 instance.
        /*!
          \param instanceID: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool rebootInstance(const Aws::String &instanceId,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Release an Elastic IP address.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool releaseAddress(const Aws::String &allocationID,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Launch an EC2 instance.
        /*!
          \param instanceName: A name for the EC2 instance.
          \param amiId: An Amazon Machine Image (AMI) identifier.
          \param[out] instanceID: String to return the instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool runInstance(const Aws::String &instanceName,
                         const Aws::String &amiId,
                         Aws::String &instanceID,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Start an EC2 instance.
        /*!
          \param instanceID: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool startInstance(const Aws::String &instanceId,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Stop an EC2 instance.
        /*!
          \param instanceID: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool stopInstance(const Aws::String &instanceId,
                          const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Terminate an EC2 instance.
        /*!
          \param instanceID: An EC2 instance ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool terminateInstances(const Aws::String &instanceID,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

    } // EC2
} // AwsDoc
#endif //EC2_EXAMPLES_EC2_SAMPLES_H
