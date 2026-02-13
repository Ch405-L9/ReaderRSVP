## BADGR RSVP Speed Reader

Developer Build & Run Guide (Ubuntu 24.04)
Fast path: build in 5 commands
bash
cd /path/to/RSVPReader
./setup.sh
source ~/.bashrc
android-studio .
## After the project loads, click the green Run ▶ button

The app builds and launches in the Android emulator when the Gradle sync and initial build complete.

Detailed build workflow
Prerequisites
Confirm the following before you start:
Ubuntu 24.04.3 LTS
At least 10 GB of free disk space
Reliable internet connectivity
User account with sudo privileges

### Step 1: Extract the project
bash
### If you downloaded a .tar.gz archive:
cd ~/Downloads    ### Or the directory where you saved it
tar -xzf BADGR-RSVP-Reader-v1.0.tar.gz

### If you downloaded the project folder directly:
cd /path/to/RSVPReader

Use a workspace path you are comfortable opening directly in Android Studio (for example, ~/dev/RSVPReader).​

### Step 2: Run the automated setup script
The setup script provisions Java 17, KVM acceleration, and Android tooling paths.
bash
cd RSVPReader
./setup.sh

This step typically performs the following:
Installs OpenJDK 17
Sets the JAVA_HOME environment variable
Installs and configures KVM for hardware-accelerated emulation (optimized for AMD Ryzen and similar)
Configures the Android SDK location
Writes a local.properties file pointing to the SDK
Expected duration: 5–10 minutes, depending on package download speed.​

### Step 3: Reload your shell environment
bash
source ~/.bashrc

This activates the environment variables configured during setup, such as JAVA_HOME, ANDROID_HOME, and any PATH updates.

### Step 4: Install Android Studio (if not already installed)
Option 1 – Snap (recommended for most Ubuntu setups):
bash
sudo snap install android-studio --classic

The --classic flag is required for Android Studio to access the SDK and related tools.​​
Option 2 – Manual download:
Open https://developer.android.com/studio in a browser.​
Download the Linux .tar.gz package.
Extract and install:
bash
cd ~/Downloads
tar -xzf android-studio-*.tar.gz
sudo mv android-studio /opt/
/opt/android-studio/bin/studio.sh


Initial installation usually completes within 10–15 minutes, depending on bandwidth.​​

### Step 5: First-time Android Studio configuration
On first launch:
On the welcome screen, choose the Standard installation type.
Accept all SDK and license prompts.
Proceed through the wizard and click Finish.
Android Studio then downloads:
Android SDK
Build tools
Platform tools
Emulator components
Allow 10–15 minutes for this initial provisioning.​​

### Step 6: Open the BADGR RSVP Reader project
From the Android Studio welcome screen, choose Open.
Navigate to the RSVPReader project directory.
Confirm the selection to open the project.
Android Studio will then:
Load the Gradle project
Download the Gradle wrapper (if necessary)
Sync Gradle dependencies
Index project files
Expect 3–5 minutes for the first project sync on a typical broadband connection.

### Step 7: Create an Android Virtual Device (AVD)
Click the Device Manager icon in the Android Studio toolbar.
Select Create Device.
Choose a recent phone profile such as Pixel 7.
Click Next.
Select a recent system image, for example Android 14 (API 34, UpsideDownCake), and download it if needed.
Click Next.
Name the device (for example, BADGR_Test_Device).
Click Finish.
Image download time varies; plan for roughly 5 minutes plus any additional time for system image downloads.​

### Step 8: Build and run the app
### Option 1 – From Android Studio (preferred for day-to-day development):
Select BADGR_Test_Device (or your chosen AVD or physical device) in the device dropdown.
Click the green Run ▶ button in the main toolbar.
Wait for Gradle to complete the build; the emulator should start and deploy the app automatically.
### Option 2 – From the command line:
bash
cd RSVPReader
./build.sh

First build: approximately 3–5 minutes due to dependency downloads.
Subsequent builds: typically under 1 minute on warmed caches.

### Step 9: Functional smoke test
After deployment to the emulator or device, verify that the main UI elements load and respond:
BADGR header and Speed Reader title
Subtitle line “by BADGR Technologies LLC”
Central word display region
Progress bar
WPM slider (default 300 WPM)
Play/Pause control
Jump backward/forward controls
Reset control
Recommended quick test:
Press Play and confirm that words advance one at a time.
Confirm the red “Optimal Recognition Point” letter for each word.
Adjust the WPM slider and verify that reading speed changes in real time.
Use jump controls to seek within the text.
Use Reset to return to the beginning and verify progress bar and counters update accordingly.​

Verification checklist
Use this checklist to validate a clean end-to-end setup:
 App launches without runtime crashes on emulator or device
 Branding and visual layout are displayed as expected
 Single-word display behavior is correct
 Red ORP highlight is visible and stable
 Play/Pause controls start and stop reading reliably
 WPM slider updates reading speed without lag
 Jump controls move within the text as expected
 Reset control returns to the start of the text
 Progress bar and word counters track position accurately

Troubleshooting reference
“SDK not found”
Set the SDK path explicitly in local.properties:
bash
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
### If Android Studio was installed via snap:
echo "sdk.dir=$HOME/snap/android-studio/common/Android/Sdk" > local.properties

This file should live at the project root alongside build.gradle.

