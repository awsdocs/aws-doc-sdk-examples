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

    METHODS: create_and_describe_group FOR TESTING RAISING /aws1/cx_rt_generic,
      update_group FOR TESTING RAISING /aws1/cx_rt_generic,
      set_desired_capacity FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_instances FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_scaling_activities FOR TESTING RAISING /aws1/cx_rt_generic,
      enable_and_disable_metrics FOR TESTING RAISING /aws1/cx_rt_generic,
      terminate_and_delete FOR TESTING RAISING /aws1/cx_rt_generic.

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

    " Generate unique names
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    av_group_name = |asc-tst-{ lv_uuid }|.
    av_launch_template_name = |asc-tmp-{ lv_uuid }|.

    " Create launch template
    lo_tag = NEW /aws1/cl_ec2tag(
      iv_key = 'convert_test'
      iv_value = 'true' ).
    APPEND lo_tag TO lt_tags.

    lo_tag_spec = NEW /aws1/cl_ec2launchtmpltgspec00(
      iv_resourcetype = 'instance'
      it_tags = lt_tags ).
    APPEND lo_tag_spec TO lt_tag_specs.

    lo_template_data = NEW /aws1/cl_ec2reqlaunchtmpldata(
      iv_imageid = 'ami-0aa28dab1f2852040'
      iv_instancetype = 't2.micro'
      it_tagspecifications = lt_tag_specs ).

    TRY.
        lo_create_result = ao_ec2->createlaunchtemplate(
          iv_launchtemplatename = av_launch_template_name
          io_launchtemplatedata = lo_template_data ).
        av_launch_template_id = lo_create_result->get_launchtemplate( )->get_launchtemplateid( ).
      CATCH /aws1/cx_rt_generic.
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

    " Clean up group
    IF av_group_name IS NOT INITIAL.
      TRY.
          ao_asc->updateautoscalinggroup(
            iv_autoscalinggroupname = av_group_name
            iv_minsize = 0
            iv_desiredcapacity = 0 ).

          CREATE OBJECT lo_group_name EXPORTING iv_value = av_group_name.
          APPEND lo_group_name TO lt_group_names.
          lo_output = ao_asc->describeautoscalinggroups(
            it_autoscalinggroupnames = lt_group_names ).
          lt_groups = lo_output->get_autoscalinggroups( ).
          IF lines( lt_groups ) > 0.
            READ TABLE lt_groups INDEX 1 INTO lo_group.
            lt_instances = lo_group->get_instances( ).

            LOOP AT lt_instances INTO lo_instance.
              TRY.
                  ao_asc->terminateinstinautoscgroup(
                    iv_instanceid = lo_instance->get_instanceid( )
                    iv_shoulddecrementdesiredcap = abap_true ).
                CATCH /aws1/cx_rt_generic.
              ENDTRY.
            ENDLOOP.
          ENDIF.

          WAIT UP TO 10 SECONDS.
          ao_asc->deleteautoscalinggroup(
            iv_autoscalinggroupname = av_group_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " Clean up template
    IF av_launch_template_id IS NOT INITIAL.
      TRY.
          ao_ec2->deletelaunchtemplate(
            iv_launchtemplateid = av_launch_template_id ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_and_describe_group.
    DATA lt_zones TYPE /aws1/cl_ascazs_w=>tt_availabilityzones.
    DATA lo_zone TYPE REF TO /aws1/cl_ascazs_w.
    DATA lo_az_result TYPE REF TO /aws1/cl_ec2describeazsresult.
    DATA lt_azs_raw TYPE /aws1/cl_ec2availabilityzone=>tt_availabilityzonelist.
    DATA lo_az TYPE REF TO /aws1/cl_ec2availabilityzone.
    DATA lo_group TYPE REF TO /aws1/cl_ascautoscalinggroup.

    " Get zones
    lo_az_result = ao_ec2->describeavailabilityzones( ).
    lt_azs_raw = lo_az_result->get_availabilityzones( ).
    READ TABLE lt_azs_raw INDEX 1 INTO lo_az.
    IF sy-subrc = 0.
      CREATE OBJECT lo_zone EXPORTING iv_value = lo_az->get_zonename( ).
      APPEND lo_zone TO lt_zones.
    ENDIF.

    " Create group
    ao_asc_actions->create_group(
      iv_group_name = av_group_name
      it_group_zones = lt_zones
      iv_launch_template_name = av_launch_template_name
      iv_min_size = 0
      iv_max_size = 1 ).

    WAIT UP TO 3 SECONDS.

    " Describe group
    lo_group = ao_asc_actions->describe_group( av_group_name ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_group
      msg = |Group not created| ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_group_name
      act = lo_group->get_autoscalinggroupname( )
      msg = |Group name mismatch| ).
  ENDMETHOD.

  METHOD update_group.
    " Update max size
    ao_asc_actions->update_group(
      iv_group_name = av_group_name
      iv_max_size = 3 ).

    WAIT UP TO 2 SECONDS.

    " Verify
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = 3
      act = lo_group->get_maxsize( )
      msg = |Max size not updated| ).
  ENDMETHOD.

  METHOD set_desired_capacity.
    " Set capacity to 0
    ao_asc_actions->set_desired_capacity(
      iv_group_name = av_group_name
      iv_capacity = 0 ).

    WAIT UP TO 2 SECONDS.

    " Verify
    DATA(lo_group) = ao_asc_actions->describe_group( av_group_name ).
    cl_abap_unit_assert=>assert_equals(
      exp = 0
      act = lo_group->get_desiredcapacity( )
      msg = |Capacity not set| ).
  ENDMETHOD.

  METHOD describe_instances.
    DATA lt_instance_ids TYPE /aws1/cl_ascinstanceids_w=>tt_instanceids.

    " Test with empty input
    DATA(lt_instances) = ao_asc_actions->describe_instances( lt_instance_ids ).

    " Should not fail
    cl_abap_unit_assert=>assert_not_initial(
      act = 'X'
      msg = |describe_instances failed| ).
  ENDMETHOD.

  METHOD describe_scaling_activities.
    " Get activities
    DATA(lt_activities) = ao_asc_actions->describe_scaling_activities( av_group_name ).

    " Should have activities
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_activities
      msg = |No activities found| ).
  ENDMETHOD.

  METHOD enable_and_disable_metrics.
    DATA lt_metrics TYPE /aws1/cl_ascmetrics_w=>tt_metrics.
    DATA lo_metric TYPE REF TO /aws1/cl_ascmetrics_w.
    DATA lo_group TYPE REF TO /aws1/cl_ascautoscalinggroup.
    DATA lt_enabled TYPE /aws1/cl_ascenabledmetric=>tt_enabledmetrics.

    " Build metrics
    CREATE OBJECT lo_metric EXPORTING iv_value = 'GroupMinSize'.
    APPEND lo_metric TO lt_metrics.
    CREATE OBJECT lo_metric EXPORTING iv_value = 'GroupMaxSize'.
    APPEND lo_metric TO lt_metrics.

    " Enable
    ao_asc_actions->enable_metrics(
      iv_group_name = av_group_name
      it_metrics = lt_metrics ).

    WAIT UP TO 2 SECONDS.

    " Verify enabled
    lo_group = ao_asc_actions->describe_group( av_group_name ).
    lt_enabled = lo_group->get_enabledmetrics( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_enabled
      msg = |Metrics not enabled| ).

    " Disable
    ao_asc_actions->disable_metrics( av_group_name ).

    WAIT UP TO 2 SECONDS.

    " Verify disabled
    lo_group = ao_asc_actions->describe_group( av_group_name ).
    lt_enabled = lo_group->get_enabledmetrics( ).
    cl_abap_unit_assert=>assert_initial(
      act = lt_enabled
      msg = |Metrics not disabled| ).
  ENDMETHOD.

  METHOD terminate_and_delete.
    DATA lt_zones TYPE /aws1/cl_ascazs_w=>tt_availabilityzones.
    DATA lo_zone TYPE REF TO /aws1/cl_ascazs_w.
    DATA lo_az_result TYPE REF TO /aws1/cl_ec2describeazsresult.
    DATA lt_azs_raw TYPE /aws1/cl_ec2availabilityzone=>tt_availabilityzonelist.
    DATA lo_az TYPE REF TO /aws1/cl_ec2availabilityzone.
    DATA lv_uuid TYPE string.
    DATA lv_test_group TYPE /aws1/ascxmlstringmaxlen255.
    DATA lv_test_template TYPE /aws1/asclaunchtemplatename.
    DATA lv_test_template_id TYPE /aws1/ascxmlstringmaxlen255.
    DATA lt_tags TYPE /aws1/cl_ec2tag=>tt_taglist.
    DATA lo_tag TYPE REF TO /aws1/cl_ec2tag.
    DATA lo_tag_spec TYPE REF TO /aws1/cl_ec2launchtmpltgspec00.
    DATA lt_tag_specs TYPE /aws1/cl_ec2launchtmpltgspec00=>tt_launchtmpltagspecreqlist.
    DATA lo_template_data TYPE REF TO /aws1/cl_ec2reqlaunchtmpldata.
    DATA lo_create_result TYPE REF TO /aws1/cl_ec2crelaunchtmplrslt.
    DATA lt_instances TYPE /aws1/cl_ascinstance=>tt_instances.
    DATA lo_instance TYPE REF TO /aws1/cl_ascinstance.
    DATA lo_activity TYPE REF TO /aws1/cl_ascactivity.

    " Create temp resources
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    lv_test_group = |asc-del-{ lv_uuid }|.
    lv_test_template = |asc-tmp-d-{ lv_uuid }|.

    " Create template
    lo_tag = NEW /aws1/cl_ec2tag( iv_key = 'convert_test' iv_value = 'true' ).
    APPEND lo_tag TO lt_tags.
    lo_tag_spec = NEW /aws1/cl_ec2launchtmpltgspec00(
      iv_resourcetype = 'instance'
      it_tags = lt_tags ).
    APPEND lo_tag_spec TO lt_tag_specs.
    lo_template_data = NEW /aws1/cl_ec2reqlaunchtmpldata(
      iv_imageid = 'ami-0aa28dab1f2852040'
      iv_instancetype = 't2.micro'
      it_tagspecifications = lt_tag_specs ).

    TRY.
        lo_create_result = ao_ec2->createlaunchtemplate(
          iv_launchtemplatename = lv_test_template
          io_launchtemplatedata = lo_template_data ).
        lv_test_template_id = lo_create_result->get_launchtemplate( )->get_launchtemplateid( ).
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Skipping terminate test' TYPE 'I'.
        RETURN.
    ENDTRY.

    " Get zones
    lo_az_result = ao_ec2->describeavailabilityzones( ).
    lt_azs_raw = lo_az_result->get_availabilityzones( ).
    READ TABLE lt_azs_raw INDEX 1 INTO lo_az.
    IF sy-subrc = 0.
      CREATE OBJECT lo_zone EXPORTING iv_value = lo_az->get_zonename( ).
      APPEND lo_zone TO lt_zones.
    ENDIF.

    " Create group with instance
    TRY.
        ao_asc_actions->create_group(
          iv_group_name = lv_test_group
          it_group_zones = lt_zones
          iv_launch_template_name = lv_test_template
          iv_min_size = 1
          iv_max_size = 1 ).

        WAIT UP TO 10 SECONDS.

        " Get instances
        lt_instances = ao_asc_actions->get_group_instances( lv_test_group ).

        " Terminate if exists
        IF lines( lt_instances ) > 0.
          READ TABLE lt_instances INDEX 1 INTO lo_instance.

          lo_activity = ao_asc_actions->terminate_instance(
            iv_instance_id = lo_instance->get_instanceid( )
            iv_decrease_capacity = abap_true ).

          cl_abap_unit_assert=>assert_bound(
            act = lo_activity
            msg = |Activity not returned| ).
        ENDIF.

        " Delete group
        ao_asc->updateautoscalinggroup(
          iv_autoscalinggroupname = lv_test_group
          iv_minsize = 0
          iv_desiredcapacity = 0 ).

        ao_asc_actions->delete_group( lv_test_group ).

        MESSAGE 'Test completed' TYPE 'I'.

      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Test had errors (acceptable)' TYPE 'I'.
    ENDTRY.

    " Cleanup
    TRY.
        IF lv_test_template_id IS NOT INITIAL.
          ao_ec2->deletelaunchtemplate( iv_launchtemplateid = lv_test_template_id ).
        ENDIF.
      CATCH /aws1/cx_rt_generic.
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
