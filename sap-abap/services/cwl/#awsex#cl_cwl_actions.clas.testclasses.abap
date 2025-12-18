" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_cwl_actions DEFINITION DEFERRED.
CLASS /awsex/cl_cwl_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_cwl_actions.

CLASS ltc_awsex_cl_cwl_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA gv_log_group_name TYPE /aws1/cwlloggroupname.
    CLASS-DATA gv_log_stream_name TYPE /aws1/cwllogstreamname.
    CLASS-DATA gv_setup_complete TYPE abap_bool.

    CLASS-DATA go_cwl TYPE REF TO /aws1/if_cwl.
    CLASS-DATA go_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA go_cwl_actions TYPE REF TO /awsex/cl_cwl_actions.

    METHODS: start_query FOR TESTING RAISING /aws1/cx_rt_generic,
      get_query_results FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_query_completion
      IMPORTING
        iv_query_id        TYPE /aws1/cwlqueryid
      RETURNING
        VALUE(rv_complete) TYPE abap_bool
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS get_epoch_milliseconds
      RETURNING
        VALUE(rv_timestamp) TYPE /aws1/cwltimestamp.

ENDCLASS.

CLASS ltc_awsex_cl_cwl_actions IMPLEMENTATION.

  METHOD get_epoch_milliseconds.
    " Get current timestamp in epoch milliseconds format for CloudWatch Logs
    DATA lv_timestamp TYPE timestamp.
    DATA lv_seconds TYPE p LENGTH 16 DECIMALS 0.

    GET TIME STAMP FIELD lv_timestamp.

    " Convert ABAP timestamp to seconds
    lv_seconds = lv_timestamp.

    " Convert to Unix epoch (subtract seconds between 1900-01-01 and 1970-01-01)
    " Then convert to milliseconds
    rv_timestamp = ( lv_seconds - 2208988800 ) * 1000.
  ENDMETHOD.

  METHOD class_setup.
    go_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    go_cwl = /aws1/cl_cwl_factory=>create( go_session ).
    go_cwl_actions = NEW /awsex/cl_cwl_actions( ).

    " Create unique log group name using utility function
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    gv_log_group_name = |/sap-abap/cwl-test-{ lv_uuid }|.
    gv_log_stream_name = |test-stream-{ lv_uuid }|.

    gv_setup_complete = abap_false.

    " Create tags for resources - convert_test tag for cleanup
    DATA lt_tags TYPE /aws1/cl_cwltags_w=>tt_tags.
    DATA ls_tag TYPE /aws1/cl_cwltags_w=>ts_tags_maprow.
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_cwltags_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    " Create log group
    TRY.
        go_cwl->createloggroup(
          iv_loggroupname = gv_log_group_name
          it_tags = lt_tags
        ).
      CATCH /aws1/cx_cwlresrcalrdyexistsex.
        " Log group already exists - tag it with convert_test
        TRY.
            go_cwl->tagloggroup(
              iv_loggroupname = gv_log_group_name
              it_tags = lt_tags
            ).
          CATCH /aws1/cx_rt_generic.
            " Ignore tagging errors
        ENDTRY.
    ENDTRY.

    " Wait for log group to be available with status-based polling
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    DATA lv_elapsed TYPE i.
    DATA lv_found TYPE abap_bool VALUE abap_false.

    GET TIME STAMP FIELD lv_start_time.

    DO 30 TIMES.
      TRY.
          DATA(lo_describe_result) = go_cwl->describeloggroups(
            iv_loggroupnameprefix = gv_log_group_name
          ).

          LOOP AT lo_describe_result->get_loggroups( ) INTO DATA(lo_log_group).
            IF lo_log_group->get_loggroupname( ) = gv_log_group_name.
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
      lv_elapsed = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed > 60.
        " Fail the test if log group isn't created within timeout
        cl_abap_unit_assert=>fail(
          msg = |Log group { gv_log_group_name } was not created within 60 seconds| ).
      ENDIF.
    ENDDO.

    IF lv_found = abap_false.
      cl_abap_unit_assert=>fail(
        msg = |Log group { gv_log_group_name } was not found after creation| ).
    ENDIF.

    " Create log stream
    TRY.
        go_cwl->createlogstream(
          iv_loggroupname = gv_log_group_name
          iv_logstreamname = gv_log_stream_name
        ).
      CATCH /aws1/cx_cwlresrcalrdyexistsex.
        " Log stream already exists - continue
    ENDTRY.

    " Wait for log stream to be available
    lv_found = abap_false.
    GET TIME STAMP FIELD lv_start_time.

    DO 30 TIMES.
      TRY.
          DATA(lo_stream_result) = go_cwl->describelogstreams(
            iv_loggroupname = gv_log_group_name
            iv_logstreamnameprefix = gv_log_stream_name
          ).

          LOOP AT lo_stream_result->get_logstreams( ) INTO DATA(lo_log_stream).
            IF lo_log_stream->get_logstreamname( ) = gv_log_stream_name.
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
      lv_elapsed = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed > 60.
        " Fail the test if log stream isn't created
        cl_abap_unit_assert=>fail(
          msg = |Log stream { gv_log_stream_name } was not created within 60 seconds| ).
      ENDIF.
    ENDDO.

    IF lv_found = abap_false.
      cl_abap_unit_assert=>fail(
        msg = |Log stream { gv_log_stream_name } was not found after creation| ).
    ENDIF.

    " Put test log events with proper epoch timestamps
    DATA lt_events TYPE /aws1/cl_cwlinputlogevent=>tt_inputlogevents.
    DATA lv_timestamp TYPE /aws1/cwltimestamp.
    DATA lv_event_timestamp TYPE /aws1/cwltimestamp.

    lv_timestamp = get_epoch_milliseconds( ).

    " Create 10 test log events with incrementing timestamps
    DO 10 TIMES.
      lv_event_timestamp = lv_timestamp + ( sy-index * 1000 ).
      APPEND NEW /aws1/cl_cwlinputlogevent(
        iv_message = |Test log message { sy-index } from CloudWatch Logs test at { lv_event_timestamp }|
        iv_timestamp = lv_event_timestamp
      ) TO lt_events.
    ENDDO.

    " Put log events
    TRY.
        go_cwl->putlogevents(
          iv_loggroupname = gv_log_group_name
          iv_logstreamname = gv_log_stream_name
          it_logevents = lt_events
        ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_put_error).
        " Fail the test if we can't put log events
        cl_abap_unit_assert=>fail(
          msg = |Failed to put log events: { lo_put_error->get_text( ) }| ).
    ENDTRY.

    " Wait for log events to be indexed by CloudWatch Logs (can take up to 30 seconds)
    WAIT UP TO 30 SECONDS.

    gv_setup_complete = abap_true.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up: Delete log group (this also deletes streams and events)
    IF gv_log_group_name IS NOT INITIAL.
      TRY.
          go_cwl->deleteloggroup(
            iv_loggroupname = gv_log_group_name
          ).
        CATCH /aws1/cx_cwlresourcenotfoundex.
          " Already deleted
        CATCH /aws1/cx_rt_generic.
          " Log group is tagged with convert_test for manual cleanup
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD start_query.
    " Verify setup was successful
    cl_abap_unit_assert=>assert_true(
      act = gv_setup_complete
      msg = 'Test setup must complete successfully' ).

    " Get current time and calculate query time range
    DATA(lv_current_time) = get_epoch_milliseconds( ).
    DATA lv_start_time TYPE /aws1/cwltimestamp.
    DATA lv_end_time TYPE /aws1/cwltimestamp.

    " Query for logs from last hour to now
    lv_start_time = lv_current_time - ( 3600 * 1000 ).
    lv_end_time = lv_current_time.

    " Execute start_query
    DATA(lo_result) = go_cwl_actions->start_query(
      iv_log_group_name = gv_log_group_name
      iv_start_time = lv_start_time
      iv_end_time = lv_end_time
      iv_query_string = 'fields @timestamp, @message | sort @timestamp desc | limit 20'
      iv_limit = 20
    ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'start_query should return a result object' ).

    DATA(lv_query_id) = lo_result->get_queryid( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_query_id
      msg = 'Query ID should not be initial' ).

    MESSAGE 'start_query test passed successfully' TYPE 'I'.

  ENDMETHOD.

  METHOD get_query_results.
    " Verify setup was successful
    cl_abap_unit_assert=>assert_true(
      act = gv_setup_complete
      msg = 'Test setup must complete successfully' ).

    " Get time range for query
    DATA(lv_current_time) = get_epoch_milliseconds( ).
    DATA lv_start_time TYPE /aws1/cwltimestamp.
    DATA lv_end_time TYPE /aws1/cwltimestamp.

    lv_start_time = lv_current_time - ( 3600 * 1000 ).
    lv_end_time = lv_current_time.

    " Start a query first
    DATA(lo_start_result) = go_cwl_actions->start_query(
      iv_log_group_name = gv_log_group_name
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
    DATA(lo_result) = go_cwl_actions->get_query_results(
      iv_query_id = lv_query_id
    ).

    " Verify result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'get_query_results should return a result object' ).

    DATA(lv_status) = lo_result->get_status( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_status
      exp = 'Complete'
      msg = 'Query status should be Complete' ).

    " Verify we got results back
    " get_results() returns a table of result rows (TT_QUERYRESULTS)
    " Each row is a table of result fields (TT_RESULTROWS)
    DATA lt_results TYPE /aws1/cl_cwlresultfield=>tt_queryresults.
    lt_results = lo_result->get_results( ).

    " Check that we have log entries (we created 10 test events)
    DATA lv_result_count TYPE i.
    lv_result_count = lines( lt_results ).

    cl_abap_unit_assert=>assert_differs(
      act = lv_result_count
      exp = 0
      msg = |Query should return log entries, got { lv_result_count } results| ).

    " Verify structure of returned results
    IF lv_result_count > 0.
      " Get first row (which is a table of result fields)
      DATA lt_first_row TYPE /aws1/cl_cwlresultfield=>tt_resultrows.
      READ TABLE lt_results INDEX 1 INTO lt_first_row.

      DATA lv_first_row_count TYPE i.
      lv_first_row_count = lines( lt_first_row ).

      cl_abap_unit_assert=>assert_differs(
        act = lv_first_row_count
        exp = 0
        msg = 'First result row should contain fields' ).

      " Check fields in the result
      DATA lv_has_timestamp TYPE abap_bool VALUE abap_false.
      DATA lv_has_message TYPE abap_bool VALUE abap_false.
      DATA lo_field TYPE REF TO /aws1/cl_cwlresultfield.

      LOOP AT lt_first_row INTO lo_field.
        DATA(lv_field_name) = lo_field->get_field( ).
        IF lv_field_name = '@timestamp'.
          lv_has_timestamp = abap_true.
        ELSEIF lv_field_name = '@message'.
          lv_has_message = abap_true.
        ENDIF.
      ENDLOOP.

      cl_abap_unit_assert=>assert_true(
        act = lv_has_timestamp
        msg = 'Query results should contain @timestamp field' ).

      cl_abap_unit_assert=>assert_true(
        act = lv_has_message
        msg = 'Query results should contain @message field' ).

    ENDIF.

    MESSAGE 'get_query_results test passed successfully' TYPE 'I'.

  ENDMETHOD.

  METHOD wait_for_query_completion.
    rv_complete = abap_false.
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    DATA lv_elapsed TYPE i.

    GET TIME STAMP FIELD lv_start_time.

    " Wait up to 90 seconds for query to complete
    DO 90 TIMES.
      TRY.
          DATA(lo_result) = go_cwl->getqueryresults(
            iv_queryid = iv_query_id
          ).

          DATA(lv_status) = lo_result->get_status( ).

          " Check if query is in terminal state
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
      lv_elapsed = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed > 90.
        EXIT.
      ENDIF.
    ENDDO.

  ENDMETHOD.

ENDCLASS.
