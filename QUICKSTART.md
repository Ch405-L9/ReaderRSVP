## BADGR RSVP SPEED READER - QUICK START GUIDE

### GET RUNNING IN 5 MINUTES
### FOR UBUNTU 24.04

---

### STEP 1: RUN AUTOMATED SETUP (INSTALLS JAVA, KVM)

cd /path/to/RSVPReader
chmod +x setup.sh
./setup.sh

---

### STEP 2: RELOAD ENVIRONMENT VARIABLES

source ~/.bashrc

---

### STEP 3: INSTALL ANDROID STUDIO (IF NOT ALREADY INSTALLED)

sudo snap install android-studio --classic

---

### STEP 4: LAUNCH ANDROID STUDIO

android-studio

---

### STEP 5: OPEN THIS PROJECT

File → Open → Navigate to RSVPReader folder → OK

---

### STEP 6: WAIT FOR GRADLE SYNC

First-time sync duration: 2–3 minutes

---

### STEP 7: CREATE VIRTUAL DEVICE

Tools → Device Manager → Create Device → Pixel 7 → Download API 34 → Finish

---

### STEP 8: RUN THE APP

Click green "Run" button → Select your device → Wait for build

---

### ALTERNATIVE: COMMAND LINE BUILD

After Android Studio setup:

cd /path/to/RSVPReader

Build debug APK:

./gradlew assembleDebug

Install on connected device/emulator:

./gradlew installDebug

APK location:

app/build/outputs/apk/debug/app-debug.apk

---

## RUNNING ON YOUR PHONE

### STEP 1: ENABLE USB DEBUGGING

Settings → About → Tap Build Number 7 times  
Settings → Developer Options → USB Debugging ON

### STEP 2: CONNECT PHONE VIA USB

### STEP 3: VERIFY CONNECTION

adb devices

### STEP 4: INSTALL APP

./gradlew installDebug

---

## TIME ESTIMATES

Java installation: 2 min  
Android Studio download: 5–10 min  
Android Studio setup: 5 min  
Project import & sync: 3 min  
First build: 3–5 min  
Total: ~20 min  

---

## SYSTEM CHECK

Confirmed compatible hardware:

AMD Ryzen 5 5500 (12 cores)  
16GB RAM (8GB+ required)  
AMD Radeon RX 6500 XT  
1TB storage (10GB+ required)  
Ubuntu 24.04.3 LTS  

---

## COMMON ISSUES

Build fails with "SDK not found"

Create local.properties manually:

echo "sdk.dir=$HOME/Android/Sdk" > local.properties

Emulator is slow

Enable KVM acceleration:

sudo apt install qemu-kvm -y
sudo adduser $USER kvm

Logout and login after adding user to group.

Gradle sync fails

In Android Studio:

File → Invalidate Caches → Invalidate and Restart

---

## NEED HELP

Check README.md for detailed instructions  
See Troubleshooting section  
Verify all prerequisites are met  

Ready to build?

Run:

./setup.sh

© 2026 BADGR Technologies LLC
