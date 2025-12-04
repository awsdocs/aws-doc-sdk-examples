CLASS ltc_zcl_aws1_fnt_actions DEFINITION DEFERRED.
CLASS zcl_aws1_fnt_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_fnt_actions.

CLASS ltc_zcl_aws1_fnt_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_fnt TYPE REF TO /aws1/if_fnt.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_fnt_actions TYPE REF TO zcl_aws1_fnt_actions.
    CLASS-DATA av_test_distribution_id TYPE /aws1/fntstring.
    CLASS-DATA av_test_bucket_name TYPE /aws1/s3_bucketname.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown.

    METHODS list_distributions FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS update_distribution FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS wait_for_distribution_deployed
      IMPORTING
        iv_distribution_id TYPE /aws1/fntstring
      RAISING
        /aws1/cx_rt_generic.
ENDCLASS.


CLASS ltc_zcl_aws1_fnt_actions IMPLEMENTATION.

  METHOD class_setup.
    " Create session and CloudFront client
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_fnt = /aws1/cl_fnt_factory=>create( ao_session ).
    ao_fnt_actions = NEW zcl_aws1_fnt_actions( ).

    " Create a test S3 bucket for CloudFront origin
    DATA(lo_s3) = /aws1/cl_s3_factory=>create( ao_session ).
    DATA(lv_region) = ao_session->get_region( ).
    DATA lv_region_string TYPE /aws1/s3_bucketlocationcnstrnt.
    lv_region_string = lv_region.
    DATA(lv_uuid) = cl_system_uuid=>create_uuid_x16_static( ).
    DATA lv_uuid_string TYPE string.
    lv_uuid_string = lv_uuid.
    av_test_bucket_name = |aws-abap-fnt-test-{ lv_uuid_string }|.
    av_test_bucket_name = to_lower( av_test_bucket_name ).

    TRY.
        " Create S3 bucket
        IF lv_region = 'us-east-1'.
          lo_s3->createbucket( iv_bucket = av_test_bucket_name ).
        ELSE.
          DATA(lo_create_bucket_config) = NEW /aws1/cl_s3_createbucketconf( iv_locationconstraint = lv_region_string ).
          lo_s3->createbucket(
            iv_bucket = av_test_bucket_name
            io_createbucketconfiguration = lo_create_bucket_config
          ).
        ENDIF.

        " Wait for bucket to be created
        DATA lv_start_time TYPE timestamp.
        DATA lv_current_time TYPE timestamp.
        DATA lv_elapsed TYPE i.
        GET TIME STAMP FIELD lv_start_time.

        DO 30 TIMES.
          TRY.
              lo_s3->headbucket( iv_bucket = av_test_bucket_name ).
              EXIT.
            CATCH /aws1/cx_s3_nosuchbucket.
              WAIT UP TO 2 SECONDS.
              GET TIME STAMP FIELD lv_current_time.
              lv_elapsed = lv_current_time - lv_start_time.
              IF lv_elapsed > 60.
                EXIT.
              ENDIF.
          ENDTRY.
        ENDDO.

        " Create a CloudFront distribution
        DATA(lv_caller_ref) = |test-dist-{ lv_uuid_string }|.
        DATA(lv_origin_id) = |S3-{ av_test_bucket_name }|.
        DATA(lv_domain) = |{ av_test_bucket_name }.s3.{ lv_region_string }.amazonaws.com|.

        " Create origin configuration
        DATA(lo_origins) = NEW /aws1/cl_fntorigins(
          iv_quantity = 1
          it_items = VALUE /aws1/cl_fntorigin=>tt_originlist(
            (
              NEW /aws1/cl_fntorigin(
                iv_id = lv_origin_id
                iv_domainname = lv_domain
                io_s3originconfig = NEW /aws1/cl_fnts3originconfig(
                  iv_originaccessidentity = ''
                )
              )
            )
          )
        ).

        " Create default cache behavior
        DATA(lo_default_cache_behavior) = NEW /aws1/cl_fntdefaultcachebehav(
          iv_targetoriginid = lv_origin_id
          iv_viewerprotocolpolicy = 'allow-all'
          io_trustedsigners = NEW /aws1/cl_fnttrustedsigners(
            iv_enabled = abap_false
            iv_quantity = 0
          )
          io_trustedkeygroups = NEW /aws1/cl_fnttrustedkeygroups(
            iv_enabled = abap_false
            iv_quantity = 0
          )
          io_forwardedvalues = NEW /aws1/cl_fntforwardedvalues(
            iv_querystring = abap_false
            io_cookies = NEW /aws1/cl_fntcookiepreference(
              iv_forward = 'none'
            )
          )
          iv_minttl = 0
        ).

        " Create distribution configuration
        DATA(lo_distribution_config) = NEW /aws1/cl_fntdistributionconfig(
          iv_callerreference = lv_caller_ref
          io_origins = lo_origins
          io_defaultcachebehavior = lo_default_cache_behavior
          iv_comment = 'Test distribution for ABAP SDK'
          iv_enabled = abap_true
        ).

        " Create the distribution
        DATA(lo_create_result) = ao_fnt->createdistribution( io_distributionconfig = lo_distribution_config ).
        DATA(lo_distribution) = lo_create_result->get_distribution( ).
        av_test_distribution_id = lo_distribution->get_id( ).

        " Wait for distribution to be deployed (this can take several minutes)
        WAIT UP TO 10 SECONDS.

      CATCH /aws1/cx_rt_generic INTO DATA(lo_ex).
        MESSAGE lo_ex->get_text( ) TYPE 'I'.
    ENDTRY.
  ENDMETHOD.


  METHOD class_teardown.
    IF av_test_distribution_id IS NOT INITIAL.
      TRY.
          " First, disable the distribution
          DATA(lo_config_result) = ao_fnt->getdistributionconfig( av_test_distribution_id ).
          DATA(lo_distribution_config) = lo_config_result->get_distributionconfig( ).
          DATA(lv_etag) = lo_config_result->get_etag( ).

          " Create disabled config
          DATA(lo_disabled_config) = NEW /aws1/cl_fntdistributionconfig(
            iv_callerreference = lo_distribution_config->get_callerreference( )
            io_origins = lo_distribution_config->get_origins( )
            io_defaultcachebehavior = lo_distribution_config->get_defaultcachebehavior( )
            iv_comment = lo_distribution_config->get_comment( )
            iv_enabled = abap_false
            io_aliases = lo_distribution_config->get_aliases( )
            io_cachebehaviors = lo_distribution_config->get_cachebehaviors( )
            io_customerrorresponses = lo_distribution_config->get_customerrorresponses( )
            io_logging = lo_distribution_config->get_logging( )
            io_origingroups = lo_distribution_config->get_origingroups( )
            io_restrictions = lo_distribution_config->get_restrictions( )
            io_viewercertificate = lo_distribution_config->get_viewercertificate( )
            iv_priceclass = lo_distribution_config->get_priceclass( )
            iv_httpversion = lo_distribution_config->get_httpversion( )
            iv_isipv6enabled = lo_distribution_config->get_isipv6enabled( )
            iv_webaclid = lo_distribution_config->get_webaclid( )
            iv_defaultrootobject = lo_distribution_config->get_defaultrootobject( )
            iv_contdeploymentpolicyid = lo_distribution_config->get_contdeploymentpolicyid( )
            iv_staging = lo_distribution_config->get_staging( )
            iv_anycastiplistid = lo_distribution_config->get_anycastiplistid( )
            io_tenantconfig = lo_distribution_config->get_tenantconfig( )
            iv_connectionmode = lo_distribution_config->get_connectionmode( )
            io_viewermtlsconfig = lo_distribution_config->get_viewermtlsconfig( )
            io_connectionfunctionassoc = lo_distribution_config->get_connectionfunctionassoc( )
          ).

          ao_fnt->updatedistribution(
            iv_id = av_test_distribution_id
            io_distributionconfig = lo_disabled_config
            iv_ifmatch = lv_etag
          ).

          " Wait for distribution to be disabled
          WAIT UP TO 60 SECONDS.

          " Get the updated ETag
          lo_config_result = ao_fnt->getdistributionconfig( av_test_distribution_id ).
          lv_etag = lo_config_result->get_etag( ).

          " Delete the distribution
          ao_fnt->deletedistribution(
            iv_id = av_test_distribution_id
            iv_ifmatch = lv_etag
          ).

        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.

    " Clean up S3 bucket
    IF av_test_bucket_name IS NOT INITIAL.
      TRY.
          DATA(lo_s3) = /aws1/cl_s3_factory=>create( ao_session ).
          lo_s3->deletebucket( iv_bucket = av_test_bucket_name ).
        CATCH /aws1/cx_rt_generic.
          " Ignore errors during cleanup
      ENDTRY.
    ENDIF.
  ENDMETHOD.


  METHOD list_distributions.
    " Call the list_distributions method
    ao_fnt_actions->list_distributions( ).

    " Verify that we can list distributions successfully
    DATA(lo_result) = ao_fnt->listdistributions( ).
    DATA(lo_distribution_list) = lo_result->get_distributionlist( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_distribution_list
      msg = 'Distribution list should not be null'
    ).

    " Check if our test distribution is in the list
    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT lo_distribution_list->get_items( ) INTO DATA(lo_summary).
      IF lo_summary->get_id( ) = av_test_distribution_id.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_equals(
      act = lv_found
      exp = abap_true
      msg = 'Test distribution should be found in the list'
    ).
  ENDMETHOD.


  METHOD update_distribution.
    " Define a new comment
    DATA(lv_new_comment) = 'Updated test distribution comment'.

    " Call the update_distribution method
    ao_fnt_actions->update_distribution(
      iv_distribution_id = av_test_distribution_id
      iv_new_comment = lv_new_comment
    ).

    " Verify the distribution was updated
    DATA(lo_config_result) = ao_fnt->getdistributionconfig( av_test_distribution_id ).
    DATA(lo_distribution_config) = lo_config_result->get_distributionconfig( ).
    DATA(lv_actual_comment) = lo_distribution_config->get_comment( ).

    cl_abap_unit_assert=>assert_equals(
      act = lv_actual_comment
      exp = lv_new_comment
      msg = 'Distribution comment should be updated'
    ).
  ENDMETHOD.


  METHOD wait_for_distribution_deployed.
    " Wait for distribution to be deployed (status = 'Deployed')
    DATA lv_start_time TYPE timestamp.
    DATA lv_current_time TYPE timestamp.
    DATA lv_elapsed TYPE i.
    GET TIME STAMP FIELD lv_start_time.

    DO 120 TIMES.
      TRY.
          DATA(lo_dist_result) = ao_fnt->getdistribution( iv_distribution_id ).
          DATA(lo_distribution) = lo_dist_result->get_distribution( ).
          DATA(lv_status) = lo_distribution->get_status( ).

          IF lv_status = 'Deployed'.
            EXIT.
          ENDIF.

          WAIT UP TO 30 SECONDS.

          GET TIME STAMP FIELD lv_current_time.
          lv_elapsed = lv_current_time - lv_start_time.
          IF lv_elapsed > 3600. " 1 hour timeout
            EXIT.
          ENDIF.

        CATCH /aws1/cx_rt_generic.
          WAIT UP TO 30 SECONDS.
      ENDTRY.
    ENDDO.
  ENDMETHOD.

ENDCLASS.
