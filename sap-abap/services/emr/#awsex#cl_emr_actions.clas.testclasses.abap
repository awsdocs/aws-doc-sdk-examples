" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_emr_actions DEFINITION DEFERRED.
CLASS /awsex/cl_emr_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_emr_actions.

CLASS ltc_awsex_cl_emr_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_emr TYPE REF TO /aws1/if_emr.
    CLASS-DATA ao_emr_actions TYPE REF TO /awsex/cl_emr_actions.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_ec2 TYPE REF TO /aws1/if_ec2.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.

    " Test resources - created once, shared across all tests
    CLASS-DATA av_cluster_id TYPE /aws1/emrclusterid.
    CLASS-DATA av_step_id TYPE /aws1/emrstepid.
    CLASS-DATA av_log_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_emr_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_ec2_role_name TYPE /aws1/iamrolenametype.
    CLASS-DATA av_emr_role_arn TYPE /aws1/emrxmlstring.
    CLASS-DATA av_ec2_role_arn TYPE /aws1/emrxmlstring.
    CLASS-DATA av_default_vpc_id TYPE /aws1/ec2string.
    CLASS-DATA av_master_sg_id TYPE /aws1/emrxmlstringmaxlen256.
    CLASS-DATA av_slave_sg_id TYPE /aws1/emrxmlstringmaxlen256.

    " Single combined test method to ensure execution order
    METHODS test_emr_operations FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_emr_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_emr = /aws1/cl_emr_factory=>create( ao_session ).
    ao_emr_actions = NEW /awsex/cl_emr_actions( ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_ec2 = /aws1/cl_ec2_factory=>create( ao_session ).
    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).

    " Create S3 bucket for logs with convert_test tag
    DATA(lv_account_id) = ao_session->get_account_id( ).
    DATA(lv_timestamp) = |{ sy-datum }{ sy-uzeit }|.
    av_log_bucket = |sap-abap-emr-demo-logs-{ lv_timestamp }-{ lv_account_id }|.
    
    TRY.
        /awsex/cl_utils=>create_bucket(
          iv_bucket = av_log_bucket
          io_s3 = ao_s3
          io_session = ao_session
        ).
      CATCH /aws1/cx_s3_bktalrdyownedbyyou.
        " Bucket already exists and is owned by us
        " This shouldn't happen with timestamp in name, but handle it anyway
        cl_abap_unit_assert=>fail( |S3 bucket { av_log_bucket } already exists| ).
      CATCH /aws1/cx_s3_bucketalrdyexists.
        " Bucket name taken by another account
        cl_abap_unit_assert=>fail( |S3 bucket name { av_log_bucket } is taken| ).
    ENDTRY.
    
    " Tag the S3 bucket for cleanup
    ao_s3->putbuckettagging(
      iv_bucket = av_log_bucket
      io_tagging = NEW /aws1/cl_s3_tagging(
        it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
          ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) )
        )
      )
    ).

    " Get default VPC
    DATA(lo_vpc_result) = ao_ec2->describevpcs(
      it_filters = VALUE /aws1/cl_ec2filter=>tt_filterlist(
        ( NEW /aws1/cl_ec2filter(
            iv_name = 'isDefault'
            it_values = VALUE /aws1/cl_ec2valuestringlist_w=>tt_valuestringlist(
              ( NEW /aws1/cl_ec2valuestringlist_w( 'true' ) )
            )
          )
        )
      )
    ).
    DATA(lt_vpcs) = lo_vpc_result->get_vpcs( ).
    READ TABLE lt_vpcs INDEX 1 INTO DATA(lo_vpc).
    IF sy-subrc = 0.
      av_default_vpc_id = lo_vpc->get_vpcid( ).
    ELSE.
      cl_abap_unit_assert=>fail( 'No default VPC found. EMR requires a VPC to create clusters.' ).
    ENDIF.

    " Create security groups
    av_master_sg_id = ao_ec2->createsecuritygroup(
      iv_groupname = |emr-master-sg-{ lv_timestamp }|
      iv_description = 'EMR Master Security Group'
      iv_vpcid = av_default_vpc_id
      it_tagspecifications = VALUE /aws1/cl_ec2tagspecification=>tt_tagspecificationlist(
        ( NEW /aws1/cl_ec2tagspecification(
            iv_resourcetype = 'security-group'
            it_tags = VALUE /aws1/cl_ec2tag=>tt_taglist(
              ( NEW /aws1/cl_ec2tag( iv_key = 'convert_test' iv_value = 'true' ) )
            )
          )
        )
      )
    )->get_groupid( ).

    av_slave_sg_id = ao_ec2->createsecuritygroup(
      iv_groupname = |emr-slave-sg-{ lv_timestamp }|
      iv_description = 'EMR Slave Security Group'
      iv_vpcid = av_default_vpc_id
      it_tagspecifications = VALUE /aws1/cl_ec2tagspecification=>tt_tagspecificationlist(
        ( NEW /aws1/cl_ec2tagspecification(
            iv_resourcetype = 'security-group'
            it_tags = VALUE /aws1/cl_ec2tag=>tt_taglist(
              ( NEW /aws1/cl_ec2tag( iv_key = 'convert_test' iv_value = 'true' ) )
            )
          )
        )
      )
    )->get_groupid( ).

    " Create IAM roles with enhanced permissions
    av_emr_role_name = |EMRServiceRole{ lv_timestamp }|.
    av_ec2_role_name = |EMREC2Role{ lv_timestamp }|.

    " EMR Service Role trust policy
    DATA(lv_emr_trust_policy) = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[\{| &&
      |"Effect":"Allow",| &&
      |"Principal":\{"Service":"elasticmapreduce.amazonaws.com"\},| &&
      |"Action":"sts:AssumeRole"| &&
      |\}]| &&
      |\}|.

    " Create EMR service role
    DATA(lo_emr_role) = ao_iam->createrole(
      iv_rolename = av_emr_role_name
      iv_assumerolepolicydocument = lv_emr_trust_policy
      it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
        ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
      )
    )->get_role( ).
    av_emr_role_arn = lo_emr_role->get_arn( ).

    " Attach managed policy to EMR service role
    ao_iam->attachrolepolicy(
      iv_rolename = av_emr_role_name
      iv_policyarn = 'arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceRole'
    ).

    " EC2 Instance Profile trust policy
    DATA(lv_ec2_trust_policy) = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[\{| &&
      |"Effect":"Allow",| &&
      |"Principal":\{"Service":"ec2.amazonaws.com"\},| &&
      |"Action":"sts:AssumeRole"| &&
      |\}]| &&
      |\}|.

    " Create EC2 role
    DATA(lo_ec2_role) = ao_iam->createrole(
      iv_rolename = av_ec2_role_name
      iv_assumerolepolicydocument = lv_ec2_trust_policy
      it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
        ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
      )
    )->get_role( ).
    av_ec2_role_arn = lo_ec2_role->get_arn( ).

    " Attach managed policy to EC2 role
    ao_iam->attachrolepolicy(
      iv_rolename = av_ec2_role_name
      iv_policyarn = 'arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceforEC2Role'
    ).

    " Create instance profile
    ao_iam->createinstanceprofile(
      iv_instanceprofilename = av_ec2_role_name
      it_tags = VALUE /aws1/cl_iamtag=>tt_taglisttype(
        ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
      )
    ).

    " Add role to instance profile
    ao_iam->addroletoinstanceprofile(
      iv_instanceprofilename = av_ec2_role_name
      iv_rolename = av_ec2_role_name
    ).

    " Wait for IAM roles to propagate
    WAIT UP TO 10 SECONDS.

  ENDMETHOD.

  METHOD class_teardown.
    " Terminate cluster if it exists
    IF av_cluster_id IS NOT INITIAL.
      TRY.
          DATA lt_cluster_ids TYPE /aws1/cl_emrxmlstringlist_w=>tt_xmlstringlist.
          APPEND NEW /aws1/cl_emrxmlstringlist_w( av_cluster_id ) TO lt_cluster_ids.
          ao_emr->terminatejobflows( it_jobflowids = lt_cluster_ids ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " S3 bucket cleanup: The EMR cluster writes logs to this bucket during termination.
    " Since cluster termination is asynchronous and can take 10+ minutes, we cannot
    " delete the bucket here. The bucket is tagged with 'convert_test' for manual cleanup.
    " To clean up: Wait for cluster termination, then delete bucket manually or via script.
    " Uncomment below line only after cluster is fully terminated:
    " /awsex/cl_utils=>cleanup_bucket( io_s3 = ao_s3 iv_bucket = av_log_bucket ).

    " Clean up security groups
    IF av_master_sg_id IS NOT INITIAL.
      TRY.
          ao_ec2->deletesecuritygroup( iv_groupid = av_master_sg_id ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    IF av_slave_sg_id IS NOT INITIAL.
      TRY.
          ao_ec2->deletesecuritygroup( iv_groupid = av_slave_sg_id ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Clean up IAM resources
    IF av_ec2_role_name IS NOT INITIAL.
      TRY.
          ao_iam->removerolefrominstprofile(
            iv_instanceprofilename = av_ec2_role_name
            iv_rolename = av_ec2_role_name
          ).
          ao_iam->deleteinstanceprofile( iv_instanceprofilename = av_ec2_role_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.

      TRY.
          ao_iam->detachrolepolicy(
            iv_rolename = av_ec2_role_name
            iv_policyarn = 'arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceforEC2Role'
          ).
          ao_iam->deleterole( iv_rolename = av_ec2_role_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    IF av_emr_role_name IS NOT INITIAL.
      TRY.
          ao_iam->detachrolepolicy(
            iv_rolename = av_emr_role_name
            iv_policyarn = 'arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceRole'
          ).
          ao_iam->deleterole( iv_rolename = av_emr_role_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD test_emr_operations.
    " This single test method tests all 6 EMR operations in sequence
    " to avoid issues with test execution order

    "=======================================================================
    " TEST 1 & 2: run_job_flow and describe_cluster
    "=======================================================================
    " Build applications list
    DATA lt_applications TYPE /aws1/cl_emrapplication=>tt_applicationlist.
    APPEND NEW /aws1/cl_emrapplication( iv_name = 'Spark' ) TO lt_applications.

    " Build minimal steps list
    DATA lt_args TYPE /aws1/cl_emrxmlstringlist_w=>tt_xmlstringlist.
    APPEND NEW /aws1/cl_emrxmlstringlist_w( 'spark-submit' ) TO lt_args.
    APPEND NEW /aws1/cl_emrxmlstringlist_w( '--deploy-mode' ) TO lt_args.
    APPEND NEW /aws1/cl_emrxmlstringlist_w( 'cluster' ) TO lt_args.
    APPEND NEW /aws1/cl_emrxmlstringlist_w( 's3://elasticmapreduce/samples/cloudfront' ) TO lt_args.

    DATA(lo_hadoop_jar_step) = NEW /aws1/cl_emrhadoopjarstepcfg(
      iv_jar = 'command-runner.jar'
      it_args = lt_args
    ).

    DATA(lo_step_config) = NEW /aws1/cl_emrstepconfig(
      iv_name = 'Initial Test Step'
      iv_actiononfailure = 'CONTINUE'
      io_hadoopjarstep = lo_hadoop_jar_step
    ).

    DATA lt_steps TYPE /aws1/cl_emrstepconfig=>tt_stepconfiglist.
    APPEND lo_step_config TO lt_steps.

    " TEST: run_job_flow - Create cluster
    av_cluster_id = ao_emr_actions->run_job_flow(
      iv_name = 'SAP-ABAP-EMR-Test'
      iv_log_uri = |s3://{ av_log_bucket }/logs/|
      iv_keep_alive = abap_false
      it_applications = lt_applications
      iv_job_flow_role = av_ec2_role_name
      iv_service_role = av_emr_role_arn
      iv_master_sec_grp = av_master_sg_id
      iv_slave_sec_grp = av_slave_sg_id
      it_steps = lt_steps
    ).

    " Verify cluster was created
    cl_abap_unit_assert=>assert_not_initial(
      act = av_cluster_id
      msg = 'run_job_flow: Cluster ID should not be empty'
    ).

    " TEST: describe_cluster - Get cluster information
    DATA(lo_describe_result) = ao_emr_actions->describe_cluster( av_cluster_id ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_describe_result
      msg = 'describe_cluster: Result should be bound'
    ).

    DATA(lo_cluster) = lo_describe_result->get_cluster( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_cluster
      msg = 'describe_cluster: Cluster object should be bound'
    ).

    DATA(lv_cluster_name) = lo_cluster->get_name( ).
    cl_abap_unit_assert=>assert_equals(
      exp = 'SAP-ABAP-EMR-Test'
      act = lv_cluster_name
      msg = 'describe_cluster: Cluster name should match'
    ).

    "=======================================================================
    " TEST 3: add_job_flow_steps
    "=======================================================================
    " Build script args for additional step
    DATA lt_script_args TYPE /aws1/cl_emrxmlstringlist_w=>tt_xmlstringlist.
    APPEND NEW /aws1/cl_emrxmlstringlist_w( 'arg1' ) TO lt_script_args.

    " TEST: add_job_flow_steps - Add a new step to the cluster
    av_step_id = ao_emr_actions->add_job_flow_steps(
      iv_cluster_id = av_cluster_id
      iv_name = 'Additional Test Step'
      iv_script_uri = 's3://elasticmapreduce/samples/cloudfront'
      it_script_args = lt_script_args
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = av_step_id
      msg = 'add_job_flow_steps: Step ID should not be empty'
    ).

    "=======================================================================
    " TEST 4: list_steps
    "=======================================================================
    " TEST: list_steps - List all steps in the cluster
    DATA(lo_list_result) = ao_emr_actions->list_steps( av_cluster_id ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_list_result
      msg = 'list_steps: Result should be bound'
    ).

    DATA(lt_steps_list) = lo_list_result->get_steps( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_steps_list )
      msg = 'list_steps: Should have at least one step'
    ).

    " Verify we have at least 2 steps (initial + additional)
    cl_abap_unit_assert=>assert_true(
      act = COND #( WHEN lines( lt_steps_list ) >= 2 THEN abap_true ELSE abap_false )
      msg = |list_steps: Expected at least 2 steps, found { lines( lt_steps_list ) }|
    ).

    "=======================================================================
    " TEST 5: describe_step
    "=======================================================================
    " TEST: describe_step - Get details of the step we just added
    DATA(lo_step_result) = ao_emr_actions->describe_step(
      iv_cluster_id = av_cluster_id
      iv_step_id = av_step_id
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_step_result
      msg = 'describe_step: Result should be bound'
    ).

    DATA(lo_step) = lo_step_result->get_step( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_step
      msg = 'describe_step: Step object should be bound'
    ).

    DATA(lv_step_name) = lo_step->get_name( ).
    cl_abap_unit_assert=>assert_equals(
      exp = 'Additional Test Step'
      act = lv_step_name
      msg = 'describe_step: Step name should match'
    ).

    "=======================================================================
    " TEST 6: terminate_job_flows
    "=======================================================================
    " TEST: terminate_job_flows - Terminate the cluster
    ao_emr_actions->terminate_job_flows( av_cluster_id ).

    " Verify cluster is terminating or terminated
    DATA(lo_terminate_result) = ao_emr->describecluster( iv_clusterid = av_cluster_id ).
    DATA(lo_cluster_after_term) = lo_terminate_result->get_cluster( ).
    DATA(lo_status) = lo_cluster_after_term->get_status( ).
    DATA(lv_state) = lo_status->get_state( ).

    cl_abap_unit_assert=>assert_true(
      act = COND #(
        WHEN lv_state = 'TERMINATING' OR
             lv_state = 'TERMINATED' OR
             lv_state = 'TERMINATED_WITH_ERRORS'
        THEN abap_true
        ELSE abap_false
      )
      msg = |terminate_job_flows: Cluster should be terminating/terminated but is { lv_state }|
    ).

  ENDMETHOD.

ENDCLASS.
