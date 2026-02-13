## BADGR RSVP Speed Reader - Quick Start Guide

## Get Running in 5 Minutes

### For Ubuntu 24.04 (Your System)

```bash
# 1. Run automated setup (installs Java, KVM)
cd /path/to/RSVPReader
chmod +x setup.sh
./setup.sh

# 2. Reload environment variables
source ~/.bashrc

# 3. Install Android Studio (if not already installed)
sudo snap install android-studio --classic

# 4. Launch Android Studio
android-studio

# 5. Open this project
# File → Open → Navigate to RSVPReader folder → OK

# 6. Wait for Gradle sync (2-3 minutes first time)

# 7. Create Virtual Device
# Tools → Device Manager → Create Device → Pixel 7 → Download API 34 → Finish

# 8. Run the app
# Click green "Run" button → Select your device → Wait for build
Alternative: Command Line Build
bash
# After Android Studio setup, build from terminal:
cd /path/to/RSVPReader

# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# APK location:
# app/build/outputs/apk/debug/app-debug.apk
Running on Your Phone
bash
# 1. Enable USB Debugging on phone
#    Settings → About → Tap Build Number 7 times
#    Settings → Developer Options → USB Debugging ON

# 2. Connect phone via USB

# 3. Verify connection
adb devices

# 4. Install app
./gradlew installDebug
Time Estimates
Task	Duration
Java installation	2 min
Android Studio download	5-10 min
Android Studio setup	5 min
Project import & sync	3 min
First build	3-5 min
Total	~20 min
System Check
Your Hardware (CONFIRMED COMPATIBLE):

AMD Ryzen 5 5500 (12 cores)

16GB RAM (8GB+ required)

AMD Radeon RX 6500 XT

1TB storage (10GB+ required)

Ubuntu 24.04.3 LTS

Common Issues
Build fails with "SDK not found"
bash
# Create local.properties manually
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
Emulator is slow
bash
# Enable KVM acceleration (already in setup.sh)
sudo apt install qemu-kvm -y
sudo adduser $USER kvm
# Logout and login
Gradle sync fails
bash
# In Android Studio:
# File → Invalidate Caches → Invalidate and Restart
Need Help?
Check README.md for detailed instructions

See Troubleshooting section

Verify all prerequisites are met

Ready to build? Run ./setup.sh now!

© 2026 BADGR Technologies LLC

text
undefined
