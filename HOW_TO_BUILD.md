## BADGR RSVP SPEED READER
### DEVELOPER BUILD AND RUN GUIDE (UBUNTU 24.04)

---

## FAST PATH: BUILD IN 5 COMMANDS

cd /path/to/RSVPReader
./setup.sh
source ~/.bashrc
android-studio .

After the project loads, click the green Run button.
The app builds and launches in the Android emulator once Gradle sync and the initial build complete.

---

## DETAILED BUILD WORKFLOW

### PREREQUISITES

Confirm before starting:

Ubuntu 24.04.3 LTS  
At least 10 GB free disk space  
Reliable internet connectivity  
User account with sudo privileges  

---

## STEP 1: EXTRACT THE PROJECT

If using a .tar.gz archive:

cd ~/Downloads
tar -xzf BADGR-RSVP-Reader-v1.0.tar.gz

If using a downloaded project folder directly:

cd /path/to/RSVPReader

Use a workspace path suitable for direct opening in Android Studio, for example:

~/dev/RSVPReader

---

## STEP 2: RUN THE AUTOMATED SETUP SCRIPT

The setup script provisions Java 17, KVM acceleration, and Android tooling paths.

cd RSVPReader
./setup.sh

This step typically:

Installs OpenJDK 17  
Sets the JAVA_HOME environment variable  
Installs and configures KVM for hardware-accelerated emulation  
Configures the Android SDK location  
Writes a local.properties file pointing to the SDK  

Expected duration: 5–10 minutes depending on network speed.

---

## STEP 3: RELOAD YOUR SHELL ENVIRONMENT

source ~/.bashrc

This activates environment variables such as JAVA_HOME, ANDROID_HOME, and PATH updates.

---

## STEP 4: INSTALL ANDROID STUDIO (IF NOT INSTALLED)

### OPTION 1 – SNAP

sudo snap install android-studio --classic

The --classic flag is required for proper SDK access.

### OPTION 2 – MANUAL DOWNLOAD

Open:

https://developer.android.com/studio

Download the Linux .tar.gz package, then:

cd ~/Downloads
tar -xzf android-studio-*.tar.gz
sudo mv android-studio /opt/
/opt/android-studio/bin/studio.sh

Initial installation typically completes in 10–15 minutes.

---

## STEP 5: FIRST-TIME ANDROID STUDIO CONFIGURATION

On first launch:

Choose Standard installation type.  
Accept all SDK and license prompts.  
Proceed through the wizard and click Finish.  

Android Studio downloads:

Android SDK  
Build tools  
Platform tools  
Emulator components  

Allow 10–15 minutes for provisioning.

---

## STEP 6: OPEN THE PROJECT

From the welcome screen:

Select Open and navigate to the RSVPReader directory.

Android Studio will:

Load the Gradle project  
Download the Gradle wrapper if required  
Sync dependencies  
Index project files  

Expected time: 3–5 minutes.

---

## STEP 7: CREATE AN ANDROID VIRTUAL DEVICE (AVD)

Open Device Manager.  
Select Create Device.  
Choose Pixel 7.  
Select Android 14 (API 34).  
Download the image if necessary.  
Name the device BADGR_Test_Device.  
Finish setup.

Plan approximately 5 minutes plus image download time.

---

## STEP 8: BUILD AND RUN THE APP

### OPTION 1 – ANDROID STUDIO

Select BADGR_Test_Device from the device dropdown.  
Click the green Run button.  
Wait for Gradle build completion.  

### OPTION 2 – COMMAND LINE

cd RSVPReader
./build.sh

First build: 3–5 minutes.  
Subsequent builds: under 1 minute.

---

## STEP 9: FUNCTIONAL SMOKE TEST

Verify:

BADGR header and Speed Reader title  
Subtitle "by BADGR Technologies LLC"  
Central word display region  
Progress bar  
WPM slider (default 300 WPM)  
Play or Pause control  
Jump backward or forward controls  
Reset control  

Quick test:

Press Play and confirm words advance one at a time.  
Confirm red ORP letter per word.  
Adjust WPM slider and verify real-time speed changes.  
Use jump controls to seek.  
Use Reset and confirm counters and progress bar update.

---

## VERIFICATION CHECKLIST

App launches without runtime crashes  
Branding displays correctly  
Single-word display behavior is correct  
Red ORP highlight visible  
Playback controls function reliably  
WPM slider updates without lag  
Jump controls move correctly  
Reset returns to beginning  
Progress tracking accurate  

---

## TROUBLESHOOTING

### SDK NOT FOUND

echo "sdk.dir=$HOME/Android/Sdk" > local.properties

If installed via snap:

echo "sdk.dir=$HOME/snap/android-studio/common/Android/Sdk" > local.properties

Place file at project root.

---

### GRADLE SYNC FAILED

In Android Studio:

File → Invalidate Caches → Invalidate and Restart

---

### INCORRECT JAVA VERSION

java -version

Target: 17.x.x

Install if needed:

sudo apt install openjdk-17-jdk -y
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

---

### SLOW EMULATOR

kvm-ok

If not configured:

sudo apt install qemu-kvm -y
sudo adduser $USER kvm

Log out and log back in.

---

### ADB NOT FOUND

export PATH=$PATH:$HOME/Android/Sdk/platform-tools
echo 'export PATH=$PATH:$HOME/Android/Sdk/platform-tools' >> ~/.bashrc
source ~/.bashrc

Verify:

adb version
adb devices

---

### DEPENDENCY BUILD FAILURES

./gradlew clean
./gradlew build

---

## RUNNING ON A PHYSICAL DEVICE

Enable Developer Mode:

Settings → About phone  
Tap Build number seven times  
Settings → System → Developer options  
Enable USB debugging  

Connect via USB.

Verify device:

adb devices

Install:

./gradlew installDebug

---

## CUSTOMIZATION HOOKS

### BRAND COLORS

Edit:

app/src/main/java/com/badgr/rsvpreader/ui/theme/Theme.kt

Example:

val BADGRBlue = Color(0xFF0000FF)
val BADGRRed = Color(0xFFFF0000)
val BADGRBlack = Color(0xFF000000)
val BADGRWhite = Color(0xFFFFFFFF)

---

### SAMPLE TEXT

Edit:

app/src/main/res/values/strings.xml

<string name="sample_text">Your new text here...</string>

---

### WPM RANGE

Edit:

app/src/main/java/com/badgr/rsvpreader/MainActivity.kt

valueRange = 200f..900f
steps = 27

---

## BUILDING A RELEASE APK

./gradlew assembleRelease

Artifact path:

app/build/outputs/apk/release/app-release-unsigned.apk

Configure signing before distribution.

---

## BUILD TIMELINE OVERVIEW

Extract project: 10 seconds  
Run setup.sh: 10 minutes  
Install Android Studio: 15 minutes  
Initial wizard: 15 minutes  
Open and sync project: 5 minutes  
Create AVD: 5 minutes  
First debug build: 5 minutes  
Total initial setup: ~60 minutes  

Subsequent builds: typically under 1 minute.

---

## SUCCESS CRITERIA

Gradle builds succeed in Studio and CLI  
Emulator and device deployments consistent  
BADGR branding visible  
Single-word display smooth across WPM range  
All controls responsive  

---

## QUICK COMMAND REFERENCE

cd RSVPReader
./setup.sh
source ~/.bashrc
android-studio .

./gradlew clean build
./gradlew installDebug

adb devices
adb logcat | grep BADGR
adb uninstall com.badgr.rsvpreader