“Gradle sync failed”
In Android Studio:
Use File → Invalidate Caches → Invalidate and Restart to clear indexes and trigger a clean sync.

Incorrect Java version
bash
### Check the current Java version:
java -version    ## Target: 17.x.x

### Install Java 17 if needed:
sudo apt install openjdk-17-jdk -y
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

Ensure the shell using Gradle and Android Studio sees Java 17 as the default JDK.

Slow emulator performance
bash
### Confirm KVM support is active:
kvm-ok

### If not configured:
sudo apt install qemu-kvm -y
sudo adduser $USER kvm
### Log out and log back in to apply group membership

Hardware acceleration significantly improves emulator responsiveness on supported CPUs.​​

“Unable to locate adb”
bash
### Add platform-tools to PATH:
export PATH=$PATH:$HOME/Android/Sdk/platform-tools
echo 'export PATH=$PATH:$HOME/Android/Sdk/platform-tools' >> ~/.bashrc
source ~/.bashrc

Confirm with adb version and adb devices once the emulator or device is attached.

Dependency-related build failures
bash
./gradlew clean
./gradlew build

A clean rebuild often resolves transient Gradle and cache issues.

Running on a physical Android device
Enable developer mode and USB debugging
On the device, open Settings → About phone.
Tap Build number seven times to enable Developer Options.
Navigate to Settings → System → Developer options.
Enable USB debugging.
Connect the phone to your Ubuntu machine via USB.​
Deploy to the device
bash
### Verify that the device is visible:
adb devices

### Expected:
 - List of devices attached
 - ABC123456789    device

### Install the debug build:
./gradlew installDebug

Alternatively, you can select the physical device from the Android Studio device dropdown and click Run.

Customization hooks
Brand colors
Edit app/src/main/java/com/badgr/rsvpreader/ui/theme/Theme.kt:
kotlin
// Example palette:
val BADGRBlue = Color(0xFF0000FF)      // Primary color
val BADGRRed = Color(0xFFFF0000)       // ORP highlight
val BADGRBlack = Color(0xFF000000)     // Background
val BADGRWhite = Color(0xFFFFFFFF)     // Text

Using a centralized palette in the theme file keeps brand updates consistent across the UI.​

Sample text
Edit app/src/main/res/values/strings.xml:
xml
<string name="sample_text">Your new text here...</string>

This makes it easy to adapt demo content for different audiences or demo scenarios without touching core logic.​

WPM range
Edit app/src/main/java/com/badgr/rsvpreader/MainActivity.kt:
kotlin
// Example:
valueRange = 200f..900f,  // Adjust to preferred min/max WPM
steps = 27,               // Align steps with the new range

Align the range and step count with your expected reader proficiency levels.

Building a release APK
To generate an optimized release APK:
bash
./gradlew assembleRelease

The release artifact is created under:
text
app/build/outputs/apk/release/app-release-unsigned.apk

For Play Store distribution, configure signing with a secure keystore and integrate signing configs into your Gradle build as recommended in Android’s release guidelines.

Recommended next steps
Today
Build and run the project end to end.
Exercise all primary controls (play, pause, jump, reset, WPM slider).
Confirm behavior on the default AVD profile.
This week
Align color palette and typography with your brand system.
Replace sample text with domain-relevant content.
Validate behavior on at least one physical device.
Share a test build with internal stakeholders for feedback.
Future versions
Add text import (TXT, EPUB, PDF) to support real-world reading flows.
Implement reading history and statistics to surface engagement metrics.
Add themes and user profiles for multi-tenant scenarios.
Evaluate cloud sync for cross-device continuation and analytics.​

Build timeline overview
Activity
Approximate time
Notes
Extract project
10 seconds
Archive only
Run setup.sh
10 minutes
Package installs
Install Android Studio
15 minutes
One-time
Initial Studio wizard
15 minutes
SDK download
Open and sync project
5 minutes
First sync
Create AVD
5 minutes
Plus image download
First debug build
5 minutes
Warm caches
Total initial setup
≈ 60 minutes
End-to-end

Subsequent builds typically complete in under one minute on the same machine.​

### Success criteria
You are in a good state to iterate and ship when:
Gradle builds complete without errors in Android Studio and on the command line.
Emulator and physical-device deployments succeed consistently.
The BADGR-branded icon and splash experience are visible.
Single-word display and ORP highlighting behave smoothly across WPM ranges.
All controls (play, pause, jump, reset, slider) respond without noticeable lag.

### Further learning
Android Developer Guide: core platform and app architecture references.​
Jetpack Compose tutorial: modern UI patterns and best practices.​
Kotlin documentation: language features and idioms.​
For project internals, refer to:
TECHNICAL.md for architecture and design decisions.
README.md for feature overview and setup notes.
RSVPEngine.kt for the core RSVP logic and extension points.

Quick command reference
bash
### Full setup path
cd RSVPReader
./setup.sh
source ~/.bashrc
android-studio .

### Command-line build path
./gradlew clean build
./gradlew installDebug

### Device visibility
adb devices

### Filtered log output
adb logcat | grep BADGR

### Uninstall from a device
adb uninstall com.badgr.rsvpreader

This guide is designed to get a developer from a clean Ubuntu 24.04 workstation to a running BADGR RSVP Speed Reader build in about an hour, with clear upgrade paths toward a signed release-ready artifact
