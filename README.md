# paymenthub-ee-core

The shared foundation for Payment Hub EE: the platform Bill of Materials (BOM) and the common library that every PH-EE Java component builds on.

[![License](https://img.shields.io/badge/License-MPL--2.0-blue.svg)](LICENSE)

## What it does

This repo publishes two artifacts that the rest of Payment Hub EE depends on. It is not a runnable service. It has no `main` method and starts no server. It only produces libraries that other PH-EE components import.

- The **BOM** (`org.mifos:paymenthub-ee-bom`) pins one agreed version for each dependency: Spring Boot, Apache Camel, Zeebe, Jackson, AWS SDK, and more. Each component imports it with `enforcedPlatform`, so every component lines up on the same versions and does not pin them by hand.
- The **core library** (`org.mifos:paymenthub-ee-core`) is the shared code, the migrated `ph-ee-connector-common`. It holds shared DTOs, channel and Mojaloop model classes, Zeebe helpers, and small utilities that the connectors reuse.

## Modules

- `bom` — the Payment Hub EE Bill of Materials; pins the versions of Spring Boot, Camel, Zeebe, Jackson, and the other shared dependencies.
- `core` — the shared common library (the migrated `ph-ee-connector-common`): shared DTOs, channel and Mojaloop model classes, Zeebe helpers, and utilities.
- `bom:bom-verification` — a small module that imports the BOM and resolves every declared dependency, so the build fails here if the BOM does not resolve correctly.

## How it fits into Payment Hub EE

Every PH-EE Java connector depends on this repo twice: on the BOM for its dependency versions, and on core for the shared code it reuses. That makes `paymenthub-ee-core` the base of the PH-EE dependency graph. When its versions or shared classes change, all the connectors pick up the change from here.

## Tech stack

Java 21, Spring Boot 3.4, Apache Camel 4, Jakarta EE 10, built with a Gradle multi-module setup. It publishes `org.mifos:paymenthub-ee-bom` and `org.mifos:paymenthub-ee-core`.

## Branches

- `dev` is the active development branch — all PRs should target `dev`.
- `main` holds released versions.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) and our [Code of Conduct](CODE_OF_CONDUCT.md).
