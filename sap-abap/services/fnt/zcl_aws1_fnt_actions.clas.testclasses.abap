CLASS ltc_zcl_aws1_fnt_actions DEFINITION DEFERRED.
CLASS zcl_aws1_fnt_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_fnt_actions.

CLASS ltc_zcl_aws1_fnt_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_fnt TYPE REF TO /aws1/if_fnt.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_fnt_actions TYPE REF TO zcl_aws1_fnt_actions.
    CLASS-DATA av_distribution_id TYPE /aws1/fntstring.
    CLASS-DATA av_s3_bucket_name TYPE /aws1/s3_bucketname.
    CLASS-DATA av_created_distrib TYPE abap_bool.

    METHODS: list_distributions FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS: update_distribution FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    CLASS-METHODS create_s3_bucket
      RETURNING
        VALUE(rv_bucket_name) TYPE /aws1/s3_bucketname
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS create_distribution
      IMPORTING
        iv_bucket_name        TYPE /aws1/s3_bucketname
      RETURNING
        VALUE(rv_distrib_id)  TYPE /aws1/fntstring
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS wait_for_distribution
      IMPORTING
        iv_distribution_id TYPE /aws1/fntstring
        iv_target_status   TYPE /aws1/fntstring DEFAULT 'Deployed'
        iv_max_wait_mins   TYPE i DEFAULT 30
      RAISING
        /aws1/cx_rt_generic.

    CLASS-METHODS disable_and_delete_dist
      IMPORTING
        iv_distribution_id TYPE /aws1/fntstring
      RAISING
        /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_zcl_aws1_fnt_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_fnt = /aws1/cl_fnt_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_fnt_actions = NEW zcl_aws1_fnt_actions( ).

    av_created_distrib = abap_false.

    " Create S3 bucket to use as CloudFront origin
    TRY.
        av_s3_bucket_name = create_s3_bucket( ).

        " Create CloudFront distribution
        av_distribution_id = create_distribution( av_s3_bucket_name ).
        av_created_distrib = abap_true.

        " Wait for distribution to be deployed (this can take 15-20 minutes)
        " We'll wait up to 30 minutes
        wait_for_distribution(
          iv_distribution_id = av_distribution_id
          iv_target_status   = 'Deployed'
          iv_max_wait_mins   = 30
        ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " If setup fails, log the error but continue
        " Tests will handle missing distribution appropriately
        DATA(lv_error_msg) = |Setup failed: { lo_exception->get_text( ) }|.
        MESSAGE lv_error_msg TYPE 'I'.
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up CloudFront distribution and S3 bucket
    IF av_distribution_id IS NOT INITIAL AND av_created_distrib = abap_true.
      TRY.
          disable_and_delete_dist( av_distribution_id ).
        CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
          DATA(lv_error) = |Failed to delete distribution: { lo_exception->get_text( ) }|.
          MESSAGE lv_error TYPE 'I'.
      ENDTRY.
    ENDIF.

    " Clean up S3 bucket
    IF av_s3_bucket_name IS NOT INITIAL.
      TRY.
          " First empty the bucket
          DATA(lo_list_result) = ao_s3->listobjectsv2( iv_bucket = av_s3_bucket_name ).
          DATA(lt_contents) = lo_list_result->get_contents( ).
          LOOP AT lt_contents INTO DATA(lo_object).
            DATA(lv_key) = lo_object->get_key( ).
            ao_s3->deleteobject(
              iv_bucket = av_s3_bucket_name
              iv_key    = lv_key
            ).
          ENDLOOP.

          " Now delete the bucket
          ao_s3->deletebucket( iv_bucket = av_s3_bucket_name ).

        CATCH /aws1/cx_rt_generic INTO lo_exception.
          lv_error = |Failed to delete S3 bucket: { lo_exception->get_text( ) }|.
          MESSAGE lv_error TYPE 'I'.
      ENDTRY.
    ENDIF.

  ENDMETHOD.

  METHOD create_s3_bucket.
    " Generate unique bucket name
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    TRY.
        lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
        lv_uuid_string = lv_uuid.
      CATCH cx_uuid_error.
        GET TIME STAMP FIELD DATA(lv_timestamp).
        lv_uuid_string = |{ lv_timestamp }|.
    ENDTRY.

    DATA(lv_account_id) = ao_session->get_account_id( ).
    rv_bucket_name = |sap-abap-fnt-test-{ lv_account_id }-{ lv_uuid_string }|.
    TRANSLATE rv_bucket_name TO LOWER CASE.
    " Ensure bucket name doesn't exceed 63 characters
    IF strlen( rv_bucket_name ) > 63.
      rv_bucket_name = rv_bucket_name(63).
    ENDIF.

    " Create the bucket with proper configuration
    DATA(lv_region) = CONV /aws1/s3_bucketlocationcnstrnt( ao_session->get_region( ) ).

    TRY.
        IF lv_region = 'us-east-1'.
          " us-east-1 doesn't require location constraint
          ao_s3->createbucket( iv_bucket = rv_bucket_name ).
        ELSE.
          " Other regions require location constraint
          DATA(lo_bucket_config) = NEW /aws1/cl_s3_createbucketconf(
            iv_locationconstraint = lv_region
          ).
          ao_s3->createbucket(
            iv_bucket                    = rv_bucket_name
            io_createbucketconfiguration = lo_bucket_config
          ).
        ENDIF.

        " Wait for bucket to be available
        DATA lv_start_tstmp TYPE timestamp.
        DATA lv_current_tstmp TYPE timestamp.
        DATA lv_wait_seconds TYPE i VALUE 5.

        GET TIME STAMP FIELD lv_start_tstmp.
        DO.
          GET TIME STAMP FIELD lv_current_tstmp.
          DATA(lv_elapsed) = lv_current_tstmp - lv_start_tstmp.
          IF lv_elapsed >= lv_wait_seconds.
            EXIT.
          ENDIF.
          WAIT UP TO '0.1' SECONDS.
        ENDDO.

      CATCH /aws1/cx_s3_bucketalrdyexists INTO DATA(lo_exists_ex).
        " If bucket already exists, try to use it or generate new name
        DATA(lv_error) = |Bucket already exists: { lo_exists_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
        " Try again with different name
        rv_bucket_name = create_s3_bucket( ).
    ENDTRY.

  ENDMETHOD.

  METHOD create_distribution.
    " Generate unique caller reference
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    TRY.
        lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
        lv_uuid_string = lv_uuid.
      CATCH cx_uuid_error.
        GET TIME STAMP FIELD DATA(lv_timestamp).
        lv_uuid_string = |{ lv_timestamp }|.
    ENDTRY.

    DATA(lv_caller_ref) = |sap-abap-test-{ lv_uuid_string }|.

    " Create S3 origin configuration
    DATA(lo_s3_origin_config) = NEW /aws1/cl_fnts3originconfig(
      iv_originaccessidentity = ||  " Empty for public S3 bucket
    ).

    " Create origin
    DATA(lo_origin) = NEW /aws1/cl_fntorigin(
      iv_id         = |S3-{ iv_bucket_name }|
      iv_domainname = |{ iv_bucket_name }.s3.amazonaws.com|
      io_s3originconfig = lo_s3_origin_config
    ).

    " Create origins list
    DATA(lo_origins) = NEW /aws1/cl_fntorigins(
      it_items    = VALUE /aws1/cl_fntorigin=>tt_originlist( ( lo_origin ) )
      iv_quantity = 1
    ).

    " Create default cache behavior
    DATA(lo_default_behavior) = NEW /aws1/cl_fntdefaultcachebehav(
      iv_targetoriginid       = |S3-{ iv_bucket_name }|
      iv_viewerprotocolpolicy = 'allow-all'
      io_trustedsigners       = NEW /aws1/cl_fnttrustedsigners(
        iv_enabled  = abap_false
        iv_quantity = 0
      )
      io_trustedkeygroups = NEW /aws1/cl_fnttrustedkeygroups(
        iv_enabled  = abap_false
        iv_quantity = 0
      )
      io_forwardedvalues = NEW /aws1/cl_fntforwardedvalues(
        iv_querystring = abap_false
        io_cookies     = NEW /aws1/cl_fntcookiepreference(
          iv_forward = 'none'
        )
      )
      iv_minttl = 0
    ).

    " Create distribution config
    DATA(lo_distrib_config) = NEW /aws1/cl_fntdistributionconfig(
      iv_callerreference      = lv_caller_ref
      iv_comment              = |ABAP SDK Test Distribution|
      iv_enabled              = abap_true
      io_origins              = lo_origins
      io_defaultcachebehavior = lo_default_behavior
    ).

    " Create the distribution
    DATA(lo_create_result) = ao_fnt->createdistribution(
      io_distributionconfig = lo_distrib_config
    ).

    DATA(lo_distribution) = lo_create_result->get_distribution( ).
    rv_distrib_id = lo_distribution->get_id( ).

  ENDMETHOD.

  METHOD wait_for_distribution.
    DATA lv_start_tstmp TYPE timestamp.
    DATA lv_current_tstmp TYPE timestamp.
    DATA lv_max_wait_secs TYPE i.
    DATA lv_status TYPE /aws1/fntstring.

    lv_max_wait_secs = iv_max_wait_mins * 60.

    GET TIME STAMP FIELD lv_start_tstmp.

    DO.
      " Get distribution status
      DATA(lo_get_result) = ao_fnt->getdistribution( iv_id = iv_distribution_id ).
      DATA(lo_distribution) = lo_get_result->get_distribution( ).
      lv_status = lo_distribution->get_status( ).

      IF lv_status = iv_target_status.
        EXIT.
      ENDIF.

      " Check if max wait time exceeded
      GET TIME STAMP FIELD lv_current_tstmp.
      DATA(lv_elapsed) = lv_current_tstmp - lv_start_tstmp.
      IF lv_elapsed >= lv_max_wait_secs.
        " Timeout - distribution not ready yet
        DATA(lv_msg) = |Distribution not { iv_target_status } after { iv_max_wait_mins } minutes|.
        MESSAGE lv_msg TYPE 'I'.
        EXIT.
      ENDIF.

      " Wait 30 seconds before checking again
      DATA lv_wait_start TYPE timestamp.
      DATA lv_wait_current TYPE timestamp.
      DATA lv_wait_secs TYPE i VALUE 30.

      GET TIME STAMP FIELD lv_wait_start.
      DO.
        GET TIME STAMP FIELD lv_wait_current.
        DATA(lv_wait_elapsed) = lv_wait_current - lv_wait_start.
        IF lv_wait_elapsed >= lv_wait_secs.
          EXIT.
        ENDIF.
        WAIT UP TO '1' SECONDS.
      ENDDO.

    ENDDO.

  ENDMETHOD.

  METHOD disable_and_delete_dist.
    " First, get the current distribution config
    DATA(lo_get_result) = ao_fnt->getdistributionconfig(
      iv_id = iv_distribution_id
    ).

    DATA(lo_config) = lo_get_result->get_distributionconfig( ).
    DATA(lv_etag) = lo_get_result->get_etag( ).

    " Check if already disabled
    DATA(lv_enabled) = lo_config->get_enabled( ).

    IF lv_enabled = abap_true.
      " Disable the distribution
      lo_config->set_enabled( abap_false ).

      ao_fnt->updatedistribution(
        io_distributionconfig = lo_config
        iv_id                 = iv_distribution_id
        iv_ifmatch            = lv_etag
      ).

      " Wait for distribution to be deployed in disabled state
      wait_for_distribution(
        iv_distribution_id = iv_distribution_id
        iv_target_status   = 'Deployed'
        iv_max_wait_mins   = 30
      ).

      " Get the new ETag after update
      lo_get_result = ao_fnt->getdistributionconfig( iv_id = iv_distribution_id ).
      lv_etag = lo_get_result->get_etag( ).
    ENDIF.

    " Now delete the distribution
    TRY.
        ao_fnt->deletedistribution(
          iv_id      = iv_distribution_id
          iv_ifmatch = lv_etag
        ).
      CATCH /aws1/cx_fntdistributionnotd00 INTO DATA(lo_not_disabled_ex).
        " Distribution not disabled yet, wait a bit more and retry
        DATA(lv_error) = |Distribution not disabled yet: { lo_not_disabled_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'I'.
    ENDTRY.

  ENDMETHOD.

  METHOD list_distributions.
    " Test listing CloudFront distributions
    DATA(lo_result) = ao_fnt_actions->list_distributions( ).

    " Verify we got a result
    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'ListDistributions should return a result object' ).

    " Verify the distribution list structure exists
    DATA(lo_distrib_list) = lo_result->get_distributionlist( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_distrib_list
      msg = 'Distribution list should be present in result' ).

    " Get the quantity
    DATA(lv_quantity) = lo_distrib_list->get_quantity( ).

    " We should have at least one distribution (the one we created)
    cl_abap_unit_assert=>assert_true(
      act = xsdbool( lv_quantity > 0 )
      msg = 'Should have at least one CloudFront distribution' ).

    " Validate the distribution we created
    DATA(lt_items) = lo_distrib_list->get_items( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_items
      msg = 'Distribution items should not be empty' ).

    " Find our test distribution
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lt_items INTO DATA(lo_distrib).
      DATA(lv_distrib_id) = lo_distrib->get_id( ).
      IF lv_distrib_id = av_distribution_id.
        lv_found = abap_true.

        " Verify essential fields
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_distrib_id
          msg = 'Distribution ID should not be empty' ).

        DATA(lv_domain_name) = lo_distrib->get_domainname( ).
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_domain_name
          msg = 'Distribution domain name should not be empty' ).

        DATA(lv_status) = lo_distrib->get_status( ).
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_status
          msg = 'Distribution status should not be empty' ).

        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Test distribution { av_distribution_id } should be found in list| ).

  ENDMETHOD.

  METHOD update_distribution.
    " Test updating a CloudFront distribution comment
    " This test requires the distribution created in class_setup

    cl_abap_unit_assert=>assert_not_initial(
      act = av_distribution_id
      msg = 'Distribution ID should be available from class_setup' ).

    " Generate a unique comment with timestamp
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    TRY.
        lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
        lv_uuid_string = lv_uuid.
      CATCH cx_uuid_error.
        GET TIME STAMP FIELD DATA(lv_timestamp).
        lv_uuid_string = |{ lv_timestamp }|.
    ENDTRY.

    DATA(lv_new_comment) = |ABAP SDK Test - Updated { lv_uuid_string }|.

    " Update the distribution
    DATA(lo_update_result) = ao_fnt_actions->update_distribution(
      iv_distribution_id = av_distribution_id
      iv_comment        = lv_new_comment
    ).

    " Verify we got a result
    cl_abap_unit_assert=>assert_bound(
      act = lo_update_result
      msg = 'UpdateDistribution should return a result object' ).

    " Verify the distribution was updated
    DATA(lo_distribution) = lo_update_result->get_distribution( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_distribution
      msg = 'Updated distribution should be present in result' ).

    " Verify the distribution ID matches
    DATA(lv_returned_id) = lo_distribution->get_id( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_returned_id
      exp = av_distribution_id
      msg = 'Returned distribution ID should match the input' ).

    " Wait a moment for the update to propagate
    DATA lv_start_tstmp TYPE timestamp.
    DATA lv_current_tstmp TYPE timestamp.
    DATA lv_wait_seconds TYPE i VALUE 5.

    GET TIME STAMP FIELD lv_start_tstmp.
    DO.
      GET TIME STAMP FIELD lv_current_tstmp.
      DATA(lv_elapsed) = lv_current_tstmp - lv_start_tstmp.
      IF lv_elapsed >= lv_wait_seconds.
        EXIT.
      ENDIF.
      WAIT UP TO '0.1' SECONDS.
    ENDDO.

    " Verify the comment was updated by getting the distribution config
    DATA(lo_get_result) = ao_fnt->getdistributionconfig(
      iv_id = av_distribution_id
    ).

    DATA(lo_config) = lo_get_result->get_distributionconfig( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_config
      msg = 'Distribution config should be returned' ).

    DATA(lv_updated_comment) = lo_config->get_comment( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_updated_comment
      exp = lv_new_comment
      msg = |Distribution comment should be updated to: { lv_new_comment }| ).

  ENDMETHOD.

ENDCLASS.
