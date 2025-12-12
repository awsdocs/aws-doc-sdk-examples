" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_asc_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS constructor
      IMPORTING
        !io_session TYPE REF TO /aws1/cl_rt_session_base OPTIONAL
      RAISING
        /aws1/cx_rt_generic.

    " Creates an Auto Scaling group
    METHODS create_group
      IMPORTING
        !iv_group_name           TYPE /aws1/ascxmlstringmaxlen255
        !it_group_zones          TYPE /aws1/cl_ascazs_w=>tt_availabilityzones
        !iv_launch_template_name TYPE /aws1/asclaunchtemplatename
        !iv_min_size             TYPE /aws1/ascautoscgroupminsize
        !iv_max_size             TYPE /aws1/ascautoscgroupmaxsize
      RAISING
        /aws1/cx_rt_generic.

    " Updates an Auto Scaling group
    METHODS update_group
      IMPORTING
        !iv_group_name TYPE /aws1/ascxmlstringmaxlen255
        !iv_max_size   TYPE /aws1/ascautoscgroupmaxsize OPTIONAL
        !iv_min_size   TYPE /aws1/ascautoscgroupminsize OPTIONAL
      RAISING
        /aws1/cx_rt_generic.

    " Deletes an Auto Scaling group
    METHODS delete_group
      IMPORTING
        !iv_group_name TYPE /aws1/ascxmlstringmaxlen255
      RAISING
        /aws1/cx_rt_generic.

    " Gets information about an Auto Scaling group
    METHODS describe_group
      IMPORTING
        !iv_group_name    TYPE /aws1/ascxmlstringmaxlen255
      RETURNING
        VALUE(oo_output)  TYPE REF TO /aws1/cl_ascautoscalinggroup
      RAISING
        /aws1/cx_rt_generic.

    " Terminates an instance in an Auto Scaling group
    METHODS terminate_instance
      IMPORTING
        !iv_instance_id       TYPE /aws1/ascxmlstringmaxlen19
        !iv_decrease_capacity TYPE /aws1/ascshoulddecrementdesi00
      RETURNING
        VALUE(oo_output)      TYPE REF TO /aws1/cl_ascactivity
      RAISING
        /aws1/cx_rt_generic.

    " Sets the desired capacity of an Auto Scaling group
    METHODS set_desired_capacity
      IMPORTING
        !iv_group_name TYPE /aws1/ascxmlstringmaxlen255
        !iv_capacity   TYPE /aws1/ascautoscgroupdesiredcap
      RAISING
        /aws1/cx_rt_generic.

    " Gets information about Auto Scaling instances
    METHODS describe_instances
      IMPORTING
        !it_instance_ids TYPE /aws1/cl_ascinstanceids_w=>tt_instanceids
      RETURNING
        VALUE(ot_output) TYPE /aws1/cl_ascautoscinstdetails=>tt_autoscalinginstances
      RAISING
        /aws1/cx_rt_generic.

    " Gets scaling activities for an Auto Scaling group
    METHODS describe_scaling_activities
      IMPORTING
        !iv_group_name   TYPE /aws1/ascxmlstringmaxlen255
      RETURNING
        VALUE(ot_output) TYPE /aws1/cl_ascactivity=>tt_activities
      RAISING
        /aws1/cx_rt_generic.

    " Enables CloudWatch metrics collection for an Auto Scaling group
    METHODS enable_metrics
      IMPORTING
        !iv_group_name TYPE /aws1/ascxmlstringmaxlen255
        !it_metrics    TYPE /aws1/cl_ascmetrics_w=>tt_metrics
      RAISING
        /aws1/cx_rt_generic.

    " Disables CloudWatch metrics collection for an Auto Scaling group
    METHODS disable_metrics
      IMPORTING
        !iv_group_name TYPE /aws1/ascxmlstringmaxlen255
      RAISING
        /aws1/cx_rt_generic.

    " Helper method to get instances in a group
    METHODS get_group_instances
      IMPORTING
        !iv_group_name     TYPE /aws1/ascxmlstringmaxlen255
      RETURNING
        VALUE(rt_instances) TYPE /aws1/cl_ascinstance=>tt_instances
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
    DATA ao_asc TYPE REF TO /aws1/if_asc.
ENDCLASS.



