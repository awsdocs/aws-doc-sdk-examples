" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_fnt_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS list_distributions
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_fntlstdistributionsrs .

    METHODS update_distribution
      IMPORTING
        !iv_distribution_id TYPE /aws1/fntstring
        !iv_new_comment     TYPE /aws1/fntcommenttype .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_fnt_actions IMPLEMENTATION.


  METHOD list_distributions.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_fnt) = /aws1/cl_fnt_factory=>create( lo_session ).

    " snippet-start:[fnt.abapv1.list_distributions]
    TRY.
        " List all CloudFront distributions
        oo_result = lo_fnt->listdistributions( ).

        " Get the distribution list from the result
        DATA(lo_distribution_list) = oo_result->get_distributionlist( ).

        IF lo_distribution_list IS NOT INITIAL.
          DATA(lv_quantity) = lo_distribution_list->get_quantity( ).
          MESSAGE 'Listed CloudFront distributions.' TYPE 'I'.

          " Loop through each distribution
          LOOP AT lo_distribution_list->get_items( ) INTO DATA(lo_distribution_summary).
            DATA(lv_id) = lo_distribution_summary->get_id( ).
            DATA(lv_domain_name) = lo_distribution_summary->get_domainname( ).
            DATA(lv_status) = lo_distribution_summary->get_status( ).

            " Output distribution information
            MESSAGE |Distribution ID: { lv_id }, Domain: { lv_domain_name }, Status: { lv_status }| TYPE 'I'.
          ENDLOOP.
        ELSE.
          MESSAGE 'No CloudFront distributions detected.' TYPE 'I'.
        ENDIF.

      CATCH /aws1/cx_fntclientexc INTO DATA(lo_client_ex).
        MESSAGE lo_client_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_fntserverexc INTO DATA(lo_server_ex).
        MESSAGE lo_server_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[fnt.abapv1.list_distributions]
  ENDMETHOD.


  METHOD update_distribution.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_fnt) = /aws1/cl_fnt_factory=>create( lo_session ).

    " snippet-start:[fnt.abapv1.update_distribution]
    TRY.
        " First, get the current distribution configuration
        DATA(lo_config_result) = lo_fnt->getdistributionconfig( iv_distribution_id ).
        DATA(lo_distribution_config) = lo_config_result->get_distributionconfig( ).
        DATA(lv_etag) = lo_config_result->get_etag( ).

        IF lo_distribution_config IS NOT INITIAL.
          " Get the current comment
          DATA(lv_old_comment) = lo_distribution_config->get_comment( ).
          MESSAGE |Current comment: { lv_old_comment }| TYPE 'I'.

          " Create a new distribution config object with updated comment
          DATA(lo_new_config) = NEW /aws1/cl_fntdistributionconfig(
            iv_callerreference = lo_distribution_config->get_callerreference( )
            io_origins = lo_distribution_config->get_origins( )
            io_defaultcachebehavior = lo_distribution_config->get_defaultcachebehavior( )
            iv_comment = iv_new_comment
            iv_enabled = lo_distribution_config->get_enabled( )
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
          ).

          " Update the distribution with the new configuration
          DATA(lo_update_result) = lo_fnt->updatedistribution(
            iv_id = iv_distribution_id
            io_distributionconfig = lo_new_config
            iv_ifmatch = lv_etag
          ).

          MESSAGE 'Distribution updated successfully.' TYPE 'I'.
        ELSE.
          MESSAGE 'Failed to retrieve distribution configuration.' TYPE 'E'.
        ENDIF.

      CATCH /aws1/cx_fntpreconditionfailed INTO DATA(lo_precond_ex).
        MESSAGE 'Precondition failed - ETag mismatch.' TYPE 'E'.
      CATCH /aws1/cx_fntclientexc INTO DATA(lo_client_ex).
        MESSAGE lo_client_ex->get_text( ) TYPE 'E'.
      CATCH /aws1/cx_fntserverexc INTO DATA(lo_server_ex).
        MESSAGE lo_server_ex->get_text( ) TYPE 'E'.
    ENDTRY.
    " snippet-end:[fnt.abapv1.update_distribution]
  ENDMETHOD.
ENDCLASS.
