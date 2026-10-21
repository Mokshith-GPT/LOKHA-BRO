# Signed APK release checklist

This project is not considered production-ready until the release APK is signed, installed, and tested on a physical device.

## Local build

1. Install JDK 17, Android SDK Platform 35, and Gradle 8.9.
2. Run `gradle assembleDebug`.
3. Install `app/build/outputs/apk/debug/app-debug.apk` on a test device.

## Signing

Create a private keystore outside the repository. Configure these environment variables:

- `LOKHA_KEYSTORE_PATH`: absolute path to the keystore
- `LOKHA_KEYSTORE_PASSWORD`
- `LOKHA_KEY_ALIAS`
- `LOKHA_KEY_PASSWORD`

Then run `gradle assembleRelease`. Verify the output with Android SDK Build Tools' `apksigner verify --verbose --print-certs app/build/outputs/apk/release/app-release.apk`.

Never commit a keystore, password, API key, or signing secret. Back up the keystore securely; future APK updates need a compatible signing identity.

## Release gates

- [ ] Live provider integration implemented and tested, or app clearly described as a prototype
- [ ] No secrets or sensitive user text in logs
- [ ] Accessibility permissions, if introduced later, are explained and tested
- [ ] App tested on a physical Android device
- [ ] APK signature verified
- [ ] SHA-256 checksum recorded
- [ ] README, version number, privacy statement, and known limitations updated
- [ ] License and third-party attribution reviewed
