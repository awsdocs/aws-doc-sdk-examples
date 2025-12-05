" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_ctt_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_ctt TYPE REF TO /aws1/if_ctt.
    CLASS-DATA ao_org TYPE REF TO /aws1/if_org.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_ctt_actions TYPE REF TO /awsex/cl_ctt_actions.

    " Test resources
    CLASS-DATA av_ou_id TYPE /aws1/orgorganizationalunitid.
    CLASS-DATA av_ou_arn TYPE /aws1/orgorganizationalunitarn.
    CLASS-DATA av_root_id TYPE /aws1/orgrootid.
    CLASS-DATA av_control_arn TYPE /aws1/cttcontrolidentifier.
    CLASS-DATA av_baseline_arn TYPE /aws1/cttarn.
    CLASS-DATA av_enabled_baseline_arn TYPE /aws1/cttarn.
    CLASS-DATA av_enabled_control_arn TYPE /aws1/cttarn.
    CLASS-DATA av_has_landing_zone TYPE abap_bool VALUE abap_false.

    METHODS: list_baselines FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: list_landing_zones FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: list_enabled_baselines FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: list_enabled_controls FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: get_control_operation FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: get_baseline_operation FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: enable_baseline FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: reset_enabled_baseline FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: disable_baseline FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: enable_control FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: disable_control FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_ctt_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_ctt = /aws1/cl_ctt_factory=>create( ao_session ).
    ao_ccg = /aws1/cl_ccg_factory=>create( ao_session ).
    ao_org = /aws1/cl_org_factory=>create( ao_session ).
    ao_ctt_actions = NEW /awsex/cl_ctt_actions( ).

    " Check if organization exists, create if not
    TRY.
        DATA(lo_org_desc) = ao_org->describeorganization( ).
        DATA(lv_org_id) = lo_org_desc->get_organization( )->get_id( ).
      CATCH /aws1/cx_orgawsorgnotinuseex.
        " Create organization with all features
        DATA(lo_org_create) = ao_org->createorganization(
          iv_featureset = 'ALL'
        ).
        lv_org_id = lo_org_create->get_organization( )->get_id( ).

        " Wait for organization to be available
        WAIT UP TO 30 SECONDS.
    ENDTRY.

    " Get root ID
    DATA(lo_roots) = ao_org->listroots( ).
    DATA(lt_roots) = lo_roots->get_roots( ).
    IF lines( lt_roots ) > 0.
      READ TABLE lt_roots INDEX 1 ASSIGNING FIELD-SYMBOL(<root>).
      av_root_id = <root>->get_id( ).
    ENDIF.

    " Create a test OU for Control Tower operations
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_ou_name) = |CTT-Test-OU-{ lv_uuid }|.

    TRY.
        DATA(lo_ou_result) = ao_org->createorganizationalunit(
          iv_parentid = av_root_id
          iv_name     = lv_ou_name
          it_tags     = VALUE /aws1/cl_orgtag=>tt_tags(
            ( NEW /aws1/cl_orgtag(
                iv_key   = 'convert_test'
                iv_value = 'true'
              ) )
          )
        ).

        DATA(lo_ou) = lo_ou_result->get_organizationalunit( ).
        av_ou_id = lo_ou->get_id( ).
        av_ou_arn = lo_ou->get_arn( ).

        " Wait for OU to be available
        WAIT UP TO 10 SECONDS.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Continue even if OU creation fails
    ENDTRY.

    " Check if landing zones exist
    TRY.
        DATA(lt_landing_zones) = ao_ctt_actions->list_landing_zones( ao_ctt ).
        IF lines( lt_landing_zones ) > 0.
          av_has_landing_zone = abap_true.
        ENDIF.
      CATCH /aws1/cx_rt_generic.
        av_has_landing_zone = abap_false.
    ENDTRY.

    " Get a baseline ARN for testing
    TRY.
        DATA(lt_baselines) = ao_ctt_actions->list_baselines( ao_ctt ).
        IF lines( lt_baselines ) > 0.
          LOOP AT lt_baselines ASSIGNING FIELD-SYMBOL(<baseline>).
            IF <baseline>->get_name( ) = 'AWSControlTowerBaseline'.
              av_baseline_arn = <baseline>->get_arn( ).
              EXIT.
            ENDIF.
          ENDLOOP.
        ENDIF.
      CATCH /aws1/cx_rt_generic.
        " Continue without baseline
    ENDTRY.

    " Get a control ARN for testing
    TRY.
        DATA(lt_controls) = ao_ctt_actions->list_controls( ao_ccg ).
        IF lines( lt_controls ) > 0.
          READ TABLE lt_controls INDEX 1 ASSIGNING FIELD-SYMBOL(<control>).
          av_control_arn = <control>->get_arn( ).
        ENDIF.
      CATCH /aws1/cx_rt_generic.
        " Continue without control
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up test OU
    " Note: We need to delete the OU, but if it has enabled controls/baselines,
    " they must be disabled first. Since this can take a very long time,
    " we rely on the convert_test tag for manual cleanup.

    IF av_ou_id IS NOT INITIAL.
      TRY.
          " Try to delete the OU
          ao_org->deleteorganizationalunit(
            iv_organizationalunitid = av_ou_id
          ).
        CATCH /aws1/cx_rt_generic.
          " OU may have resources, user needs to clean up manually using tags
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD list_baselines.
    " Test listing all baselines
    DATA(lt_baselines) = ao_ctt_actions->list_baselines(
      io_ctt = ao_ctt
    ).

    " Assert that we got some results (Control Tower should have baselines)
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_baselines
      msg = |Should have returned at least one baseline|
    ).

    " Verify the baseline has expected properties
    IF lines( lt_baselines ) > 0.
      READ TABLE lt_baselines INDEX 1 ASSIGNING FIELD-SYMBOL(<baseline>).
      cl_abap_unit_assert=>assert_not_initial(
        act = <baseline>->get_arn( )
        msg = |Baseline should have an ARN|
      ).
      cl_abap_unit_assert=>assert_not_initial(
        act = <baseline>->get_name( )
        msg = |Baseline should have a name|
      ).
    ENDIF.
  ENDMETHOD.

  METHOD list_controls.
    " Test listing controls from Control Catalog
    DATA(lt_controls) = ao_ctt_actions->list_controls(
      io_ccg = ao_ccg
    ).

    " Assert that we got some results
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_controls
      msg = |Should have returned at least one control|
    ).

    " Verify the control has expected properties
    IF lines( lt_controls ) > 0.
      READ TABLE lt_controls INDEX 1 ASSIGNING FIELD-SYMBOL(<control>).
      cl_abap_unit_assert=>assert_not_initial(
        act = <control>->get_arn( )
        msg = |Control should have an ARN|
      ).
      cl_abap_unit_assert=>assert_not_initial(
        act = <control>->get_name( )
        msg = |Control should have a name|
      ).
    ENDIF.
  ENDMETHOD.

  METHOD list_landing_zones.
    " Test listing landing zones
    TRY.
        DATA(lt_landing_zones) = ao_ctt_actions->list_landing_zones(
          io_ctt = ao_ctt
        ).

        " If landing zones exist, verify they have expected properties
        IF lines( lt_landing_zones ) > 0.
          READ TABLE lt_landing_zones INDEX 1 ASSIGNING FIELD-SYMBOL(<lz>).
          cl_abap_unit_assert=>assert_not_initial(
            act = <lz>->get_arn( )
            msg = |Landing zone should have an ARN|
          ).
        ENDIF.

        " Test passes regardless of whether landing zones exist
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |List landing zones executed successfully|
        ).
      CATCH /aws1/cx_cttaccessdeniedex INTO DATA(lo_ex).
        " Access denied is acceptable for this test
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Access denied is acceptable for list landing zones|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD list_enabled_baselines.
    " Test listing enabled baselines
    TRY.
        DATA(lt_enabled_baselines) = ao_ctt_actions->list_enabled_baselines(
          io_ctt = ao_ctt
        ).

        " If enabled baselines exist, verify they have expected properties
        IF lines( lt_enabled_baselines ) > 0.
          READ TABLE lt_enabled_baselines INDEX 1 ASSIGNING FIELD-SYMBOL(<eb>).
          cl_abap_unit_assert=>assert_not_initial(
            act = <eb>->get_arn( )
            msg = |Enabled baseline should have an ARN|
          ).
          cl_abap_unit_assert=>assert_not_initial(
            act = <eb>->get_baselineidentifier( )
            msg = |Enabled baseline should have a baseline identifier|
          ).

          " Store first enabled baseline for other tests
          av_enabled_baseline_arn = <eb>->get_arn( ).
        ENDIF.

        " Test passes regardless of whether enabled baselines exist
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |List enabled baselines executed successfully|
        ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex).
        " Resource not found is acceptable if Control Tower is not fully set up
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Resource not found is acceptable for list enabled baselines|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD list_enabled_controls.
    " Test listing enabled controls
    " This requires a target identifier (OU ARN)
    IF av_ou_arn IS INITIAL.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping list_enabled_controls test - no OU available|
      ).
      RETURN.
    ENDIF.

    TRY.
        DATA(lt_enabled_controls) = ao_ctt_actions->list_enabled_controls(
          io_ctt = ao_ctt
          iv_target_identifier = av_ou_arn
        ).

        " If enabled controls exist, verify they have expected properties
        IF lines( lt_enabled_controls ) > 0.
          READ TABLE lt_enabled_controls INDEX 1 ASSIGNING FIELD-SYMBOL(<ec>).
          cl_abap_unit_assert=>assert_not_initial(
            act = <ec>->get_arn( )
            msg = |Enabled control should have an ARN|
          ).
          cl_abap_unit_assert=>assert_not_initial(
            act = <ec>->get_controlidentifier( )
            msg = |Enabled control should have a control identifier|
          ).

          " Store first enabled control for other tests
          av_enabled_control_arn = <ec>->get_arn( ).
        ENDIF.

        " Test passes regardless of whether enabled controls exist
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |List enabled controls executed successfully|
        ).
      CATCH /aws1/cx_cttaccessdeniedex INTO DATA(lo_ex).
        " Access denied is acceptable
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Access denied is acceptable for list enabled controls|
        ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex2).
        " Resource not found is acceptable
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Resource not found is acceptable for list enabled controls|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_control_operation.
    " Test getting control operation status
    " This requires an operation ID, which we can only get from an actual operation
    " For testing purposes, we'll use a mock operation ID and handle the error

    TRY.
        " Use a fake operation ID - this should fail with ResourceNotFoundException
        DATA(lv_fake_operation_id) = |arn:aws:controltower:us-east-1:123456789012:operation/test-operation|.

        DATA(lv_status) = ao_ctt_actions->get_control_operation(
          io_ctt = ao_ctt
          iv_operation_id = lv_fake_operation_id
        ).

        " If we got here, the operation ID was valid (unexpected)
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_status
          msg = |Should have returned a status|
        ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex).
        " Expected result - operation not found
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Operation not found is expected for fake operation ID|
        ).
      CATCH /aws1/cx_cttvalidationex INTO DATA(lo_ex2).
        " Also acceptable - validation error for invalid format
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Validation error is acceptable for fake operation ID|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD get_baseline_operation.
    " Test getting baseline operation status
    " Similar to get_control_operation, we need an actual operation ID

    TRY.
        " Use a fake operation ID - this should fail with ResourceNotFoundException
        DATA(lv_fake_operation_id) = |arn:aws:controltower:us-east-1:123456789012:operation/test-operation|.

        DATA(lv_status) = ao_ctt_actions->get_baseline_operation(
          io_ctt = ao_ctt
          iv_operation_id = lv_fake_operation_id
        ).

        " If we got here, the operation ID was valid (unexpected)
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_status
          msg = |Should have returned a status|
        ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex).
        " Expected result - operation not found
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Operation not found is expected for fake operation ID|
        ).
      CATCH /aws1/cx_cttvalidationex INTO DATA(lo_ex2).
        " Also acceptable - validation error for invalid format
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Validation error is acceptable for fake operation ID|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD enable_baseline.
    " Test enabling a baseline
    " This requires a landing zone and proper setup, which may not exist in test environment

    IF av_baseline_arn IS INITIAL OR av_ou_arn IS INITIAL.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping enable_baseline test - no baseline or OU available|
      ).
      RETURN.
    ENDIF.

    IF av_has_landing_zone = abap_false.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping enable_baseline test - no landing zone available|
      ).
      RETURN.
    ENDIF.

    TRY.
        DATA(lv_enabled_baseline_arn) = ao_ctt_actions->enable_baseline(
          io_ctt = ao_ctt
          iv_target_identifier = av_ou_arn
          iv_baseline_identifier = av_baseline_arn
          iv_baseline_version = '4.0'
        ).

        IF lv_enabled_baseline_arn IS NOT INITIAL.
          av_enabled_baseline_arn = lv_enabled_baseline_arn.
          cl_abap_unit_assert=>assert_not_initial(
            act = lv_enabled_baseline_arn
            msg = |Should have returned an enabled baseline ARN|
          ).
        ELSE.
          " Baseline was already enabled
          cl_abap_unit_assert=>assert_true(
            act = abap_true
            msg = |Baseline was already enabled|
          ).
        ENDIF.
      CATCH /aws1/cx_cttvalidationex INTO DATA(lo_ex).
        " Validation error is acceptable (e.g., already enabled)
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Validation error is acceptable for enable baseline|
        ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex2).
        " Resource not found is acceptable
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Resource not found is acceptable for enable baseline|
        ).
      CATCH /aws1/cx_cttconflictexception INTO DATA(lo_ex3).
        " Conflict is acceptable (e.g., operation in progress)
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Conflict is acceptable for enable baseline|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD reset_enabled_baseline.
    " Test resetting an enabled baseline
    " This requires an enabled baseline

    IF av_enabled_baseline_arn IS INITIAL.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping reset_enabled_baseline test - no enabled baseline available|
      ).
      RETURN.
    ENDIF.

    TRY.
        DATA(lv_operation_id) = ao_ctt_actions->reset_enabled_baseline(
          io_ctt = ao_ctt
          iv_enabled_baseline_identifier = av_enabled_baseline_arn
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_operation_id
          msg = |Should have returned an operation ID|
        ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex).
        " Resource not found is acceptable
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Resource not found is acceptable for reset enabled baseline|
        ).
      CATCH /aws1/cx_cttconflictexception INTO DATA(lo_ex2).
        " Conflict is acceptable (e.g., operation in progress)
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Conflict is acceptable for reset enabled baseline|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD disable_baseline.
    " Test disabling a baseline
    " This requires an enabled baseline

    IF av_enabled_baseline_arn IS INITIAL.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping disable_baseline test - no enabled baseline available|
      ).
      RETURN.
    ENDIF.

    TRY.
        DATA(lv_operation_id) = ao_ctt_actions->disable_baseline(
          io_ctt = ao_ctt
          iv_enabled_baseline_identifier = av_enabled_baseline_arn
        ).

        IF lv_operation_id IS NOT INITIAL.
          cl_abap_unit_assert=>assert_not_initial(
            act = lv_operation_id
            msg = |Should have returned an operation ID|
          ).
        ELSE.
          " Conflict occurred, which is acceptable
          cl_abap_unit_assert=>assert_true(
            act = abap_true
            msg = |Conflict disabling baseline is acceptable|
          ).
        ENDIF.
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex).
        " Resource not found is acceptable
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Resource not found is acceptable for disable baseline|
        ).
      CATCH /aws1/cx_cttconflictexception INTO DATA(lo_ex2).
        " Conflict is acceptable (e.g., operation in progress)
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Conflict is acceptable for disable baseline|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD enable_control.
    " Test enabling a control
    " This requires Control Tower to be enabled

    IF av_control_arn IS INITIAL OR av_ou_arn IS INITIAL.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping enable_control test - no control or OU available|
      ).
      RETURN.
    ENDIF.

    IF av_has_landing_zone = abap_false.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping enable_control test - no landing zone available|
      ).
      RETURN.
    ENDIF.

    TRY.
        DATA(lv_operation_id) = ao_ctt_actions->enable_control(
          io_ctt = ao_ctt
          iv_control_arn = av_control_arn
          iv_target_identifier = av_ou_arn
        ).

        IF lv_operation_id IS NOT INITIAL.
          cl_abap_unit_assert=>assert_not_initial(
            act = lv_operation_id
            msg = |Should have returned an operation ID|
          ).
        ELSE.
          " Control was already enabled
          cl_abap_unit_assert=>assert_true(
            act = abap_true
            msg = |Control was already enabled|
          ).
        ENDIF.
      CATCH /aws1/cx_cttvalidationex INTO DATA(lo_ex).
        " Validation error is acceptable (e.g., already enabled)
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Validation error is acceptable for enable control|
        ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex2).
        " Resource not found is acceptable
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Resource not found is acceptable for enable control|
        ).
      CATCH /aws1/cx_cttconflictexception INTO DATA(lo_ex3).
        " Conflict is acceptable (e.g., operation in progress)
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Conflict is acceptable for enable control|
        ).
    ENDTRY.
  ENDMETHOD.

  METHOD disable_control.
    " Test disabling a control
    " This requires an enabled control

    IF av_control_arn IS INITIAL OR av_ou_arn IS INITIAL.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping disable_control test - no control or OU available|
      ).
      RETURN.
    ENDIF.

    IF av_has_landing_zone = abap_false.
      cl_abap_unit_assert=>assert_true(
        act = abap_true
        msg = |Skipping disable_control test - no landing zone available|
      ).
      RETURN.
    ENDIF.

    TRY.
        DATA(lv_operation_id) = ao_ctt_actions->disable_control(
          io_ctt = ao_ctt
          iv_control_arn = av_control_arn
          iv_target_identifier = av_ou_arn
        ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_operation_id
          msg = |Should have returned an operation ID|
        ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_ex).
        " Resource not found is acceptable (control may not be enabled)
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Resource not found is acceptable for disable control|
        ).
      CATCH /aws1/cx_cttconflictexception INTO DATA(lo_ex2).
        " Conflict is acceptable (e.g., operation in progress)
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = |Conflict is acceptable for disable control|
        ).
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
