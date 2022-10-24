use crate::{
    client::{Email, RdsClient, SesClient},
    work_item::{repository::list, WorkItem, WorkItemArchived, WorkItemError},
};
use actix_web::{
    http::StatusCode,
    put,
    web::{Data, Json},
    HttpResponse, ResponseError,
};
use aws_sdk_ses::{model::RawMessage, types::Blob};
use mail_builder::MessageBuilder;
use serde::Deserialize;
use serde_json::json;
use thiserror::Error;
use uuid::Uuid;
use xlsxwriter::Workbook;

#[derive(Debug, Error)]
pub enum ReportError {
    #[error("WorkItemError: {0}")]
    WorkItemError(WorkItemError),

    #[error("SES Error: {0}")]
    SesError(aws_sdk_ses::Error),

    #[error("Failed to create email message: {0}")]
    MailError(std::io::Error),

    #[error("Failed to write report xsls: {0}")]
    XslxError(xlsxwriter::XlsxError),

    #[error("Other Report Error: {0}")]
    Other(String),
}

impl ResponseError for ReportError {
    fn status_code(&self) -> StatusCode {
        match self {
            ReportError::WorkItemError(err) => err.status_code(),
            _ => StatusCode::INTERNAL_SERVER_ERROR,
        }
    }

    fn error_response(&self) -> actix_web::HttpResponse<actix_web::body::BoxBody> {
        HttpResponse::build(self.status_code()).json(json!({ "error": format!("{}", self) }))
    }
}

#[derive(Debug, Deserialize)]
pub struct ReportEmail {
    pub email: Email,
}

const TEXT_BODY: &str = &"Hello,\r\n\r\nPlease see the attached file for a weekly update.";
const HTML_BODY: &str = &"<!DOCTYPE html><html lang=\"en-US\"><body><h1>Hello!</h1><p>Please see the attached file for a weekly update.</p></body></html>";
const ATTACHMENT_NAME: &str = &"WorkReport.xlsx";
const ATTACHMENT_CONTENT_TYPE: &str =
    &"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

#[put("/items:report")]
pub async fn send_report(
    to: Json<ReportEmail>,
    rds: Data<RdsClient>,
    ses: Data<SesClient>,
) -> Result<HttpResponse, ReportError> {
    let report_items = list(WorkItemArchived::Active, &rds)
        .await
        .map_err(ReportError::WorkItemError)?;

    let attachment = make_report(report_items)?;

    let message_builder = MessageBuilder::new()
        .from(ses.from().clone())
        .to(to.email.clone())
        .subject("WorkItem Report")
        .text_body(TEXT_BODY)
        .html_body(HTML_BODY)
        .binary_attachment(ATTACHMENT_CONTENT_TYPE, ATTACHMENT_NAME, attachment);

    let message = message_builder
        .write_to_vec()
        .map_err(ReportError::MailError)?;

    let data = Blob::new(message);

    let email = ses
        .send_raw_email()
        .raw_message(RawMessage::builder().data(data).build())
        .send()
        .await
        .map_err(|err| ReportError::SesError(err.into()))?;

    Ok(HttpResponse::build(StatusCode::OK)
        .json(json!({ "success": {"email": format!("{:?}", email.message_id())}})))
}

const FONT_SIZE: f64 = 12.0;
fn make_report<'a>(items: Vec<WorkItem>) -> Result<Vec<u8>, ReportError> {
    let path = format!("{ATTACHMENT_NAME}.{}.xlsx", Uuid::new_v4().to_string());
    let workbook = Workbook::new(path.as_str());

    let body_format = &workbook.add_format().set_font_size(FONT_SIZE);
    let date_format = &workbook
        .add_format()
        .set_num_format("dd/mm/yyyy")
        .set_font_size(FONT_SIZE);
    let header_format = &workbook
        .add_format()
        .set_bold()
        .set_font_size(FONT_SIZE * 1.125);

    let mut report_sheet = workbook
        .add_worksheet(Some(&"Workitems"))
        .map_err(ReportError::XslxError)?;

    macro_rules! header {
        ($col:ident, $val:expr) => {
            report_sheet
                .write_string(0, $col, $val, Some(header_format))
                .map_err(ReportError::XslxError)?
        };
    }

    for (i, h) in vec!["Writer", "Date", "Guide", "Description", "Status"]
        .iter()
        .enumerate()
    {
        let i: u16 = i.try_into().unwrap();
        header!(i, h);
    }

    macro_rules! body {
        ($row:ident, $col:literal, $val:expr, $format:expr) => {
            report_sheet
                .write_string($row + 1, $col, format!("{}", $val).as_str(), $format)
                .map_err(ReportError::XslxError)?
        };
        ($row:ident, $col:expr, $val:expr) => {
            report_sheet
                .write_string(
                    $row + 1,
                    $col,
                    format!("{}", $val).as_str(),
                    Some(body_format),
                )
                .map_err(ReportError::XslxError)?
        };
    }

    for (row, item) in items.iter().enumerate() {
        let row: u32 = row.try_into().unwrap();
        body!(row, 0, item.name());
        body!(row, 1, item.date(), Some(date_format));
        body!(row, 2, item.guide());
        body!(row, 3, item.description());
        body!(row, 4, item.status());
    }

    workbook.close().map_err(ReportError::XslxError)?;

    let result = std::fs::read(&path).map_err(|err| {
        ReportError::Other(format!("Failed to read back report from {path}: {err}"))
    });
    let _ = std::fs::remove_file(&path);
    result
}
