# LOKHA BRO

**Your words. Upgraded.**  
A minimal, white-and-charcoal Android text assistant.

<p align="center">
  <strong>LOKHA BRO</strong><br/>
  Write clearly. Sound like yourself.
</p>

## Current status

**Prototype scaffold — not yet a production AI assistant or signed release.**

This repository was initialized as an empty repository, so the current implementation is a clean Kotlin + Jetpack Compose Android scaffold rather than a port of SwiftSlate's existing accessibility-service implementation. The current UI includes an editor, five writing-style choices, and an explicit status message explaining that live AI is not connected yet.

### Product direction

- Android-only first release
- White surfaces, charcoal typography, subtle gray borders
- Text workflows planned: Improve, Professional, Shorten, Expand, Fix grammar
- AI provider selection and secure API-key storage are next implementation tasks
- Accessibility-based inline assistance is not yet implemented in this scaffold

## Stack

- Kotlin
- Android SDK 35; minimum Android API 23
- Jetpack Compose and Material 3
- JDK 17
- Gradle 8.9

## Build locally

Install Android Studio, JDK 17, Android SDK Platform 35, and Gradle 8.9.

```bash
gradle assembleDebug
```

The debug APK is generated at:

`app/build/outputs/apk/debug/app-debug.apk`

Install it on a connected device:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

GitHub Actions builds the debug APK and stores it as a workflow artifact.

## Signed release APK

See [docs/RELEASE.md](docs/RELEASE.md). Release signing uses environment variables and a private keystore; never commit signing credentials or API keys. A signed production APK has **not** been published yet. Before release, connect and test a real AI provider, validate the app on a physical device, verify the APK signature, and document privacy behavior.

## Privacy

The current prototype does not send entered text to an AI service. The UI explicitly says provider integration is not connected. When provider calls are implemented, explain which content is transmitted, store API credentials securely, and handle provider errors without logging sensitive text.

## Upstream and licensing

The planned product direction references [SwiftSlate](https://github.com/Musheer360/SwiftSlate), but this repository currently contains a fresh scaffold and does not yet include SwiftSlate's source. Before importing or adapting upstream code, preserve the applicable license and notices and document the source clearly.
