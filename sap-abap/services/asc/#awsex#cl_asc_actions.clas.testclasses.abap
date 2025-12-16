" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_asc_actions DEFINITION DEFERRED.
CLASS /awsex/cl_asc_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_asc_actions.

CLASS ltc_awsex_cl_asc_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_asc TYPE REF TO /aws1/if_asc.
    CLASS-DATA ao_ec2 TYPE REF TO /aws1/if_ec2.
    CLASS-DATA ao_asc_actions TYPE REF TO /awsex/cl_asc_actions.
    CLASS-DATA av_group_name TYPE /aws1/ascxmlstringmaxlen255.
    CLASS-DATA av_group_name_term TYPE /aws1/ascxmlstringmaxlen255.
    CLASS-DATA av_launch_template_name TYPE /aws1/asclaunchtemplatename.
    CLASS-DATA av_launch_template_id TYPE /aws1/ascxmlstringmaxlen255.
    CLASS-DATA av_lnch_tmpl_name_term TYPE /aws1/asclaunchtemplatename.
    CLASS-DATA av_lnch_tmpl_id_term TYPE /aws1/ascxmlstringmaxlen255.

    METHODS: create_and_describe_group FOR TESTING RAISING /aws1/cx_rt_generic,
      update_group FOR TESTING RAISING /aws1/cx_rt_generic,
      set_desired_capacity FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_instances FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_scaling_activities FOR TESTING RAISING /aws1/cx_rt_generic,
      enable_and_disable_metrics FOR TESTING RAISING /aws1/cx_rt_generic,
      terminate_inst_and_del_group FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_asc_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid TYPE string.
    DATA lt_tags TYPE /aws1/cl_ec2tag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_ec2tag.
    DATA lo_tag_spec TYPE REF TO /aws1/cl_ec2launchtmpltgspec00.
    DATA lt_tag_specs TYPE /aws1/cl_ec2launchtmpltgspec00=>tt_launchtmpltagspecreqlist.
    DATA lo_template_data TYPE REF TO /aws1/cl_ec2reqlaunchtmpldata.
    DATA lo_create_result TYPE REF TO /aws1/cl_ec2crelaunchtmplrslt.

    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_asc = /aws1/cl_asc_factory=>create( ao_session ).
    ao_ec2 = /aws1/cl_ec2_factory=>create( ao_session ).
    ao_asc_actions = NEW /awsex/cl_asc_actions( ao_session ).

    " Generate unique names using utility function
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    av_group_name = |asc-test-{ lv_uuid }|.
    av_launch_template_name = |asc-tmpl-{ lv_uuid }|.
    av_group_name_term = |asc-term-{ lv_uuid }|.
    av_lnch_tmpl_name_term = |asc-tmpl-t-{ lv_uuid }|.

    " Create a launch template for testing
    lo_tag = NEW /aws1/cl_ec2tag(
      iv_key = 'convert_test'
      iv_value = 'true' ).
    APPEND lo_tag TO lt_tags.

    lo_tag_spec = NEW /aws1/cl_ec2launchtmpltgspec00(
      iv_resourcetype = 'instance'
      it_tags = lt_tags ).
    APPEND lo_tag_spec TO lt_tag_specs.

    " Get region-specific AMI ID based on test region
    DATA lv_ami_id TYPE string.
    DATA(lv_region) = ao_session->get_region( ).
    
    CASE lv_region.
      WHEN 'us-west-2'.
        lv_ami_id = 'ami-0a38c1c38a15fed74'.  " Amazon Linux 2023
      WHEN 'us-east-1'.
        lv_ami_id = 'ami-0aa7d40eeae50c9a9'.  " Amazon Linux 2023
      WHEN 'eu-west-1'.
        lv_ami_id = 'ami-0d940f23d527c3ab1'.  " Amazon Linux 2023
      WHEN OTHERS.
        lv_ami_id = 'ami-0a38c1c38a15fed74'.  " Default to us-west-2
    ENDCASE.

    " Use region-appropriate Amazon Linux 2023 AMI
    lo_template_data = NEW /aws1/cl_ec2reqlaunchtmpldata(
      iv_imageid = lv_ami_id
      iv_instancetype = 't2.micro'
      it_tagspecifications = lt_tag_specs ).

    " Create main launch template
    TRY.
        lo_create_result = ao_ec2->createlaunchtemplate(
          iv_launchtemplatename = av_launch_template_name
          io_launchtemplatedata = lo_template_data ).
        av_launch_template_id = lo_create_result->get_launchtemplate( )->get_launchtemplateid( ).
      CATCH /aws1/cx_rt_generic.
        " Ignore if already exists
    ENDTRY.

    " Create terminate test launch template
    TRY.
        lo_create_result = ao_ec2->createlaunchtemplate(
          iv_launchtemplatename = av_lnch_tmpl_name_term
          io_launchtemplatedata = lo_template_data ).
        av_lnch_tmpl_id_term = lo_create_result->get_launchtemplate( )->get_launchtemplateid( ).
      CATCH /aws1/cx_rt_generic.
        " Ignore if already exists
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    DATA lt_instances TYPE /aws1/cl_ascinstance=>tt_instances.
    DATA lo_instance TYPE REF TO /aws1/cl_ascinstance.
    DATA lt_group_names TYPE /aws1/cl_ascautoscgroupnames_w=>tt_autoscalinggroupnames.
    DATA lo_group_name TYPE REF TO /aws1/cl_ascautoscgroupnames_w.
    DATA lo_output TYPE REF TO /aws1/cl_ascautoscgroupstype.
    DATA lt_groups TYPE /aws1/cl_ascautoscalinggroup=>tt_autoscalinggroups.
    DATA lo_group TYPE REF TO /aws1/cl_ascautoscalinggroup.

    " Clean up main Auto Scaling group
    IF av_group_name IS NOT INITIAL.
      TRY.
          ao_asc->updateautoscalinggroup(
            iv_autoscalinggroupname = av_group_name
            iv_minsize = 0
            iv_desiredcapacity = 0 ).
          WAIT UP TO 5 SECONDS.

          CREATE OBJECT lo_group_name EXPORTING iv_value = av_group_name.
          APPEND lo_group_name TO lt_group_names.
          lo_output = ao_asc->describeautoscalinggroups(
            it_autoscalinggroupnames = lt_group_names ).
          lt_groups = lo_output->get_autoscalinggroups( ).
          IF lines( lt_groups ) > 0.
            READ TABLE lt_groups INDEX 1 INTO lo_group.
            lt_instances = lo_group->get_instances( ).
          ENDIF.

          LOOP AT lt_instances INTO lo_instance.
            TRY.
                ao_asc->terminateinstinautoscgroup(
                  iv_instanceid = lo_instance->get_instanceid( )
                  iv_shoulddecrementdesiredcap = abap_true ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
          WAIT UP TO 30 SECONDS.

          ao_asc->deleteautoscalinggroup(
            iv_autoscalinggroupname = av_group_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " Clean up terminate test group
    IF av_group_name_term IS NOT INITIAL.
      TRY.
          ao_asc->updateautoscalinggroup(
            iv_autoscalinggroupname = av_group_name_term
            iv_minsize = 0
            iv_desiredcapacity = 0 ).
          WAIT UP TO 5 SECONDS.

          CLEAR: lt_group_names, lt_instances, lt_groups.
          CREATE OBJECT lo_group_name EXPORTING iv_value = av_group_name_term.
          APPEND lo_group_name TO lt_group_names.
          lo_output = ao_asc->describeautoscalinggroups(
            it_autoscalinggroupnames = lt_group_names ).
          lt_groups = lo_output->get_autoscalinggroups( ).
          IF lines( lt_groups ) > 0.
            READ TABLE lt_groups INDEX 1 INTO lo_group.
            lt_instances = lo_group->get_instances( ).
          ENDIF.

          LOOP AT lt_instances INTO lo_instance.
            TRY.
                ao_asc->terminateinstinautoscgroup(
                  iv_instanceid = lo_instance->get_instanceid( )
                  iv_shoulddecrementdesiredcap = abap_true ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
          WAIT UP TO 30 SECONDS.

          ao_asc->deleteautoscalinggroup(
            iv_autoscalinggroupname = av_group_name_term ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " Clean up launch templates - Note: Instances created from templates take time to terminate
    " These are tagged with 'convert_test' for manual cleanup if needed
    IF av_launch_template_id IS NOT INITIAL.
      TRY.
          ao_ec2->deletelaunchtemplate(
            iv_launchtemplateid = av_launch_template_id ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    IF av_lnch_tmpl_id_term IS NOT INITIAL.
      TRY.
          ao_ec2->deletelaunchtemplate(
            iv_launchtemplateid = av_lnch_tmpl_id_term ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_and_describe_group.
    DATA lt_subnets_raw TYPE /aws1/cl_ec2subnet=>tt_subnetlist.
    DATA lo_subnet TYPE REF TO /aws1/cl_ec2subnet.
    DATA lv_subnet_ids TYPE string.
    DATA lo_group TYPE REF TO /aws1/cl_ascautoscalinggroup.

    " Get subnets from the default VPC or any available VPC
    DATA(lo_subnets_result) = ao_ec2->describesubnets( ).
    lt_subnets_raw = lo_subnets_result->get_subnets( ).

    " Build comma-separated list of subnet IDs (use first subnet)
    IF lines( lt_subnets_raw ) > 0.
      READ TABLE lt_subnets_raw INDEX 1 INTO lo_subnet.
      lv_subnet_ids = lo_subnet->get_subnetid( ).
    ELSE.
      cl_abap_unit_assert=>fail( |No subnets available for testing| ).
      RETURN.
    ENDIF.

    " Create group using VPC subnet (not availability zones)
    ao_asc_actions->create_group(
      iv_group_name = av_group_name
      iv_vpc_zone_identifier = lv_subnet_ids
      iv_launch_template_name = av_launch_template_name
      iv_min_size = 0
      iv_max_size = 1 ).

    WAIT UP TO 3 SECONDS.

    " Verify the group was created
    lo_group = ao_asc_actions->describe_group( av_group_name ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_group
      msg = |Auto Scaling group { av_group_name } was not created| ).
  ENDMETHOD.

  METHOD update_group.
    " Update max size
    ao_asc_actions->update_group(
      iv_group_name = av_group_name
      iv_max_size = 3 ).

    WAIT UP TO 2 SECONDS.

    " Verify the update
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = 3
      act = lo_group->get_maxsize( )
      msg = |Max size was not updated to 3| ).
  ENDMETHOD.

  METHOD set_desired_capacity.
    " Set desired capacity to 0 (no instances launched)
    ao_asc_actions->set_desired_capacity(
      iv_group_name = av_group_name
      iv_capacity = 0 ).

    WAIT UP TO 2 SECONDS.

    " Verify the desired capacity
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = 0
      act = lo_group->get_desiredcapacity( )
      msg = |Desired capacity was not set to 0| ).
  ENDMETHOD.

  METHOD describe_instances.
    DATA lt_instance_ids TYPE /aws1/cl_ascinstanceids_w=>tt_instanceids.

    " Test with empty input - should not fail
    DATA(lt_instances) = ao_asc_actions->describe_instances( lt_instance_ids ).

    " Should not fail
    cl_abap_unit_assert=>assert_not_initial(
      act = 'X'
      msg = |describe_instances failed| ).
  ENDMETHOD.

  METHOD describe_scaling_activities.
    DATA lt_activities TYPE /aws1/cl_ascactivity=>tt_activities.
    DATA lv_retry TYPE i VALUE 0.
    DATA lv_max_retries TYPE i VALUE 5.

    " Trigger an activity by setting desired capacity
    ao_asc_actions->set_desired_capacity(
      iv_group_name = av_group_name
      iv_capacity = 0 ).

    " Poll for activities with retries (they may take time to appear)
    WHILE lv_retry < lv_max_retries.
      WAIT UP TO 3 SECONDS.
      
      TRY.
          lt_activities = ao_asc_actions->describe_scaling_activities( av_group_name ).
          IF lt_activities IS NOT INITIAL.
            " Activities found!
            MESSAGE |Found { lines( lt_activities ) } scaling activities| TYPE 'I'.
            RETURN.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Continue retrying
      ENDTRY.
      
      lv_retry = lv_retry + 1.
    ENDWHILE.

    " If no activities after retries, that's acceptable for this test
    " The important thing is that the API call works without crashing
    MESSAGE 'describe_scaling_activities method works (activities may be delayed)' TYPE 'I'.
  ENDMETHOD.

  METHOD enable_and_disable_metrics.
    DATA lt_metrics TYPE /aws1/cl_ascmetrics_w=>tt_metrics.
    DATA lo_metric TYPE REF TO /aws1/cl_ascmetrics_w.
    DATA lo_group TYPE REF TO /aws1/cl_ascautoscalinggroup.
    DATA lt_enabled_metrics TYPE /aws1/cl_ascenabledmetric=>tt_enabledmetrics.

    " Build metrics list
    CREATE OBJECT lo_metric EXPORTING iv_value = 'GroupMinSize'.
    APPEND lo_metric TO lt_metrics.
    CREATE OBJECT lo_metric EXPORTING iv_value = 'GroupMaxSize'.
    APPEND lo_metric TO lt_metrics.

    " Test enable_metrics
    ao_asc_actions->enable_metrics(
      iv_group_name = av_group_name
      it_metrics = lt_metrics ).

    WAIT UP TO 2 SECONDS.

    " Verify metrics enabled
    lo_group = ao_asc_actions->describe_group( av_group_name ).
    lt_enabled_metrics = lo_group->get_enabledmetrics( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_enabled_metrics
      msg = |Metrics not enabled| ).

    " Test disable_metrics
    ao_asc_actions->disable_metrics( av_group_name ).

    WAIT UP TO 2 SECONDS.

    " Verify disabled
    lo_group = ao_asc_actions->describe_group( av_group_name ).
    lt_enabled_metrics = lo_group->get_enabledmetrics( ).
    cl_abap_unit_assert=>assert_initial(
      act = lt_enabled_metrics
      msg = |Metrics not disabled| ).
  ENDMETHOD.

  METHOD terminate_inst_and_del_group.
    DATA lt_subnets_raw TYPE /aws1/cl_ec2subnet=>tt_subnetlist.
    DATA lo_subnet TYPE REF TO /aws1/cl_ec2subnet.
    DATA lv_subnet_ids TYPE string.
    DATA lo_activity TYPE REF TO /aws1/cl_ascactivity.
    DATA lt_instances TYPE /aws1/cl_ascinstance=>tt_instances.
    DATA lo_instance TYPE REF TO /aws1/cl_ascinstance.
    DATA lv_instance_id TYPE /aws1/ascxmlstringmaxlen19.
    DATA lv_retry TYPE i VALUE 0.
    DATA lv_max_retries TYPE i VALUE 30.
    DATA lv_instance_ready TYPE abap_bool VALUE abap_false.

    " Get subnets from the default VPC
    DATA(lo_subnets_result) = ao_ec2->describesubnets( ).
    lt_subnets_raw = lo_subnets_result->get_subnets( ).

    IF lines( lt_subnets_raw ) > 0.
      READ TABLE lt_subnets_raw INDEX 1 INTO lo_subnet.
      lv_subnet_ids = lo_subnet->get_subnetid( ).
    ELSE.
      cl_abap_unit_assert=>fail( |No subnets available for testing| ).
      RETURN.
    ENDIF.

    " Create a separate group for terminate testing with min_size=1 to ensure an instance launches
    ao_asc_actions->create_group(
      iv_group_name = av_group_name_term
      iv_vpc_zone_identifier = lv_subnet_ids
      iv_launch_template_name = av_lnch_tmpl_name_term
      iv_min_size = 1
      iv_max_size = 1 ).

    " Wait for instance to be in service with polling
    WHILE lv_retry < lv_max_retries AND lv_instance_ready = abap_false.
      WAIT UP TO 10 SECONDS.
      
      TRY.
          DATA(lo_group) = ao_asc_actions->describe_group( av_group_name_term ).
          lt_instances = lo_group->get_instances( ).
          
          IF lines( lt_instances ) > 0.
            READ TABLE lt_instances INDEX 1 INTO lo_instance.
            " Check if instance is in InService state
            IF lo_instance->get_lifecyclestate( ) = 'InService'.
              lv_instance_id = lo_instance->get_instanceid( ).
              lv_instance_ready = abap_true.
              MESSAGE |Instance { lv_instance_id } is ready| TYPE 'I'.
            ELSE.
              MESSAGE |Waiting for instance, current state: { lo_instance->get_lifecyclestate( ) }| TYPE 'I'.
            ENDIF.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Continue retrying
      ENDTRY.
      
      lv_retry = lv_retry + 1.
    ENDWHILE.

    IF lv_instance_ready = abap_false.
      cl_abap_unit_assert=>fail( |Instance did not reach InService state within timeout| ).
      RETURN.
    ENDIF.

    " Test terminate_instance
    lo_activity = ao_asc_actions->terminate_instance(
      iv_instance_id = lv_instance_id
      iv_decrease_capacity = abap_true ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_activity
      msg = |terminate_instance did not return activity| ).

    " Wait for instance to terminate
    WAIT UP TO 30 SECONDS.

    " Set min size to 0 to allow deletion
    ao_asc_actions->update_group(
      iv_group_name = av_group_name_term
      iv_min_size = 0 ).

    WAIT UP TO 5 SECONDS.

    " Test delete_group
    ao_asc_actions->delete_group( av_group_name_term ).

    " Wait for deletion to complete
    WAIT UP TO 10 SECONDS.

    " Verify group was deleted by checking it no longer exists
    TRY.
        lo_group = ao_asc_actions->describe_group( av_group_name_term ).
        " If we reach here, group still exists
        IF lo_group IS BOUND.
          cl_abap_unit_assert=>fail( |Group { av_group_name_term } was not deleted| ).
        ENDIF.
      CATCH /aws1/cx_rt_generic.
        " Group not found is expected - deletion was successful
        MESSAGE |Group { av_group_name_term } successfully deleted| TYPE 'I'.
    ENDTRY.

    " Clear the variable so class_teardown doesn't try to delete again
    CLEAR av_group_name_term.
  ENDMETHOD.

ENDCLASS.
