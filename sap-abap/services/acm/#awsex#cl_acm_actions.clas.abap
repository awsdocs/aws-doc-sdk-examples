" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_acm_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS describe_certificate
      IMPORTING
        !iv_certificate_arn        TYPE /aws1/acmarn
      RETURNING
        VALUE(oo_result)           TYPE REF TO /aws1/cl_acmdescrcertresponse
      RAISING
        /aws1/cx_rt_generic .

    METHODS get_certificate
      IMPORTING
        !iv_certificate_arn        TYPE /aws1/acmarn
      RETURNING
        VALUE(oo_result)           TYPE REF TO /aws1/cl_acmgetcertresponse
      RAISING
        /aws1/cx_rt_generic .

    METHODS list_certificates
      IMPORTING
        !iv_max_items              TYPE /aws1/acmmaxitems
        !it_statuses               TYPE /aws1/cl_acmcertstatuses_w=>tt_certificatestatuses OPTIONAL
        !io_includes               TYPE REF TO /aws1/cl_acmfilters OPTIONAL
      RETURNING
        VALUE(oo_result)           TYPE REF TO /aws1/cl_acmlistcertsresponse
      RAISING
        /aws1/cx_rt_generic .

    METHODS import_certificate
      IMPORTING
        !iv_certificate            TYPE /aws1/acmcertificatebodyblob
        !iv_private_key            TYPE /aws1/acmprivatekeyblob
        !iv_certificate_chain      TYPE /aws1/acmcertificatechainblob OPTIONAL
      RETURNING
        VALUE(ov_certificate_arn)  TYPE /aws1/acmarn
      RAISING
        /aws1/cx_rt_generic .

    METHODS delete_certificate
      IMPORTING
        !iv_certificate_arn        TYPE /aws1/acmarn
      RAISING
        /aws1/cx_rt_generic .

    METHODS add_tags_to_certificate
      IMPORTING
        !iv_certificate_arn        TYPE /aws1/acmarn
        !it_tags                   TYPE /aws1/cl_acmtag=>tt_taglist
      RAISING
        /aws1/cx_rt_generic .

    METHODS list_tags_for_cert
      IMPORTING
        !iv_certificate_arn        TYPE /aws1/acmarn
      RETURNING
        VALUE(ot_tags)             TYPE /aws1/cl_acmtag=>tt_taglist
      RAISING
        /aws1/cx_rt_generic .

    METHODS remove_tags_from_cert
      IMPORTING
        !iv_certificate_arn        TYPE /aws1/acmarn
        !it_tags                   TYPE /aws1/cl_acmtag=>tt_taglist
      RAISING
        /aws1/cx_rt_generic .

    METHODS request_certificate
      IMPORTING
        !iv_domain_name            TYPE /aws1/acmdomainnamestring
        !it_alternate_domains      TYPE /aws1/cl_acmdomainlist_w=>tt_domainlist
        !iv_validation_method      TYPE /aws1/acmvalidationmethod
      RETURNING
        VALUE(ov_certificate_arn)  TYPE /aws1/acmarn
      RAISING
        /aws1/cx_rt_generic .

    METHODS resend_validation_email
      IMPORTING
        !iv_certificate_arn        TYPE /aws1/acmarn
        !iv_domain                 TYPE /aws1/acmdomainnamestring
        !iv_validation_domain      TYPE /aws1/acmdomainnamestring
      RAISING
        /aws1/cx_rt_generic .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_acm_actions IMPLEMENTATION.

  METHOD describe_certificate.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.describe_certificate]
    TRY.
        " iv_certificate_arn = 'arn:aws:acm:region:123456789012:certificate/certificate-id'
        oo_result = lo_acm->describecertificate( iv_certificatearn = iv_certificate_arn ).
        MESSAGE 'Certificate details retrieved.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidarnex.
        MESSAGE 'The certificate ARN is not valid.' TYPE 'E'.
      CATCH /aws1/cx_acmresourcenotfoundex.
        MESSAGE 'Certificate not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.describe_certificate]

  ENDMETHOD.

  METHOD get_certificate.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.get_certificate]
    TRY.
        " iv_certificate_arn = 'arn:aws:acm:region:123456789012:certificate/certificate-id'
        oo_result = lo_acm->getcertificate( iv_certificatearn = iv_certificate_arn ).
        MESSAGE 'Certificate body and chain retrieved.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidarnex.
        MESSAGE 'The certificate ARN is not valid.' TYPE 'E'.
      CATCH /aws1/cx_acmresourcenotfoundex.
        MESSAGE 'Certificate not found.' TYPE 'E'.
      CATCH /aws1/cx_acmrequestinprgssex.
        MESSAGE 'Certificate request is in progress.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.get_certificate]

  ENDMETHOD.

  METHOD list_certificates.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.list_certificates]
    TRY.
        oo_result = lo_acm->listcertificates(
          iv_maxitems = iv_max_items
          it_certificatestatuses = it_statuses
          io_includes = io_includes
        ).
        MESSAGE 'Certificates listed successfully.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidargsex.
        MESSAGE 'Invalid arguments provided.' TYPE 'E'.
      CATCH /aws1/cx_acmvalidationex.
        MESSAGE 'Validation error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.list_certificates]

  ENDMETHOD.

  METHOD import_certificate.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.import_certificate]
    TRY.
        DATA(lo_result) = lo_acm->importcertificate(
          iv_certificate = iv_certificate
          iv_privatekey = iv_private_key
          iv_certificatechain = iv_certificate_chain
        ).
        ov_certificate_arn = lo_result->get_certificatearn( ).
        MESSAGE 'Certificate imported successfully.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidparameterex.
        MESSAGE 'Invalid parameter provided.' TYPE 'E'.
      CATCH /aws1/cx_acmlimitexceededex.
        MESSAGE 'Certificate limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.import_certificate]

  ENDMETHOD.

  METHOD delete_certificate.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.delete_certificate]
    TRY.
        " iv_certificate_arn = 'arn:aws:acm:region:123456789012:certificate/certificate-id'
        lo_acm->deletecertificate( iv_certificatearn = iv_certificate_arn ).
        MESSAGE 'Certificate deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidarnex.
        MESSAGE 'The certificate ARN is not valid.' TYPE 'E'.
      CATCH /aws1/cx_acmresourcenotfoundex.
        MESSAGE 'Certificate not found.' TYPE 'E'.
      CATCH /aws1/cx_acmresourceinuseex.
        MESSAGE 'Certificate is in use and cannot be deleted.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.delete_certificate]

  ENDMETHOD.

  METHOD add_tags_to_certificate.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.add_tags_to_certificate]
    TRY.
        " iv_certificate_arn = 'arn:aws:acm:region:123456789012:certificate/certificate-id'
        lo_acm->addtagstocertificate(
          iv_certificatearn = iv_certificate_arn
          it_tags = it_tags
        ).
        MESSAGE 'Tags added to certificate successfully.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidarnex.
        MESSAGE 'The certificate ARN is not valid.' TYPE 'E'.
      CATCH /aws1/cx_acmresourcenotfoundex.
        MESSAGE 'Certificate not found.' TYPE 'E'.
      CATCH /aws1/cx_acminvalidtagex.
        MESSAGE 'Invalid tag provided.' TYPE 'E'.
      CATCH /aws1/cx_acmtoomanytagsex.
        MESSAGE 'Too many tags for certificate.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.add_tags_to_certificate]

  ENDMETHOD.

  METHOD list_tags_for_cert.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.list_tags_for_certificate]
    TRY.
        " iv_certificate_arn = 'arn:aws:acm:region:123456789012:certificate/certificate-id'
        DATA(lo_result) = lo_acm->listtagsforcertificate(
          iv_certificatearn = iv_certificate_arn
        ).
        ot_tags = lo_result->get_tags( ).
        MESSAGE 'Certificate tags retrieved successfully.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidarnex.
        MESSAGE 'The certificate ARN is not valid.' TYPE 'E'.
      CATCH /aws1/cx_acmresourcenotfoundex.
        MESSAGE 'Certificate not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.list_tags_for_certificate]

  ENDMETHOD.

  METHOD remove_tags_from_cert.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.remove_tags_from_certificate]
    TRY.
        " iv_certificate_arn = 'arn:aws:acm:region:123456789012:certificate/certificate-id'
        lo_acm->removetagsfromcertificate(
          iv_certificatearn = iv_certificate_arn
          it_tags = it_tags
        ).
        MESSAGE 'Tags removed from certificate successfully.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidarnex.
        MESSAGE 'The certificate ARN is not valid.' TYPE 'E'.
      CATCH /aws1/cx_acmresourcenotfoundex.
        MESSAGE 'Certificate not found.' TYPE 'E'.
      CATCH /aws1/cx_acminvalidtagex.
        MESSAGE 'Invalid tag provided.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.remove_tags_from_certificate]

  ENDMETHOD.

  METHOD request_certificate.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.request_certificate]
    TRY.
        " iv_domain_name = 'example.com'
        " iv_validation_method = 'DNS' or 'EMAIL'
        DATA(lo_result) = lo_acm->requestcertificate(
          iv_domainname = iv_domain_name
          it_subjectalternativenames = it_alternate_domains
          iv_validationmethod = iv_validation_method
        ).
        ov_certificate_arn = lo_result->get_certificatearn( ).
        MESSAGE 'Certificate requested successfully.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidparameterex.
        MESSAGE 'Invalid parameter provided.' TYPE 'E'.
      CATCH /aws1/cx_acmlimitexceededex.
        MESSAGE 'Certificate limit exceeded.' TYPE 'E'.
      CATCH /aws1/cx_acminvdomvationoptsex.
        MESSAGE 'Invalid domain validation options.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.request_certificate]

  ENDMETHOD.

  METHOD resend_validation_email.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_acm) = /aws1/cl_acm_factory=>create( lo_session ).

    " snippet-start:[acm.abapv1.resend_validation_email]
    TRY.
        " iv_certificate_arn = 'arn:aws:acm:region:123456789012:certificate/certificate-id'
        " iv_domain = 'example.com'
        " iv_validation_domain = 'example.com'
        lo_acm->resendvalidationemail(
          iv_certificatearn = iv_certificate_arn
          iv_domain = iv_domain
          iv_validationdomain = iv_validation_domain
        ).
        MESSAGE 'Validation email resent successfully.' TYPE 'I'.
      CATCH /aws1/cx_acminvalidarnex.
        MESSAGE 'The certificate ARN is not valid.' TYPE 'E'.
      CATCH /aws1/cx_acmresourcenotfoundex.
        MESSAGE 'Certificate not found.' TYPE 'E'.
      CATCH /aws1/cx_acminvalidstateex.
        MESSAGE 'Certificate is not in a valid state.' TYPE 'E'.
      CATCH /aws1/cx_acminvdomvationoptsex.
        MESSAGE 'Invalid domain validation options.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[acm.abapv1.resend_validation_email]

  ENDMETHOD.

ENDCLASS.
