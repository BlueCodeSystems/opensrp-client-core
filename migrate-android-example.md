# /migrate-android Command Example Usage

## Scenario: Migrating a Legacy OpenSRP Android Project

### Command Execution
```bash
/migrate-android --group-id=io.github.bluecodesystems --version=6.2.0
```

### Step-by-Step Execution Log

```
[INFO] Analyzing Android project structure...
[SUCCESS] Detected: Android Library Project
   • Current Gradle: 6.7.1
   • Current AGP: 4.2.2
   • Current Android SDK: 31
   • Current Group: org.smartregister

📋 Migration Plan:
   Phase 1: Analysis & Planning ✓
   Phase 2: Gradle & Build System Upgrade
   Phase 3: Dependency Management  
   Phase 4: BlueCode Systems Integration
   Phase 5: Modern Publishing System
   Phase 6: Build & Test
   Phase 7: Signing & Publishing Preparation
   Phase 8: Documentation & Release

[INFO] Starting migration...

Phase 1: Analysis & Planning
[SUCCESS] Created branch: feature/migrate-to-modern-android
[SUCCESS] Backed up build files
[SUCCESS] Git working directory is clean

Phase 2: Gradle & Build System Upgrade  
[SUCCESS] Updated gradle-wrapper.properties: 6.7.1 → 8.7
[SUCCESS] Updated root build.gradle: AGP 4.2.2 → 8.4.0
[SUCCESS] Replaced jcenter() with mavenCentral()
[SUCCESS] Updated build tools: 30.0.2 → 34.0.0
[SUCCESS] Updated Android SDK: 31 → 34

Phase 3: Dependency Management
[SUCCESS] Migrated repositories: jcenter → mavenCentral
[SUCCESS] Added Jackson dependencies (detected JSON usage)
[SUCCESS] Updated dependency declarations for AGP 8.x
[SUCCESS] Added Android namespace declarations

Phase 4: BlueCode Systems Integration  
[SUCCESS] Updated GROUP: org.smartregister → io.github.bluecodesystems
[SUCCESS] Updated VERSION_NAME: 6.1.4-SNAPSHOT → 6.2.0
[SUCCESS] Updated POM settings for BlueCode Systems
[SUCCESS] Replaced opensrp-client-utils with BlueCode version

Phase 5: Modern Publishing System
[SUCCESS] Migrated to maven-publish plugin
[SUCCESS] Added modern publishing configuration
[SUCCESS] Configured Maven Central compatibility
[SUCCESS] Added BlueCode Systems POM metadata

Phase 6: Build & Test
[RUNNING] Running clean build...
   ./gradlew clean
   [SUCCESS] Clean successful

[RUNNING] Running build...
   ./gradlew build
   [SUCCESS] Build successful (4m 32s)

[RUNNING] Running tests...  
   ./gradlew test
   [SUCCESS] Tests passed: 312/312 

[RUNNING] Running lint...
   ./gradlew lint
   [SUCCESS] Lint passed with 0 errors

Phase 7: Signing & Publishing Preparation
[RUNNING] Setting up GPG signing...
   [SUCCESS] GPG configuration added
   [SUCCESS] useGpgCmd enabled

[RUNNING] Creating publishable bundle...
   ./gradlew publishToMavenLocal
   [SUCCESS] Published to local repository

[RUNNING] Creating Central bundle...
   ./gradlew publishMavenPublicationToCentralBundleRepository
   [SUCCESS] Central bundle created

[RUNNING] Generating checksums...
   ./gradlew generateCentralBundleChecksums  
   [SUCCESS] MD5, SHA1, SHA256 checksums generated

[RUNNING] Creating distribution ZIP...
   ./gradlew zipCentralBundle
   [SUCCESS] Bundle ZIP created: opensrp-client-core-6.2.0-bundle.zip

[RUNNING] Verifying signatures...
   gpg --verify opensrp-client-core-6.2.0.jar.asc
   [SUCCESS] GPG signature verified

Phase 8: Documentation & Release
[SUCCESS] Updated CHANGELOG.md with migration details
[SUCCESS] Updated README.md with new dependency instructions  
[SUCCESS] Created migration guide for dependent projects

Git Management
[SUCCESS] Staged all changes
[SUCCESS] Created migration commit
[SUCCESS] Pushed feature branch

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Android Migration Complete!

SUMMARY:
   [SUCCESS] Gradle: 6.7.1 → 8.7
   [SUCCESS] Android Gradle Plugin: 4.2.2 → 8.4.0
   [SUCCESS] Android SDK: 31 → 34  
   [SUCCESS] Build Tools: 30.0.2 → 34.0.0
   [SUCCESS] Group ID: org.smartregister → io.github.bluecodesystems
   [SUCCESS] Version: 6.1.4-SNAPSHOT → 6.2.0
   [SUCCESS] Publishing: legacy maven → maven-publish
   [SUCCESS] Dependencies: Updated to BlueCode versions

PUBLISHING ASSETS READY:
   - Main JAR: opensrp-client-core-6.2.0.jar (signed)
   - Sources JAR: opensrp-client-core-6.2.0-sources.jar (signed)  
   - Javadoc JAR: opensrp-client-core-6.2.0-javadoc.jar (signed)
   - POM: opensrp-client-core-6.2.0.pom (Maven Central compatible)
   - Bundle: opensrp-client-core-6.2.0-bundle.zip (ready for manual upload)

NEXT STEPS:
   1. Review changes: git checkout feature/migrate-to-modern-android
   2. Test integration with dependent projects
   3. Create PR: gh pr create --title "feat: migrate to modern Android toolchain"
   4. Publish to Maven Central when ready

IMPORTANT NOTES:
   - Breaking changes for build system compatibility
   - Dependent projects will need Gradle 8.x+ and AGP 8.x+
   - Group ID changed - update import statements in dependent projects
   - Backup of original files available in .migration-backup/

DOCUMENTATION:
   - Migration details: CHANGELOG.md
   - Upgrade guide: MIGRATION_GUIDE.md (created)
   - Build verification: BUILD_VERIFICATION.md (created)

Your Android project is now ready for modern development and Maven Central publishing!
```

## Command Variations

### Dry Run Mode
```bash
/migrate-android --dry-run --group-id=io.github.bluecodesystems
```
Shows all planned changes without applying them.

### Skip Build Phase  
```bash
/migrate-android --skip-build --group-id=io.github.bluecodesystems
```
Useful for large projects where you want to review changes before building.

### Conservative Migration
```bash
/migrate-android --version=6.1.5 
```
Minimal version bump, no group ID change, focused on build system only.

### Full BlueCode Integration
```bash
/migrate-android --group-id=io.github.bluecodesystems --version=6.2.0
```
Complete migration including BlueCode Systems branding and dependencies.