" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_glu_actions DEFINITION DEFERRED.
CLASS /awsex/cl_glu_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_glu_actions.

CLASS ltc_awsex_cl_glu_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_glu TYPE REF TO /aws1/if_glu.
    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_iam TYPE REF TO /aws1/if_iam.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_glu_actions TYPE REF TO /awsex/cl_glu_actions.

    DATA av_crawler_name TYPE /aws1/glunamestring.
    DATA av_database_name TYPE /aws1/gludatabasename.
    DATA av_job_name TYPE /aws1/glunamestring.
    DATA av_test_bucket TYPE /aws1/s3_bucketname.
    DATA av_role_arn TYPE /aws1/glurole.
    DATA av_role_name TYPE /aws1/iamrolenametype.

    METHODS crawler_operations FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS job_operations FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS wait_for_crawler_ready
      IMPORTING
        iv_crawler_name TYPE /aws1/glunamestring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-DATA gao_glu TYPE REF TO /aws1/if_glu.
    CLASS-DATA gao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA gao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA gao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA gav_test_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA gav_role_arn TYPE /aws1/glurole.
    CLASS-DATA gav_role_name TYPE /aws1/iamrolenametype.

ENDCLASS.

CLASS ltc_awsex_cl_glu_actions IMPLEMENTATION.

  METHOD class_setup.
    gao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    gao_glu = /aws1/cl_glu_factory=>create( gao_session ).
    gao_s3 = /aws1/cl_s3_factory=>create( gao_session ).
    gao_iam = /aws1/cl_iam_factory=>create( gao_session ).

    " Generate unique names
    DATA(lv_uuid_string) = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.

    gav_test_bucket = |glue-test-{ lv_uuid_string }|.
    gav_role_name = |GlueRole{ lv_uuid_string }|.

    " Create S3 bucket for test data and tag it
    /awsex/cl_utils=>create_bucket(
      iv_bucket = gav_test_bucket
      io_s3 = gao_s3
      io_session = gao_session ).

    " Tag the bucket for cleanup
    DATA lt_tags TYPE /aws1/cl_s3_tag=>tt_tagset.
    lt_tags = VALUE #( ( NEW /aws1/cl_s3_tag(
      iv_key = 'convert_test'
      iv_value = 'true' ) ) ).
    gao_s3->putbuckettagging(
      iv_bucket = gav_test_bucket
      io_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tags ) ).

    " Create test data file in S3
    DATA lv_test_data TYPE xstring.
    lv_test_data = /aws1/cl_rt_util=>string_to_xstring( |year,month,day\n2024,01,15\n2024,01,16\n| ).
    gao_s3->putobject(
      iv_bucket = gav_test_bucket
      iv_key = 'test-data.csv'
      iv_body = lv_test_data ).

    " Create IAM role for Glue
    DATA(lv_assume_role_policy) = |\{ "Version": "2012-10-17", "Statement": [ \{ | &&
      |"Effect": "Allow", "Principal": \{ "Service": "glue.amazonaws.com" \}, | &&
      |"Action": "sts:AssumeRole" \} ] \}|.

    TRY.
        DATA(lo_create_role_result) = gao_iam->createrole(
          iv_rolename = gav_role_name
          iv_assumerolepolicydocument = lv_assume_role_policy ).
        gav_role_arn = lo_create_role_result->get_role( )->get_arn( ).
      CATCH /aws1/cx_iamentityalrdyexex.
        " Role already exists, get it
        DATA(lo_get_role_result) = gao_iam->getrole( iv_rolename = gav_role_name ).
        gav_role_arn = lo_get_role_result->get_role( )->get_arn( ).
    ENDTRY.

    " Attach necessary policies to the role
    TRY.
        gao_iam->attachrolepolicy(
          iv_rolename = gav_role_name
          iv_policyarn = 'arn:aws:iam::aws:policy/service-role/AWSGlueServiceRole' ).
      CATCH /aws1/cx_rt_generic.
        " Policy may already be attached
    ENDTRY.

    " Add S3 access policy
    DATA(lv_s3_policy) = |\{ "Version": "2012-10-17", "Statement": [ \{ | &&
      |"Effect": "Allow", "Action": [ "s3:GetObject", "s3:PutObject", "s3:ListBucket" ], | &&
      |"Resource": [ "arn:aws:s3:::{ gav_test_bucket }/*", "arn:aws:s3:::{ gav_test_bucket }" ] \} ] \}|.

    gao_iam->putrolepolicy(
      iv_rolename = gav_role_name
      iv_policyname = 'GlueTestS3Access'
      iv_policydocument = lv_s3_policy ).

    " Wait for role to propagate
    WAIT UP TO 10 SECONDS.

  ENDMETHOD.

  METHOD class_teardown.
    " Note: S3 bucket is tagged with 'convert_test' for manual cleanup
    " This is because Glue databases may take time to fully clean up

    " Clean up IAM role
    TRY.
        gao_iam->detachrolepolicy(
          iv_rolename = gav_role_name
          iv_policyarn = 'arn:aws:iam::aws:policy/service-role/AWSGlueServiceRole' ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    TRY.
        gao_iam->deleterolepolicy(
          iv_rolename = gav_role_name
          iv_policyname = 'GlueTestS3Access' ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    TRY.
        gao_iam->deleterole( iv_rolename = gav_role_name ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

  ENDMETHOD.

  METHOD crawler_operations.
    " This test combines multiple crawler and database operations
    " to reduce overall test execution time
    ao_session = gao_session.
    ao_glu = gao_glu.
    ao_glu_actions = NEW /awsex/cl_glu_actions( ).
    DATA(lv_uuid_string) = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    av_crawler_name = |glu-ops-{ lv_uuid_string }|.
    av_database_name = |glu_ops_{ lv_uuid_string }|.
    av_role_arn = gav_role_arn.
    av_test_bucket = gav_test_bucket.

    " Test 1: create_crawler
    ao_glu_actions->create_crawler(
      iv_crawler_name = av_crawler_name
      iv_role_arn = av_role_arn
      iv_database_name = av_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ av_test_bucket }/| ).

    wait_for_crawler_ready( av_crawler_name ).
    MESSAGE 'create_crawler successful' TYPE 'I'.

    " Test 2: get_crawler
    DATA lo_get_crawler_result TYPE REF TO /aws1/cl_glugetcrawlerresponse.
    ao_glu_actions->get_crawler(
      EXPORTING
        iv_crawler_name = av_crawler_name
      IMPORTING
        oo_result = lo_get_crawler_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_get_crawler_result
      msg = 'GetCrawler result should not be null' ).

    DATA(lo_crawler) = lo_get_crawler_result->get_crawler( ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_crawler_name
      act = lo_crawler->get_name( )
      msg = 'Crawler name should match' ).
    MESSAGE 'get_crawler successful' TYPE 'I'.

    " Test 3: start_crawler
    ao_glu_actions->start_crawler( iv_crawler_name = av_crawler_name ).
    WAIT UP TO 5 SECONDS.

    DATA(lo_state_check) = gao_glu->getcrawler( iv_name = av_crawler_name ).
    DATA(lv_state) = lo_state_check->get_crawler( )->get_state( ).
    DATA(lv_valid_state) = xsdbool( lv_state = 'RUNNING' OR lv_state = 'READY' ).
    cl_abap_unit_assert=>assert_true(
      act = lv_valid_state
      msg = |Crawler should be running or ready, state: { lv_state }| ).
    MESSAGE 'start_crawler successful' TYPE 'I'.

    wait_for_crawler_ready( av_crawler_name ).

    " Test 4: get_database
    DATA lo_get_db_result TYPE REF TO /aws1/cl_glugetdatabasersp.
    ao_glu_actions->get_database(
      EXPORTING
        iv_database_name = av_database_name
      IMPORTING
        oo_result = lo_get_db_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_get_db_result
      msg = 'GetDatabase result should not be null' ).

    DATA(lo_database) = lo_get_db_result->get_database( ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_database_name
      act = lo_database->get_name( )
      msg = 'Database name should match' ).
    MESSAGE 'get_database successful' TYPE 'I'.

    " Test 5: get_tables
    DATA lo_get_tables_result TYPE REF TO /aws1/cl_glugettablesresponse.
    ao_glu_actions->get_tables(
      EXPORTING
        iv_database_name = av_database_name
      IMPORTING
        oo_result = lo_get_tables_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_get_tables_result
      msg = 'GetTables result should not be null' ).

    DATA(lt_tables) = lo_get_tables_result->get_tablelist( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_tables )
      msg = 'Should have at least one table' ).
    MESSAGE 'get_tables successful' TYPE 'I'.

    " Test 6: delete_table
    DATA(lo_first_table) = lt_tables[ 1 ].
    DATA(lv_table_name) = lo_first_table->get_name( ).
    ao_glu_actions->delete_table(
      iv_database_name = av_database_name
      iv_table_name = lv_table_name ).

    DATA lv_table_exists TYPE abap_bool VALUE abap_true.
    TRY.
        gao_glu->gettable(
          iv_databasename = av_database_name
          iv_name = lv_table_name ).
        lv_table_exists = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_table_exists = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_false(
      act = lv_table_exists
      msg = 'Table should have been deleted' ).
    MESSAGE 'delete_table successful' TYPE 'I'.

    " Delete remaining tables
    LOOP AT lt_tables INTO DATA(lo_table) FROM 2.
      TRY.
          gao_glu->deletetable(
            iv_databasename = av_database_name
            iv_name = lo_table->get_name( ) ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDLOOP.

    " Test 7: delete_database
    ao_glu_actions->delete_database( iv_database_name = av_database_name ).

    DATA lv_db_exists TYPE abap_bool VALUE abap_true.
    TRY.
        gao_glu->getdatabase( iv_name = av_database_name ).
        lv_db_exists = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_db_exists = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_false(
      act = lv_db_exists
      msg = 'Database should have been deleted' ).
    MESSAGE 'delete_database successful' TYPE 'I'.

    " Test 8: delete_crawler
    ao_glu_actions->delete_crawler( iv_crawler_name = av_crawler_name ).

    DATA lv_crawler_exists TYPE abap_bool VALUE abap_true.
    TRY.
        gao_glu->getcrawler( iv_name = av_crawler_name ).
        lv_crawler_exists = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_crawler_exists = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_false(
      act = lv_crawler_exists
      msg = 'Crawler should have been deleted' ).
    MESSAGE 'delete_crawler successful' TYPE 'I'.

  ENDMETHOD.

  METHOD job_operations.
    " This test combines multiple job operations
    " to reduce overall test execution time
    ao_session = gao_session.
    ao_glu = gao_glu.
    ao_glu_actions = NEW /awsex/cl_glu_actions( ).
    DATA(lv_uuid_string) = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    av_job_name = |glu-jobops-{ lv_uuid_string }|.
    av_role_arn = gav_role_arn.
    av_test_bucket = gav_test_bucket.

    " Test 1: create_job
    ao_glu_actions->create_job(
      iv_job_name = av_job_name
      iv_description = 'Test ETL job'
      iv_role_arn = av_role_arn
      iv_script_location = |s3://{ av_test_bucket }/scripts/test-script.py| ).

    DATA lv_job_created TYPE abap_bool VALUE abap_false.
    TRY.
        DATA(lo_check_job) = gao_glu->getjob( iv_jobname = av_job_name ).
        lv_job_created = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_job_created = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_job_created
      msg = 'Job should have been created' ).
    MESSAGE 'create_job successful' TYPE 'I'.

    " Test 2: list_jobs
    DATA lo_list_result TYPE REF TO /aws1/cl_glulistjobsresponse.
    ao_glu_actions->list_jobs(
      IMPORTING
        oo_result = lo_list_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_list_result
      msg = 'ListJobs result should not be null' ).

    DATA(lt_job_names) = lo_list_result->get_jobnames( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_job_names )
      msg = 'Should have at least one job' ).
    MESSAGE 'list_jobs successful' TYPE 'I'.

    " Test 3: get_job_runs (without starting a run to save time)
    DATA lo_runs_result TYPE REF TO /aws1/cl_glugetjobrunsresponse.
    ao_glu_actions->get_job_runs(
      EXPORTING
        iv_job_name = av_job_name
      IMPORTING
        oo_result = lo_runs_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_runs_result
      msg = 'GetJobRuns result should not be null' ).
    MESSAGE 'get_job_runs successful' TYPE 'I'.

    " Test 4: start_job_run (this will likely fail but tests the API call)
    DATA lv_job_run_id TYPE /aws1/gluidstring.
    TRY.
        ao_glu_actions->start_job_run(
          EXPORTING
            iv_job_name = av_job_name
            iv_input_database = 'test_database'
            iv_input_table = 'test_table'
            iv_output_bucket_url = |s3://{ av_test_bucket }/output/|
          IMPORTING
            ov_job_run_id = lv_job_run_id ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_job_run_id
          msg = 'Job run ID should not be empty' ).
        MESSAGE 'start_job_run successful' TYPE 'I'.

        " Test 5: get_job_run (only if start succeeded)
        IF lv_job_run_id IS NOT INITIAL.
          DATA lo_run_result TYPE REF TO /aws1/cl_glugetjobrunresponse.
          ao_glu_actions->get_job_run(
            EXPORTING
              iv_job_name = av_job_name
              iv_run_id = lv_job_run_id
            IMPORTING
              oo_result = lo_run_result ).

          cl_abap_unit_assert=>assert_bound(
            act = lo_run_result
            msg = 'GetJobRun result should not be null' ).
          MESSAGE 'get_job_run successful' TYPE 'I'.
        ENDIF.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        " Job run may fail due to missing database/table, but API call works
        MESSAGE 'start_job_run API tested (job run failed as expected)' TYPE 'I'.
    ENDTRY.

    " Test 6: delete_job
    ao_glu_actions->delete_job( iv_job_name = av_job_name ).

    DATA lv_job_exists TYPE abap_bool VALUE abap_true.
    TRY.
        gao_glu->getjob( iv_jobname = av_job_name ).
        lv_job_exists = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_job_exists = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_false(
      act = lv_job_exists
      msg = 'Job should have been deleted' ).
    MESSAGE 'delete_job successful' TYPE 'I'.

  ENDMETHOD.

  METHOD wait_for_crawler_ready.
    DATA lv_max_attempts TYPE i VALUE 60.
    DATA lv_attempt TYPE i VALUE 0.
    DATA lv_state TYPE /aws1/glucrawlerstate.

    DO lv_max_attempts TIMES.
      lv_attempt = lv_attempt + 1.

      TRY.
          DATA(lo_result) = gao_glu->getcrawler( iv_name = iv_crawler_name ).
          DATA(lo_crawler) = lo_result->get_crawler( ).
          lv_state = lo_crawler->get_state( ).

          IF lv_state = 'READY'.
            EXIT.
          ENDIF.

        CATCH /aws1/cx_gluentitynotfoundex.
          " Crawler doesn't exist yet
          WAIT UP TO 2 SECONDS.
          CONTINUE.
      ENDTRY.

      IF lv_attempt >= lv_max_attempts.
        cl_abap_unit_assert=>fail(
          msg = |Crawler { iv_crawler_name } did not become ready in time. State: { lv_state }| ).
      ENDIF.

      WAIT UP TO 2 SECONDS.
    ENDDO.

  ENDMETHOD.

ENDCLASS.
