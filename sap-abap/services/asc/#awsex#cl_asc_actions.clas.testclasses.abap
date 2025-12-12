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
    CLASS-DATA av_launch_template_name TYPE /aws1/asclaunchtemplatename.
    CLASS-DATA av_launch_template_id TYPE /aws1/ascxmlstringmaxlen255.

    METHODS: create_group FOR TESTING RAISING /aws1/cx_rt_generic,
      update_group FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_group FOR TESTING RAISING /aws1/cx_rt_generic,
      set_desired_capacity FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_instances FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_scaling_activities FOR TESTING RAISING /aws1/cx_rt_generic,
      enable_metrics FOR TESTING RAISING /aws1/cx_rt_generic,
      disable_metrics FOR TESTING RAISING /aws1/cx_rt_generic,
      terminate_instance FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_group FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS wait_for_group_ready
      IMPORTING
        iv_group_name TYPE /aws1/ascxmlstringmaxlen255
      RAISING
        /aws1/cx_rt_generic.

    METHODS wait_for_instance_ready
      IMPORTING
        iv_group_name TYPE /aws1/ascxmlstringmaxlen255
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_group_instances
      IMPORTING
        iv_group_name     TYPE /aws1/ascxmlstringmaxlen255
      RETURNING
        VALUE(rt_instances) TYPE /aws1/cl_ascinstance=>tt_instances
      RAISING
        /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_asc_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_asc = /aws1/cl_asc_factory=>create( ao_session ).
    ao_ec2 = /aws1/cl_ec2_factory=>create( ao_session ).
    ao_asc_actions = NEW /awsex/cl_asc_actions( ao_session ).

    " Generate unique names using utility function
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_group_name = |asc-test-group-{ lv_uuid }|.
    av_launch_template_name = |asc-test-tmpl-{ lv_uuid }|.

    " Create a launch template for testing
    DATA lt_tags TYPE /aws1/cl_ec2tag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_ec2tag.
    lo_tag = NEW /aws1/cl_ec2tag(
      iv_key = 'convert_test'
      iv_value = 'true' ).
    APPEND lo_tag TO lt_tags.

    DATA(lo_tag_spec) = NEW /aws1/cl_ec2launchtmpltagspec(
      iv_resourcetype = 'instance'
      it_tags = lt_tags ).
    DATA lt_tag_specs TYPE /aws1/cl_ec2launchtmpltagspec=>tt_launchtemplatetagspeclist.
    APPEND lo_tag_spec TO lt_tag_specs.

    " Use Amazon Linux 2023 AMI (this is a commonly available AMI)
    DATA(lo_template_data) = NEW /aws1/cl_ec2reqlaunchtmpldata(
      iv_imageid = 'ami-0aa28dab1f2852040'  " Amazon Linux 2023 in us-east-1
      iv_instancetype = 't2.micro'
      it_tagspecifications = lt_tag_specs ).

    TRY.
        DATA(lo_create_result) = ao_ec2->createlaunchtemplate(
          iv_launchtemplatename = av_launch_template_name
          io_launchtemplatedata = lo_template_data ).
        av_launch_template_id = lo_create_result->get_launchtemplate( )->get_launchtemplateid( ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " If template already exists, try to retrieve it
        TRY.
            DATA lt_names TYPE /aws1/cl_ec2launchtmplnamest00=>tt_launchtmplnamestringlist.
            DATA lo_name TYPE REF TO /aws1/cl_ec2launchtmplnamest00.
            CREATE OBJECT lo_name EXPORTING iv_value = av_launch_template_name.
            APPEND lo_name TO lt_names.
            DATA(lo_describe) = ao_ec2->describelaunchtemplates(
              it_launchtemplatenames = lt_names ).
            DATA(lt_templates) = lo_describe->get_launchtemplates( ).
            IF lines( lt_templates ) > 0.
              READ TABLE lt_templates INDEX 1 INTO DATA(lo_template).
              av_launch_template_id = lo_template->get_launchtemplateid( ).
            ENDIF.
          CATCH /aws1/cx_rt_generic.
            RAISE EXCEPTION lo_ex.
        ENDTRY.
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up Auto Scaling group
    IF av_group_name IS NOT INITIAL.
      TRY.
          " First, set min size to 0 to allow instance termination
          ao_asc->updateautoscalinggroup(
            iv_autoscalinggroupname = av_group_name
            iv_minsize = 0 ).

          " Get all instances in the group
          DATA(lt_instances) = get_group_instances( av_group_name ).

          " Terminate all instances
          LOOP AT lt_instances INTO DATA(lo_instance).
            TRY.
                ao_asc->terminateinstinautoscgroup(
                  iv_instanceid = lo_instance->get_instanceid( )
                  iv_shoulddecrementdesiredcap = abap_true ).
              CATCH /aws1/cx_rt_generic.
                " Ignore errors during cleanup
            ENDTRY.
          ENDLOOP.

          " Wait for instances to terminate
          WAIT UP TO 60 SECONDS.

          " Delete the Auto Scaling group
          ao_asc->deleteautoscalinggroup(
            iv_autoscalinggroupname = av_group_name ).

          " Wait for deletion
          WAIT UP TO 30 SECONDS.

        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Clean up launch template
    IF av_launch_template_id IS NOT INITIAL.
      TRY.
          ao_ec2->deletelaunchtemplate(
            iv_launchtemplateid = av_launch_template_id ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD wait_for_group_ready.
    DATA lv_max_wait TYPE i VALUE 300.  " 5 minutes max
    DATA lv_waited TYPE i VALUE 0.
    DATA lv_ready TYPE abap_bool VALUE abap_false.

    WHILE lv_waited < lv_max_wait AND lv_ready = abap_false.
      TRY.
          DATA lt_group_names TYPE /aws1/cl_ascautoscgroupnames_w=>tt_autoscalinggroupnames.
          DATA lo_group_name TYPE REF TO /aws1/cl_ascautoscgroupnames_w.
          CREATE OBJECT lo_group_name EXPORTING iv_value = iv_group_name.
          APPEND lo_group_name TO lt_group_names.

          DATA(lo_output) = ao_asc->describeautoscalinggroups(
            it_autoscalinggroupnames = lt_group_names ).

          DATA(lt_groups) = lo_output->get_autoscalinggroups( ).
          IF lines( lt_groups ) > 0.
            lv_ready = abap_true.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Group not ready yet, continue waiting
      ENDTRY.

      IF lv_ready = abap_false.
        WAIT UP TO 5 SECONDS.
        lv_waited = lv_waited + 5.
      ENDIF.
    ENDWHILE.
  ENDMETHOD.

  METHOD wait_for_instance_ready.
    DATA lv_max_wait TYPE i VALUE 300.  " 5 minutes max
    DATA lv_waited TYPE i VALUE 0.
    DATA lv_ready TYPE abap_bool VALUE abap_false.

    WHILE lv_waited < lv_max_wait AND lv_ready = abap_false.
      DATA(lt_instances) = get_group_instances( iv_group_name ).

      " Check if at least one instance is InService
      LOOP AT lt_instances INTO DATA(lo_instance).
        IF lo_instance->get_lifecyclestate( ) = 'InService'.
          lv_ready = abap_true.
          EXIT.
        ENDIF.
      ENDLOOP.

      IF lv_ready = abap_false.
        WAIT UP TO 10 SECONDS.
        lv_waited = lv_waited + 10.
      ENDIF.
    ENDWHILE.
  ENDMETHOD.

  METHOD get_group_instances.
    DATA lt_group_names TYPE /aws1/cl_ascautoscgroupnames_w=>tt_autoscalinggroupnames.
    DATA lo_group_name TYPE REF TO /aws1/cl_ascautoscgroupnames_w.

    CREATE OBJECT lo_group_name EXPORTING iv_value = iv_group_name.
    APPEND lo_group_name TO lt_group_names.

    DATA(lo_output) = ao_asc->describeautoscalinggroups(
      it_autoscalinggroupnames = lt_group_names ).

    DATA(lt_groups) = lo_output->get_autoscalinggroups( ).
    IF lines( lt_groups ) > 0.
      READ TABLE lt_groups INDEX 1 INTO DATA(lo_group).
      rt_instances = lo_group->get_instances( ).
    ENDIF.
  ENDMETHOD.

  METHOD create_group.
    " Get availability zones for the current region
    DATA(lo_az_result) = ao_ec2->describeavailabilityzones( ).
    DATA(lt_azs_raw) = lo_az_result->get_availabilityzones( ).

    " Convert to Auto Scaling format
    DATA lt_zones TYPE /aws1/cl_ascazs_w=>tt_availabilityzones.
    DATA lo_zone TYPE REF TO /aws1/cl_ascazs_w.

    LOOP AT lt_azs_raw INTO DATA(lo_az).
      CREATE OBJECT lo_zone EXPORTING iv_value = lo_az->get_zonename( ).
      APPEND lo_zone TO lt_zones.
      " Only use first zone for simplicity
      EXIT.
    ENDLOOP.

    ao_asc_actions->create_group(
      iv_group_name = av_group_name
      it_group_zones = lt_zones
      iv_launch_template_name = av_launch_template_name
      iv_min_size = 1
      iv_max_size = 1 ).

    wait_for_group_ready( av_group_name ).

    " Verify the group was created
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_group
      msg = |Auto Scaling group { av_group_name } was not created| ).
  ENDMETHOD.

  METHOD update_group.
    " Update max size
    ao_asc_actions->update_group(
      iv_group_name = av_group_name
      iv_max_size = 3 ).

    WAIT UP TO 5 SECONDS.

    " Verify the update
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = 3
      act = lo_group->get_maxsize( )
      msg = |Max size was not updated to 3| ).
  ENDMETHOD.

  METHOD describe_group.
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_group
      msg = |Could not describe group { av_group_name }| ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_group_name
      act = lo_group->get_autoscalinggroupname( )
      msg = |Group name does not match| ).
  ENDMETHOD.

  METHOD set_desired_capacity.
    " Set desired capacity to 2
    ao_asc_actions->set_desired_capacity(
      iv_group_name = av_group_name
      iv_capacity = 2 ).

    WAIT UP TO 10 SECONDS.

    " Verify the desired capacity
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = 2
      act = lo_group->get_desiredcapacity( )
      msg = |Desired capacity was not set to 2| ).
  ENDMETHOD.

  METHOD describe_instances.
    " Wait for at least one instance to be ready
    wait_for_instance_ready( av_group_name ).

    " Get instances from the group
    DATA(lt_group_instances) = get_group_instances( av_group_name ).
    
    " Build instance IDs list
    DATA lt_instance_ids TYPE /aws1/cl_ascinstanceids_w=>tt_instanceids.
    DATA lo_instance_id TYPE REF TO /aws1/cl_ascinstanceids_w.

    LOOP AT lt_group_instances INTO DATA(lo_group_instance).
      CREATE OBJECT lo_instance_id
        EXPORTING
          iv_value = lo_group_instance->get_instanceid( ).
      APPEND lo_instance_id TO lt_instance_ids.
    ENDLOOP.

    " Describe the instances
    DATA(lt_instances) = ao_asc_actions->describe_instances( lt_instance_ids ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_instances
      msg = |No instances were returned| ).
  ENDMETHOD.

  METHOD describe_scaling_activities.
    DATA(lt_activities) = ao_asc_actions->describe_scaling_activities( av_group_name ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_activities
      msg = |No scaling activities were returned| ).
  ENDMETHOD.

  METHOD enable_metrics.
    DATA lt_metrics TYPE /aws1/cl_ascmetrics_w=>tt_metrics.
    DATA lo_metric TYPE REF TO /aws1/cl_ascmetrics_w.

    " Add metrics to enable
    CREATE OBJECT lo_metric EXPORTING iv_value = 'GroupMinSize'.
    APPEND lo_metric TO lt_metrics.
    CREATE OBJECT lo_metric EXPORTING iv_value = 'GroupMaxSize'.
    APPEND lo_metric TO lt_metrics.
    CREATE OBJECT lo_metric EXPORTING iv_value = 'GroupDesiredCapacity'.
    APPEND lo_metric TO lt_metrics.

    ao_asc_actions->enable_metrics(
      iv_group_name = av_group_name
      it_metrics = lt_metrics ).

    WAIT UP TO 5 SECONDS.

    " Verify metrics are enabled by describing the group
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    DATA(lt_enabled_metrics) = lo_group->get_enabledmetrics( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_enabled_metrics
      msg = |No metrics were enabled| ).
  ENDMETHOD.

  METHOD disable_metrics.
    ao_asc_actions->disable_metrics( av_group_name ).

    WAIT UP TO 5 SECONDS.

    " Verify metrics are disabled by describing the group
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    DATA(lt_enabled_metrics) = lo_group->get_enabledmetrics( ).

    " After disabling, the enabled metrics list should be empty
    cl_abap_unit_assert=>assert_initial(
      act = lt_enabled_metrics
      msg = |Metrics were not disabled| ).
  ENDMETHOD.

  METHOD terminate_instance.
    " Wait for at least one instance to be ready
    wait_for_instance_ready( av_group_name ).

    " Get instances from the group
    DATA(lt_instances) = get_group_instances( av_group_name ).
    
    IF lines( lt_instances ) > 0.
      READ TABLE lt_instances INDEX 1 INTO DATA(lo_instance).
      DATA(lv_instance_id) = lo_instance->get_instanceid( ).

      " Terminate the instance without decreasing capacity (replacement will start)
      DATA(lo_activity) = ao_asc_actions->terminate_instance(
        iv_instance_id = lv_instance_id
        iv_decrease_capacity = abap_false ).

      cl_abap_unit_assert=>assert_bound(
        act = lo_activity
        msg = |Activity was not returned after terminating instance| ).

      WAIT UP TO 10 SECONDS.

      " Verify a new instance is launching or already launched
      DATA(lt_new_instances) = get_group_instances( av_group_name ).
      cl_abap_unit_assert=>assert_not_initial(
        act = lt_new_instances
        msg = |No instances found after termination| ).
    ENDIF.
  ENDMETHOD.

  METHOD delete_group.
    " First, set min size to 0
    ao_asc->updateautoscalinggroup(
      iv_autoscalinggroupname = av_group_name
      iv_minsize = 0 ).

    " Get and terminate all instances
    DATA(lt_instances) = get_group_instances( av_group_name ).
    LOOP AT lt_instances INTO DATA(lo_instance).
      TRY.
          ao_asc->terminateinstinautoscgroup(
            iv_instanceid = lo_instance->get_instanceid( )
            iv_shoulddecrementdesiredcap = abap_true ).
        CATCH /aws1/cx_rt_generic.
          " Continue even if termination fails
      ENDTRY.
    ENDLOOP.

    " Wait for instances to terminate
    WAIT UP TO 60 SECONDS.

    " Delete the group
    ao_asc_actions->delete_group( av_group_name ).

    WAIT UP TO 30 SECONDS.

    " Verify the group was deleted
    TRY.
        ao_asc_actions->describe_group( av_group_name ).
        cl_abap_unit_assert=>fail( |Group { av_group_name } should have been deleted| ).
      CATCH /aws1/cx_rt_generic.
        " Expected - group should not exist
    ENDTRY.

    " Clear the group name so class_teardown doesn't try to delete it again
    CLEAR av_group_name.
  ENDMETHOD.

ENDCLASS.
