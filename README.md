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

### Requirements
- Android SDK 26+
- Java 17
- Physical device with BLE support (emulators will not support mesh features)

### Building from Source
1. Clone the repository.
2. Place TFLite models in `app/src/main/assets/models/`.
3. Configure `local.properties` with your Android SDK path.
4. Execute `./gradlew installDebug` to deploy to a connected device.

## License
Distributed under the MIT License. See `LICENSE` for more information.
