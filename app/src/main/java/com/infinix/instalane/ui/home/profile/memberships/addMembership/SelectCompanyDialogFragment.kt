package com.infinix.instalane.ui.home.profile.memberships.addMembership

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Memberships
import com.infinix.instalane.databinding.FragmentSelectStoreBinding
import com.infinix.instalane.utils.listenerAfterTextChanged


class SelectCompanyDialogFragment () : BottomSheetDialogFragment() {

    lateinit var onStoreSelected:(Memberships.Company)->Unit

    private val viewModel by lazy {
        ViewModelProvider(this)[CompanyViewModel::class.java].apply {
            companyLiveData.observe(this@SelectCompanyDialogFragment, this@SelectCompanyDialogFragment::showStores)
            onError.observe(this@SelectCompanyDialogFragment) {
                binding.mProgress.visibility = View.GONE
            }
        }
    }

    private val binding by lazy { FragmentSelectStoreBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }

        return bottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.mClose.setOnClickListener { dismiss() }

        binding.mSearch.listenerAfterTextChanged {

        }

        binding.mList.visibility = View.INVISIBLE
        binding.mProgress.visibility = View.VISIBLE
        viewModel.getCompanies()
    }

    private fun showStores(list:List<Memberships.Company>){
        binding.mList.visibility = View.VISIBLE
        binding.mProgress.visibility = View.GONE
        val adapter = CompanyAdapter(list)
        adapter.onItemSelected = {
            onStoreSelected(it)
            dismiss()
        }
        binding.mList.adapter = adapter
    }

}