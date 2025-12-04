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

        DATA(lo_distrib_config) = lo_get_result->get_distributionconfig( ).
        DATA(lv_etag) = lo_get_result->get_etag( ).

        " Update the comment field
        " Example: 'Updated comment for CloudFront distribution'
        lo_distrib_config->set_comment( iv_comment ).

        " Update the distribution
        oo_result = lo_fnt->updatedistribution(
          io_distributionconfig = lo_distrib_config
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
