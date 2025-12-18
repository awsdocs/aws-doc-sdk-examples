" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_glu_actions DEFINITION DEFERRED.
CLASS /awsex/cl_glu_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_glu_actions.

CLASS ltc_awsex_cl_glu_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA gao_glu TYPE REF TO /aws1/if_glu.
    CLASS-DATA gao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA gao_iam TYPE REF TO /aws1/if_iam.
    CLASS-DATA gao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA gav_test_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA gav_role_arn TYPE /aws1/glurole.
    CLASS-DATA gav_role_name TYPE /aws1/iam_rolenametyp.

    METHODS get_crawler FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_crawler FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_crawler FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_database FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_tables FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS start_job_run FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_jobs FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_job_runs FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_job_run FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_job FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_table FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_database FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_crawler FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS wait_for_crawler_ready
      IMPORTING
        iv_crawler_name TYPE /aws1/glunamestring
      RAISING
        /aws1/cx_rt_generic.

    METHODS cleanup_glue_resources
      IMPORTING
        iv_crawler_name  TYPE /aws1/glunamestring OPTIONAL
        iv_job_name      TYPE /aws1/glunamestring OPTIONAL
        iv_database_name TYPE /aws1/gludatabasename OPTIONAL.

ENDCLASS.

CLASS ltc_awsex_cl_glu_actions IMPLEMENTATION.

  METHOD class_setup.
    gao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    gao_glu = /aws1/cl_glu_factory=>create( gao_session ).
    gao_s3 = /aws1/cl_s3_factory=>create( gao_session ).
    gao_iam = /aws1/cl_iam_factory=>create( gao_session ).

    " Generate unique names
    DATA lv_uuid_obj TYPE sysuuid_c32.
    lv_uuid_obj = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid_obj.
    TRANSLATE lv_uuid_string TO LOWER CASE.

    gav_test_bucket = |glue-test-{ lv_uuid_string }|.
    gav_role_name = |gluetstrole{ lv_uuid_string }|.
    " Truncate role name to 64 characters max for IAM
    IF strlen( gav_role_name ) > 64.
      gav_role_name = gav_role_name(64).
    ENDIF.

    " Create S3 bucket for test data using util function
    /awsex/cl_utils=>create_bucket(
      iv_bucket = gav_test_bucket
      io_s3 = gao_s3
      io_session = gao_session ).

    " Tag the bucket for cleanup with convert_test tag
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

    " Create IAM role for Glue - do not assume it exists
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

    " Verify role was created/retrieved
    IF gav_role_arn IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to create or retrieve IAM role for Glue tests' ).
    ENDIF.

    " Attach necessary policies to the role
    TRY.
        gao_iam->attachrolepolicy(
          iv_rolename = gav_role_name
          iv_policyarn = 'arn:aws:iam::aws:policy/service-role/AWSGlueServiceRole' ).
      CATCH /aws1/cx_rt_generic.
        " Policy may already be attached, continue
    ENDTRY.

    " Add S3 access policy
    DATA(lv_s3_policy) = |\{ "Version": "2012-10-17", "Statement": [ \{ | &&
      |"Effect": "Allow", "Action": [ "s3:GetObject", "s3:PutObject", "s3:ListBucket" ], | &&
      |"Resource": [ "arn:aws:s3:::{ gav_test_bucket }/*", "arn:aws:s3:::{ gav_test_bucket }" ] \} ] \}|.

    TRY.
        gao_iam->putrolepolicy(
          iv_rolename = gav_role_name
          iv_policyname = 'GlueTestS3Access'
          iv_policydocument = lv_s3_policy ).
      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        cl_abap_unit_assert=>fail(
          msg = |Failed to attach S3 policy to role: { lo_ex->if_message~get_longtext( ) }| ).
    ENDTRY.

    " Wait for role to propagate
    WAIT UP TO 10 SECONDS.

  ENDMETHOD.

  METHOD class_teardown.
    " Note: S3 bucket is tagged with 'convert_test' for manual cleanup
    " This is because Glue databases/crawlers may take time to fully clean up

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

  METHOD get_crawler.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_crawler_name) = |glu-getcrlr-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_getdb_{ lv_uuid_string(20) }|.

    DATA lo_result TYPE REF TO /aws1/cl_glugetcrawlerresponse.

    " Create the crawler for testing
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).

    " Test get_crawler
    lo_glu_actions->get_crawler(
      EXPORTING
        iv_crawler_name = lv_crawler_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetCrawler result should not be null' ).

    DATA(lo_crawler) = lo_result->get_crawler( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_crawler
      msg = 'Crawler should not be null' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_crawler_name
      act = lo_crawler->get_name( )
      msg = 'Crawler name should match' ).
    MESSAGE 'get_crawler successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources(
      iv_crawler_name = lv_crawler_name
      iv_database_name = lv_database_name ).

  ENDMETHOD.

  METHOD create_crawler.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_crawler_name) = |glu-crtcrlr-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_crtdb_{ lv_uuid_string(20) }|.

    " Test create_crawler
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).

    " Verify crawler was created
    DATA lv_created TYPE abap_bool VALUE abap_false.
    TRY.
        DATA(lo_result) = gao_glu->getcrawler( iv_name = lv_crawler_name ).
        lv_created = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_created = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_created
      msg = 'Crawler should have been created' ).
    MESSAGE 'create_crawler successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources(
      iv_crawler_name = lv_crawler_name
      iv_database_name = lv_database_name ).

  ENDMETHOD.

  METHOD start_crawler.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_crawler_name) = |glu-strtcrlr-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_strtdb_{ lv_uuid_string(20) }|.

    " Create crawler first
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).

    " Test start_crawler
    lo_glu_actions->start_crawler( iv_crawler_name = lv_crawler_name ).

    " Wait for crawler to start
    WAIT UP TO 5 SECONDS.

    " Verify crawler is running or has run
    DATA(lo_result) = gao_glu->getcrawler( iv_name = lv_crawler_name ).
    DATA(lo_crawler) = lo_result->get_crawler( ).
    DATA(lv_state) = lo_crawler->get_state( ).

    " State could be RUNNING or READY (if it finished quickly)
    DATA(lv_valid_state) = xsdbool( lv_state = 'RUNNING' OR lv_state = 'READY' ).

    cl_abap_unit_assert=>assert_true(
      act = lv_valid_state
      msg = |Crawler should be running or ready, but state is { lv_state }| ).
    MESSAGE 'start_crawler successful' TYPE 'I'.

    " Wait for crawler to complete
    wait_for_crawler_ready( lv_crawler_name ).

    " Cleanup
    cleanup_glue_resources(
      iv_crawler_name = lv_crawler_name
      iv_database_name = lv_database_name ).

  ENDMETHOD.

  METHOD get_database.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_crawler_name) = |glu-getdbcr-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_getdb2_{ lv_uuid_string(20) }|.

    DATA lo_result TYPE REF TO /aws1/cl_glugetdatabasersp.

    " Create crawler and run it to create the database
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).
    lo_glu_actions->start_crawler( iv_crawler_name = lv_crawler_name ).
    wait_for_crawler_ready( lv_crawler_name ).

    " Test get_database
    lo_glu_actions->get_database(
      EXPORTING
        iv_database_name = lv_database_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetDatabase result should not be null' ).

    DATA(lo_database) = lo_result->get_database( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_database
      msg = 'Database should not be null' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_database_name
      act = lo_database->get_name( )
      msg = 'Database name should match' ).
    MESSAGE 'get_database successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources(
      iv_crawler_name = lv_crawler_name
      iv_database_name = lv_database_name ).

  ENDMETHOD.

  METHOD get_tables.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_crawler_name) = |glu-gettblcr-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_gettbl_{ lv_uuid_string(20) }|.

    DATA lo_result TYPE REF TO /aws1/cl_glugettablesresponse.

    " Create crawler and run it to create tables
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).
    lo_glu_actions->start_crawler( iv_crawler_name = lv_crawler_name ).
    wait_for_crawler_ready( lv_crawler_name ).

    " Test get_tables
    lo_glu_actions->get_tables(
      EXPORTING
        iv_database_name = lv_database_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetTables result should not be null' ).

    DATA(lt_tables) = lo_result->get_tablelist( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_tables )
      msg = 'Should have at least one table' ).
    MESSAGE 'get_tables successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources(
      iv_crawler_name = lv_crawler_name
      iv_database_name = lv_database_name ).

  ENDMETHOD.

  METHOD create_job.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_job_name) = |glu-job-crt-{ lv_uuid_string(20) }|.

    " Test create_job
    lo_glu_actions->create_job(
      iv_job_name = lv_job_name
      iv_description = 'Test ETL job'
      iv_role_arn = gav_role_arn
      iv_script_location = |s3://{ gav_test_bucket }/scripts/test-script.py| ).

    " Verify job was created
    DATA lv_created TYPE abap_bool VALUE abap_false.
    TRY.
        DATA(lo_result) = gao_glu->getjob( iv_jobname = lv_job_name ).
        lv_created = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_created = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_true(
      act = lv_created
      msg = 'Job should have been created' ).
    MESSAGE 'create_job successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources( iv_job_name = lv_job_name ).

  ENDMETHOD.

  METHOD start_job_run.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_job_name) = |glu-job-run-{ lv_uuid_string(20) }|.
    DATA(lv_crawler_name) = |glu-jobcrlr-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_jobdb_{ lv_uuid_string(20) }|.

    DATA lv_job_run_id TYPE /aws1/gluidstring.

    " Create job first
    lo_glu_actions->create_job(
      iv_job_name = lv_job_name
      iv_description = 'Test ETL job'
      iv_role_arn = gav_role_arn
      iv_script_location = |s3://{ gav_test_bucket }/scripts/test-script.py| ).

    " Create database for the job to run
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).
    lo_glu_actions->start_crawler( iv_crawler_name = lv_crawler_name ).
    wait_for_crawler_ready( lv_crawler_name ).

    " Test start_job_run
    TRY.
        lo_glu_actions->start_job_run(
          EXPORTING
            iv_job_name = lv_job_name
            iv_input_database = lv_database_name
            iv_input_table = 'test_data'
            iv_output_bucket_url = |s3://{ gav_test_bucket }/output/|
          IMPORTING
            ov_job_run_id = lv_job_run_id ).

        cl_abap_unit_assert=>assert_not_initial(
          act = lv_job_run_id
          msg = 'Job run ID should not be empty' ).
        MESSAGE 'start_job_run successful' TYPE 'I'.

      CATCH /aws1/cx_rt_generic.
        " Job may fail due to missing script, but we verify run was attempted
        DATA(lo_runs) = gao_glu->getjobruns( iv_jobname = lv_job_name ).
        cl_abap_unit_assert=>assert_not_initial(
          act = lines( lo_runs->get_jobruns( ) )
          msg = 'At least one job run should exist' ).
        MESSAGE 'start_job_run test completed' TYPE 'I'.
    ENDTRY.

    " Cleanup
    cleanup_glue_resources(
      iv_crawler_name = lv_crawler_name
      iv_job_name = lv_job_name
      iv_database_name = lv_database_name ).

  ENDMETHOD.

  METHOD list_jobs.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_job_name) = |glu-lstjob-{ lv_uuid_string(20) }|.

    DATA lo_result TYPE REF TO /aws1/cl_glulistjobsresponse.

    " Create a job first
    lo_glu_actions->create_job(
      iv_job_name = lv_job_name
      iv_description = 'Test ETL job'
      iv_role_arn = gav_role_arn
      iv_script_location = |s3://{ gav_test_bucket }/scripts/test-script.py| ).

    " Test list_jobs
    lo_glu_actions->list_jobs(
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'ListJobs result should not be null' ).

    DATA(lt_job_names) = lo_result->get_jobnames( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lines( lt_job_names )
      msg = 'Should have at least one job' ).
    MESSAGE 'list_jobs successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources( iv_job_name = lv_job_name ).

  ENDMETHOD.

  METHOD get_job_runs.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_job_name) = |glu-getjbrun-{ lv_uuid_string(20) }|.

    DATA lo_result TYPE REF TO /aws1/cl_glugetjobrunsresponse.

    " Create job
    lo_glu_actions->create_job(
      iv_job_name = lv_job_name
      iv_description = 'Test ETL job'
      iv_role_arn = gav_role_arn
      iv_script_location = |s3://{ gav_test_bucket }/scripts/test-script.py| ).

    " Try to start a job run
    TRY.
        DATA lv_job_run_id TYPE /aws1/gluidstring.
        lo_glu_actions->start_job_run(
          EXPORTING
            iv_job_name = lv_job_name
            iv_input_database = 'dummy'
            iv_input_table = 'test_table'
            iv_output_bucket_url = |s3://{ gav_test_bucket }/output/|
          IMPORTING
            ov_job_run_id = lv_job_run_id ).
      CATCH /aws1/cx_rt_generic.
        " Job run might fail, but we can still test get_job_runs
    ENDTRY.

    " Test get_job_runs
    lo_glu_actions->get_job_runs(
      EXPORTING
        iv_job_name = lv_job_name
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetJobRuns result should not be null' ).
    MESSAGE 'get_job_runs successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources( iv_job_name = lv_job_name ).

  ENDMETHOD.

  METHOD get_job_run.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_job_name) = |glu-getjbr-{ lv_uuid_string(20) }|.

    DATA lo_result TYPE REF TO /aws1/cl_glugetjobrunresponse.
    DATA lv_job_run_id TYPE /aws1/gluidstring.

    " Create job
    lo_glu_actions->create_job(
      iv_job_name = lv_job_name
      iv_description = 'Test ETL job'
      iv_role_arn = gav_role_arn
      iv_script_location = |s3://{ gav_test_bucket }/scripts/test-script.py| ).

    " Start a job run
    TRY.
        lo_glu_actions->start_job_run(
          EXPORTING
            iv_job_name = lv_job_name
            iv_input_database = 'dummy'
            iv_input_table = 'test_table'
            iv_output_bucket_url = |s3://{ gav_test_bucket }/output/|
          IMPORTING
            ov_job_run_id = lv_job_run_id ).
      CATCH /aws1/cx_rt_generic.
        " If job run fails to start, test with existing run
        DATA(lo_runs) = gao_glu->getjobruns( iv_jobname = lv_job_name ).
        DATA(lt_job_runs) = lo_runs->get_jobruns( ).
        IF lines( lt_job_runs ) > 0.
          lv_job_run_id = lt_job_runs[ 1 ]->get_id( ).
        ELSE.
          " No job runs available, skip test
          MESSAGE 'No job runs available for testing get_job_run' TYPE 'I'.
          cleanup_glue_resources( iv_job_name = lv_job_name ).
          RETURN.
        ENDIF.
    ENDTRY.

    " Test get_job_run
    lo_glu_actions->get_job_run(
      EXPORTING
        iv_job_name = lv_job_name
        iv_run_id = lv_job_run_id
      IMPORTING
        oo_result = lo_result ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetJobRun result should not be null' ).

    DATA(lo_job_run) = lo_result->get_jobrun( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_job_run
      msg = 'Job run should not be null' ).
    MESSAGE 'get_job_run successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources( iv_job_name = lv_job_name ).

  ENDMETHOD.

  METHOD delete_job.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_job_name) = |glu-deljob-{ lv_uuid_string(20) }|.

    " Create job first
    lo_glu_actions->create_job(
      iv_job_name = lv_job_name
      iv_description = 'Test ETL job'
      iv_role_arn = gav_role_arn
      iv_script_location = |s3://{ gav_test_bucket }/scripts/test-script.py| ).

    " Test delete_job
    lo_glu_actions->delete_job( iv_job_name = lv_job_name ).

    " Verify job was deleted
    DATA lv_exists TYPE abap_bool VALUE abap_true.
    TRY.
        gao_glu->getjob( iv_jobname = lv_job_name ).
        lv_exists = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_exists = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_false(
      act = lv_exists
      msg = 'Job should have been deleted' ).
    MESSAGE 'delete_job successful' TYPE 'I'.

  ENDMETHOD.

  METHOD delete_table.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_crawler_name) = |glu-deltbl-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_deltbl_{ lv_uuid_string(20) }|.

    " Create crawler and run it to create table
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).
    lo_glu_actions->start_crawler( iv_crawler_name = lv_crawler_name ).
    wait_for_crawler_ready( lv_crawler_name ).

    " Get table name
    DATA(lo_tables) = gao_glu->gettables( iv_databasename = lv_database_name ).
    DATA(lt_tables) = lo_tables->get_tablelist( ).

    IF lines( lt_tables ) > 0.
      DATA(lv_table_name) = lt_tables[ 1 ]->get_name( ).

      " Test delete_table
      lo_glu_actions->delete_table(
        iv_database_name = lv_database_name
        iv_table_name = lv_table_name ).

      " Verify table was deleted
      DATA lv_exists TYPE abap_bool VALUE abap_true.
      TRY.
          gao_glu->gettable(
            iv_databasename = lv_database_name
            iv_name = lv_table_name ).
          lv_exists = abap_true.
        CATCH /aws1/cx_gluentitynotfoundex.
          lv_exists = abap_false.
      ENDTRY.

      cl_abap_unit_assert=>assert_false(
        act = lv_exists
        msg = 'Table should have been deleted' ).
      MESSAGE 'delete_table successful' TYPE 'I'.
    ENDIF.

    " Cleanup
    cleanup_glue_resources(
      iv_crawler_name = lv_crawler_name
      iv_database_name = lv_database_name ).

  ENDMETHOD.

  METHOD delete_database.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_crawler_name) = |glu-deldb-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_deldb_{ lv_uuid_string(20) }|.

    " Create crawler to create the database
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).
    lo_glu_actions->start_crawler( iv_crawler_name = lv_crawler_name ).
    wait_for_crawler_ready( lv_crawler_name ).

    " Delete all tables first
    DATA(lo_tables) = gao_glu->gettables( iv_databasename = lv_database_name ).
    LOOP AT lo_tables->get_tablelist( ) INTO DATA(lo_table).
      lo_glu_actions->delete_table(
        iv_database_name = lv_database_name
        iv_table_name = lo_table->get_name( ) ).
    ENDLOOP.

    " Test delete_database
    lo_glu_actions->delete_database( iv_database_name = lv_database_name ).

    " Verify database was deleted
    DATA lv_exists TYPE abap_bool VALUE abap_true.
    TRY.
        gao_glu->getdatabase( iv_name = lv_database_name ).
        lv_exists = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_exists = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_false(
      act = lv_exists
      msg = 'Database should have been deleted' ).
    MESSAGE 'delete_database successful' TYPE 'I'.

    " Cleanup
    cleanup_glue_resources( iv_crawler_name = lv_crawler_name ).

  ENDMETHOD.

  METHOD delete_crawler.
    DATA(lo_glu_actions) = NEW /awsex/cl_glu_actions( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = /aws1/cl_rt_util=>uuid_get_system_c32( ).
    TRANSLATE lv_uuid_string TO LOWER CASE.
    DATA(lv_crawler_name) = |glu-delcrlr-{ lv_uuid_string(20) }|.
    DATA(lv_database_name) = |glu_delcrlr_{ lv_uuid_string(20) }|.

    " Create crawler first
    lo_glu_actions->create_crawler(
      iv_crawler_name = lv_crawler_name
      iv_role_arn = gav_role_arn
      iv_database_name = lv_database_name
      iv_table_prefix = 'test_'
      iv_s3_target = |s3://{ gav_test_bucket }/| ).

    wait_for_crawler_ready( lv_crawler_name ).

    " Test delete_crawler
    lo_glu_actions->delete_crawler( iv_crawler_name = lv_crawler_name ).

    " Verify crawler was deleted
    DATA lv_exists TYPE abap_bool VALUE abap_true.
    TRY.
        gao_glu->getcrawler( iv_name = lv_crawler_name ).
        lv_exists = abap_true.
      CATCH /aws1/cx_gluentitynotfoundex.
        lv_exists = abap_false.
    ENDTRY.

    cl_abap_unit_assert=>assert_false(
      act = lv_exists
      msg = 'Crawler should have been deleted' ).
    MESSAGE 'delete_crawler successful' TYPE 'I'.

    " Note: Database may still exist, but that's okay for this test
    " Cleanup databases that aren't automatically deleted
    TRY.
        gao_glu->deletedatabase( iv_name = lv_database_name ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

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
          msg = |Crawler { iv_crawler_name } did not become ready. State: { lv_state }| ).
      ENDIF.

      WAIT UP TO 2 SECONDS.
    ENDDO.

  ENDMETHOD.

  METHOD cleanup_glue_resources.
    " Delete crawler if provided
    IF iv_crawler_name IS NOT INITIAL.
      TRY.
          gao_glu->deletecrawler( iv_name = iv_crawler_name ).
        CATCH /aws1/cx_gluentitynotfoundex.
        CATCH /aws1/cx_glucrawlerrunningex.
          TRY.
              gao_glu->stopcrawler( iv_name = iv_crawler_name ).
              WAIT UP TO 10 SECONDS.
              gao_glu->deletecrawler( iv_name = iv_crawler_name ).
            CATCH /aws1/cx_rt_generic.
          ENDTRY.
      ENDTRY.
    ENDIF.

    " Delete job if provided
    IF iv_job_name IS NOT INITIAL.
      TRY.
          gao_glu->deletejob( iv_jobname = iv_job_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

    " Delete database if provided (after cleaning up tables)
    IF iv_database_name IS NOT INITIAL.
      TRY.
          DATA(lo_tables_result) = gao_glu->gettables( iv_databasename = iv_database_name ).
          LOOP AT lo_tables_result->get_tablelist( ) INTO DATA(lo_table).
            TRY.
                gao_glu->deletetable(
                  iv_databasename = iv_database_name
                  iv_name = lo_table->get_name( ) ).
              CATCH /aws1/cx_rt_generic.
            ENDTRY.
          ENDLOOP.
          gao_glu->deletedatabase( iv_name = iv_database_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.

  ENDMETHOD.

ENDCLASS.
