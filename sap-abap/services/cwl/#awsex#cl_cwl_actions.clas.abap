" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_cwl_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS start_query
      IMPORTING
        !iv_log_group_name TYPE /aws1/cwlloggroupname
        !iv_start_time     TYPE /aws1/cwltimestamp
        !iv_end_time       TYPE /aws1/cwltimestamp
        !iv_query_string   TYPE /aws1/cwlquerystring
        !iv_limit          TYPE /aws1/cwleventslimit
      RETURNING
        VALUE(oo_result)   TYPE REF TO /aws1/cl_cwlstartqueryresponse
      RAISING
        /aws1/cx_rt_generic .

    METHODS get_query_results
      IMPORTING
        !iv_query_id     TYPE /aws1/cwlqueryid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_cwlgetqueryresultsrsp
      RAISING
        /aws1/cx_rt_generic .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_CWL_ACTIONS IMPLEMENTATION.


* <SIGNATURE>---------------------------------------------------------------------------------------+
* | Instance Public Method /AWSEX/CL_CWL_ACTIONS->GET_QUERY_RESULTS
* +-------------------------------------------------------------------------------------------------+
* | [--->] IV_QUERY_ID                    TYPE        /AWS1/CWLQUERYID
* | [<-()] OO_RESULT                      TYPE REF TO /AWS1/CL_CWLGETQUERYRESULTSRSP
* | [!CX!] /AWS1/CX_RT_GENERIC
* +--------------------------------------------------------------------------------------</SIGNATURE>
  METHOD get_query_results.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwl) = /aws1/cl_cwl_factory=>create( lo_session ).

    " snippet-start:[cwl.abapv1.get_query_results]
    TRY.
        oo_result = lo_cwl->getqueryresults(
          iv_queryid = iv_query_id ).
        
        " Display query status and result count
        DATA(lv_status) = oo_result->get_status( ).
        DATA(lt_results) = oo_result->get_results( ).
        DATA(lv_result_count) = lines( lt_results ).
        
        MESSAGE |Query status: { lv_status }. Retrieved { lv_result_count } log event(s).| TYPE 'I'.
      CATCH /aws1/cx_cwlinvalidparameterex.
        MESSAGE 'Invalid parameter.' TYPE 'E'.
      CATCH /aws1/cx_cwlresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'E'.
      CATCH /aws1/cx_cwlserviceunavailex.
        MESSAGE 'Service unavailable.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cwl.abapv1.get_query_results]

  ENDMETHOD.


* <SIGNATURE>---------------------------------------------------------------------------------------+
* | Instance Public Method /AWSEX/CL_CWL_ACTIONS->START_QUERY
* +-------------------------------------------------------------------------------------------------+
* | [--->] IV_LOG_GROUP_NAME              TYPE        /AWS1/CWLLOGGROUPNAME
* | [--->] IV_START_TIME                  TYPE        /AWS1/CWLTIMESTAMP
* | [--->] IV_END_TIME                    TYPE        /AWS1/CWLTIMESTAMP
* | [--->] IV_QUERY_STRING                TYPE        /AWS1/CWLQUERYSTRING
* | [--->] IV_LIMIT                       TYPE        /AWS1/CWLEVENTSLIMIT
* | [<-()] OO_RESULT                      TYPE REF TO /AWS1/CL_CWLSTARTQUERYRESPONSE
* | [!CX!] /AWS1/CX_RT_GENERIC
* +--------------------------------------------------------------------------------------</SIGNATURE>
  METHOD start_query.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cwl) = /aws1/cl_cwl_factory=>create( lo_session ).

    " snippet-start:[cwl.abapv1.start_query]
    TRY.
        " iv_log_group_name = '/aws/lambda/my-function'
        " iv_query_string = 'fields @timestamp, @message | sort @timestamp desc | limit 20'
        oo_result = lo_cwl->startquery(
          iv_loggroupname = iv_log_group_name
          iv_starttime    = iv_start_time
          iv_endtime      = iv_end_time
          iv_querystring  = iv_query_string
          iv_limit        = iv_limit ).
        
        " Display the query ID for tracking
        DATA(lv_query_id) = oo_result->get_queryid( ).
        MESSAGE |Query started successfully with ID: { lv_query_id }| TYPE 'I'.
      CATCH /aws1/cx_cwlinvalidparameterex.
        MESSAGE 'Invalid parameter.' TYPE 'E'.
      CATCH /aws1/cx_cwllimitexceededex.
        MESSAGE 'Limit exceeded.' TYPE 'E'.
      CATCH /aws1/cx_cwlmalformedqueryex.
        MESSAGE 'Malformed query.' TYPE 'E'.
      CATCH /aws1/cx_cwlresourcenotfoundex.
        MESSAGE 'Resource not found.' TYPE 'E'.
      CATCH /aws1/cx_cwlserviceunavailex.
        MESSAGE 'Service unavailable.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[cwl.abapv1.start_query]

  ENDMETHOD.
ENDCLASS.
