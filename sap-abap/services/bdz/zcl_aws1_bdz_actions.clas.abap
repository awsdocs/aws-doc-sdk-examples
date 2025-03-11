" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS zcl_aws1_bdz_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS invoke_bedrock_agent
      IMPORTING
        !io_session      TYPE REF TO /aws1/cl_rt_session_base
        !iv_agentid      TYPE /aws1/bdaid
        !iv_agentaliasid TYPE /aws1/bdaagentaliasid
      RETURNING
        VALUE(ov_answer) TYPE string
      RAISING
        /aws1/cx_bdzserverexc
        /aws1/cx_bdzclientexc
        /aws1/cx_rt_technical_generic
        /aws1/cx_rt_service_generic
        /aws1/cx_rt_no_auth_generic
        cx_uuid_error .
  PROTECTED SECTION.
  PRIVATE SECTION.

ENDCLASS.



CLASS ZCL_AWS1_BDZ_ACTIONS IMPLEMENTATION.


  METHOD invoke_bedrock_agent.
    DATA(lo_bdz) = /aws1/cl_bdz_factory=>create( io_session ).

    "snippet-start:[bdz.abapv1.invokeagent]

    DATA(lo_result) = lo_bdz->invokeagent(
      iv_agentid      = iv_agentid
        iv_agentaliasid = iv_agentaliasid
        iv_enabletrace  = abap_true
        iv_sessionid    = CONV #( cl_system_uuid=>create_uuid_c26_static( ) )
        iv_inputtext    = |Let's play "rock, paper, scissors".  I choose rock.| ).
    DATA(lo_stream) = lo_result->get_completion( ).
    TRY.
        " loop while there are still events in the stream
        WHILE lo_stream->/aws1/if_rt_stream_reader~data_available( ) = abap_true.
          DATA(lo_evt) = lo_stream->read( ).
          " each /AWS1/CL_BDZRESPONSESTREAM_EV event contains exactly one member
          " all others are INITIAL.  For each event, process the non-initial
          " member if desired
          IF lo_evt->get_chunk( ) IS NOT INITIAL.
            " Process a Chunk event
            DATA(lv_xstr) = lo_evt->get_chunk( )->get_bytes( ).
            DATA(lv_answer) = /aws1/cl_rt_util=>xstring_to_string( lv_xstr ).
            " the answer says something like "I chose paper, so you lost"
          ELSEIF lo_evt->get_files( ) IS NOT INITIAL.
            " process a Files event if desired
          ELSEIF lo_evt->get_returncontrol( ) IS NOT INITIAL.
            " process a ReturnControl event if desired
          ELSEIF lo_evt->get_trace( ) IS NOT INITIAL.
            " process a Trace event if desired
          ENDIF.
        ENDWHILE.
        " the stream of events can possibly contain an exception
        " which will be raised to break the loop
        " catch /AWS1/CX_BDZACCESSDENIEDEX.
        " catch /AWS1/CX_BDZINTERNALSERVEREX.
        " catch /AWS1/CX_BDZMODELNOTREADYEX.
        " catch /AWS1/CX_BDZVALIDATIONEX.
        " catch /AWS1/CX_BDZTHROTTLINGEX.
        " catch /AWS1/CX_BDZDEPENDENCYFAILEDEX.
        " catch /AWS1/CX_BDZBADGATEWAYEX.
        " catch /AWS1/CX_BDZRESOURCENOTFOUNDEX.
        " catch /AWS1/CX_BDZSERVICEQUOTAEXCDEX.
        " catch /AWS1/CX_BDZCONFLICTEXCEPTION.
    ENDTRY.
    "snippet-end:[bdz.abapv1.invokeagent].
    ov_answer = lv_answer.
  ENDMETHOD.
ENDCLASS.