CLASS /AWSEX/CL_ASC_ACTIONS IMPLEMENTATION.


  METHOD constructor.
    IF io_session IS BOUND.
      ao_asc = /aws1/cl_asc_factory=>create( io_session ).
    ENDIF.
  ENDMETHOD.


  METHOD create_group.
    " snippet-start:[asc.abapv1.create_group]
    DATA lo_launch_template TYPE REF TO /aws1/cl_asclaunchtemplatespec.
    
    " Example: iv_group_name = 'my-auto-scaling-group'
    " Example: iv_launch_template_name = 'my-launch-template'
    " Example: iv_min_size = 1
    " Example: iv_max_size = 3
    
    TRY.
        " Create launch template specification
        lo_launch_template = NEW /aws1/cl_asclaunchtemplatespec(
          iv_launchtemplatename = iv_launch_template_name
          iv_version = '$Default' ).

        " Create the Auto Scaling group
        ao_asc->createautoscalinggroup(
          iv_autoscalinggroupname = iv_group_name
          it_availabilityzones = it_group_zones
          io_launchtemplate = lo_launch_template
          iv_minsize = iv_min_size
          iv_maxsize = iv_max_size ).

        " Wait for the group to be created (simplified - in production use proper polling)
        WAIT UP TO 10 SECONDS.

        MESSAGE 'Auto Scaling group created successfully' TYPE 'I'.

      CATCH /aws1/cx_ascalreadyexistsfault INTO DATA(lo_already_exists).
        RAISE EXCEPTION lo_already_exists.
      CATCH /aws1/cx_asclimitexceededfault INTO DATA(lo_limit_exceeded).
        RAISE EXCEPTION lo_limit_exceeded.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.create_group]
  ENDMETHOD.


  METHOD update_group.
    " snippet-start:[asc.abapv1.update_group]
    " Example: iv_group_name = 'my-auto-scaling-group'
    " Example: iv_max_size = 5
    
    TRY.
        ao_asc->updateautoscalinggroup(
          iv_autoscalinggroupname = iv_group_name
          iv_maxsize = iv_max_size
          iv_minsize = iv_min_size ).

        MESSAGE 'Auto Scaling group updated successfully' TYPE 'I'.

      CATCH /aws1/cx_ascresrccontionfault INTO DATA(lo_contention).
        RAISE EXCEPTION lo_contention.
      CATCH /aws1/cx_ascscaactivityinprg00 INTO DATA(lo_activity_in_progress).
        RAISE EXCEPTION lo_activity_in_progress.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.update_group]
  ENDMETHOD.


  METHOD delete_group.
    " snippet-start:[asc.abapv1.delete_group]
    " Example: iv_group_name = 'my-auto-scaling-group'
    
    TRY.
        ao_asc->deleteautoscalinggroup(
          iv_autoscalinggroupname = iv_group_name ).

        " Wait for the group to be deleted (simplified - in production use proper polling)
        WAIT UP TO 10 SECONDS.

        MESSAGE 'Auto Scaling group deleted successfully' TYPE 'I'.

      CATCH /aws1/cx_ascscaactivityinprg00 INTO DATA(lo_activity_in_progress).
        RAISE EXCEPTION lo_activity_in_progress.
      CATCH /aws1/cx_ascresourceinusefault INTO DATA(lo_resource_in_use).
        RAISE EXCEPTION lo_resource_in_use.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.delete_group]
  ENDMETHOD.


  METHOD describe_group.
    " snippet-start:[asc.abapv1.describe_group]
    DATA lt_group_names TYPE /aws1/cl_ascautoscgroupnames_w=>tt_autoscalinggroupnames.
    DATA lo_group_name TYPE REF TO /aws1/cl_ascautoscgroupnames_w.
    
    " Example: iv_group_name = 'my-auto-scaling-group'
    
    TRY.
        " Build group names parameter
        CREATE OBJECT lo_group_name
          EXPORTING
            iv_value = iv_group_name.
        APPEND lo_group_name TO lt_group_names.

        " Describe the Auto Scaling group
        DATA(lo_output) = ao_asc->describeautoscalinggroups(
          it_autoscalinggroupnames = lt_group_names ).

        " Return the first (and only) group in the result
        DATA(lt_groups) = lo_output->get_autoscalinggroups( ).
        IF lines( lt_groups ) > 0.
          READ TABLE lt_groups INDEX 1 INTO DATA(lo_group).
          oo_output = lo_group.
        ENDIF.

        MESSAGE 'Auto Scaling group information retrieved successfully' TYPE 'I'.

      CATCH /aws1/cx_ascresrccontionfault INTO DATA(lo_contention).
        RAISE EXCEPTION lo_contention.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.describe_group]
  ENDMETHOD.


  METHOD terminate_instance.
    " snippet-start:[asc.abapv1.terminate_instance]
    " Example: iv_instance_id = 'i-1234567890abcdef0'
    " Example: iv_decrease_capacity = abap_true
    
    TRY.
        DATA(lo_output) = ao_asc->terminateinstinautoscgroup(
          iv_instanceid = iv_instance_id
          iv_shoulddecrementdesiredcap = iv_decrease_capacity ).

        oo_output = lo_output->get_activity( ).

        MESSAGE 'Instance terminated successfully' TYPE 'I'.

      CATCH /aws1/cx_ascscaactivityinprg00 INTO DATA(lo_activity_in_progress).
        RAISE EXCEPTION lo_activity_in_progress.
      CATCH /aws1/cx_ascresrccontionfault INTO DATA(lo_contention).
        RAISE EXCEPTION lo_contention.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.terminate_instance]
  ENDMETHOD.


  METHOD set_desired_capacity.
    " snippet-start:[asc.abapv1.set_desired_capacity]
    " Example: iv_group_name = 'my-auto-scaling-group'
    " Example: iv_capacity = 2
    
    TRY.
        ao_asc->setdesiredcapacity(
          iv_autoscalinggroupname = iv_group_name
          iv_desiredcapacity = iv_capacity
          iv_honorcooldown = abap_false ).

        MESSAGE 'Desired capacity set successfully' TYPE 'I'.

      CATCH /aws1/cx_ascscaactivityinprg00 INTO DATA(lo_activity_in_progress).
        RAISE EXCEPTION lo_activity_in_progress.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.set_desired_capacity]
  ENDMETHOD.


  METHOD describe_instances.
    " snippet-start:[asc.abapv1.describe_instances]
    " Example: it_instance_ids contains a list of instance IDs
    
    TRY.
        DATA(lo_output) = ao_asc->describeautoscalinginstances(
          it_instanceids = it_instance_ids ).

        ot_output = lo_output->get_autoscalinginstances( ).

        MESSAGE 'Auto Scaling instances information retrieved successfully' TYPE 'I'.

      CATCH /aws1/cx_ascresrccontionfault INTO DATA(lo_contention).
        RAISE EXCEPTION lo_contention.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.describe_instances]
  ENDMETHOD.


  METHOD describe_scaling_activities.
    " snippet-start:[asc.abapv1.describe_scaling_activities]
    " Example: iv_group_name = 'my-auto-scaling-group'
    
    TRY.
        DATA(lo_output) = ao_asc->describescalingactivities(
          iv_autoscalinggroupname = iv_group_name ).

        ot_output = lo_output->get_activities( ).

        MESSAGE 'Scaling activities retrieved successfully' TYPE 'I'.

      CATCH /aws1/cx_ascresrccontionfault INTO DATA(lo_contention).
        RAISE EXCEPTION lo_contention.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.describe_scaling_activities]
  ENDMETHOD.


  METHOD enable_metrics.
    " snippet-start:[asc.abapv1.enable_metrics]
    " Example: iv_group_name = 'my-auto-scaling-group'
    " Example: it_metrics contains list of metrics like 'GroupMinSize', 'GroupMaxSize', etc.
    
    TRY.
        ao_asc->enablemetricscollection(
          iv_autoscalinggroupname = iv_group_name
          it_metrics = it_metrics
          iv_granularity = '1Minute' ).

        MESSAGE 'Metrics collection enabled successfully' TYPE 'I'.

      CATCH /aws1/cx_ascresrccontionfault INTO DATA(lo_contention).
        RAISE EXCEPTION lo_contention.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.enable_metrics]
  ENDMETHOD.


  METHOD disable_metrics.
    " snippet-start:[asc.abapv1.disable_metrics]
    " Example: iv_group_name = 'my-auto-scaling-group'
    
    TRY.
        ao_asc->disablemetricscollection(
          iv_autoscalinggroupname = iv_group_name ).

        MESSAGE 'Metrics collection disabled successfully' TYPE 'I'.

      CATCH /aws1/cx_ascresrccontionfault INTO DATA(lo_contention).
        RAISE EXCEPTION lo_contention.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_exception).
        RAISE EXCEPTION lo_generic_exception.
    ENDTRY.
    " snippet-end:[asc.abapv1.disable_metrics]
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
ENDCLASS.
