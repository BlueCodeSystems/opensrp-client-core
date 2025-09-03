# /migrate-android Slash Command Specification

## Overview
A comprehensive slash command for migrating legacy Android projects to modern versions (Android SDK 34, Gradle 8.7, AGP 8.4.0) with optional BlueCode Systems integration and publishing readiness.

## Command Signature
```
/migrate-android [--group-id=<group-id>] [--version=<version>] [--dry-run] [--skip-build] [--skip-sign]
```

## Parameters
- `--group-id`: Optional. Changes project group ID (default: prompts user, suggests `io.github.bluecodesystems`)
- `--version`: Optional. Sets new version (default: auto-increment patch version)
- `--dry-run`: Optional. Shows what changes would be made without applying them
- `--skip-build`: Optional. Skip build and test phases
- `--skip-sign`: Optional. Skip GPG signing steps

## Migration Steps

### Phase 1: Analysis & Planning
1. **Project Detection**
   - Detect Android project type (library, application, multi-module)
   - Analyze current Gradle, AGP, and Android SDK versions
   - Check existing dependencies and repositories
   - Identify potential breaking changes

2. **Backup & Preparation**
   - Create git branch: `feature/migrate-to-modern-android`
   - Backup critical files (build.gradle, gradle.properties, etc.)
   - Check git status and ensure clean working directory

### Phase 2: Gradle & Build System Upgrade
3. **Gradle Wrapper Update**
   ```gradle
   # Update gradle/wrapper/gradle-wrapper.properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
   ```

4. **Root build.gradle Updates**
   ```gradle
   buildscript {
       dependencies {
           classpath 'com.android.tools.build:gradle:8.4.0'
       }
   }
   
   # Replace jcenter() with mavenCentral()
   repositories {
       google()
       mavenCentral()  // Replace jcenter()
   }
   ```

5. **Module build.gradle Modernization**
   - Update Android SDK versions:
     ```gradle
     android {
         compileSdk 34
         buildToolsVersion "34.0.0"
         
         defaultConfig {
             minSdk 18  // Maintain backward compatibility
             targetSdk 34
         }
     }
     ```
   - Fix deprecated API usage:
     ```gradle
     lint {
         abortOnError false
         checkReleaseBuilds false
     }
     
     packaging {
         resources {
             excludes += ['META-INF/DEPENDENCIES', 'LICENSE.txt']
         }
     }
     ```

### Phase 3: Dependency Management
6. **Repository Migration**
   - Replace all `jcenter()` with `mavenCentral()`
   - Update dependency declarations for AGP 8.x compatibility
   - Add namespace declarations for Android libraries

7. **Jackson Dependencies** (if JSON processing detected)
   ```gradle
   implementation 'com.fasterxml.jackson.core:jackson-core:2.13.3'
   implementation 'com.fasterxml.jackson.core:jackson-annotations:2.13.3'
   implementation 'com.fasterxml.jackson.core:jackson-databind:2.13.3'
   ```

### Phase 4: BlueCode Systems Integration (Optional)
8. **Group ID Migration**
   - Update `gradle.properties`:
     ```properties
     GROUP=io.github.bluecodesystems
     VERSION_NAME=X.Y.Z
     ```
   - Update POM settings for BlueCode Systems
   - Replace OpenSRP dependencies with BlueCode equivalents where available

9. **Dependency Updates**
   ```gradle
   implementation 'io.github.bluecodesystems:opensrp-client-utils:0.0.6'
   implementation 'io.github.bluecodesystems:circleprogressbar:1.0.0'
   ```

### Phase 5: Modern Publishing System
10. **Maven Publishing Modernization**
    ```gradle
    apply plugin: 'maven-publish'
    apply plugin: 'signing'
    
    publishing {
        publications {
            maven(MavenPublication) {
                afterEvaluate {
                    from components.release
                }
                
                artifact sourcesJar
                artifact javadocJar
                
                pom {
                    name = project.name
                    description = project.description
                    url = 'https://github.com/BlueCodeSystems/project-name'
                    
                    licenses {
                        license {
                            name = 'Apache-2.0'
                            url = 'https://www.apache.org/licenses/LICENSE-2.0.txt'
                        }
                    }
                    
                    developers {
                        developer {
                            id = 'bluecodesystems'
                            name = 'BlueCode Systems'
                            email = 'engineering@bluecodeltd.org'
                        }
                    }
                    
                    scm {
                        connection = 'scm:git:git://github.com/BlueCodeSystems/project-name.git'
                        developerConnection = 'scm:git:ssh://github.com:BlueCodeSystems/project-name.git'
                        url = 'https://github.com/BlueCodeSystems/project-name'
                    }
                }
            }
        }
        
        repositories {
            maven {
                name = "OSSRH"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = findProperty("ossrhUsername")
                    password = findProperty("ossrhPassword")
                }
            }
        }
    }
    ```

### Phase 6: Build & Test
11. **Clean Build**
    ```bash
    ./gradlew clean
    ./gradlew build
    ```

