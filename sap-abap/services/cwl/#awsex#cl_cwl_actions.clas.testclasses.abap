" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_cwl_actions DEFINITION DEFERRED.
CLASS /awsex/cl_cwl_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_cwl_actions.

CLASS ltc_awsex_cl_cwl_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_log_group_name TYPE /aws1/cwlloggroupname.
    CLASS-DATA av_log_stream_name TYPE /aws1/cwllogstreamname.

    CLASS-DATA ao_cwl TYPE REF TO /aws1/if_cwl.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_cwl_actions TYPE REF TO /awsex/cl_cwl_actions.

    METHODS: start_query FOR TESTING RAISING /aws1/cx_rt_generic,
      get_query_results FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS wait_for_query_completion
      IMPORTING
        iv_query_id        TYPE /aws1/cwlqueryid
      RETURNING
        VALUE(rv_complete) TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_cwl_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_cwl = /aws1/cl_cwl_factory=>create( ao_session ).
    ao_cwl_actions = NEW /awsex/cl_cwl_actions( ).

    " Create a unique log group name with convert_test tag
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    av_log_group_name = |/sap-abap/cwl-demo-{ lv_uuid }|.
    av_log_stream_name = |test-stream-{ lv_uuid }|.

    " Create tags for the log group - correct structure
    DATA lt_tags TYPE /aws1/cl_cwltags_w=>tt_tags.
    DATA ls_tag TYPE /aws1/cl_cwltags_w=>ts_tags_maprow.
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_cwltags_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    " Create log group with convert_test tag
    TRY.
        ao_cwl->createloggroup(
          iv_loggroupname = av_log_group_name
          it_tags = lt_tags
        ).
      CATCH /aws1/cx_cwlresrcalrdyexistsex.
        " Log group already exists, tag it
        TRY.
            ao_cwl->tagloggroup(
              iv_loggroupname = av_log_group_name
              it_tags = lt_tags
            ).
          CATCH /aws1/cx_rt_generic.
            " Continue if tagging fails
        ENDTRY.
    ENDTRY.

    " Wait for log group to be created
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    DATA lv_elapsed_seconds TYPE i.
    GET TIME STAMP FIELD lv_start_time.

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DO 30 TIMES.
      TRY.
          DATA(lo_result) = ao_cwl->describeloggroups(
            iv_loggroupnameprefix = av_log_group_name
          ).
          LOOP AT lo_result->get_loggroups( ) INTO DATA(lo_log_group).
            IF lo_log_group->get_loggroupname( ) = av_log_group_name.
              lv_found = abap_true.
              EXIT.
            ENDIF.
          ENDLOOP.
          IF lv_found = abap_true.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Continue waiting
      ENDTRY.
      WAIT UP TO 2 SECONDS.
      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed_seconds = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).
      IF lv_elapsed_seconds > 60.
        EXIT.
      ENDIF.
    ENDDO.

    " Create log stream
    TRY.
        ao_cwl->createlogstream(
          iv_loggroupname = av_log_group_name
          iv_logstreamname = av_log_stream_name
        ).
      CATCH /aws1/cx_cwlresrcalrdyexistsex.
        " Log stream already exists, continue
    ENDTRY.

    " Wait for log stream to be created
    GET TIME STAMP FIELD lv_start_time.
    lv_found = abap_false.
    DO 30 TIMES.
      TRY.
          DATA(lo_stream_result) = ao_cwl->describelogstreams(
            iv_loggroupname = av_log_group_name
            iv_logstreamnameprefix = av_log_stream_name
          ).
          LOOP AT lo_stream_result->get_logstreams( ) INTO DATA(lo_log_stream).
            IF lo_log_stream->get_logstreamname( ) = av_log_stream_name.
              lv_found = abap_true.
              EXIT.
            ENDIF.
          ENDLOOP.
          IF lv_found = abap_true.
            EXIT.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Continue waiting
      ENDTRY.
      WAIT UP TO 2 SECONDS.
      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed_seconds = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).
      IF lv_elapsed_seconds > 60.
        EXIT.
      ENDIF.
    ENDDO.

    " Put some log events
    DATA lt_events TYPE /aws1/cl_cwlinputlogevent=>tt_inputlogevents.
    DATA lv_timestamp TYPE /aws1/cwltimestamp.
    DATA lv_ts TYPE timestamp.
    GET TIME STAMP FIELD lv_ts.
    " Convert timestamp to Unix milliseconds (CloudWatch Logs format)
    " Timestamp is in format YYYYMMDDhhmmss.mmmuuun
    " We need to convert to milliseconds since epoch
    DATA lv_tstmp_seconds TYPE p DECIMALS 0.
    lv_tstmp_seconds = lv_ts.
    " Convert ABAP timestamp (seconds since 1900) to Unix timestamp (seconds since 1970)
    " Difference between 1900 and 1970 is 2208988800 seconds
    lv_timestamp = ( lv_tstmp_seconds - 2208988800 ) * 1000.

    DO 5 TIMES.
      APPEND NEW /aws1/cl_cwlinputlogevent(
        iv_message = |Test log message { sy-index } at { lv_timestamp }|
        iv_timestamp = lv_timestamp + sy-index
      ) TO lt_events.
    ENDDO.

    TRY.
        ao_cwl->putlogevents(
          iv_loggroupname = av_log_group_name
          iv_logstreamname = av_log_stream_name
          it_logevents = lt_events
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_error).
        " Log the error but continue - logs may not be immediately available
    ENDTRY.

    " Wait for log events to be indexed (CloudWatch Logs can take time)
    WAIT UP TO 30 SECONDS.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up log group (this will also delete the log stream and all log events)
    TRY.
        ao_cwl->deleteloggroup(
          iv_loggroupname = av_log_group_name
        ).
      CATCH /aws1/cx_cwlresourcenotfoundex.
        " Log group already deleted or doesn't exist
      CATCH /aws1/cx_rt_generic.
        " Error deleting, but continue - resource is tagged with convert_test
        " User can manually clean up using the tag
    ENDTRY.
  ENDMETHOD.

  METHOD start_query.
    " Get current timestamp for query time range
    DATA lv_timestamp TYPE /aws1/cwltimestamp.
    DATA lv_ts TYPE timestamp.
    GET TIME STAMP FIELD lv_ts.
    " Convert timestamp to Unix milliseconds
    DATA lv_tstmp_seconds TYPE p DECIMALS 0.
    lv_tstmp_seconds = lv_ts.
    lv_timestamp = ( lv_tstmp_seconds - 2208988800 ) * 1000.

    " Query for logs from last hour - use proper type
    DATA lv_start_time TYPE /aws1/cwltimestamp.
    DATA lv_end_time TYPE /aws1/cwltimestamp.
    lv_start_time = lv_timestamp - ( 3600 * 1000 ).
    lv_end_time = lv_timestamp.

    DATA(lo_result) = ao_cwl_actions->start_query(
      iv_log_group_name = av_log_group_name
      iv_start_time = lv_start_time
      iv_end_time = lv_end_time
      iv_query_string = 'fields @timestamp, @message | sort @timestamp desc | limit 20'
      iv_limit = 20
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Query result should be returned' ).

    DATA(lv_query_id) = lo_result->get_queryid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_query_id
      msg = 'Query ID should not be initial' ).

  ENDMETHOD.

  METHOD get_query_results.
    " First start a query
    DATA lv_timestamp TYPE /aws1/cwltimestamp.
    DATA lv_ts TYPE timestamp.
    GET TIME STAMP FIELD lv_ts.
    " Convert timestamp to Unix milliseconds
    DATA lv_tstmp_seconds TYPE p DECIMALS 0.
    lv_tstmp_seconds = lv_ts.
    lv_timestamp = ( lv_tstmp_seconds - 2208988800 ) * 1000.

    " Use proper type for timestamps
    DATA lv_start_time TYPE /aws1/cwltimestamp.
    DATA lv_end_time TYPE /aws1/cwltimestamp.
    lv_start_time = lv_timestamp - ( 3600 * 1000 ).
    lv_end_time = lv_timestamp.

    DATA(lo_start_result) = ao_cwl_actions->start_query(
      iv_log_group_name = av_log_group_name
      iv_start_time = lv_start_time
      iv_end_time = lv_end_time
      iv_query_string = 'fields @timestamp, @message | sort @timestamp desc | limit 20'
      iv_limit = 20
    ).

    DATA(lv_query_id) = lo_start_result->get_queryid( ).

    " Wait for query to complete
    DATA(lv_complete) = wait_for_query_completion( lv_query_id ).

    cl_abap_unit_assert=>assert_true(
      act = lv_complete
      msg = 'Query should complete within timeout period' ).

    " Get query results
    DATA(lo_result) = ao_cwl_actions->get_query_results(
      iv_query_id = lv_query_id
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Query results should be returned' ).

    DATA(lv_status) = lo_result->get_status( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_status
      exp = 'Complete'
      msg = 'Query status should be Complete' ).

  ENDMETHOD.

  METHOD wait_for_query_completion.
    rv_complete = abap_false.
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    GET TIME STAMP FIELD lv_start_time.

    " Wait up to 60 seconds for query to complete
    DO 60 TIMES.
      TRY.
          DATA(lo_result) = ao_cwl->getqueryresults(
            iv_queryid = iv_query_id
          ).

          DATA(lv_status) = lo_result->get_status( ).
          IF lv_status = 'Complete' OR lv_status = 'Failed' OR
             lv_status = 'Cancelled' OR lv_status = 'Timeout'.
            IF lv_status = 'Complete'.
              rv_complete = abap_true.
            ENDIF.
            EXIT.
          ENDIF.

        CATCH /aws1/cx_rt_generic.
          " Continue waiting
      ENDTRY.

      WAIT UP TO 1 SECONDS.

      GET TIME STAMP FIELD lv_current_time.
      " Check if 60 seconds have elapsed
      DATA lv_elapsed_seconds TYPE i.
      lv_elapsed_seconds = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed_seconds > 60.
        EXIT.
      ENDIF.
    ENDDO.

  ENDMETHOD.

ENDCLASS.
