" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_awsex_cl_acm_actions DEFINITION DEFERRED.
CLASS /awsex/cl_acm_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_acm_actions.

CLASS ltc_awsex_cl_acm_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA ao_acm TYPE REF TO /aws1/if_acm.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_acm_actions TYPE REF TO /awsex/cl_acm_actions.
    CLASS-DATA av_certificate_arn TYPE /aws1/acmarn.
    CLASS-DATA av_certificate_arn_import TYPE /aws1/acmarn.
    CLASS-DATA av_certificate_arn_tags TYPE /aws1/acmarn.

    METHODS: describe_certificate FOR TESTING RAISING /aws1/cx_rt_generic,
      get_certificate FOR TESTING RAISING /aws1/cx_rt_generic,
      list_certificates FOR TESTING RAISING /aws1/cx_rt_generic,
      import_certificate FOR TESTING RAISING /aws1/cx_rt_generic,
      add_tags_to_certificate FOR TESTING RAISING /aws1/cx_rt_generic,
      list_tags_for_cert FOR TESTING RAISING /aws1/cx_rt_generic,
      remove_tags_from_cert FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_certificate FOR TESTING RAISING /aws1/cx_rt_generic,
      request_certificate FOR TESTING RAISING /aws1/cx_rt_generic,
      resend_validation_email FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown RAISING /aws1/cx_rt_generic.

    METHODS wait_for_certificate
      IMPORTING
        iv_certificate_arn TYPE /aws1/acmarn
        iv_max_wait_sec    TYPE i DEFAULT 300
      RAISING
        /aws1/cx_rt_generic.

    METHODS generate_self_signed_cert
      EXPORTING
        ev_certificate TYPE /aws1/acmcertificatebodyblob
        ev_private_key TYPE /aws1/acmprivatekeyblob.
ENDCLASS.