12. **Test Execution**
    ```bash
    ./gradlew test
    ./gradlew connectedAndroidTest  # If applicable
    ```

13. **Lint & Code Quality**
    ```bash
    ./gradlew lint
    ./gradlew checkstyle  # If configured
    ```

### Phase 7: Signing & Publishing Preparation
14. **GPG Signing Setup**
    ```gradle
    signing {
        required { 
            !gradle.taskGraph.hasTask("publishToMavenLocal") && 
            !project.findProperty('skipSigning')?.toString()?.toBoolean()
        }
        
        def useGpgCmd = project.findProperty('useGpgCmd')?.toString()?.toBoolean()
        if (useGpgCmd) {
            useGpgCmd()
        }
        
        sign publishing.publications.maven
    }
    ```

15. **Bundle Creation & Verification**
    ```bash
    # Generate signed bundle
    ./gradlew publishToMavenLocal
    
    # Create Central-compatible bundle
    ./gradlew publishMavenPublicationToCentralBundleRepository
    
    # Generate checksums
    ./gradlew generateCentralBundleChecksums
    
    # Create distribution ZIP
    ./gradlew zipCentralBundle
    ```

16. **Verification Steps**
    ```bash
    # Verify JAR contents
    jar tf build/libs/project-name-X.Y.Z.jar | head -20
    
    # Verify POM structure
    xmllint --format build/central-bundle/pom.xml
    
    # Check signatures
    gpg --verify build/central-bundle/project-name-X.Y.Z.jar.asc
    ```

### Phase 8: Documentation & Release
17. **Update Documentation**
    - Update README.md with new dependency declarations
    - Update CHANGELOG.md with migration notes
    - Create migration guide for dependent projects

18. **Git Management**
    ```bash
    git add .
    git commit -m "feat: migrate to Android SDK 34 and modern build system"
    git push origin feature/migrate-to-modern-android
    ```

## Command Implementation

### Entry Point
```typescript
async function migrateAndroidCommand(args: MigrateAndroidArgs): Promise<void> {
    // 1. Validate Android project
    await validateAndroidProject();
    
    // 2. Create migration plan
    const plan = await createMigrationPlan(args);
    
    // 3. Execute migration phases
    for (const phase of plan.phases) {
        await executePhase(phase, args);
    }
    
    // 4. Verify migration success
    await verifyMigration();
    
    // 5. Generate summary report
    await generateMigrationReport();
}
```

### Validation Checks
```typescript
async function validateAndroidProject(): Promise<void> {
    // Check for Android project structure
    const hasAndroidManifest = await fileExists('*/AndroidManifest.xml');
    const hasBuildGradle = await fileExists('build.gradle') || await fileExists('build.gradle.kts');
    const hasGradleWrapper = await fileExists('gradlew');
    
    if (!hasAndroidManifest || !hasBuildGradle) {
        throw new Error('Not a valid Android project');
    }
    
    // Check current versions
    const currentVersions = await detectCurrentVersions();
    console.log('Current versions detected:', currentVersions);
}
```

### Safe Execution
- All file modifications use atomic operations
- Backup critical files before changes
- Rollback capability if migration fails
- Dry-run mode shows all planned changes
- Git integration for version control

## Error Handling

### Common Issues & Solutions
1. **Gradle Daemon Issues**
   ```bash
   ./gradlew --stop
   ./gradlew clean
   ```

2. **Dependency Conflicts**
   - Use `./gradlew dependencies` to analyze
   - Implement conflict resolution strategies
   - Suggest dependency updates

3. **Build Failures**
   - Detailed error analysis and suggestions
   - Common fixes for AGP 8.x migration issues
   - Namespace declaration requirements

## Output Format

```
🚀 Android Migration Complete!

✅ Phases Completed:
  • Gradle 6.7.1 → 8.7
  • Android Gradle Plugin 4.2.2 → 8.4.0  
  • Android SDK 31 → 34
  • Build tools 30.0.2 → 34.0.0
  • Maven publishing modernized
  • BlueCode Systems integration ✓
  • GPG signing configured ✓

📦 Publishing Ready:
  • Bundle: build/distributions/project-name-X.Y.Z-bundle.zip
  • Checksums: ✓ MD5, SHA1, SHA256
  • Signatures: ✓ GPG verified
  • POM: ✓ Maven Central compatible

🔄 Next Steps:
  1. Review changes in feature/migrate-to-modern-android branch
  2. Test build: ./gradlew build
  3. Publish locally: ./gradlew publishToMavenLocal  
  4. Create pull request for review

⚠️ Breaking Changes:
  • Dependent projects need Gradle 8.x+ compatibility
  • Group ID changed to io.github.bluecodesystems (if selected)
```

## Integration with Claude Code

This command should be available as a slash command in Claude Code CLI and integrate with existing tools:
- File manipulation via Edit/Write tools
- Git operations via Bash tool  
- Project analysis via Grep/Glob tools
- Progress tracking via TodoWrite tool

The command provides a complete solution for modernizing Android projects while maintaining compatibility and preparing for professional distribution.