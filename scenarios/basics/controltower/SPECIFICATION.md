# AWS Control Tower Basics Scenario - Technical specification

This document contains the technical specifications for _AWS Control Tower Basics Scenario_,
a basics scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example scenario.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Hello](#hello)
- [Scenario](#scenario)
- [Errors](#errors)
- [Metadata](#metadata)

## Resources and User Input

- This example can run with no additional resources, or can use an existing landing zone. Since landing zone creation
- requires multiple AWS accounts (which cannot be deleted for 7 days), this example does not support creating new
- landing zones. The example will prompt to use a current landing zone, or run only that portion that doesn't 
- require landing zone identifiers.

### Hello
The Hello example is a separate runnable example.

- Set up the service client.
- List available Baselines by name.

Example
```
Hello, AWS Control Tower! Let's list available baselines:

7 baseline(s) retrieved.
        AuditBaseline
        LogArchiveBaseline
        IdentityCenterBaseline
        BackupCentralVaultBaseline
        BackupAdminBaseline
        BackupBaseline

```
## Scenario

#### Setup
- List available landing zones, and prompt the user if they would like to use the first or other landing zone.
- If no landing zones, provide a link to set up a landing zone and only use the list operations that do not require a target id.

Example
```
TODO

```

#### Baselines
- List available baselines.
- If a landing zone exists:
  - List enabled baselines.
  - Prompt the user if they would like to enable another baseline from the list.
  - Get the operational status of the baseline operation.
  - Reset the baseline.
  - Disable the baseline.

Example
```
TODO

```

#### Controls
Some control operations require the use of the ControlCatalog client. This client does not have it's own documentation,
and so is included as part of this example.

- List Controls in Control Catalog.
- If a landing zone exists:
  - Enable a control.
  - Get the operational status of the control.
  - List enabled controls.
  - Disable the control.

Example
```
TODO

```


---

## Errors
The following errors are handled in the Control Tower wrapper class:

| action                 | Error                 | Handling                                                       |
|------------------------|-----------------------|----------------------------------------------------------------|
| `ListBaselines`        | AccessDeniedException | Notify the user of insufficient permissions and exit.          |
| `ListEnabledBaselines` | AccessDeniedException | Notify the user of insufficient permissions and exit.          |
| `EnableBaseline`       | ValidationException   | Handle case where baseline is already enabled and return None. |
| `DisableBaseline`      | ResourceNotFound      | Notify the user that the baseline was not found.               |
| `ListControls`         | AccessDeniedException | Notify the user of insufficient permissions and exit.          |
| `EnableControl`        | ValidationException   | Handle case where control is already enabled and return None.  |
| `GetControlOperation`  | ResourceNotFound      | Notify the user that the control operation was not found.      |
| `DisableControl`       | ResourceNotFound      | Notify the user that the control was not found.                | 
| `ListLandingZones`     | AccessDeniedException | Notify the user of insufficient permissions and exit.          |


---

## Metadata

| action / scenario               | metadata file              | metadata key                      |
|---------------------------------|----------------------------|-----------------------------------|
| `ListBaselines`                 | controltower_metadata.yaml | controltower_Hello                |
| `ListBaselines`                 | controltower_metadata.yaml | controltower_ListBaselines        |
| `ListEnabledBaselines`          | controltower_metadata.yaml | controltower_ListEnabledBaselines |
| `EnableBaseline`                | controltower_metadata.yaml | controltower_EnableBaseline       |
| `DisableBaseline`               | controltower_metadata.yaml | controltower_DisableBaseline      |
| `EnableControl`                 | controltower_metadata.yaml | controltower_EnableControl        |
| `GetControlOperation`           | controltower_metadata.yaml | controltower_GetControlOperation  |
| `DisableControl`                | controltower_metadata.yaml | controltower_DisableControl       |
| `ListLandingZones`              | controltower_metadata.yaml | controltower_ListLandingZones     |
| `Control Tower Basics Scenario` | controltower_metadata.yaml | controltower_Scenario             |

