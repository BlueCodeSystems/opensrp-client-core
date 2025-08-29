 Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


[Unreleased]
----------------------

## [6.2.0] - 2024-08-29
----------------------
#### Added
- Added complete Jackson dependency set (jackson-core, jackson-annotations, jackson-databind) for better JSON processing
- Added support for BlueCode Systems group ID (io.github.bluecodesystems)
- Added proper Maven Central publishing configuration with signing support
- Added CircleProgressbar dependency under BlueCode Systems group
- Added comprehensive dependency management for local AARs and JARs

#### Changed  
- **BREAKING**: Updated Gradle wrapper from 6.7.1 to 8.7
- **BREAKING**: Updated Android Gradle Plugin from 4.2.2 to 8.4.0
- **BREAKING**: Updated Android build tools from 30.0.2 to 34.0.0
- **BREAKING**: Updated compile/target SDK from 31 to 34
- **BREAKING**: Replaced deprecated jcenter() repository with mavenCentral()
- **BREAKING**: Changed project group from 'org.smartregister' to 'io.github.bluecodesystems'
- Modernized Maven publishing system from legacy 'maven' plugin to 'maven-publish'
- Updated opensrp-client-utils to BlueCode Systems version (io.github.bluecodesystems:opensrp-client-utils:0.0.6)
- Improved build configuration for Android Gradle Plugin 8.x compatibility
- Updated Android namespace configuration
- Modernized lint and packaging options syntax
- Enhanced test configuration with Java 17 module support

#### Fixed
- Fixed resource reference ambiguity in sample login activity (R.layout.activity_login → org.smartregister.R.layout.activity_login)
- Fixed deprecated API usage in build scripts (html.setDestination, reports configuration)
- Fixed buildDashboard and test report configuration for modern Gradle
- Fixed signing and publishing workflow for Maven Central compatibility

#### Dependencies
- Upgraded to Gradle 8.7 and Android Gradle Plugin 8.4.0
- Updated all build tools and SDK versions to latest stable versions
- Migrated from jcenter to mavenCentral for all repository references

##### Previous Unreleased Changes
- Refactored the entities around Server settings enities e.g. Charactersitic* Objects/Entities are now named ServerSetting* This is a breaking change
- Refactored the way server settings json file is stored in the settings repo, It now stores the whole server json to enable persistence of Metatdata - This is a breaking change
  Code that was retrieving settings from the setting repo should should refactor to get the settings by value.get(AllConstants.SETTINGS) after retrieving the value from the repo

## [1.6.14] - 03-18-2019
--------------------
#### Added
- Added the ability to create Event observations from the native radio button secondary values
- Added the ability to create Event observations from the Expansion values which have the same format as the secondary values above
- A test to verify the Event creation


## [unreleased]
___________________
### Added
- Compression helper classes
- Cryptography helper classes
- Added language switching support

### Changed
- Upgrade Build tools to version 28
