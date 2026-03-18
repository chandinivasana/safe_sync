package com.crispyc.safesync.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crispyc.safesync.core.blockchain.BlockchainManager
import com.crispyc.safesync.core.profile.ProfileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val blockchainManager: BlockchainManager
) : ViewModel() {

    private val _mnemonic = MutableStateFlow("")
    val mnemonic = _mnemonic.asStateFlow()

    fun saveProfile(name: String, language: String) {
        viewModelScope.launch {
            profileManager.saveProfile(name, language)
        }
    }

    fun generateWallet() {
        if (_mnemonic.value.isEmpty()) {
            _mnemonic.value = blockchainManager.generateWallet()
        }
    }
}
