" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_s3c_actions DEFINITION DEFERRED.
CLASS /awsex/cl_s3c_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_s3c_actions.

CLASS ltc_awsex_cl_s3c_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_s3        TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_s3c       TYPE REF TO /aws1/if_s3c.
    CLASS-DATA ao_sts       TYPE REF TO /aws1/if_sts.
    CLASS-DATA ao_iam       TYPE REF TO /aws1/if_iam.
    CLASS-DATA ao_session   TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_s3c_acts  TYPE REF TO /awsex/cl_s3c_actions.

    " Shared resources created once in class_setup
    CLASS-DATA gv_account_id    TYPE /aws1/s3caccountid.
    CLASS-DATA gv_bucket_name   TYPE /aws1/s3_bucketname.
    CLASS-DATA gv_role_arn      TYPE /aws1/s3ciamrolearn.
    CLASS-DATA gv_role_name     TYPE /aws1/iamrolenametype.
    CLASS-DATA gv_manifest_arn  TYPE /aws1/s3cs3keyarnstring.
    CLASS-DATA gv_manifest_etag TYPE string.
    CLASS-DATA gv_report_bucket TYPE /aws1/s3cs3bucketarnstring.
    CLASS-DATA gv_uuid          TYPE string.

    " Shared job used by read-only tests (describe, list, get_job_tagging)
    CLASS-DATA gv_shared_job_id TYPE /aws1/s3cjobid.

    " All dedicated jobs created by mutation tests are tracked here and
    " cancelled in class_teardown, guaranteeing cleanup even when a test
    " throws before reaching its own inline cancel.
    CLASS-DATA gt_cleanup_jobs  TYPE TABLE OF /aws1/s3cjobid.

    CLASS-METHODS class_setup    RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS create_job          FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS describe_job        FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS update_job_priority FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS update_job_status   FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_job_tagging     FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS put_job_tagging     FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS list_jobs           FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_job_tagging  FOR TESTING RAISING /aws1/cx_rt_generic.

    " Creates a fresh S3 Batch job in Suspended state and registers it for
    " teardown cleanup.  ConfirmationRequired=true keeps it Suspended.
    CLASS-METHODS create_fresh_job
      RETURNING
        VALUE(rv_job_id) TYPE /aws1/s3cjobid
      RAISING
        /aws1/cx_rt_generic.

    " Polls DescribeJob until the job reaches iv_desired_status or a terminal state.
    CLASS-METHODS wait_for_job
      IMPORTING
        iv_job_id         TYPE /aws1/s3cjobid
        iv_desired_status TYPE string
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_s3c_actions IMPLEMENTATION.

* ─────────────────────────────────────────────────────────────────────────────
  METHOD class_setup.
