//! WorkItemArchived represents several ways to specify the archival status of a WorkItem.

use serde::{de, Deserialize, Serialize};
use std::convert::TryFrom;

use crate::work_item::WorkItemError;

/// An enum to represent the archival status of a WorkItem.
/// Because this field has varying representations at different parts of the stack, it needs specialized serde visitors.
/// The Elwing front end uses "active" and "archive".
#[derive(Clone, Copy, Debug, Default, PartialEq, Eq)]
pub enum WorkItemArchived {
    Active,
    Archived,
    #[default]
    All,
}

const ACTIVE: &str = "active";
const ARCHIVED: &str = "archived";

impl From<&WorkItemArchived> for u8 {
    fn from(value: &WorkItemArchived) -> Self {
        match value {
            WorkItemArchived::Active => 0,
            WorkItemArchived::Archived => 1,
            WorkItemArchived::All => 255,
        }
    }
}

impl From<&WorkItemArchived> for &str {
    fn from(value: &WorkItemArchived) -> Self {
        match value {
            WorkItemArchived::Active => ACTIVE,
            WorkItemArchived::Archived => ARCHIVED,
            WorkItemArchived::All => ACTIVE,
        }
    }
}

impl From<bool> for WorkItemArchived {
    fn from(value: bool) -> Self {
        match value {
            false => WorkItemArchived::Active,
            true => WorkItemArchived::Archived,
        }
    }
}

impl TryFrom<u8> for WorkItemArchived {
    type Error = WorkItemError;

    fn try_from(value: u8) -> Result<Self, Self::Error> {
        match value {
            0 => Ok(WorkItemArchived::Active),
            1 => Ok(WorkItemArchived::Archived),
            _ => Err(WorkItemError::Archival(format!(
                "Unrecognized archive number {value}"
            ))),
        }
    }
}

impl TryFrom<&str> for WorkItemArchived {
    type Error = WorkItemError;
    fn try_from(value: &str) -> Result<Self, Self::Error> {
        match value {
            "" => Ok(WorkItemArchived::All),
            ACTIVE | "0" => Ok(WorkItemArchived::Active),
            ARCHIVED | "1" => Ok(WorkItemArchived::Archived),
            _ => Err(WorkItemError::Archival(format!(
                "Unrecognized archive string {value}"
            ))),
        }
    }
}

impl TryFrom<Option<String>> for WorkItemArchived {
    type Error = WorkItemError;

    fn try_from(value: Option<String>) -> Result<Self, Self::Error> {
        value.unwrap_or_default().as_str().try_into()
    }
}

impl Serialize for WorkItemArchived {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::Serializer,
    {
        serializer.serialize_str(self.into())
    }
}

impl<'de> Deserialize<'de> for WorkItemArchived {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        /// The front end specifies archived status with the strings "archived" and "active".
        /// The RDS table encodes status as 1 and 0.
        /// This serde visit handles those disparate cases, using the try_from impls.
        struct Visitor;
        impl<'de> de::Visitor<'de> for Visitor {
            type Value = WorkItemArchived;

            fn visit_u64<E>(self, value: u64) -> Result<Self::Value, E>
            where
                E: de::Error,
            {
                (value as u8).try_into().map_err(E::custom)
            }

            fn visit_str<E>(self, value: &str) -> Result<Self::Value, E>
            where
                E: de::Error,
            {
                value.try_into().map_err(E::custom)
            }

            fn expecting(&self, formatter: &mut std::fmt::Formatter) -> std::fmt::Result {
                formatter.write_str("int 0 or 1, or string 'active' or 'archived'")
            }
        }

        deserializer.deserialize_any(Visitor)
    }
}
