package com.infinix.instalane.ui.home.profile.twoFactorAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.FragmentSendCodeBinding
import com.infinix.instalane.ui.base.FragmentAppBase
import com.infinix.instalane.ui.home.profile.UserProfileViewModel
import com.infinix.instalane.utils.AppDialog
import com.infinix.instalane.utils.gone
import com.infinix.instalane.utils.listenerAfterTextChanged
import com.infinix.instalane.utils.visible


class SendCodeFragment : FragmentAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[UserProfileViewModel::class.java].apply {
            editLiveData.observe(this@SendCodeFragment) {
                hideProgressDialog()
                activity?.finish()
            }
            onError.observe(this@SendCodeFragment) {
                hideProgressDialog()
                showErrorAlert(it)
            }
        }
    }

    private val binding by lazy { FragmentSendCodeBinding.inflate(layoutInflater) }
    private val accessToken by lazy { AppPreferences.getUser()!!.accessToken!! }

    private var isFromLogin = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.getBoolean(TwoFactorAuthActivity.ARG_FROM_LOGIN, false)?.apply {
            isFromLogin = this
        }

        if (isFromLogin || AppPreferences.getUser() == null){
            AppPreferences.setUser(null)
            binding.mCheckTwoFactor.gone()
        } else {
            val user = AppPreferences.getUser()!!
            if (user.twofactor==true)
                binding.mCheckTwoFactor.visible()
            else
                binding.mCheckTwoFactor.gone()

            binding.mCheckTwoFactor.isChecked = user.twofactor!!
            binding.mCheckTwoFactor.setOnCheckedChangeListener { buttonView, isChecked ->
                if (!isChecked){
                    askForRemoving()
                }
            }
        }

        //binding.ccp.registerPhoneNumberTextView(binding.etPhoneNumber)
        binding.etPhoneNumber.listenerAfterTextChanged {
            binding.mContinue.isEnabled = it.isNotEmpty()
        }

        binding.mContinue.setOnClickListener {
            //val countryCode = binding.ccp.selectedCountryCodeWithPlus
            val phoneNumber = binding.etPhoneNumber.text.toString().trim().replace(" ", "").replace("-", "")
            (activity as TwoFactorAuthActivity).sendCode(phoneNumber)
        }
    }

    private fun askForRemoving(){
        AppDialog.showDialog(requireContext(),
            title = getString(R.string.app_name),
            body = getString(R.string.want_to_disable_biometric),
            confirm = getString(R.string.ok),
            cancel = getString(R.string._cancel),
            cancelListener = object : AppDialog.CancelListener{
                override fun onCancel() {
                    binding.mCheckTwoFactor.isChecked = true
                }
            },
            confirmListener = object : AppDialog.ConfirmListener{
                override fun onClick() {
                    binding.mCheckTwoFactor.isChecked = false
                    showProgressDialog()
                    viewModel.setTwoFactor(false, null,accessToken)
                }
            }
        )
    }

}