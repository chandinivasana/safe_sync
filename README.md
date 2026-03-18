# SafeSync Offline

SafeSync Offline is a women's safety and empowerment platform designed for environments with zero internet connectivity. It utilizes on-device AI and peer-to-peer mesh networking to provide immediate safety triggers, secure offline logging, and local community resource discovery.

## Technical Overview

The application is built on a decentralized architecture, removing reliance on centralized servers or cellular infrastructure.

### Core Modules

- **Mesh Networking:** Peer-to-peer communication using Bluetooth Low Energy (BLE) and WiFi Direct. It implements a custom routing protocol for SOS broadcasting and community data sharing (gigs, coworking spots) within a 1km+ mesh range.
- **On-Device AI (TF Lite):** Local processing of sensor and audio data for anomaly detection and voice stress analysis. No data leaves the device for inference, ensuring absolute privacy.
- **Safety Engine:** An accelerometer-based gesture detection system (3 shakes in 1.5s at 2.5G threshold) that triggers an automated SOS workflow, including encrypted audio recording and mesh-wide alerts.
- **Blockchain Integration (Polygon):** A tamper-proof audit trail for safety events. Hashes of safety incidents are queued locally and synchronized to the Polygon network once internet connectivity is restored.
- **Encrypted Local Storage:** All personal data, including the user's journal and mesh messages, are stored in a local Room database encrypted with SQLCipher (AES-256).

## Technology Stack

- **Language:** Kotlin
- **Architecture:** MVVM + Clean Architecture
- **UI:** Jetpack Compose (Material 3)
- **Dependency Injection:** Hilt
- **Database:** Room + SQLCipher (AES-256)
- **AI Engine:** TensorFlow Lite
- **Mesh Layer:** Android BLE & WiFi P2P APIs
- **Blockchain:** Web3j (Polygon Network)
- **Smart Contracts:** Solidity (SafeSyncLogs.sol)

## Implementation Details

### Security
The application implements a zero-trust model. All communication over the mesh is encrypted using Libsodium (NaCl). Identity is managed via locally generated HD wallets (BIP-39), ensuring users retain full ownership of their safety logs and private data.

### Scalability
The gossip-based mesh protocol allows for up to 10 hops, extending the safety network far beyond the range of a single device. The system is designed to handle message TTL (Time to Live) and duplicate packet detection to maintain network efficiency.

## Setup and Installation

### Android Studio Setup
1. **Open Android Studio:** Select "Open" and navigate to the `safesync/` directory.
2. **Gradle Sync:** Allow Android Studio to sync Gradle and download dependencies.
3. **SDK Requirements:** Ensure you have Android SDK 34 (API 34) and Build Tools installed.
4. **Java Version:** Set the Gradle JDK to Java 17 in `Settings > Build, Execution, Deployment > Build Tools > Gradle`.

### Building from Source & Running
1. **Physical Device Recommended:** BLE Mesh and Shake-to-SOS require hardware sensors. Connect a device via USB or WiFi.
2. **Run Configuration:** Select the `app` module and click the "Run" (green play) button.
3. **CLI Build:** Alternatively, use the command line:
   ```bash
   ./gradlew assembleDebug
   ```
4. **Deployment:**
   ```bash
   ./gradlew installDebug
   ```

### Implementation Progress (Phase 1)
- [x] **A-01:** User Profile Setup (Encrypted DataStore)
- [x] **A-05:** Permissions Onboarding
- [x] **B-01/02:** BLE Mesh Protocol (Custom Serialized Packets)
- [x] **B-05:** Ed25519 Packet Signing (Google Tink)
- [x] **C-01:** Shake-to-SOS (3 shakes in 2s)
- [x] **G-01:** HD Wallet Auto-Generation (web3j/BIP-39)
- [x] **H-02:** DPDP Consent Flow
- [x] **H-04:** Local Data Delete (SQLCipher/Room)

## License
Distributed under the MIT License. See `LICENSE` for more information.
