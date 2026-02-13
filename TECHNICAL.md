```text
BADGR RSVP Reader – Technical Documentation
Version: 1.0.0 (Production)
Maintained By: BADGR Technologies LLC Workflow Engineering Team

## 1. Architecture Overview
The ReaderRSVP application is built on a modern reactive stack, utilizing Jetpack Compose for the UI layer and Kotlin Coroutines for high-precision timing logic. The architecture follows a strict separation of concerns via the MVVM pattern.

Core Components
Component                Responsibility
MainActivity.kt          Entry point; hosts the Jetpack Compose UI and manages the lifecycle of the RSVPEngine.
RSVPReaderScreen         High-level Composable containing the Word Display, Progress telemetry, and Control interfaces.
RSVPEngine.kt            The "Brain." Handles text tokenization, ORP calculation, timing loops, and state management.

## 2. Data Flow & Execution Logic
The system utilizes a unidirectional data flow to ensure UI consistency and predictable playback performance.

Ingestion: Text is passed to *RSVPEngine.loadText()*.  
Tokenization: String is split via whitespace into a sanitized word array.  
Initialization: The *currentIndex* is set to 0, and *StateFlow* emits the first word.  
Playback: A Coroutine-based Timing Loop executes:
- Calculates ORP Index for the current word.
- Constructs an AnnotatedString with the red fixation highlight.
- Suspends via *delay(60,000ms / WPM)*.
- Increments index and triggers UI recomposition.

## 3. ORP (Optimal Recognition Point) Algorithm
The ORP is the specific fixation point in a word where the human eye recognizes the entire word fastest. By highlighting this point in BADGR Red, saccadic eye movement is eliminated, allowing for speeds up to 900 WPM.

Algorithm Logic (Kotlin)
fun calculateOrpIndex(word: String): Int {
    return when (word.length) {
        in 1..2 -> 0      // "it" → "it"
        in 3..5 -> 1      // "word" → "wOrd"
        in 6..9 -> 2      // "reading" → "reAding"
        in 10..13 -> 3    // "understanding" → "undErstanding"
        else -> 4         // "extraordinarily" → "extrAordinarily"
    }
}

Visual Implementation
To ensure maximum readability, the ORP letter is rendered with a distinct SpanStyle:
- Color: #FF0000 (BADGR Red)
- Weight: FontWeight.Bold
- Alignment: Fixed horizontal coordinate (X) to prevent eye jitter.

## 4. Timing Engine Mechanics
Precision timing is critical at high speeds. At 900 WPM, each word is displayed for only 67 milliseconds.

WPM     Delay     Words Per Second
200     300ms     3.33
300     200ms     5.00
450     133ms     7.50
600     100ms     10.00
900     67ms      15.00

The Coroutine Advantage: Unlike *Thread.sleep()*, Kotlin Coroutines provide non-blocking suspension, ensuring the Android UI thread remains responsive for user input (Pause/Reset) even during high-frequency updates.

## 5. UI/UX Hierarchy
The interface is optimized for focus, utilizing the BADGR Technologies professional color palette.

Element        Hex Code    Purpose
BADGR Blue     #0000FF     Primary Branding & Accents
BADGR White    #FFFFFF     Primary Text & Content
BADGR Black    #000000     Background (OLED/High Contrast)
BADGR Red      #FF0000     ORP Fixation Highlight

## 6. State Management (Reactive Pattern)
The engine leverages *StateFlow* to provide a lifecycle-aware stream of data to the UI.

Encapsulation: The engine maintains a *MutableStateFlow* (private) while exposing a read-only *StateFlow* to the UI.  
Lifecycle: The UI "collects" the state as a Compose State object, ensuring that the app only consumes resources when it is in the foreground.

## 7. Performance & Security
Optimization Status
- Zero-Dependency: No external library bloat (minimized attack surface).
- O(n) Tokenization: Text is processed once during load; word lookups are O(1).
- 60 FPS Target: UI updates are optimized to avoid unnecessary recomposition of static elements (logos/titles).

Security Model
- Local-First: No network permissions requested; text data remains on-device.
- Sandboxed: Operates within the standard Android security sandbox with no external file access (v1.0).

## 8. Build Configuration
Requirement     Specification
Min SDK         24 (Android 7.0)
Target SDK      34 (Android 14)
Language        Kotlin 1.9.0
JDK             Java 17

© 2026 BADGR Technologies LLC. All rights reserved. Built with modern Android best practices.
```