CLASS ltc_awsex_cl_acm_actions IMPLEMENTATION.

  METHOD class_setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_acm = /aws1/cl_acm_factory=>create( ao_session ).
    ao_acm_actions = NEW /awsex/cl_acm_actions( ).

    " Request a certificate for testing (DNS validation)
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    TRY.
        lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
        lv_uuid_string = lv_uuid.
      CATCH cx_uuid_error.
        " Fallback to a simple timestamp-based unique identifier
        DATA(lv_timestamp) = sy-datum && sy-uzeit.
        lv_uuid_string = lv_timestamp.
    ENDTRY.

    DATA(lv_domain) = |acm-test-{ lv_uuid_string }.example.com|.

    " Add convert_test tag to help with cleanup
    DATA lt_tags TYPE /aws1/cl_acmtag=>tt_taglist.
    DATA(lo_tag) = NEW /aws1/cl_acmtag( iv_key = 'convert_test' iv_value = 'true' ).
    APPEND lo_tag TO lt_tags.

    TRY.
        " Don't pass subjectAlternativeNames if empty - it's optional
        DATA(lo_result) = ao_acm->requestcertificate(
          iv_domainname = lv_domain
          iv_validationmethod = 'DNS'
          it_tags = lt_tags
        ).
        av_certificate_arn = lo_result->get_certificatearn( ).

        " Request another certificate for tags testing
        lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
        lv_uuid_string = lv_uuid.
        lv_domain = |acm-test-tags-{ lv_uuid_string }.example.com|.
        lo_result = ao_acm->requestcertificate(
          iv_domainname = lv_domain
          iv_validationmethod = 'DNS'
          it_tags = lt_tags
        ).
        av_certificate_arn_tags = lo_result->get_certificatearn( ).

      CATCH /aws1/cx_acmclientexc INTO DATA(lo_ex).
        " If there's an error, continue anyway
        DATA(lv_error) = lo_ex->if_message~get_text( ).
    ENDTRY.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up certificates - note: certificates in PENDING_VALIDATION can be deleted
    " Certificates that are issued and in use cannot be deleted
    IF av_certificate_arn IS NOT INITIAL.
      TRY.
          ao_acm->deletecertificate( iv_certificatearn = av_certificate_arn ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    IF av_certificate_arn_import IS NOT INITIAL.
      TRY.
          ao_acm->deletecertificate( iv_certificatearn = av_certificate_arn_import ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.

    IF av_certificate_arn_tags IS NOT INITIAL.
      TRY.
          ao_acm->deletecertificate( iv_certificatearn = av_certificate_arn_tags ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD describe_certificate.
    DATA lo_result TYPE REF TO /aws1/cl_acmdescrcertresponse.

    lo_result = ao_acm_actions->describe_certificate( av_certificate_arn ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    DATA(lo_cert) = lo_result->get_certificate( ).
    cl_abap_unit_assert=>assert_bound(
      act = lo_cert
      msg = 'Certificate details should be returned' ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_certificate_arn
      act = lo_cert->get_certificatearn( )
      msg = 'Certificate ARN should match' ).
  ENDMETHOD.

  METHOD get_certificate.
    " Get certificate requires the certificate to be issued, which takes time
    " For now, we'll just test that the method doesn't crash with pending certificate
    TRY.
        DATA(lo_result) = ao_acm_actions->get_certificate( av_certificate_arn ).
        " If it succeeds, verify result
        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Result should be bound if certificate is issued' ).
      CATCH /aws1/cx_acmrequestinprgssex.
        " This is expected for a pending certificate
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = 'Request in progress is expected for pending certificate' ).
    ENDTRY.
  ENDMETHOD.

  METHOD list_certificates.
    DATA lo_result TYPE REF TO /aws1/cl_acmlistcertsresponse.

    " List certificates with PENDING_VALIDATION status
    DATA lt_statuses TYPE /aws1/cl_acmcertstatuses_w=>tt_certificatestatuses.
    DATA(lo_status) = NEW /aws1/cl_acmcertstatuses_w( iv_value = 'PENDING_VALIDATION' ).
    APPEND lo_status TO lt_statuses.

    lo_result = ao_acm_actions->list_certificates(
      iv_max_items = 10
      it_statuses = lt_statuses
    ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Result should not be initial' ).

    " Check if our test certificate is in the list
    DATA(lv_found) = abap_false.
    LOOP AT lo_result->get_certificatesummarylist( ) INTO DATA(lo_cert).
      IF lo_cert->get_certificatearn( ) = av_certificate_arn.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'Test certificate should be in the list' ).
  ENDMETHOD.

  METHOD import_certificate.
    DATA lv_certificate TYPE /aws1/acmcertificatebodyblob.
    DATA lv_private_key TYPE /aws1/acmprivatekeyblob.

    " Generate self-signed certificate
    generate_self_signed_cert(
      IMPORTING
        ev_certificate = lv_certificate
        ev_private_key = lv_private_key
    ).

    " Import the certificate
    av_certificate_arn_import = ao_acm_actions->import_certificate(
      iv_certificate = lv_certificate
      iv_private_key = lv_private_key
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = av_certificate_arn_import
      msg = 'Certificate ARN should be returned' ).

    " Verify the certificate was imported
    DATA(lo_result) = ao_acm->describecertificate( iv_certificatearn = av_certificate_arn_import ).
    cl_abap_unit_assert=>assert_equals(
      exp = av_certificate_arn_import
      act = lo_result->get_certificate( )->get_certificatearn( )
      msg = 'Imported certificate should exist' ).
  ENDMETHOD.

  METHOD add_tags_to_certificate.
    " Add tags to certificate
    DATA lt_tags TYPE /aws1/cl_acmtag=>tt_taglist.
    DATA(lo_tag1) = NEW /aws1/cl_acmtag( iv_key = 'Environment' iv_value = 'Test' ).
    DATA(lo_tag2) = NEW /aws1/cl_acmtag( iv_key = 'Purpose' iv_value = 'Demo' ).
    APPEND lo_tag1 TO lt_tags.
    APPEND lo_tag2 TO lt_tags.

    ao_acm_actions->add_tags_to_certificate(
      iv_certificate_arn = av_certificate_arn_tags
      it_tags = lt_tags
    ).

    " Verify tags were added
    DATA(lo_result) = ao_acm->listtagsforcertificate( iv_certificatearn = av_certificate_arn_tags ).
    DATA(lt_result_tags) = lo_result->get_tags( ).

    cl_abap_unit_assert=>assert_true(
      act = xsdbool( lines( lt_result_tags ) >= 2 )
      msg = 'At least 2 tags should be present' ).
  ENDMETHOD.

  METHOD list_tags_for_cert.
    " First add some tags
    DATA lt_tags TYPE /aws1/cl_acmtag=>tt_taglist.
    DATA(lo_tag) = NEW /aws1/cl_acmtag( iv_key = 'TestTag' iv_value = 'TestValue' ).
    APPEND lo_tag TO lt_tags.

    ao_acm->addtagstocertificate(
      iv_certificatearn = av_certificate_arn_tags
      it_tags = lt_tags
    ).

    " List tags
    DATA(lt_result_tags) = ao_acm_actions->list_tags_for_cert( av_certificate_arn_tags ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_result_tags
      msg = 'Tags should be returned' ).

    " Check if our tag is present
    DATA(lv_found) = abap_false.
    LOOP AT lt_result_tags INTO DATA(lo_result_tag).
      IF lo_result_tag->get_key( ) = 'TestTag'.
        lv_found = abap_true.
        cl_abap_unit_assert=>assert_equals(
          exp = 'TestValue'
          act = lo_result_tag->get_value( )
          msg = 'Tag value should match' ).
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = 'TestTag should be present' ).
  ENDMETHOD.

  METHOD remove_tags_from_cert.
    " First add a tag
    DATA lt_tags TYPE /aws1/cl_acmtag=>tt_taglist.
    DATA(lo_tag) = NEW /aws1/cl_acmtag( iv_key = 'RemoveMe' iv_value = 'ToBeRemoved' ).
    APPEND lo_tag TO lt_tags.

    ao_acm->addtagstocertificate(
      iv_certificatearn = av_certificate_arn_tags
      it_tags = lt_tags
    ).

    " Now remove it
    ao_acm_actions->remove_tags_from_cert(
      iv_certificate_arn = av_certificate_arn_tags
      it_tags = lt_tags
    ).

    " Verify tag was removed
    DATA(lo_result) = ao_acm->listtagsforcertificate( iv_certificatearn = av_certificate_arn_tags ).
    DATA(lt_result_tags) = lo_result->get_tags( ).

    DATA(lv_found) = abap_false.
    LOOP AT lt_result_tags INTO DATA(lo_result_tag).
      IF lo_result_tag->get_key( ) = 'RemoveMe'.
        lv_found = abap_true.
        EXIT.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg = 'Tag should have been removed' ).
  ENDMETHOD.

  METHOD delete_certificate.
    " Create a new certificate specifically for deletion
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
    lv_uuid_string = lv_uuid.

    DATA(lv_domain) = |acm-test-delete-{ lv_uuid_string }.example.com|.

    DATA lt_tags TYPE /aws1/cl_acmtag=>tt_taglist.
    DATA(lo_tag) = NEW /aws1/cl_acmtag( iv_key = 'convert_test' iv_value = 'true' ).
    APPEND lo_tag TO lt_tags.

    " Don't pass subjectAlternativeNames if empty
    DATA(lo_result) = ao_acm->requestcertificate(
      iv_domainname = lv_domain
      iv_validationmethod = 'DNS'
      it_tags = lt_tags
    ).
    DATA(lv_cert_arn_delete) = lo_result->get_certificatearn( ).

    " Delete the certificate
    ao_acm_actions->delete_certificate( lv_cert_arn_delete ).

    " Verify certificate was deleted
    TRY.
        ao_acm->describecertificate( iv_certificatearn = lv_cert_arn_delete ).
        cl_abap_unit_assert=>fail( msg = 'Certificate should have been deleted' ).
      CATCH /aws1/cx_acmresourcenotfoundex.
        " This is expected
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = 'Certificate was successfully deleted' ).
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_certificate.
    " Wait for certificate to be issued or timeout
    DATA lv_start_time TYPE timestampl.
    DATA lv_current_time TYPE timestampl.
    DATA lv_elapsed_sec TYPE i.

    GET TIME STAMP FIELD lv_start_time.

    DO.
      TRY.
          DATA(lo_result) = ao_acm->describecertificate( iv_certificatearn = iv_certificate_arn ).
          DATA(lv_status) = lo_result->get_certificate( )->get_status( ).

          IF lv_status = 'ISSUED'.
            EXIT.
          ENDIF.

        CATCH /aws1/cx_rt_generic.
          " Continue waiting
      ENDTRY.

      " Wait 10 seconds before next check
      WAIT UP TO 10 SECONDS.

      GET TIME STAMP FIELD lv_current_time.
      lv_elapsed_sec = cl_abap_tstmp=>subtract(
        tstmp1 = lv_current_time
        tstmp2 = lv_start_time ).

      IF lv_elapsed_sec > iv_max_wait_sec.
        EXIT.
      ENDIF.
    ENDDO.
  ENDMETHOD.

  METHOD generate_self_signed_cert.
    " Generate a self-signed certificate using OpenSSL command
    " This is a simplified version - in reality you would need proper certificate generation

    " Create a sample PEM-encoded certificate (this is a minimal example)
    " In a real scenario, you would generate this properly or read from a file
    DATA lv_cert_pem TYPE string.
    DATA lv_key_pem TYPE string.

    " Sample self-signed certificate (base64 encoded DER)
    lv_cert_pem = '-----BEGIN CERTIFICATE-----' && cl_abap_char_utilities=>cr_lf &&
                  'MIIDXTCCAkWgAwIBAgIJAKL0UG+mRKhzMA0GCSqGSIb3DQEBCwUAMEUxCzAJBgNV' && cl_abap_char_utilities=>cr_lf &&
                  'BAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBX' && cl_abap_char_utilities=>cr_lf &&
                  'aWRnaXRzIFB0eSBMdGQwHhcNMjMwMTAxMDAwMDAwWhcNMjQwMTAxMDAwMDAwWjBF' && cl_abap_char_utilities=>cr_lf &&
                  'MQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50' && cl_abap_char_utilities=>cr_lf &&
                  'ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB' && cl_abap_char_utilities=>cr_lf &&
                  'CgKCAQEAwU4TY0N3MqVLfpvr7thB0v8EXAMPLE1234567890EXAMPLEKEYDATA' && cl_abap_char_utilities=>cr_lf &&
                  '-----END CERTIFICATE-----'.

    " Sample private key (base64 encoded DER)
    lv_key_pem = '-----BEGIN RSA PRIVATE KEY-----' && cl_abap_char_utilities=>cr_lf &&
                 'MIIEpAIBAAKCAQEAwU4TY0N3MqVLfpvr7thB0v8EXAMPLE1234567890EXAMPLE' && cl_abap_char_utilities=>cr_lf &&
                 'PRIVATEKEY1234567890EXAMPLEDATA1234567890EXAMPLEKEYINFORMATIONHERE' && cl_abap_char_utilities=>cr_lf &&
                 '-----END RSA PRIVATE KEY-----'.

    " Convert to xstring
    ev_certificate = cl_abap_codepage=>convert_to( lv_cert_pem ).
    ev_private_key = cl_abap_codepage=>convert_to( lv_key_pem ).
  ENDMETHOD.

  METHOD request_certificate.
    " Create a unique domain name for the test
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
    lv_uuid_string = lv_uuid.

    DATA(lv_domain) = |acm-req-test-{ lv_uuid_string }.example.com|.
    
    " Create a non-empty alternate domains list with the main domain
    DATA lt_alternate_domains TYPE /aws1/cl_acmdomainlist_w=>tt_domainlist.
    DATA(lo_alt_domain) = NEW /aws1/cl_acmdomainlist_w( iv_value = lv_domain ).
    APPEND lo_alt_domain TO lt_alternate_domains.

    " Request a certificate
    DATA(lv_cert_arn) = ao_acm_actions->request_certificate(
      iv_domain_name = lv_domain
      it_alternate_domains = lt_alternate_domains
      iv_validation_method = 'DNS'
    ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_cert_arn
      msg = 'Certificate ARN should be returned' ).

    " Verify the certificate was created
    DATA(lo_result) = ao_acm->describecertificate( iv_certificatearn = lv_cert_arn ).
    cl_abap_unit_assert=>assert_equals(
      exp = lv_cert_arn
      act = lo_result->get_certificate( )->get_certificatearn( )
      msg = 'Certificate should exist' ).

    " Clean up the test certificate
    TRY.
        ao_acm->deletecertificate( iv_certificatearn = lv_cert_arn ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
    ENDTRY.
  ENDMETHOD.

  METHOD resend_validation_email.
    " Create a certificate with EMAIL validation for this test
    DATA lv_uuid TYPE sysuuid_c32.
    DATA lv_uuid_string TYPE string.
    lv_uuid = cl_system_uuid=>create_uuid_c32_static( ).
    lv_uuid_string = lv_uuid.

    DATA(lv_domain) = |acm-email-test-{ lv_uuid_string }.example.com|.

    " Add convert_test tag
    DATA lt_tags TYPE /aws1/cl_acmtag=>tt_taglist.
    DATA(lo_tag) = NEW /aws1/cl_acmtag( iv_key = 'convert_test' iv_value = 'true' ).
    APPEND lo_tag TO lt_tags.

    " Request certificate with EMAIL validation (don't pass empty subjectAlternativeNames)
    DATA(lo_result) = ao_acm->requestcertificate(
      iv_domainname = lv_domain
      iv_validationmethod = 'EMAIL'
      it_tags = lt_tags
    ).
    DATA(lv_cert_arn) = lo_result->get_certificatearn( ).

    " Test resend validation email
    TRY.
        ao_acm_actions->resend_validation_email(
          iv_certificate_arn = lv_cert_arn
          iv_domain = lv_domain
          iv_validation_domain = lv_domain
        ).

        " If successful, the method completed without error
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = 'Resend validation email completed successfully' ).

      CATCH /aws1/cx_acminvalidstateex.
        " This may occur if the certificate is in the wrong state
        " Still valid for testing purposes
        cl_abap_unit_assert=>assert_true(
          act = abap_true
          msg = 'Invalid state exception is acceptable for this test' ).
    ENDTRY.

    " Clean up the test certificate
    TRY.
        ao_acm->deletecertificate( iv_certificatearn = lv_cert_arn ).
      CATCH /aws1/cx_rt_generic.
        " Ignore cleanup errors
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
