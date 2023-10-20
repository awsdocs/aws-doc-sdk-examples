" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_bdr_actions DEFINITION DEFERRED.
CLASS zcl_aws1_bdr_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_bdr_actions.

CLASS ltc_zcl_aws1_bdr_actions DEFINITION FOR TESTING  DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_bdr TYPE REF TO /aws1/if_bdr.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_bdr_actions TYPE REF TO zcl_aws1_bdr_actions.

    METHODS: test_claude_v2 FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: test_stable_diffusion FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS: setup RAISING /aws1/cx_rt_generic.

ENDCLASS.
CLASS ltc_zcl_aws1_bdr_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_bdr = /aws1/cl_bdr_factory=>create( ao_session ).
    ao_bdr_actions = NEW zcl_aws1_bdr_actions( ).
  ENDMETHOD.
  METHOD test_claude_v2.
    DATA(lv_joke) = ao_bdr_actions->prompt_claude_v2(
      'Tell me a joke about ABAP programmers and Java programmers walking into a bar'
    ).
    cl_abap_unit_assert=>assert_not_initial( act = lv_joke ).
  ENDMETHOD.

  METHOD test_stable_diffusion.
    DATA(lv_joke) = ao_bdr_actions->prompt_stable_diffusion(
      'Show me a picture of a kitten coding in ABAP on an SAP system'
    ).
    cl_abap_unit_assert=>assert_not_initial( act = lv_joke ).
  ENDMETHOD.

ENDCLASS.
