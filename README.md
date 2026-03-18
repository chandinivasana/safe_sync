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

## Setup and Installation (Android Studio)

### Step 1: Open Project
1. Launch **Android Studio**.
2. Select **Open** and navigate to the `safesync/` directory.

### Step 2: Configure JDK 17
1. Go to **Settings** (on macOS: `Android Studio > Settings`).
2. Navigate to **Build, Execution, Deployment > Build Tools > Gradle**.
3. Set **Gradle JDK** to **Java 17**.

### Step 3: Sync Gradle
1. Android Studio should automatically prompt for a **Gradle Sync**. If not, click the "Elephant" icon in the top right.
2. Ensure you have internet access for the initial download of dependencies (Room, Hilt, web3j, Tink, SQLCipher).

### Step 4: Run the Application
1. **Connect a Physical Device:** (Highly Recommended) Enable USB Debugging on an Android device (API 26+). BLE features and Shake-to-SOS require physical sensors.
2. **Select Run Configuration:** Ensure `app` is selected in the top toolbar.
3. **Click Run:** Press the green "Play" button.

---

## Implementation Progress (Phase 1 MVP)

The following items are implemented and functional:

### Module A: Onboarding & Setup
- [x] **A-01: User Profile Setup** (Encrypted DataStore)
- [x] **A-02: Home Zone Setup** (Location persistence)
- [x] **A-04: Language Selection** (Kannada/Hindi support)
- [x] **A-05: Permissions Onboarding** (Seamless multi-permission flow)

### Module B: BLE Mesh Network
- [x] **B-01: BLE Scanner + Advertiser** (Background foreground service)
- [x] **B-02: Mesh Packet Protocol** (Custom binary serialization)
- [x] **B-05: Packet Signing & Verification** (Ed25519 via Google Tink)

### Module C: Safety Shield
- [x] **C-01: Shake-to-SOS Trigger** (Accelerometer-based, 3 shakes)
- [x] **C-03: SOS Mesh Broadcast** (Signed packet flood)
- [x] **C-04: SOS Alert Receiver UI** (Real-time mesh alert popup)

### Module G: Blockchain Sync
- [x] **G-01: HD Wallet Auto-Generation** (BIP-39 mnemonic, Polygon/web3j)

### Module H: Settings & Privacy
- [x] **H-02: DPDP Consent Flow** (Privacy-first onboarding)
- [x] **H-04: Local Data Delete** (Room clearTable + SQLCipher)


## License
Distributed under the MIT License. See `LICENSE` for more information.
