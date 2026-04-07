" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_iop_actions DEFINITION DEFERRED.
CLASS /awsex/cl_iop_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_iop_actions.

CLASS ltc_awsex_cl_iop_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_iot TYPE REF TO /aws1/if_iot.
    CLASS-DATA ao_iop TYPE REF TO /aws1/if_iop.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_iop_actions TYPE REF TO /awsex/cl_iop_actions.

    CLASS-DATA av_thing_name TYPE /aws1/iopthingname.

    METHODS: update_thing_shadow FOR TESTING RAISING /aws1/cx_rt_generic,
      get_thing_shadow FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.
    
    CLASS-METHODS wait_for_thing_ready
      IMPORTING
        iv_thing_name TYPE /aws1/iotthingname
        iv_max_wait   TYPE i DEFAULT 30
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_iop_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_iot = /aws1/cl_iot_factory=>create( ao_session ).
    ao_iop = /aws1/cl_iop_factory=>create( ao_session ).
    ao_iop_actions = NEW /awsex/cl_iop_actions( ).

    " Create a test thing for shadow operations - MUST succeed
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_thing_name = |sap-abap-iot-shadow-{ lv_uuid }|.

    TRY.
        DATA(lo_thing_result) = ao_iot->creatething( iv_thingname = av_thing_name ).

        IF lo_thing_result IS NOT BOUND.
          MESSAGE 'Failed to create IoT thing for shadow tests' TYPE 'E'.
        ENDIF.

        " Verify thing was created
        cl_abap_unit_assert=>assert_not_initial(
          act = lo_thing_result->get_thingname( )
          msg = |Thing name is initial after creation| ).

      CATCH /aws1/cx_iotresrcalrdyexistsex.
        " Thing already exists - verify it's accessible
        TRY.
            ao_iot->describething( iv_thingname = av_thing_name ).
          CATCH /aws1/cx_rt_generic INTO DATA(lo_desc_ex).
            MESSAGE |Thing exists but cannot be accessed: { lo_desc_ex->get_text( ) }| TYPE 'E'.
        ENDTRY.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_create_ex).
        MESSAGE |Failed to create thing: { lo_create_ex->get_text( ) }| TYPE 'E'.
    ENDTRY.

    " Wait for thing to be fully ready
    wait_for_thing_ready( iv_thing_name = av_thing_name ).

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up test thing
    IF av_thing_name IS NOT INITIAL.
      TRY.
          " Delete the thing shadow first if it exists
          TRY.
              ao_iop->deletethingshadow( iv_thingname = av_thing_name ).
              " Wait for shadow deletion to propagate
              WAIT UP TO 2 SECONDS.
            CATCH /aws1/cx_rt_generic.
              " Shadow may not exist
          ENDTRY.

          " Delete the thing
          ao_iot->deletething( iv_thingname = av_thing_name ).
        CATCH /aws1/cx_rt_generic.
          " Thing may not exist or may have dependencies
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD wait_for_thing_ready.
    " Wait for thing to be ready with status-based polling
    DATA lv_elapsed TYPE i VALUE 0.
    DATA lv_ready TYPE abap_bool VALUE abap_false.
    DATA lv_start_time TYPE timestampl.
    DATA lv_current_time TYPE timestampl.

    GET TIME STAMP FIELD lv_start_time.

    WHILE lv_elapsed < iv_max_wait AND lv_ready = abap_false.
      TRY.
          " Try to describe the thing
          DATA(lo_thing_desc) = ao_iot->describething( iv_thingname = iv_thing_name ).
          IF lo_thing_desc IS BOUND.
            lv_ready = abap_true.
          ELSE.
            WAIT UP TO 2 SECONDS.
            GET TIME STAMP FIELD lv_current_time.
            lv_elapsed = lv_current_time - lv_start_time.
          ENDIF.

        CATCH /aws1/cx_rt_generic.
          " Thing not ready yet
          WAIT UP TO 2 SECONDS.
          GET TIME STAMP FIELD lv_current_time.
          lv_elapsed = lv_current_time - lv_start_time.
      ENDTRY.
    ENDWHILE.

    IF lv_ready = abap_false.
      MESSAGE 'Thing did not become ready in time' TYPE 'E'.
    ENDIF.

  ENDMETHOD.

  METHOD update_thing_shadow.
    " Verify thing exists
    TRY.
        ao_iot->describething( iv_thingname = av_thing_name ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_verify_ex).
        MESSAGE |Thing does not exist: { lo_verify_ex->get_text( ) }| TYPE 'E'.
    ENDTRY.

    " Create a shadow state JSON
    DATA lv_shadow_state TYPE string.
    lv_shadow_state =
      '{"state":{"desired":{"temperature":25,"humidity":60},"reported":{"temperature":24,"humidity":58}}}'.

    " Update thing shadow - this MUST succeed
    ao_iop_actions->update_thing_shadow(
      iv_thing_name = av_thing_name
      iv_shadow_state = lv_shadow_state ).

    " Wait for shadow to be updated
    WAIT UP TO 3 SECONDS.

    " Verify shadow was updated by getting it - MUST be retrievable
    DATA(lo_shadow_result) = ao_iop->getthingshadow( iv_thingname = av_thing_name ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_shadow_result
      msg = |Shadow result is not bound| ).

    DATA(lv_payload) = lo_shadow_result->get_payload( ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_payload
      msg = |Shadow payload is empty| ).

    " Convert payload to string and verify it contains expected data
    DATA(lv_shadow_json) = /aws1/cl_rt_util=>xstring_to_string( lv_payload ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_shadow_json
      msg = |Shadow JSON is empty| ).

    " Verify shadow structure - MUST contain expected fields
    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow_json sub = 'state' )
      exp = -1
      msg = |Shadow does not contain state field| ).

    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow_json sub = 'desired' )
      exp = -1
      msg = |Shadow does not contain desired state| ).

    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow_json sub = 'reported' )
      exp = -1
      msg = |Shadow does not contain reported state| ).

    " Verify our test values are present
    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow_json sub = 'temperature' )
      exp = -1
      msg = |Shadow does not contain temperature field| ).

    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow_json sub = 'humidity' )
      exp = -1
      msg = |Shadow does not contain humidity field| ).

  ENDMETHOD.

  METHOD get_thing_shadow.
    " Verify thing exists
    TRY.
        ao_iot->describething( iv_thingname = av_thing_name ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_verify_ex).
        MESSAGE |Thing does not exist: { lo_verify_ex->get_text( ) }| TYPE 'E'.
    ENDTRY.

    " First ensure shadow exists by updating it
    DATA lv_shadow_state TYPE string.
    lv_shadow_state =
      '{"state":{"desired":{"power":"on","level":75},"reported":{"power":"on","level":70}}}'.

    TRY.
        ao_iop->updatethingshadow(
          iv_thingname = av_thing_name
          iv_payload = /aws1/cl_rt_util=>string_to_xstring( lv_shadow_state ) ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_update_ex).
        MESSAGE |Failed to create shadow for test: { lo_update_ex->get_text( ) }| TYPE 'E'.
    ENDTRY.

    " Wait for shadow to be updated with status-based polling
    DATA lv_shadow_ready TYPE abap_bool VALUE abap_false.
    DATA lv_retry_count TYPE i VALUE 0.

    WHILE lv_retry_count < 10 AND lv_shadow_ready = abap_false.
      TRY.
          DATA(lo_check_shadow) = ao_iop->getthingshadow( iv_thingname = av_thing_name ).
          IF lo_check_shadow IS BOUND AND lo_check_shadow->get_payload( ) IS NOT INITIAL.
            lv_shadow_ready = abap_true.
          ELSE.
            lv_retry_count = lv_retry_count + 1.
            WAIT UP TO 2 SECONDS.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          lv_retry_count = lv_retry_count + 1.
          IF lv_retry_count < 10.
            WAIT UP TO 2 SECONDS.
          ELSE.
            MESSAGE 'Shadow did not become ready in time' TYPE 'E'.
          ENDIF.
      ENDTRY.
    ENDWHILE.

    IF lv_shadow_ready = abap_false.
      MESSAGE 'Shadow was not created successfully' TYPE 'E'.
    ENDIF.

    " Now get the shadow using our action method - this MUST succeed
    DATA(lv_shadow) = ao_iop_actions->get_thing_shadow( iv_thing_name = av_thing_name ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_shadow
      msg = |Shadow is empty| ).

    " Verify shadow contains expected structure - MUST contain these fields
    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow sub = 'state' )
      exp = -1
      msg = |Shadow does not contain state| ).

    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow sub = 'desired' )
      exp = -1
      msg = |Shadow does not contain desired state| ).

    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow sub = 'reported' )
      exp = -1
      msg = |Shadow does not contain reported state| ).

    " Verify it contains our test values - MUST contain these fields
    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow sub = 'power' )
      exp = -1
      msg = |Shadow does not contain power field| ).

    cl_abap_unit_assert=>assert_differs(
      act = find( val = lv_shadow sub = 'level' )
      exp = -1
      msg = |Shadow does not contain level field| ).

    " Verify the actual values are correct
    cl_abap_unit_assert=>assert_true(
      act = xsdbool( find( val = lv_shadow sub = '75' ) >= 0 OR find( val = lv_shadow sub = '"level":75' ) >= 0 )
      msg = |Shadow does not contain expected level value 75| ).

  ENDMETHOD.

ENDCLASS.
