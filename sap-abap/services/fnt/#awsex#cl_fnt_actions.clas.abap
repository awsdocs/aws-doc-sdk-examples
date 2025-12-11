" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_fnt_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS list_distributions
      EXPORTING
        !oo_result TYPE REF TO /aws1/cl_fntlstdistributionsrs
      RAISING
        /aws1/cx_rt_generic.

    METHODS update_distribution
      IMPORTING
        !iv_distribution_id TYPE /aws1/fntstring
        !iv_comment         TYPE /aws1/fntcommenttype
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_FNT_ACTIONS IMPLEMENTATION.


  METHOD list_distributions.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_fnt) = /aws1/cl_fnt_factory=>create( lo_session ).

    " snippet-start:[fnt.abapv1.list_distributions]
    TRY.
        oo_result = lo_fnt->listdistributions( ). " oo_result is returned for testing purposes. "
        MESSAGE 'Retrieved list of CloudFront distributions.' TYPE 'I'.
      CATCH /aws1/cx_fntinvalidargument.
        MESSAGE 'Invalid argument provided.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[fnt.abapv1.list_distributions]

  ENDMETHOD.


  METHOD update_distribution.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_fnt) = /aws1/cl_fnt_factory=>create( lo_session ).

    " snippet-start:[fnt.abapv1.update_distribution]
    TRY.
        " Get the current distribution configuration and ETag "
        DATA(lo_distribution_config_result) = lo_fnt->getdistributionconfig( iv_id = iv_distribution_id ).
        DATA(lo_distribution_config) = lo_distribution_config_result->get_distributionconfig( ).
        DATA(lv_etag) = lo_distribution_config_result->get_etag( ).

        " Update the comment field "
        lo_distribution_config->set_comment( iv_comment ).

        " Update the distribution with the modified configuration "
        lo_fnt->updatedistribution(
          io_distributionconfig = lo_distribution_config
          iv_id = iv_distribution_id
          iv_ifmatch = lv_etag ).
        MESSAGE 'CloudFront distribution updated successfully.' TYPE 'I'.
      CATCH /aws1/cx_fntnosuchdistribution.
        MESSAGE 'Distribution does not exist.' TYPE 'E'.
      CATCH /aws1/cx_fntpreconditionfailed.
        MESSAGE 'Precondition failed - ETag mismatch.' TYPE 'E'.
      CATCH /aws1/cx_fntinvalidifmatchvrs.
        MESSAGE 'Invalid If-Match version.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[fnt.abapv1.update_distribution]

  ENDMETHOD.
ENDCLASS.
