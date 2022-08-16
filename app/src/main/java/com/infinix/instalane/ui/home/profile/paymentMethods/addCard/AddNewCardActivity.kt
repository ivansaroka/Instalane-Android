package com.infinix.instalane.ui.home.profile.paymentMethods.addCard

import android.os.Bundle
import com.infinix.instalane.R
import com.infinix.instalane.databinding.ActivityAddNewCardBinding
import com.infinix.instalane.ui.base.ActivityAppBase

class AddNewCardActivity : ActivityAppBase() {

    private val binding by lazy { ActivityAddNewCardBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.add_new_card))
    }
}