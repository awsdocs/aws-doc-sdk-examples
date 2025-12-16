" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_smr_actions DEFINITION DEFERRED.
CLASS /awsex/cl_smr_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_smr_actions.

CLASS ltc_awsex_cl_smr_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_smr TYPE REF TO /aws1/if_smr.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_smr_actions TYPE REF TO /awsex/cl_smr_actions.
    CLASS-DATA av_secret_name TYPE /aws1/smrsecretnametype.
    CLASS-DATA av_secret_name_2 TYPE /aws1/smrsecretnametype.
    CLASS-DATA av_secret_name_3 TYPE /aws1/smrsecretnametype.

    METHODS: get_secret_value FOR TESTING RAISING /aws1/cx_rt_generic,
             batch_get_secret_value FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_smr_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_smr = /aws1/cl_smr_factory=>create( ao_session ).
    ao_smr_actions = NEW /awsex/cl_smr_actions( ).

    " Generate unique secret names
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_secret_name = |abap-smr-test-secret-{ lv_uuid }|.
    av_secret_name_2 = |abap-smr-test-secret-{ lv_uuid }-2|.
    av_secret_name_3 = |abap-smr-test-secret-{ lv_uuid }-3|.

    " Create test secrets
    TRY.
        ao_smr->createsecret(
          iv_name = av_secret_name
          iv_secretstring = '{"username":"testuser","password":"testpass123"}'
          it_tags = VALUE /aws1/cl_smrtag=>tt_taglisttype(
            ( NEW /aws1/cl_smrtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).

        ao_smr->createsecret(
          iv_name = av_secret_name_2
          iv_secretstring = '{"api_key":"test-api-key-456"}'
          it_tags = VALUE /aws1/cl_smrtag=>tt_taglisttype(
            ( NEW /aws1/cl_smrtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).

        ao_smr->createsecret(
          iv_name = av_secret_name_3
          iv_secretstring = '{"database":"mydb","connection":"test-conn"}'
          it_tags = VALUE /aws1/cl_smrtag=>tt_taglisttype(
            ( NEW /aws1/cl_smrtag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        ).

        " Wait for secrets to propagate
        WAIT UP TO 5 SECONDS.

      CATCH /aws1/cx_smrresourceexistsex.
        " Secret already exists from previous test, continue
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up test secrets
    TRY.
        ao_smr->deletesecret(
          iv_secretid = av_secret_name
          iv_forcedeletewithoutrecovery = abap_true
        ).
      CATCH /aws1/cx_smrresourcenotfoundex.
        " Secret already deleted
    ENDTRY.

    TRY.
        ao_smr->deletesecret(
          iv_secretid = av_secret_name_2
          iv_forcedeletewithoutrecovery = abap_true
        ).
      CATCH /aws1/cx_smrresourcenotfoundex.
        " Secret already deleted
    ENDTRY.

    TRY.
        ao_smr->deletesecret(
          iv_secretid = av_secret_name_3
          iv_forcedeletewithoutrecovery = abap_true
        ).
      CATCH /aws1/cx_smrresourcenotfoundex.
        " Secret already deleted
    ENDTRY.

  ENDMETHOD.

  METHOD get_secret_value.
    DATA lv_secret_value TYPE /aws1/smrsecretstringtype.

    ao_smr_actions->get_secret_value(
      EXPORTING
        iv_secret_name = av_secret_name
      IMPORTING
        ov_secret_value = lv_secret_value
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_secret_value
      msg = |Secret value should not be empty|
    ).

    cl_abap_unit_assert=>assert_char_cp(
      act = lv_secret_value
      pattern = '*username*'
      msg = |Secret value should contain expected content|
    ).

  ENDMETHOD.

  METHOD batch_get_secret_value.
    DATA lt_secret_values TYPE /aws1/cl_smrsecretvalueentry=>tt_secretvaluestype.

    " Use a filter that matches the prefix of our test secrets
    " Extract just the prefix before the last hyphen
    DATA(lv_secret_prefix) = av_secret_name.
    FIND REGEX '(.+)-[^-]+$' IN lv_secret_prefix SUBMATCHES lv_secret_prefix.

    ao_smr_actions->batch_get_secret_value(
      EXPORTING
        iv_filter_name = lv_secret_prefix
      IMPORTING
        ot_secret_values = lt_secret_values
    ).

    " Should have retrieved at least one secret
    cl_abap_unit_assert=>assert_true(
      act = xsdbool( lines( lt_secret_values ) >= 1 )
      msg = |Should have retrieved at least one secret|
    ).

    " Verify that at least one secret has the expected content
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_secret_values INTO DATA(lo_secret_entry).
      DATA(lv_secret_string) = lo_secret_entry->get_secretstring( ).
      IF lv_secret_string IS NOT INITIAL.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |At least one secret should have non-empty content|
    ).

  ENDMETHOD.

ENDCLASS.
