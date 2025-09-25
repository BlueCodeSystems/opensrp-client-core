# opensrp-client-core
OpenSRP client core is an Android library that provides shared infrastructure for OpenSRP-based applications, including data sync, repositories, security layers, and reusable UI components.

## Project Status
- Toolchain: Gradle 8.7 (wrapper), Android Gradle Plugin 8.6.0, Kotlin 1.9.24, requires JDK 17.
- CI: GitHub Actions workflows (`.github/workflows/ci.yml`, `release.yml`).
- Default branch: `master`; latest tag: `v6.2.3` (git).

## Features
- Domain models, repositories, and services for OpenSRP clients, events, and reporting.
- Secure sync helpers for server communication, encryption, and offline-first workflows.
- Peer-to-peer transfer options for device-to-device data exchange with authorization hooks.
- Utility layers: shared preferences helpers, caching, logging, compression, and multilingual view scaffolding.

## Requirements
- JDK 17+
- Gradle 8.7 (via `./gradlew`)
- Android Gradle Plugin 8.6.0
- Kotlin 1.9.24
- Android `minSdk` 28, `compileSdk`/`targetSdk` 35

## Install
Groovy DSL:
```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation 'io.github.bluecodesystems:opensrp-client-core:<version>' // see Releases for the latest version
}
```

Kotlin DSL:
```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("io.github.bluecodesystems:opensrp-client-core:<version>") // see Releases for the latest version
}
```

Replace `<version>` with the current release published on the repository's Releases page.

## Initialize
Register the library from your `Application` class and supply your `SyncConfiguration` implementation. P2P options are optional.

```java
public final class CoreApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    SyncConfiguration syncConfig = new SampleSyncConfiguration();
    CoreLibrary.init(this, syncConfig);
  }
}
```

```java
public final class SampleSyncConfiguration extends SyncConfiguration {
  @Override
  public int getSyncMaxRetries() { return 3; }

  @Override
  public SyncFilter getSyncFilterParam() { return SyncFilter.PROVIDER; }

  @Override
  public String getSyncFilterValue() { return "demo-provider"; }

  @Override
  public int getUniqueIdSource() { return 1; }

  @Override
  public int getUniqueIdBatchSize() { return 250; }

  @Override
  public int getUniqueIdInitialBatchSize() { return 500; }

  @Override
  public SyncFilter getEncryptionParam() { return SyncFilter.TEAM_ID; }

  @Override
  public boolean updateClientDetailsTable() { return true; }
}
```

## Usage examples
```java
// Access shared services
org.smartregister.Context opensrpContext = CoreLibrary.getInstance().context();
AllSharedPreferences prefs = opensrpContext.allSharedPreferences();
String teamId = prefs.fetchDefaultTeamId(prefs.fetchRegisteredANM());
```

```java
// Persist synced clients/events
JSONArray events = /* build payload */;
JSONArray clients = /* build payload */;
ECSyncHelper syncHelper = ECSyncHelper.getInstance(this);
syncHelper.batchSave(events, clients);
syncHelper.updateLastSyncTimeStamp(System.currentTimeMillis());
```

```java
// Enable peer-to-peer sync with custom authorization
P2POptions options = new P2POptions(true);
options.setBatchSize(50);
options.setAuthorizationService(new MyAuthorizationService());
CoreLibrary.init(this, new SampleSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP, options);
```

Additional APIs live under `org.smartregister.*`; see class-level documentation for services, view fragments, and utility helpers.

## Sample app
A reference implementation lives in `sample/`.
- Install on a device/emulator: `./gradlew :sample:installDebug`
- You can also import the project into Android Studio and run the `sample` configuration directly.

## Build & test
- Build artifacts: `./gradlew clean assemble`
- JVM tests: `./gradlew test`

## Releases
Check the [Releases](https://github.com/BlueCodeSystems/opensrp-client-core/releases) page for published versions, changelogs, and upgrade notes.

## Contributing
Issues and pull requests are welcome. Please:
- Use the toolchain versions listed above when building locally.
- Run `./gradlew clean assemble test` before opening a PR.
- Consult the [OpenSRP developer wiki](https://smartregister.atlassian.net/wiki/dashboard.action) for architecture and setup guides.

## License
Licensed under the [Apache License, Version 2.0](LICENSE).
