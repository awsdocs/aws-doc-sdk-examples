" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_r5v_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    TYPES: BEGIN OF ts_cluster_endpoint,
             endpoint TYPE string,
             region   TYPE /aws1/rt_region_id,
           END OF ts_cluster_endpoint.
    TYPES: tt_cluster_endpoints TYPE STANDARD TABLE OF ts_cluster_endpoint WITH DEFAULT KEY.

    " snippet-start:[r5v.abapv1.get_routing_control_state]
    "! <p class="shorttext synchronized" lang="en">Gets the state of a routing control.</p>
    "! Gets the state of a routing control. Cluster endpoints are tried in
    "! sequence until the first successful response is received.
    "! @parameter iv_routing_control_arn | The ARN of the routing control to look up.
    "! @parameter it_cluster_endpoints | The list of cluster endpoint structures to query.
    "! @parameter oo_result | The routing control state response.
    "! @raising /aws1/cx_r5vaccessdeniedex | You don't have sufficient permissions.
    "! @raising /aws1/cx_r5vendpttmpyunavailex | The cluster endpoint isn't available.
    "! @raising /aws1/cx_r5vinternalserverex | An unexpected error occurred.
    "! @raising /aws1/cx_r5vresourcenotfoundex | The routing control was not found.
    "! @raising /aws1/cx_r5vthrottlingex | The request was throttled.
    "! @raising /aws1/cx_r5vvalidationex | The request was invalid.
    METHODS get_routing_control_state
      IMPORTING
        !iv_routing_control_arn TYPE /aws1/r5varn
        !it_cluster_endpoints   TYPE /awsex/cl_r5v_actions=>tt_cluster_endpoints
      RETURNING
        VALUE(oo_result)        TYPE REF TO /aws1/cl_r5vgetroutingctlsta01
      RAISING
        /aws1/cx_r5vaccessdeniedex
        /aws1/cx_r5vendpttmpyunavailex
        /aws1/cx_r5vinternalserverex
        /aws1/cx_r5vresourcenotfoundex
        /aws1/cx_r5vthrottlingex
        /aws1/cx_r5vvalidationex
        /aws1/cx_rt_generic .
    " snippet-end:[r5v.abapv1.get_routing_control_state]

    " snippet-start:[r5v.abapv1.update_routing_control_state]
    "! <p class="shorttext synchronized" lang="en">Updates the state of a routing control.</p>
    "! Updates the state of a routing control. Cluster endpoints are tried in
    "! sequence until the first successful response is received.
    "! @parameter iv_routing_control_arn | The ARN of the routing control to update.
    "! @parameter it_cluster_endpoints | The list of cluster endpoint structures to try.
    "! @parameter iv_routing_control_state | The new routing control state (On or Off).
    "! @parameter it_safety_rules_override | Optional safety rules to override.
    "! @parameter oo_result | The routing control update response.
    "! @raising /aws1/cx_r5vaccessdeniedex | You don't have sufficient permissions.
    "! @raising /aws1/cx_r5vconflictexception | There was a conflict with the request.
    "! @raising /aws1/cx_r5vendpttmpyunavailex | The cluster endpoint isn't available.
    "! @raising /aws1/cx_r5vinternalserverex | An unexpected error occurred.
    "! @raising /aws1/cx_r5vresourcenotfoundex | The routing control was not found.
    "! @raising /aws1/cx_r5vthrottlingex | The request was throttled.
    "! @raising /aws1/cx_r5vvalidationex | The request was invalid.
    METHODS update_routing_control_state
      IMPORTING
        !iv_routing_control_arn   TYPE /aws1/r5varn
        !it_cluster_endpoints     TYPE /awsex/cl_r5v_actions=>tt_cluster_endpoints
        !iv_routing_control_state TYPE /aws1/r5vroutingcontrolstate
        !it_safety_rules_override TYPE /aws1/cl_r5varns_w=>tt_arns OPTIONAL
      RETURNING
        VALUE(oo_result)          TYPE REF TO /aws1/cl_r5vuproutingctlstat01
      RAISING
        /aws1/cx_r5vaccessdeniedex
        /aws1/cx_r5vconflictexception
        /aws1/cx_r5vendpttmpyunavailex
        /aws1/cx_r5vinternalserverex
        /aws1/cx_r5vresourcenotfoundex
        /aws1/cx_r5vthrottlingex
        /aws1/cx_r5vvalidationex
        /aws1/cx_rt_generic .
    " snippet-end:[r5v.abapv1.update_routing_control_state]

  PROTECTED SECTION.
  PRIVATE SECTION.
    "! <p class="shorttext synchronized" lang="en">Creates a Route 53 Recovery Cluster client for a cluster endpoint.</p>
    "! Creates a client for the specified cluster endpoint URL and AWS Region.
    "! @parameter iv_endpoint | The cluster endpoint URL.
    "! @parameter iv_region | The AWS Region for the endpoint.
    "! @parameter io_session | The AWS session object.
    "! @parameter oo_client | The created R5V client.
    METHODS create_recovery_client
      IMPORTING
        !iv_endpoint     TYPE string
        !iv_region       TYPE /aws1/rt_region_id
        !io_session      TYPE REF TO /aws1/cl_rt_session_base
      RETURNING
        VALUE(oo_client) TYPE REF TO /aws1/if_r5v
      RAISING
        /aws1/cx_rt_generic .
