package com.infinix.instalane.ui.home.profile.twoFactorAuth

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.infinix.instalane.databinding.FragmentValidateCodeBinding
import com.infinix.instalane.utils.visible

class ValidateCodeFragment : Fragment() {

    private val binding by lazy { FragmentValidateCodeBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        addTextWatcher(binding.etCode1, binding.etCode2)
        addTextWatcher(binding.etCode2, binding.etCode3)
        addTextWatcher(binding.etCode3, binding.etCode4)
        addTextWatcher(binding.etCode4, binding.etCode5)
        addTextWatcher(binding.etCode5, binding.etCode6)
        addTextWatcher(binding.etCode6, null)

        addKeyListener(null, binding.etCode1)
        addKeyListener(binding.etCode1, binding.etCode2)
        addKeyListener(binding.etCode2, binding.etCode3)
        addKeyListener(binding.etCode3, binding.etCode4)
        addKeyListener(binding.etCode4, binding.etCode5)
        addKeyListener(binding.etCode5, binding.etCode6)

        binding.mContinue.setOnClickListener {
            if (binding.etCode1.text.toString().length == 1
                && binding.etCode2.text.toString().length == 1
                && binding.etCode3.text.toString().length == 1
                && binding.etCode4.text.toString().length == 1
                && binding.etCode5.text.toString().length == 1
                && binding.etCode6.text.toString().length == 1
            ) {
                verifyNumber()
            }
        }
    }

    private fun verifyNumber() {
        val code = StringBuilder()
            .append(binding.etCode1.text.toString())
            .append(binding.etCode2.text.toString())
            .append(binding.etCode3.text.toString())
            .append(binding.etCode4.text.toString())
            .append(binding.etCode5.text.toString())
            .append(binding.etCode6.text.toString())
            .toString()

        (activity as TwoFactorAuthActivity).validateCode(code)
    }

    private fun addTextWatcher(etCode: EditText, etCodeNext: EditText?) {
        etCode.addTextChangedListener {
            if (it?.length == 1) etCodeNext?.requestFocus()
            binding.mContinue.isEnabled = binding.etCode1.text.isNotEmpty()
                    && binding.etCode2.text.isNotEmpty()
                    && binding.etCode3.text.isNotEmpty()
                    && binding.etCode4.text.isNotEmpty()
                    && binding.etCode5.text.isNotEmpty()
                    && binding.etCode6.text.isNotEmpty()
        }
    }

    private fun addKeyListener(etCodeBefore: EditText?, etCode: EditText) {
        etCode.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && etCode.text.isEmpty()) {
                etCodeBefore?.requestFocus()
            }
            false
        }
    }

    fun hideProgress() {
        binding.mContinue.visible()
    }

}