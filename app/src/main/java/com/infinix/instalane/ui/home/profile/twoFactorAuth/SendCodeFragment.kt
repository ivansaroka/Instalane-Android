package com.infinix.instalane.ui.home.profile.twoFactorAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.infinix.instalane.databinding.FragmentSendCodeBinding
import com.infinix.instalane.utils.listenerAfterTextChanged


class SendCodeFragment : Fragment() {

    private val binding by lazy { FragmentSendCodeBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.ccp.registerPhoneNumberTextView(binding.etPhoneNumber)

        binding.etPhoneNumber.listenerAfterTextChanged {
            binding.mContinue.isEnabled = it.isNotEmpty()
        }

        binding.mContinue.setOnClickListener {

            val countryCode = binding.ccp.selectedCountryCodeWithPlus
            val phoneNumber = binding.etPhoneNumber.text.toString().trim().replace(" ", "").replace("-", "")

            (activity as TwoFactorAuthActivity).sendCode(phoneNumber)
            //(activity as TwoFactorAuthActivity).sendCode("$countryCode$phoneNumber")
        }
    }

}