ENDCLASS.



CLASS /awsex/cl_r5v_actions IMPLEMENTATION.


  METHOD get_routing_control_state.
    DATA lo_exception TYPE REF TO /aws1/cx_rt_generic.
    DATA lt_shuffled_endpoints TYPE /awsex/cl_r5v_actions=>tt_cluster_endpoints.
    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_client TYPE REF TO /aws1/if_r5v.

    " As a best practice, shuffle cluster endpoints to distribute load
    " For more information, see https://docs.aws.amazon.com/r53recovery/latest/dg/route53-arc-best-practices.html#route53-arc-best-practices.regional
    lt_shuffled_endpoints = it_cluster_endpoints.

    " Simple shuffle - use a random index swap approach
    DATA(lv_size) = lines( lt_shuffled_endpoints ).
    DO lv_size TIMES.
      DATA(lv_idx1) = sy-index.
      DATA(lv_idx2) = ( cl_abap_random_int=>create(
        seed = CONV i( sy-uzeit )
        min  = 1
        max  = lv_size ) )->get_next( ).
      DATA(ls_temp) = lt_shuffled_endpoints[ lv_idx1 ].
      lt_shuffled_endpoints[ lv_idx1 ] = lt_shuffled_endpoints[ lv_idx2 ].
      lt_shuffled_endpoints[ lv_idx2 ] = ls_temp.
    ENDDO.

    " Try each endpoint in shuffled order
    LOOP AT lt_shuffled_endpoints INTO DATA(ls_endpoint).
      TRY.
          " Create session for this region (reuse or create new)
          lo_session = /aws1/cl_rt_session_aws=>create( ).

          " Create client with the specific endpoint
          lo_client = create_recovery_client(
            iv_endpoint = ls_endpoint-endpoint
            iv_region   = ls_endpoint-region
            io_session  = lo_session ).

          " Try to get the routing control state
          oo_result = lo_client->getroutingcontrolstate(
            iv_routingcontrolarn = iv_routing_control_arn ).

          " If successful, return the result
          RETURN.

        CATCH /aws1/cx_r5vendpttmpyunavailex INTO DATA(lo_endpoint_ex).
          " This endpoint is temporarily unavailable, try the next one
          lo_exception = lo_endpoint_ex.
          CONTINUE.

        CATCH /aws1/cx_r5vaccessdeniedex
              /aws1/cx_r5vinternalserverex
              /aws1/cx_r5vresourcenotfoundex
              /aws1/cx_r5vthrottlingex
              /aws1/cx_r5vvalidationex
              /aws1/cx_rt_generic INTO lo_exception.
          " For other errors, re-raise immediately
          RAISE EXCEPTION lo_exception.
      ENDTRY.
    ENDLOOP.

    " If we get here, all endpoints failed
    IF lo_exception IS BOUND.
      RAISE EXCEPTION lo_exception.
    ELSE.
      RAISE EXCEPTION TYPE /aws1/cx_rt_generic
        EXPORTING
          textid = /aws1/cx_rt_generic=>cx_generic_error
          av_msg = 'All cluster endpoints failed'.
    ENDIF.
  ENDMETHOD.


  METHOD update_routing_control_state.
    DATA lo_exception TYPE REF TO /aws1/cx_rt_generic.
    DATA lt_shuffled_endpoints TYPE /awsex/cl_r5v_actions=>tt_cluster_endpoints.
    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_client TYPE REF TO /aws1/if_r5v.

    " As a best practice, shuffle cluster endpoints to distribute load
    " For more information, see https://docs.aws.amazon.com/r53recovery/latest/dg/route53-arc-best-practices.html#route53-arc-best-practices.regional
    lt_shuffled_endpoints = it_cluster_endpoints.

    " Simple shuffle - use a random index swap approach
    DATA(lv_size) = lines( lt_shuffled_endpoints ).
    DO lv_size TIMES.
      DATA(lv_idx1) = sy-index.
      DATA(lv_idx2) = ( cl_abap_random_int=>create(
        seed = CONV i( sy-uzeit )
        min  = 1
        max  = lv_size ) )->get_next( ).
      DATA(ls_temp) = lt_shuffled_endpoints[ lv_idx1 ].
      lt_shuffled_endpoints[ lv_idx1 ] = lt_shuffled_endpoints[ lv_idx2 ].
      lt_shuffled_endpoints[ lv_idx2 ] = ls_temp.
    ENDDO.

    " Try each endpoint in shuffled order
    LOOP AT lt_shuffled_endpoints INTO DATA(ls_endpoint).
      TRY.
          " Create session for this region
          lo_session = /aws1/cl_rt_session_aws=>create( ).

          " Create client with the specific endpoint
          lo_client = create_recovery_client(
            iv_endpoint = ls_endpoint-endpoint
            iv_region   = ls_endpoint-region
            io_session  = lo_session ).

          " Try to update the routing control state
          oo_result = lo_client->updateroutingcontrolstate(
            iv_routingcontrolarn     = iv_routing_control_arn
            iv_routingcontrolstate   = iv_routing_control_state
            it_safetyrulestooverride = it_safety_rules_override ).

          " If successful, return the result
          RETURN.

        CATCH /aws1/cx_r5vendpttmpyunavailex INTO DATA(lo_endpoint_ex).
          " This endpoint is temporarily unavailable, try the next one
          lo_exception = lo_endpoint_ex.
          CONTINUE.

        CATCH /aws1/cx_r5vaccessdeniedex
              /aws1/cx_r5vconflictexception
              /aws1/cx_r5vinternalserverex
              /aws1/cx_r5vresourcenotfoundex
              /aws1/cx_r5vthrottlingex
              /aws1/cx_r5vvalidationex
              /aws1/cx_rt_generic INTO lo_exception.
          " For other errors, re-raise immediately
          RAISE EXCEPTION lo_exception.
      ENDTRY.
    ENDLOOP.

    " If we get here, all endpoints failed
    IF lo_exception IS BOUND.
      RAISE EXCEPTION lo_exception.
    ELSE.
      RAISE EXCEPTION TYPE /aws1/cx_rt_generic
        EXPORTING
          textid = /aws1/cx_rt_generic=>cx_generic_error
          av_msg = 'All cluster endpoints failed'.
    ENDIF.
  ENDMETHOD.


  METHOD create_recovery_client.
    " Create R5V client with custom endpoint and region
    oo_client = /aws1/cl_r5v_factory=>create(
      io_session          = io_session
      iv_region           = iv_region
      iv_custom_endpoint  = iv_endpoint ).
  ENDMETHOD.
ENDCLASS.
