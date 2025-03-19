" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_zcl_aws1_bdz_actions DEFINITION FOR TESTING
    DURATION SHORT
    RISK LEVEL DANGEROUS.
  PROTECTED SECTION.
    METHODS test_invoke_agent FOR TESTING RAISING /aws1/cx_rt_generic cx_uuid_error.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    CONSTANTS cv_bdz_lrole TYPE string VALUE 'YMIT1_BDZ_ROLEARN'.
    CONSTANTS cv_foundationmodel TYPE string VALUE 'us.amazon.nova-micro-v1:0'.
    CONSTANTS cv_alias TYPE string VALUE 'live'.
    CONSTANTS cv_action_group_name TYPE string VALUE 'action_group'.

    DATA av_func_name_fail TYPE /aws1/lmdfunctionname.
    DATA ao_alias TYPE REF TO /aws1/cl_bdaagentalias.
    DATA ao_bda TYPE REF TO /aws1/if_bda.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA av_bdz_rolearn TYPE /aws1/bdaagentrolearn.

    METHODS setup RAISING /aws1/cx_rt_generic zcx_aws1_ex_generic.
    METHODS teardown RAISING /aws1/cx_rt_service_generic /aws1/cx_rt_technical_generic /aws1/cx_rt_generic zcx_aws1_ex_generic.

    METHODS wait_for_agent_status
      IMPORTING iv_agentid      TYPE string
                iv_status       TYPE string
      RETURNING VALUE(oo_agent) TYPE REF TO /aws1/cl_bdaagent
      RAISING   /aws1/cx_rt_generic
                zcx_aws1_ex_generic.

    METHODS    wait_for_agent_alias_status
      IMPORTING iv_agentid      TYPE string
                iv_aliasid      TYPE string
                iv_status       TYPE string
      RETURNING VALUE(oo_alias) TYPE REF TO /aws1/cl_bdaagentalias
      RAISING   /aws1/cx_rt_generic
                zcx_aws1_ex_generic.

    METHODS prepare
      IMPORTING iv_agentid      TYPE string
      RETURNING VALUE(oo_agent) TYPE REF TO /aws1/cl_bdaagent
      RAISING   /aws1/cx_rt_generic zcx_aws1_ex_generic.

ENDCLASS.



CLASS ltc_ZCL_AWS1_BDZ_ACTIONS IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_bda = /aws1/cl_bda_factory=>create( ao_session ).
    av_bdz_rolearn = ao_session->resolve_lresource( cv_bdz_lrole ).

    DATA(lv_random_string) = zcl_aws1_ex_utils=>get_random_string( ).

    DATA(lv_instruction) = |You are an agent that plays "rock, paper, scissors". Choose rock, | &&
                           |paper, or scissors.. When the human prompts you with | &&
                           |their choice, reveal your choice and declare the winner. |.

    DATA(lo_agent) = ao_bda->createagent(
      iv_agentname = |{ zcl_aws1_ex_utils=>cv_asset_prefix }-bdragt-{ lv_random_string }|
      iv_foundationmodel = cv_foundationmodel
      iv_agentresourcerolearn = av_bdz_rolearn
      iv_instruction = lv_instruction )->get_agent( ).
    DATA(lv_agentid) = lo_agent->get_agentid( ).

    wait_for_agent_status( iv_agentid = lv_agentid
                           iv_status = |NOT_PREPARED| ).

    " get draft version
    prepare( lv_agentid ).

    ao_alias = ao_bda->createagentalias( iv_agentid = lv_agentid
                                         iv_agentaliasname = cv_alias )->get_agentalias( ).
    wait_for_agent_alias_status( iv_agentid = lv_agentid
                                 iv_aliasid = ao_alias->get_agentaliasid( )
                                 iv_status = |PREPARED| ).

  ENDMETHOD.

  METHOD prepare.
    ao_bda->prepareagent( iv_agentid = iv_agentid ).
    oo_agent = wait_for_agent_status( iv_agentid = iv_agentid
                                      iv_status = |PREPARED| ).
  ENDMETHOD.

  METHOD wait_for_agent_status.
    oo_agent = ao_bda->getagent( iv_agentid )->get_agent( ).
    WHILE oo_agent->get_agentstatus( ) <> iv_status.
      WAIT UP TO 2 SECONDS.
      IF sy-index > 20.
        RAISE EXCEPTION TYPE zcx_aws1_ex_generic
          EXPORTING
            av_msg = |Bedrock agent { iv_agentid } never reached status { iv_status }|.
      ENDIF.
      oo_agent = ao_bda->getagent( iv_agentid )->get_agent( ).
    ENDWHILE.
  ENDMETHOD.

  METHOD wait_for_agent_alias_status.
    oo_alias = ao_bda->getagentalias( iv_agentid = iv_agentid
                                      iv_agentaliasid = iv_aliasid )->get_agentalias( ).
    WHILE oo_alias->get_agentaliasstatus( ) <> iv_status.
      WAIT UP TO 2 SECONDS.
      IF sy-index > 20.
        RAISE EXCEPTION TYPE zcx_aws1_ex_generic
          EXPORTING
            av_msg = |Bedrock agent alias { iv_aliasid } never reached status { iv_status }|.
      ENDIF.
      oo_alias = ao_bda->getagentalias( iv_agentid = iv_agentid
                                        iv_agentaliasid = iv_aliasid )->get_agentalias( ).
    ENDWHILE.
  ENDMETHOD.


  METHOD teardown.
    IF ao_alias IS NOT INITIAL.
      DATA(lv_agentid) = ao_alias->get_agentid( ).
      TRY.
          " first delete aliases
          DATA(lt_agent_aliases) = ao_bda->listagentaliases( iv_agentid = lv_agentid )->get_agentaliassummaries( ).
          LOOP AT lt_agent_aliases INTO DATA(lo_alias).
            ao_bda->deleteagentalias( iv_agentid = lv_agentid
                                      iv_agentaliasid = ao_alias->get_agentaliasid( ) ).
          ENDLOOP.
          DATA(lv_status) = ao_bda->deleteagent( iv_agentid = lv_agentid )->get_agentstatus( ).
          WHILE lv_status <> 'DELETED'.
            lv_status = ao_bda->getagent( lv_agentid )->get_agent( )->get_agentstatus( ).
          ENDWHILE.
        CATCH /aws1/cx_bdaresourcenotfoundex.
          " it's gone
      ENDTRY.
    ENDIF.

  ENDMETHOD.


  METHOD test_invoke_agent.
    DATA lv_agentid TYPE string.
    DATA lv_agentaliasid TYPE string.
    DATA(lo_example) = NEW zcl_aws1_bdz_actions( ).
    DATA(lv_result) = lo_example->invoke_bedrock_agent(
      io_session      = ao_session
      iv_agentid      = ao_alias->get_agentid( )
      iv_agentaliasid = ao_alias->get_agentaliasid( ) ).
    cl_abap_unit_assert=>assert_text_matches(
      text = lv_result
      pattern = '.*((rock)|(paper)|(scissors)).*'
      msg = |Expected "rock", "paper" or "scissors" in the response but got { lv_result }| ).
  ENDMETHOD.

ENDCLASS.
