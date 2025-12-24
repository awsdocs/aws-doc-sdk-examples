" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_r5v_actions DEFINITION DEFERRED.
CLASS /awsex/cl_r5v_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_r5v_actions.

CLASS ltc_awsex_cl_r5v_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_r5c TYPE REF TO /aws1/if_r5c.
    CLASS-DATA ao_r5v_actions TYPE REF TO /awsex/cl_r5v_actions.
    CLASS-DATA av_cluster_arn TYPE /aws1/r5carn.
    CLASS-DATA av_routing_control_arn TYPE /aws1/r5carn.
    CLASS-DATA av_control_panel_arn TYPE /aws1/r5carn.
    CLASS-DATA at_cluster_endpoints TYPE /awsex/cl_r5v_actions=>tt_cluster_endpoints.
    CLASS-DATA av_test_role_name TYPE /aws1/iamrolename.
    CLASS-DATA av_test_role_arn TYPE /aws1/iamarntype.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS setup_iam_permissions RAISING /aws1/cx_rt_generic.
    CLASS-METHODS wait_for_cluster_deployed
      IMPORTING
        iv_cluster_arn    TYPE /aws1/r5carn
      RETURNING
        VALUE(rv_success) TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic.
    CLASS-METHODS wait_for_routing_control
      IMPORTING
        iv_routing_control_arn TYPE /aws1/r5carn
      RETURNING
        VALUE(rv_success)      TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_routing_control_state FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS update_routing_control_state FOR TESTING RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_r5v_actions IMPLEMENTATION.

  METHOD setup_iam_permissions.
    " Setup IAM role with necessary permissions for Route 53 ARC testing
    DATA lv_policy_document TYPE string.
    DATA lv_policy_name TYPE /aws1/iampolicyname.
    DATA lv_uuid TYPE sysuuid_x16.

    ao_iam = /aws1/cl_iam_factory=>create( ao_session ).

    " Generate unique role name
    lv_uuid = cl_system_uuid=>create_uuid_x16_static( ).
    DATA(lv_uuid_string) TYPE string.
    lv_uuid_string = lv_uuid.
    av_test_role_name = |r5v-test-role-{ lv_uuid_string(8) }|.

    " Create assume role policy document for test role
    DATA(lv_assume_role_policy) = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Principal":\{"Service":"route53-recovery-control.amazonaws.com"\},| &&
      |"Action":"sts:AssumeRole"| &&
      |\}| &&
      |]| &&
      |\}|.

    TRY.
        " Create IAM role for testing
        DATA(lo_create_role_result) = ao_iam->createrole(
          iv_rolename                 = av_test_role_name
          iv_assumerolepolicydocument = lv_assume_role_policy
          iv_description              = 'Test role for Route 53 Application Recovery Controller examples'
          it_tags                     = VALUE /aws1/cl_iamtag=>tt_taglisttype(
            ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).

        av_test_role_arn = lo_create_role_result->get_role( )->get_arn( ).
        MESSAGE |Created IAM role: { av_test_role_name } with ARN: { av_test_role_arn }| TYPE 'I'.

      CATCH /aws1/cx_iamentityalrdyexsex INTO DATA(lo_exists_ex).
        " Role already exists, get its ARN
        DATA(lo_get_role_result) = ao_iam->getrole( iv_rolename = av_test_role_name ).
        av_test_role_arn = lo_get_role_result->get_role( )->get_arn( ).
        MESSAGE |IAM role already exists: { av_test_role_name }| TYPE 'I'.
    ENDTRY.

    " Create inline policy for Route 53 ARC permissions
    lv_policy_name = 'R5VTestPolicy'.
    lv_policy_document = |\{| &&
      |"Version":"2012-10-17",| &&
      |"Statement":[| &&
      |\{| &&
      |"Effect":"Allow",| &&
      |"Action":[| &&
      |"route53-recovery-control-config:*",| &&
      |"route53-recovery-cluster:*"| &&
      |],| &&
      |"Resource":"*"| &&
      |\}| &&
      |]| &&
      |\}|.

    TRY.
        ao_iam->putrolepolicy(
          iv_rolename       = av_test_role_name
          iv_policyname     = lv_policy_name
          iv_policydocument = lv_policy_document
        ).
        MESSAGE |Attached inline policy { lv_policy_name } to role { av_test_role_name }| TYPE 'I'.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_policy_ex).
        MESSAGE |Warning: Could not attach policy: { lo_policy_ex->if_message~get_text( ) }| TYPE 'I'.
    ENDTRY.

    " Wait for IAM role to propagate
    WAIT UP TO 10 SECONDS.
  ENDMETHOD.

  METHOD wait_for_cluster_deployed.
    DATA lv_max_wait_time TYPE i VALUE 900. " 15 minutes
    DATA lv_wait_interval TYPE i VALUE 15. " 15 seconds
    DATA lv_elapsed TYPE i VALUE 0.
    DATA lv_cluster_status TYPE /aws1/r5cstatus.

    rv_success = abap_false.

    WHILE lv_elapsed < lv_max_wait_time.
      TRY.
          DATA(lo_describe_result) = ao_r5c->describecluster(
            iv_clusterarn = iv_cluster_arn ).

          IF lo_describe_result IS BOUND AND lo_describe_result->has_cluster( ) = abap_true.
            DATA(lo_cluster) = lo_describe_result->get_cluster( ).
            lv_cluster_status = lo_cluster->get_status( ).

            MESSAGE |Cluster status: { lv_cluster_status }, waited { lv_elapsed } seconds| TYPE 'I'.

            IF lv_cluster_status = 'DEPLOYED'.
              rv_success = abap_true.
              RETURN.
            ELSEIF lv_cluster_status = 'FAILED' OR lv_cluster_status = 'PENDING_DELETION'.
              MESSAGE |Cluster deployment failed with status: { lv_cluster_status }| TYPE 'I'.
              RETURN.
            ENDIF.
          ENDIF.

        CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
          MESSAGE |Error checking cluster status: { lo_ex->if_message~get_text( ) }| TYPE 'I'.
      ENDTRY.

      WAIT UP TO lv_wait_interval SECONDS.
      lv_elapsed = lv_elapsed + lv_wait_interval.
    ENDWHILE.

    MESSAGE |Cluster did not become deployed within { lv_max_wait_time } seconds| TYPE 'I'.
  ENDMETHOD.

  METHOD wait_for_routing_control.
    DATA lv_max_wait_time TYPE i VALUE 900. " 15 minutes
    DATA lv_wait_interval TYPE i VALUE 15. " 15 seconds
    DATA lv_elapsed TYPE i VALUE 0.
    DATA lv_status TYPE /aws1/r5cstatus.

    rv_success = abap_false.

    WHILE lv_elapsed < lv_max_wait_time.
      TRY.
          DATA(lo_describe_result) = ao_r5c->describeroutingcontrol(
            iv_routingcontrolarn = iv_routing_control_arn ).

          IF lo_describe_result IS BOUND AND lo_describe_result->has_routingcontrol( ) = abap_true.
            DATA(lo_routing_control) = lo_describe_result->get_routingcontrol( ).
            lv_status = lo_routing_control->get_status( ).

            MESSAGE |Routing control status: { lv_status }, waited { lv_elapsed } seconds| TYPE 'I'.

            IF lv_status = 'DEPLOYED'.
              rv_success = abap_true.
              RETURN.
            ELSEIF lv_status = 'FAILED' OR lv_status = 'PENDING_DELETION'.
              MESSAGE |Routing control deployment failed with status: { lv_status }| TYPE 'I'.
              RETURN.
            ENDIF.
          ENDIF.

        CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
          MESSAGE |Error checking routing control status: { lo_ex->if_message~get_text( ) }| TYPE 'I'.
      ENDTRY.

      WAIT UP TO lv_wait_interval SECONDS.
      lv_elapsed = lv_elapsed + lv_wait_interval.
    ENDWHILE.

    MESSAGE |Routing control did not become deployed within { lv_max_wait_time } seconds| TYPE 'I'.
  ENDMETHOD.

  METHOD class_setup.
    " Initialize AWS session and clients
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_r5c = /aws1/cl_r5c_factory=>create( ao_session ).
    ao_r5v_actions = NEW /awsex/cl_r5v_actions( ).

    " Setup IAM permissions
    TRY.
        setup_iam_permissions( ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_iam_ex).
        MESSAGE |Warning: IAM setup encountered issues: { lo_iam_ex->if_message~get_text( ) }| TYPE 'I'.
    ENDTRY.

    " Generate unique resource names using utility function
    DATA(lv_random_suffix) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_cluster_name) = |abap-ex-cluster-{ lv_random_suffix }|.
    DATA(lv_routing_control_name) = |abap-ex-rc-{ lv_random_suffix }|.

    " Create a cluster for testing - MUST NOT SKIP
    " Note: Cluster creation can take several minutes (5-15 minutes typically)
    MESSAGE |Starting cluster creation: { lv_cluster_name }| TYPE 'I'.

    TRY.
        DATA(lo_cluster_result) = ao_r5c->createcluster(
          iv_clustername = lv_cluster_name
          it_tags        = VALUE /aws1/cl_r5c__mapof__strmin000=>tt_tags(
            ( NEW /aws1/cl_r5c__mapof__strmin000(
                iv_key   = 'convert_test'
                iv_value = 'true' ) )
          )
        ).

        IF lo_cluster_result IS NOT BOUND OR lo_cluster_result->has_cluster( ) = abap_false.
          cl_abap_unit_assert=>fail(
            msg = |Failed to create cluster { lv_cluster_name }. Result object is not bound or has no cluster.| ).
        ENDIF.

        DATA(lo_cluster) = lo_cluster_result->get_cluster( ).
        av_cluster_arn = lo_cluster->get_clusterarn( ).

        " Extract cluster endpoints - REQUIRED for routing control operations
        DATA(lt_endpoints) = lo_cluster->get_clusterendpoints( ).
        IF lines( lt_endpoints ) = 0.
          cl_abap_unit_assert=>fail(
            msg = |Cluster { av_cluster_arn } has no endpoints| ).
        ENDIF.

        LOOP AT lt_endpoints INTO DATA(lo_endpoint).
          APPEND VALUE #(
            endpoint = lo_endpoint->get_endpoint( )
            region   = lo_endpoint->get_region( )
          ) TO at_cluster_endpoints.
        ENDLOOP.

        MESSAGE |Created cluster { av_cluster_arn } with { lines( at_cluster_endpoints ) } endpoints| TYPE 'I'.

      CATCH /aws1/cx_r5cconflictexception INTO DATA(lo_conflict_ex).
        " Cluster might already exist, try to describe it
        MESSAGE |Cluster conflict, attempting to use existing cluster| TYPE 'I'.

        TRY.
            " Build potential existing cluster ARN
            DATA(lv_account_id) = ao_session->get_account_id( ).
            DATA(lv_existing_arn) = |arn:aws:route53-recovery-control::{ lv_account_id }:cluster/{ lv_cluster_name }|.

            DATA(lo_describe_cluster) = ao_r5c->describecluster( iv_clusterarn = lv_existing_arn ).

            IF lo_describe_cluster IS NOT BOUND OR lo_describe_cluster->has_cluster( ) = abap_false.
              cl_abap_unit_assert=>fail(
                msg = |Conflict creating cluster but could not describe existing cluster: { lo_conflict_ex->if_message~get_text( ) }| ).
            ENDIF.

            lo_cluster = lo_describe_cluster->get_cluster( ).
            av_cluster_arn = lo_cluster->get_clusterarn( ).

            " Extract endpoints
            lt_endpoints = lo_cluster->get_clusterendpoints( ).
            LOOP AT lt_endpoints INTO lo_endpoint.
              APPEND VALUE #(
                endpoint = lo_endpoint->get_endpoint( )
                region   = lo_endpoint->get_region( )
              ) TO at_cluster_endpoints.
            ENDLOOP.

          CATCH /aws1/cx_rt_generic INTO DATA(lo_describe_ex).
            cl_abap_unit_assert=>fail(
              msg = |Error handling cluster conflict: { lo_describe_ex->if_message~get_text( ) }| ).
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_create_ex).
        cl_abap_unit_assert=>fail(
          msg = |Error creating cluster: { lo_create_ex->if_message~get_text( ) }| ).
    ENDTRY.

    " MUST wait for cluster to be deployed - DO NOT SKIP
    MESSAGE |Waiting for cluster to be deployed (this may take 5-15 minutes)...| TYPE 'I'.

    IF wait_for_cluster_deployed( av_cluster_arn ) = abap_false.
      cl_abap_unit_assert=>fail(
        msg = |Cluster { av_cluster_arn } did not become deployed in time. Tests cannot proceed.| ).
    ENDIF.

    MESSAGE |Cluster { av_cluster_arn } is now deployed| TYPE 'I'.

    " Get the default control panel ARN
    TRY.
        DATA(lo_list_panels) = ao_r5c->listcontrolpanels(
          iv_clusterarn = av_cluster_arn
          iv_maxresults = 1
        ).

        IF lo_list_panels IS BOUND AND lo_list_panels->has_controlpanels( ) = abap_true.
          DATA(lt_control_panels) = lo_list_panels->get_controlpanels( ).
          IF lines( lt_control_panels ) > 0.
            av_control_panel_arn = lt_control_panels[ 1 ]->get_controlpanelarn( ).
            MESSAGE |Using control panel: { av_control_panel_arn }| TYPE 'I'.
          ELSE.
            cl_abap_unit_assert=>fail(
              msg = |No control panels found for cluster { av_cluster_arn }| ).
          ENDIF.
        ELSE.
          cl_abap_unit_assert=>fail(
            msg = |Failed to list control panels for cluster { av_cluster_arn }| ).
        ENDIF.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_list_ex).
        cl_abap_unit_assert=>fail(
          msg = |Error listing control panels: { lo_list_ex->if_message~get_text( ) }| ).
    ENDTRY.

    " Create a routing control - REQUIRED for tests
    MESSAGE |Creating routing control: { lv_routing_control_name }| TYPE 'I'.

    TRY.
        DATA(lo_rc_result) = ao_r5c->createroutingcontrol(
          iv_clusterarn         = av_cluster_arn
          iv_routingcontrolname = lv_routing_control_name
          iv_controlpanelarn    = av_control_panel_arn
        ).

        IF lo_rc_result IS NOT BOUND OR lo_rc_result->has_routingcontrol( ) = abap_false.
          cl_abap_unit_assert=>fail(
            msg = |Failed to create routing control { lv_routing_control_name }| ).
        ENDIF.

        DATA(lo_routing_control) = lo_rc_result->get_routingcontrol( ).
        av_routing_control_arn = lo_routing_control->get_routingcontrolarn( ).

        MESSAGE |Created routing control { av_routing_control_arn }| TYPE 'I'.

      CATCH /aws1/cx_r5cconflictexception INTO DATA(lo_rc_conflict).
        " Routing control might already exist
        MESSAGE |Routing control conflict, trying to find existing control| TYPE 'I'.

        TRY.
            DATA(lo_list_rc) = ao_r5c->listroutingcontrols(
              iv_controlpanelarn = av_control_panel_arn
            ).

            IF lo_list_rc IS BOUND AND lo_list_rc->has_routingcontrols( ) = abap_true.
              DATA(lt_routing_controls) = lo_list_rc->get_routingcontrols( ).
              LOOP AT lt_routing_controls INTO DATA(lo_rc).
                IF lo_rc->get_name( ) = lv_routing_control_name.
                  av_routing_control_arn = lo_rc->get_routingcontrolarn( ).
                  EXIT.
                ENDIF.
              ENDLOOP.

              IF av_routing_control_arn IS INITIAL.
                cl_abap_unit_assert=>fail(
                  msg = |Conflict creating routing control but could not find existing control| ).
              ENDIF.
            ELSE.
              cl_abap_unit_assert=>fail(
                msg = |Conflict creating routing control and could not list existing controls| ).
            ENDIF.

          CATCH /aws1/cx_rt_generic INTO DATA(lo_list_rc_ex).
            cl_abap_unit_assert=>fail(
              msg = |Error handling routing control conflict: { lo_list_rc_ex->if_message~get_text( ) }| ).
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_rc_create_ex).
        cl_abap_unit_assert=>fail(
          msg = |Error creating routing control: { lo_rc_create_ex->if_message~get_text( ) }| ).
    ENDTRY.

    " MUST wait for routing control to be deployed - DO NOT SKIP
    MESSAGE |Waiting for routing control to be deployed...| TYPE 'I'.

    IF wait_for_routing_control( av_routing_control_arn ) = abap_false.
      cl_abap_unit_assert=>fail(
        msg = |Routing control { av_routing_control_arn } did not become deployed in time. Tests cannot proceed.| ).
    ENDIF.

    MESSAGE |Routing control { av_routing_control_arn } is now deployed| TYPE 'I'.

    " Final validation - ensure all required resources are ready
    IF av_cluster_arn IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Cluster ARN is not set' ).
    ENDIF.

    IF av_routing_control_arn IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Routing control ARN is not set' ).
    ENDIF.

    IF lines( at_cluster_endpoints ) = 0.
      cl_abap_unit_assert=>fail( msg = 'No cluster endpoints available' ).
    ENDIF.

    MESSAGE |Setup complete! Cluster: { av_cluster_arn }, Routing Control: { av_routing_control_arn }, Endpoints: { lines( at_cluster_endpoints ) }| TYPE 'I'.
  ENDMETHOD.

  METHOD class_teardown.
    " Clean up resources created during tests
    " Note: Cluster deletion takes a very long time (20-30 minutes) and we tag it for manual cleanup

    " Delete routing control first
    IF av_routing_control_arn IS NOT INITIAL.
      TRY.
          ao_r5c->deleteroutingcontrol( iv_routingcontrolarn = av_routing_control_arn ).
          MESSAGE |Deleted routing control: { av_routing_control_arn }| TYPE 'I'.

        CATCH /aws1/cx_r5cresourcenotfoundex.
          MESSAGE 'Routing control already deleted or not found' TYPE 'I'.

        CATCH /aws1/cx_rt_generic INTO DATA(lo_rc_ex).
          MESSAGE |Error deleting routing control: { lo_rc_ex->if_message~get_text( ) }| TYPE 'I'.
      ENDTRY.
    ENDIF.

    " DO NOT delete cluster here as it takes 20-30 minutes
    " Resources are tagged with 'convert_test' for manual cleanup
    IF av_cluster_arn IS NOT INITIAL.
      MESSAGE |Note: Cluster { av_cluster_arn } was NOT deleted automatically.| TYPE 'I'.
      MESSAGE |Cluster deletion takes 20-30 minutes and must be done manually.| TYPE 'I'.
      MESSAGE |Please delete it using the AWS console or CLI by filtering resources with tag 'convert_test=true'.| TYPE 'I'.
    ENDIF.

    " Clean up IAM role and policy
    IF av_test_role_name IS NOT INITIAL AND ao_iam IS BOUND.
      TRY.
          " Delete inline policy first
          ao_iam->deleterolepolicy(
            iv_rolename   = av_test_role_name
            iv_policyname = 'R5VTestPolicy'
          ).
          MESSAGE |Deleted inline policy from role { av_test_role_name }| TYPE 'I'.

        CATCH /aws1/cx_iamnosuchentityex.
          MESSAGE 'Inline policy already deleted or not found' TYPE 'I'.

        CATCH /aws1/cx_rt_generic INTO DATA(lo_policy_ex).
          MESSAGE |Error deleting inline policy: { lo_policy_ex->if_message~get_text( ) }| TYPE 'I'.
      ENDTRY.

      TRY.
          " Delete IAM role
          ao_iam->deleterole( iv_rolename = av_test_role_name ).
          MESSAGE |Deleted IAM role: { av_test_role_name }| TYPE 'I'.

        CATCH /aws1/cx_iamnosuchentityex.
          MESSAGE 'IAM role already deleted or not found' TYPE 'I'.

        CATCH /aws1/cx_rt_generic INTO DATA(lo_role_ex).
          MESSAGE |Error deleting IAM role: { lo_role_ex->if_message~get_text( ) }| TYPE 'I'.
      ENDTRY.
    ENDIF.

    MESSAGE 'Teardown complete' TYPE 'I'.
  ENDMETHOD.

  METHOD get_routing_control_state.
    " Test getting the routing control state
    DATA lo_result TYPE REF TO /aws1/cl_r5vgetroutingctlsta01.

    TRY.
        lo_result = ao_r5v_actions->get_routing_control_state(
          iv_routing_control_arn = av_routing_control_arn
          it_cluster_endpoints   = at_cluster_endpoints ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'GetRoutingControlState should return a result' ).

        DATA(lv_state) = lo_result->get_routingcontrolstate( ).
        DATA(lv_name) = lo_result->get_routingcontrolname( ).
        DATA(lv_arn) = lo_result->get_routingcontrolarn( ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_state
          msg = 'Routing control state should not be empty' ).

        cl_abap_unit_assert=>assert_true(
          act = xsdbool( lv_state = 'On' OR lv_state = 'Off' )
          msg = |Routing control state should be On or Off, got: { lv_state }| ).

        cl_abap_unit_assert=>assert_equals(
          exp = av_routing_control_arn
          act = lv_arn
          msg = 'Routing control ARN should match' ).

        MESSAGE |Successfully retrieved routing control state: { lv_state } for control: { lv_name }| TYPE 'I'.

      CATCH /aws1/cx_r5vaccessdeniedex INTO DATA(lo_access_ex).
        cl_abap_unit_assert=>fail(
          msg = |Access denied: { lo_access_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vendpttmpyunavailex INTO DATA(lo_endpoint_ex).
        cl_abap_unit_assert=>fail(
          msg = |All endpoints unavailable: { lo_endpoint_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vinternalserverex INTO DATA(lo_server_ex).
        cl_abap_unit_assert=>fail(
          msg = |Internal server error: { lo_server_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vresourcenotfoundex INTO DATA(lo_notfound_ex).
        cl_abap_unit_assert=>fail(
          msg = |Routing control not found: { lo_notfound_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vthrottlingex INTO DATA(lo_throttle_ex).
        cl_abap_unit_assert=>fail(
          msg = |Request throttled: { lo_throttle_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vvalidationex INTO DATA(lo_validation_ex).
        cl_abap_unit_assert=>fail(
          msg = |Validation error: { lo_validation_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_ex).
        cl_abap_unit_assert=>fail(
          msg = |Generic error: { lo_generic_ex->if_message~get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD update_routing_control_state.
    " Test updating the routing control state
    DATA lo_get_result TYPE REF TO /aws1/cl_r5vgetroutingctlsta01.
    DATA lo_update_result TYPE REF TO /aws1/cl_r5vuproutingctlstat01.

    TRY.
        " First, get the current state
        lo_get_result = ao_r5v_actions->get_routing_control_state(
          iv_routing_control_arn = av_routing_control_arn
          it_cluster_endpoints   = at_cluster_endpoints ).

        DATA(lv_current_state) = lo_get_result->get_routingcontrolstate( ).

        " Toggle the state
        DATA(lv_new_state) = COND /aws1/r5vroutingcontrolstate(
          WHEN lv_current_state = 'On' THEN 'Off'
          ELSE 'On' ).

        MESSAGE |Current state: { lv_current_state }, updating to: { lv_new_state }| TYPE 'I'.

        " Update the routing control state
        lo_update_result = ao_r5v_actions->update_routing_control_state(
          iv_routing_control_arn   = av_routing_control_arn
          it_cluster_endpoints     = at_cluster_endpoints
          iv_routing_control_state = lv_new_state ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_update_result
          msg = 'UpdateRoutingControlState should return a result' ).

        " Verify the state was updated by getting it again
        WAIT UP TO 2 SECONDS.

        lo_get_result = ao_r5v_actions->get_routing_control_state(
          iv_routing_control_arn = av_routing_control_arn
          it_cluster_endpoints   = at_cluster_endpoints ).

        DATA(lv_updated_state) = lo_get_result->get_routingcontrolstate( ).

        cl_abap_unit_assert=>assert_equals(
          exp = lv_new_state
          act = lv_updated_state
          msg = |Routing control state should be updated to { lv_new_state }, but got { lv_updated_state }| ).

        MESSAGE |Successfully updated routing control state from { lv_current_state } to { lv_updated_state }| TYPE 'I'.

        " Toggle it back to the original state for cleanup
        lo_update_result = ao_r5v_actions->update_routing_control_state(
          iv_routing_control_arn   = av_routing_control_arn
          it_cluster_endpoints     = at_cluster_endpoints
          iv_routing_control_state = lv_current_state ).

        MESSAGE |Restored routing control to original state: { lv_current_state }| TYPE 'I'.

      CATCH /aws1/cx_r5vaccessdeniedex INTO DATA(lo_access_ex).
        cl_abap_unit_assert=>fail(
          msg = |Access denied: { lo_access_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vconflictexception INTO DATA(lo_conflict_ex).
        cl_abap_unit_assert=>fail(
          msg = |Conflict error: { lo_conflict_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vendpttmpyunavailex INTO DATA(lo_endpoint_ex).
        cl_abap_unit_assert=>fail(
          msg = |All endpoints unavailable: { lo_endpoint_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vinternalserverex INTO DATA(lo_server_ex).
        cl_abap_unit_assert=>fail(
          msg = |Internal server error: { lo_server_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vresourcenotfoundex INTO DATA(lo_notfound_ex).
        cl_abap_unit_assert=>fail(
          msg = |Routing control not found: { lo_notfound_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vthrottlingex INTO DATA(lo_throttle_ex).
        cl_abap_unit_assert=>fail(
          msg = |Request throttled: { lo_throttle_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_r5vvalidationex INTO DATA(lo_validation_ex).
        cl_abap_unit_assert=>fail(
          msg = |Validation error: { lo_validation_ex->if_message~get_text( ) }| ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_generic_ex).
        cl_abap_unit_assert=>fail(
          msg = |Generic error: { lo_generic_ex->if_message~get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
