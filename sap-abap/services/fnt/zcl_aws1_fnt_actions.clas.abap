CLASS zcl_aws1_fnt_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS list_distributions
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_fntlstdistributionsrs
      RAISING
        /aws1/cx_rt_generic .

    METHODS update_distribution
      IMPORTING
        !iv_distribution_id TYPE /aws1/fntstring
        !iv_comment         TYPE /aws1/fntcommenttype
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_fntupdistributionrs
      RAISING
        /aws1/cx_rt_generic .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS zcl_aws1_fnt_actions IMPLEMENTATION.

  METHOD list_distributions.
    " Standard constants for session and client creation
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    " Create session and CloudFront client
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_fnt) = /aws1/cl_fnt_factory=>create( lo_session ).

    " snippet-start:[fnt.abapv1.list_distributions]
    TRY.
        " List all CloudFront distributions
        oo_result = lo_fnt->listdistributions( ).

        " Output distribution information
        DATA(lo_distrib_list) = oo_result->get_distributionlist( ).
        IF lo_distrib_list IS NOT INITIAL.
          DATA(lv_quantity) = lo_distrib_list->get_quantity( ).
          IF lv_quantity > 0.
            LOOP AT lo_distrib_list->get_items( ) INTO DATA(lo_distribution).
              " Example: 'my-distribution.cloudfront.net'
              DATA(lv_domain_name) = lo_distribution->get_domainname( ).
              " Example: 'E1234567890ABC'
              DATA(lv_distrib_id) = lo_distribution->get_id( ).
              DATA(lo_viewer_cert) = lo_distribution->get_viewercertificate( ).
              IF lo_viewer_cert IS NOT INITIAL.
                " Example: 'acm' or 'cloudfront'
                DATA(lv_cert_source) = lo_viewer_cert->get_certificatesource( ).
                IF lv_cert_source = 'acm'.
                  " Example: 'arn:aws:acm:us-east-1:123456789012:certificate/...'
                  DATA(lv_certificate) = lo_viewer_cert->get_certificate( ).
                ENDIF.
              ENDIF.
              MESSAGE 'Retrieved CloudFront distribution.' TYPE 'I'.
            ENDLOOP.
          ELSE.
            MESSAGE 'No CloudFront distributions detected.' TYPE 'I'.
          ENDIF.
        ENDIF.

      CATCH /aws1/cx_fntinvalidargument INTO DATA(lo_inv_arg_ex).
        DATA(lv_error) = |Invalid argument: { lo_inv_arg_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[fnt.abapv1.list_distributions]

  ENDMETHOD.

  METHOD update_distribution.
    " Standard constants for session and client creation
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    " Create session and CloudFront client
    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_fnt) = /aws1/cl_fnt_factory=>create( lo_session ).

    " snippet-start:[fnt.abapv1.update_distribution]
    TRY.
        " First, get the current distribution configuration
        DATA(lo_get_result) = lo_fnt->getdistributionconfig(
          iv_id = iv_distribution_id
        ).

        DATA(lo_old_config) = lo_get_result->get_distributionconfig( ).
        DATA(lv_etag) = lo_get_result->get_etag( ).

        " Create new config with updated comment
        " We need to recreate the config object with all existing values
        " and the new comment value
        " Example: 'Updated comment for CloudFront distribution'
        DATA(lo_new_config) = NEW /aws1/cl_fntdistributionconfig(
          iv_callerreference         = lo_old_config->get_callerreference( )
          io_aliases                 = lo_old_config->get_aliases( )
          iv_defaultrootobject       = lo_old_config->get_defaultrootobject( )
          io_origins                 = lo_old_config->get_origins( )
          io_origingroups            = lo_old_config->get_origingroups( )
          io_defaultcachebehavior    = lo_old_config->get_defaultcachebehavior( )
          io_cachebehaviors          = lo_old_config->get_cachebehaviors( )
          io_customerrorresponses    = lo_old_config->get_customerrorresponses( )
          iv_comment                 = iv_comment
          io_logging                 = lo_old_config->get_logging( )
          iv_priceclass              = lo_old_config->get_priceclass( )
          iv_enabled                 = lo_old_config->get_enabled( )
          io_viewercertificate       = lo_old_config->get_viewercertificate( )
          io_restrictions            = lo_old_config->get_restrictions( )
          iv_webaclid                = lo_old_config->get_webaclid( )
          iv_httpversion             = lo_old_config->get_httpversion( )
          iv_isipv6enabled           = lo_old_config->get_isipv6enabled( )
          iv_contdeploymentpolicyid  = lo_old_config->get_contdeploymentpolicyid( )
          iv_staging                 = lo_old_config->get_staging( )
          iv_anycastiplistid         = lo_old_config->get_anycastiplistid( )
          io_tenantconfig            = lo_old_config->get_tenantconfig( )
          iv_connectionmode          = lo_old_config->get_connectionmode( )
        ).

        " Update the distribution
        oo_result = lo_fnt->updatedistribution(
          io_distributionconfig = lo_new_config
          iv_id                 = iv_distribution_id
          iv_ifmatch            = lv_etag
        ).

        MESSAGE 'Updated CloudFront distribution successfully.' TYPE 'I'.

      CATCH /aws1/cx_fntnosuchdistribution INTO DATA(lo_no_dist_ex).
        DATA(lv_error) = |Distribution not found: { lo_no_dist_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_fntpreconditionfailed INTO DATA(lo_precond_ex).
        lv_error = |Precondition failed (ETag mismatch): { lo_precond_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_fntinvalidargument INTO DATA(lo_inv_arg_ex).
        lv_error = |Invalid argument: { lo_inv_arg_ex->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[fnt.abapv1.update_distribution]

  ENDMETHOD.

ENDCLASS.