* ─────────────────────────────────────────────────────────────────────────────
    DATA lv_uuid_string   TYPE string.
    DATA lv_trust_policy  TYPE string.
    DATA lv_inline_policy TYPE string.
    DATA lv_file_xstr     TYPE xstring.
    DATA lv_manifest_body TYPE string.
    DATA lv_manifest_xstr TYPE xstring.
    DATA lv_etag_raw      TYPE string.

    ao_session  = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_s3       = /aws1/cl_s3_factory=>create( ao_session ).
    ao_s3c      = /aws1/cl_s3c_factory=>create( ao_session ).
    ao_sts      = /aws1/cl_sts_factory=>create( ao_session ).
    ao_iam      = /aws1/cl_iam_factory=>create( ao_session ).
    ao_s3c_acts = NEW /awsex/cl_s3c_actions( ).

    " ── Unique suffix for all resource names ───────────────────────────────
    lv_uuid_string = /awsex/cl_utils=>get_random_string( ).
    CONDENSE lv_uuid_string NO-GAPS.
    gv_uuid = to_lower( lv_uuid_string(10) ).

    " ── Resolve AWS account ID via STS ────────────────────────────────────
    DATA(lo_sts_result) = ao_sts->getcalleridentity( ).
    gv_account_id = lo_sts_result->get_account( ).
    IF gv_account_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Could not determine AWS account ID' ).
    ENDIF.

    " ── Create test S3 bucket (respects us-east-1 constraint rule) ─────────
    gv_bucket_name = |s3c-test-{ gv_uuid }|.
    /awsex/cl_utils=>create_bucket(
      iv_bucket  = gv_bucket_name
      io_s3      = ao_s3
      io_session = ao_session ).

    " Tag bucket with convert_test
    TRY.
        ao_s3->putbuckettagging(
          iv_bucket  = gv_bucket_name
          io_tagging = NEW /aws1/cl_s3_tagging(
            it_tagset = VALUE /aws1/cl_s3_tag=>tt_tagset(
              ( NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) )
            ) ) ).
      CATCH /aws1/cx_rt_generic.
        " Tagging failure is non-fatal here
    ENDTRY.

    " ── Upload three sample objects ────────────────────────────────────────
    lv_file_xstr = cl_abap_codepage=>convert_to( 'Content for object1.txt' ).
    ao_s3->putobject( iv_bucket = gv_bucket_name iv_key = 'object1.txt' iv_body = lv_file_xstr ).
    lv_file_xstr = cl_abap_codepage=>convert_to( 'Content for object2.txt' ).
    ao_s3->putobject( iv_bucket = gv_bucket_name iv_key = 'object2.txt' iv_body = lv_file_xstr ).
    lv_file_xstr = cl_abap_codepage=>convert_to( 'Content for object3.txt' ).
    ao_s3->putobject( iv_bucket = gv_bucket_name iv_key = 'object3.txt' iv_body = lv_file_xstr ).

    " ── Build and upload the job manifest CSV ─────────────────────────────
    lv_manifest_body = |{ gv_bucket_name },object1.txt\n| &&
                       |{ gv_bucket_name },object2.txt\n| &&
                       |{ gv_bucket_name },object3.txt\n|.
    lv_manifest_xstr = cl_abap_codepage=>convert_to( lv_manifest_body ).
    ao_s3->putobject(
      iv_bucket = gv_bucket_name
      iv_key    = 'job-manifest.csv'
      iv_body   = lv_manifest_xstr ).

    " ── Retrieve the manifest ETag (strip surrounding quotes) ─────────────
    DATA(lo_head) = ao_s3->headobject(
      iv_bucket = gv_bucket_name
      iv_key    = 'job-manifest.csv' ).
    lv_etag_raw = lo_head->get_etag( ).
    REPLACE ALL OCCURRENCES OF '"' IN lv_etag_raw WITH ''.
    gv_manifest_etag = lv_etag_raw.
    IF gv_manifest_etag IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Could not retrieve manifest ETag' ).
    ENDIF.

    gv_manifest_arn  = |arn:aws:s3:::{ gv_bucket_name }/job-manifest.csv|.
    gv_report_bucket = |arn:aws:s3:::{ gv_bucket_name }|.

    " ── Create IAM role that S3 Batch Operations will assume ───────────────
    gv_role_name = |s3c-test-role-{ gv_uuid }|.
    lv_trust_policy =
      '{"Version":"2012-10-17","Statement":[{"Effect":"Allow",' &&
      '"Principal":{"Service":"batchoperations.s3.amazonaws.com"},' &&
      '"Action":"sts:AssumeRole"}]}'.

    DATA(lo_iam_result) = ao_iam->createrole(
      iv_rolename                 = gv_role_name
      iv_assumerolepolicydocument = lv_trust_policy
      iv_description              = 'Role for S3 Batch Operations ABAP test'
      it_tags                     = VALUE /aws1/cl_iamtag=>tt_taglisttype(
        ( NEW /aws1/cl_iamtag( iv_key = 'convert_test' iv_value = 'true' ) )
      ) ).
    gv_role_arn = lo_iam_result->get_role( )->get_arn( ).
    IF gv_role_arn IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to create IAM role' ).
    ENDIF.

    " ── Attach inline policy granting all permissions required by
    "    S3 Batch Operations for PutObjectTagging jobs:
    "      ReadObjects  – read objects listed in the manifest
    "      TagObjects   – apply tags to those objects
    "      BucketAccess – resolve bucket location / versioning state
    "      WriteReports – write the per-job completion report
    " ─────────────────────────────────────────────────────────────────────────
    lv_inline_policy =
      '{"Version":"2012-10-17","Statement":[' &&
        '{"Sid":"ReadObjects","Effect":"Allow",' &&
          '"Action":["s3:GetObject","s3:GetObjectVersion",' &&
                   '"s3:GetObjectTagging","s3:GetObjectVersionTagging"],' &&
          '"Resource":"arn:aws:s3:::' && gv_bucket_name && '/*"},' &&
        '{"Sid":"TagObjects","Effect":"Allow",' &&
          '"Action":["s3:PutObjectTagging","s3:PutObjectVersionTagging"],' &&
          '"Resource":"arn:aws:s3:::' && gv_bucket_name && '/*"},' &&
        '{"Sid":"BucketAccess","Effect":"Allow",' &&
          '"Action":["s3:GetBucketLocation","s3:GetBucketObjectLockConfiguration",' &&
                   '"s3:GetBucketVersioning","s3:ListBucket","s3:ListBucketVersions"],' &&
          '"Resource":"arn:aws:s3:::' && gv_bucket_name && '"},' &&
        '{"Sid":"WriteReports","Effect":"Allow",' &&
          '"Action":["s3:PutObject","s3:GetObject","s3:GetBucketLocation"],' &&
          '"Resource":["arn:aws:s3:::' && gv_bucket_name && '/batch-op-reports/*",' &&
                      '"arn:aws:s3:::' && gv_bucket_name && '"]}' &&
      ']}' .

    ao_iam->putrolepolicy(
      iv_rolename       = gv_role_name
      iv_policyname     = 's3c-batch-policy'
      iv_policydocument = lv_inline_policy ).

    " IAM changes take a few seconds to propagate globally
    WAIT UP TO 15 SECONDS.

    " ── Create the shared job used by read-only tests ──────────────────────
    "    ConfirmationRequired=true → job lands in Suspended state, a stable
    "    state that supports describe/list/tag operations without risk of
    "    the job actually executing and consuming resources.
    gv_shared_job_id = create_fresh_job( ).
    IF gv_shared_job_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to create shared S3 Batch Operations job' ).
    ENDIF.
    wait_for_job( iv_job_id = gv_shared_job_id iv_desired_status = 'Suspended' ).
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD class_teardown.
* ─────────────────────────────────────────────────────────────────────────────

    " ── Cancel ALL tracked jobs (shared + dedicated) ───────────────────────
    "    Each cancel is in its own TRY/CATCH so one failure does not block the
    "    rest.  Jobs in terminal states (Complete/Failed/Cancelled) are
    "    silently skipped by the SDK; jobs that cannot be deleted via API are
    "    tagged convert_test=true for manual identification.
    LOOP AT gt_cleanup_jobs INTO DATA(lv_jid).
      TRY.
          DATA(lo_d) = ao_s3c->describejob(
            iv_accountid = gv_account_id iv_jobid = lv_jid ).
          DATA(lv_st) = lo_d->get_job( )->get_status( ).
          IF lv_st = 'Suspended' OR lv_st = 'Ready'
             OR lv_st = 'New'    OR lv_st = 'Active'.
            ao_s3c->updatejobstatus(
              iv_accountid          = gv_account_id
              iv_jobid              = lv_jid
              iv_requestedjobstatus = 'Cancelled' ).
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Job already terminal or not found – ignore
      ENDTRY.
    ENDLOOP.

    " ── Empty and delete the test bucket ──────────────────────────────────
    IF gv_bucket_name IS NOT INITIAL.
      TRY.
          /awsex/cl_utils=>cleanup_bucket( iv_bucket = gv_bucket_name io_s3 = ao_s3 ).
        CATCH /aws1/cx_rt_generic.
          " Ignore – bucket may already be gone
      ENDTRY.
    ENDIF.

    " ── Remove IAM inline policy then the role itself ─────────────────────
    IF gv_role_name IS NOT INITIAL.
      TRY.
          ao_iam->deleterolepolicy(
            iv_rolename   = gv_role_name
            iv_policyname = 's3c-batch-policy' ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
      TRY.
          ao_iam->deleterole( iv_rolename = gv_role_name ).
        CATCH /aws1/cx_rt_generic.
      ENDTRY.
    ENDIF.
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD create_fresh_job.
* ─────────────────────────────────────────────────────────────────────────────
    " Creates a brand-new job with ConfirmationRequired=true (→ Suspended) and
    " immediately appends its ID to gt_cleanup_jobs so class_teardown always
    " cancels it, regardless of whether the calling test succeeds or fails.
    DATA(lo_result) = ao_s3c->createjob(
      iv_accountid            = gv_account_id
      iv_rolearn              = gv_role_arn
      iv_confirmationrequired = abap_true
      iv_priority             = 10
      iv_description          = |s3c-test-{ gv_uuid }|
      io_operation            = NEW /aws1/cl_s3cjoboperation(
        io_s3putobjecttagging = NEW /aws1/cl_s3cs3setobjecttagop(
          it_tagset = VALUE /aws1/cl_s3cs3tag=>tt_s3tagset(
            ( NEW /aws1/cl_s3cs3tag( iv_key = 'convert_test' iv_value = 'true' ) )
          )
        )
      )
      io_manifest             = NEW /aws1/cl_s3cjobmanifest(
        io_spec     = NEW /aws1/cl_s3cjobmanifestspec(
          iv_format = 'S3BatchOperations_CSV_20180820'
          it_fields = VALUE /aws1/cl_s3cjobmanifestfield00=>tt_jobmanifestfieldlist(
            ( NEW /aws1/cl_s3cjobmanifestfield00( 'Bucket' ) )
            ( NEW /aws1/cl_s3cjobmanifestfield00( 'Key' ) )
          )
        )
        io_location = NEW /aws1/cl_s3cjobmanifestloc(
          iv_objectarn = gv_manifest_arn
          iv_etag      = gv_manifest_etag
        )
      )
      io_report               = NEW /aws1/cl_s3cjobreport(
        iv_bucket      = gv_report_bucket
        iv_format      = 'Report_CSV_20180820'
        iv_enabled     = abap_true
        iv_prefix      = 'batch-op-reports'
        iv_reportscope = 'AllTasks'
      )
    ).
    rv_job_id = lo_result->get_jobid( ).
    " Register for guaranteed teardown cleanup
    APPEND rv_job_id TO gt_cleanup_jobs.
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD wait_for_job.
* ─────────────────────────────────────────────────────────────────────────────
    " Polls DescribeJob up to ~2.5 minutes (30 × 5 s) until the job reaches
    " iv_desired_status or enters a terminal state (Failed/Cancelled/Complete).
    DATA lv_max TYPE i VALUE 30.
    DATA lv_i   TYPE i VALUE 0.
    DATA lv_cur TYPE string.

    WHILE lv_i < lv_max.
      TRY.
          DATA(lo_d) = ao_s3c->describejob(
            iv_accountid = gv_account_id
            iv_jobid     = iv_job_id ).
          lv_cur = lo_d->get_job( )->get_status( ).
          IF lv_cur = iv_desired_status
             OR lv_cur = 'Failed' OR lv_cur = 'Cancelled' OR lv_cur = 'Complete'.
            RETURN.
          ENDIF.
        CATCH /aws1/cx_rt_generic.
          " Job not visible yet – retry
      ENDTRY.
      WAIT UP TO 5 SECONDS.
      lv_i = lv_i + 1.
    ENDWHILE.
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD create_job.
* ─────────────────────────────────────────────────────────────────────────────
    " Calls the action method and asserts a non-empty Job ID is returned.
    " The new job is registered for teardown via create_fresh_job's append to
    " gt_cleanup_jobs, so it is always cancelled even if the test fails mid-way.
    DATA(lv_new_job_id) = ao_s3c_acts->create_job(
      iv_account_id    = gv_account_id
      iv_role_arn      = gv_role_arn
      iv_manifest_arn  = gv_manifest_arn
      iv_manifest_etag = gv_manifest_etag
      iv_report_bucket = gv_report_bucket ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_new_job_id
      msg = 'create_job must return a non-empty Job ID' ).

    " Register the new job for teardown (the action method itself calls the
    " SDK directly, so it is NOT registered via create_fresh_job).
    APPEND lv_new_job_id TO gt_cleanup_jobs.
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD describe_job.
* ─────────────────────────────────────────────────────────────────────────────
    " Verifies the RETURNING value contains the expected job ID and a non-empty
    " status — confirming the result object is correctly populated and returned.
    DATA(lo_result) = ao_s3c_acts->describe_job(
      iv_account_id = gv_account_id
      iv_job_id     = gv_shared_job_id ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'describe_job must return a bound result object' ).

    DATA(lo_job) = lo_result->get_job( ).
    cl_abap_unit_assert=>assert_equals(
      exp = gv_shared_job_id
      act = lo_job->get_jobid( )
      msg = 'Returned job ID must match the shared job ID' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_job->get_status( )
      msg = 'Returned job status must not be initial' ).
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD update_job_priority.
* ─────────────────────────────────────────────────────────────────────────────
    " Uses a dedicated fresh job to avoid order-dependency with other tests.
    " Asserts via the RETURNING value that the priority was applied as requested.
    DATA(lv_job_id) = create_fresh_job( ).
    wait_for_job( iv_job_id = lv_job_id iv_desired_status = 'Suspended' ).

    DATA(lo_result) = ao_s3c_acts->update_job_priority(
      iv_account_id = gv_account_id
      iv_job_id     = lv_job_id ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'update_job_priority must return a bound result object' ).

    " The action hard-codes priority=60; verify via the returned value
    cl_abap_unit_assert=>assert_equals(
      exp = 60
      act = lo_result->get_priority( )
      msg = 'Returned priority must be 60' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_job_id
      act = lo_result->get_jobid( )
      msg = 'Returned job ID must match the job that was updated' ).
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD update_job_status.
* ─────────────────────────────────────────────────────────────────────────────
    " Uses a dedicated fresh job so cancellation does not affect the shared job
    " or any other test.  Asserts via the RETURNING value.
    DATA(lv_job_id) = create_fresh_job( ).
    wait_for_job( iv_job_id = lv_job_id iv_desired_status = 'Suspended' ).

    " Guard: verify the job is actually cancellable before proceeding
    DATA(lo_pre) = ao_s3c->describejob(
      iv_accountid = gv_account_id iv_jobid = lv_job_id ).
    DATA(lv_pre_status) = lo_pre->get_job( )->get_status( ).
    IF lv_pre_status <> 'Suspended' AND lv_pre_status <> 'Ready'
       AND lv_pre_status <> 'New'.
      cl_abap_unit_assert=>fail(
        msg = |Job reached unexpected state '{ lv_pre_status }' – cannot cancel| ).
    ENDIF.

    DATA(lo_result) = ao_s3c_acts->update_job_status(
      iv_account_id       = gv_account_id
      iv_job_id           = lv_job_id
      iv_requested_status = 'Cancelled' ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'update_job_status must return a bound result object' ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'Cancelled'
      act = lo_result->get_status( )
      msg = 'Returned status must be Cancelled' ).

    cl_abap_unit_assert=>assert_equals(
      exp = lv_job_id
      act = lo_result->get_jobid( )
      msg = 'Returned job ID must match the job that was updated' ).
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD get_job_tagging.
* ─────────────────────────────────────────────────────────────────────────────
    " Pre-loads a known tag on the shared job, then asserts the RETURNING value
    " contains that tag — verifying the result object is correctly populated.
    ao_s3c->putjobtagging(
      iv_accountid = gv_account_id
      iv_jobid     = gv_shared_job_id
      it_tags      = VALUE /aws1/cl_s3cs3tag=>tt_s3tagset(
        ( NEW /aws1/cl_s3cs3tag( iv_key = 'convert_test' iv_value = 'true' ) )
      ) ).

    DATA(lo_result) = ao_s3c_acts->get_job_tagging(
      iv_account_id = gv_account_id
      iv_job_id     = gv_shared_job_id ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'get_job_tagging must return a bound result object' ).

    DATA(lt_tags) = lo_result->get_tags( ).
    cl_abap_unit_assert=>assert_true(
      act  = xsdbool( lines( lt_tags ) >= 1 )
      msg  = 'get_job_tagging: at least 1 tag must be present' ).

    DATA lv_found TYPE abap_bool.
    LOOP AT lt_tags INTO DATA(lo_tag).
      IF lo_tag->get_key( ) = 'convert_test'.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'Tag convert_test must be present in the returned tag list' ).
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD put_job_tagging.
* ─────────────────────────────────────────────────────────────────────────────
    " Uses a dedicated fresh job so tag mutations are isolated.
    " Verifies via a direct SDK call that the correct tags were stored.
    DATA(lv_job_id) = create_fresh_job( ).
    wait_for_job( iv_job_id = lv_job_id iv_desired_status = 'Suspended' ).

    " Ensure the job starts with no tags
    ao_s3c->deletejobtagging(
      iv_accountid = gv_account_id iv_jobid = lv_job_id ).

    ao_s3c_acts->put_job_tagging(
      iv_account_id = gv_account_id
      iv_job_id     = lv_job_id ).

    " Verify exactly 2 tags (Environment + Team) via direct SDK call
    DATA(lo_tags) = ao_s3c->getjobtagging(
      iv_accountid = gv_account_id iv_jobid = lv_job_id ).
    DATA(lt_tags) = lo_tags->get_tags( ).
    cl_abap_unit_assert=>assert_equals(
      exp = 2
      act = lines( lt_tags )
      msg = 'put_job_tagging must store exactly 2 tags' ).

    DATA lv_found_env  TYPE abap_bool.
    DATA lv_found_team TYPE abap_bool.
    LOOP AT lt_tags INTO DATA(lo_tag).
      IF lo_tag->get_key( ) = 'Environment'. lv_found_env  = abap_true. ENDIF.
      IF lo_tag->get_key( ) = 'Team'.        lv_found_team = abap_true. ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_true(
      act = lv_found_env  msg = 'Tag key ''Environment'' must be present' ).
    cl_abap_unit_assert=>assert_true(
      act = lv_found_team msg = 'Tag key ''Team'' must be present' ).
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD list_jobs.
* ─────────────────────────────────────────────────────────────────────────────
    " Verifies the RETURNING value contains the shared job, confirming the
    " result object is correctly populated and returned.
    DATA(lo_result) = ao_s3c_acts->list_jobs( iv_account_id = gv_account_id ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'list_jobs must return a bound result object' ).

    DATA lv_found TYPE abap_bool.
    LOOP AT lo_result->get_jobs( ) INTO DATA(lo_job).
      IF lo_job->get_jobid( ) = gv_shared_job_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.
    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'list_jobs: the shared test job must appear in the returned job list' ).
  ENDMETHOD.


