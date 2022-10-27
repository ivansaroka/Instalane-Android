package com.infinix.instalane.ui.home.profile.twoFactorAuth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.infinix.instalane.databinding.FragmentValidateCodeBinding

class ValidateCodeFragment : Fragment() {

    private val binding by lazy { FragmentValidateCodeBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.mContinue.setOnClickListener {
            (activity as TwoFactorAuthActivity).validateCode("")
        }
    }

}