// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract SafeSyncLogs {
    struct SafetyEvent {
        bytes32 eventHash;   // keccak256 of raw event data
        uint256 timestamp;
        address user;
        uint8 eventType;     // 1=SOS, 2=RouteAlert, 3=AnomalyDetected
    }

    mapping(address => SafetyEvent[]) private userLogs;
    event EventLogged(address indexed user, bytes32 eventHash, uint8 eventType);

    function logEvent(bytes32 _hash, uint8 _type) external {
        userLogs[msg.sender].push(
            SafetyEvent(_hash, block.timestamp, msg.sender, _type)
        );
        emit EventLogged(msg.sender, _hash, _type);
    }

    function getMyLogs() external view returns (SafetyEvent[] memory) {
        return userLogs[msg.sender];
    }

    function getLogCount(address _user) external view returns (uint256) {
        return userLogs[_user].length;
    }
}