* ─────────────────────────────────────────────────────────────────────────────
  METHOD delete_job_tagging.
* ─────────────────────────────────────────────────────────────────────────────
    " Uses a dedicated fresh job so deletion does not affect the shared job.
    DATA(lv_job_id) = create_fresh_job( ).
    wait_for_job( iv_job_id = lv_job_id iv_desired_status = 'Suspended' ).

    " Put a tag on the job first
    ao_s3c->putjobtagging(
      iv_accountid = gv_account_id
      iv_jobid     = lv_job_id
      it_tags      = VALUE /aws1/cl_s3cs3tag=>tt_s3tagset(
        ( NEW /aws1/cl_s3cs3tag( iv_key = 'ToDelete' iv_value = 'yes' ) )
      ) ).

    " Confirm the tag exists before deleting
    DATA(lo_before) = ao_s3c->getjobtagging(
      iv_accountid = gv_account_id iv_jobid = lv_job_id ).
    cl_abap_unit_assert=>assert_true(
      act  = xsdbool( lines( lo_before->get_tags( ) ) >= 1 )
      msg  = 'delete_job_tagging pre-condition: tag must exist before deletion' ).

    ao_s3c_acts->delete_job_tagging(
      iv_account_id = gv_account_id
      iv_job_id     = lv_job_id ).

    " Verify all tags are gone
    DATA(lo_after) = ao_s3c->getjobtagging(
      iv_accountid = gv_account_id iv_jobid = lv_job_id ).
    cl_abap_unit_assert=>assert_equals(
      exp = 0
      act = lines( lo_after->get_tags( ) )
      msg = 'delete_job_tagging must remove all tags' ).
  ENDMETHOD.

ENDCLASS.
