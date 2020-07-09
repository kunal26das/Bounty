package kudos26.bounty.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import kotlinx.android.synthetic.main.fragment_settings.*
import kudos26.bounty.BuildConfig
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.databinding.FragmentSettingsBinding
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 30-01-2020.
 */

class SettingsFragment : Fragment() {

    override val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var dataBinding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.buildConfig = BuildConfig()
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDisplayName()
        initUpiAddress()
    }

    private fun initDisplayName() {
        displayName.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                        maxLength = 25,
                        allowEmpty = false,
                        hintRes = R.string.display_name,
                        prefill = viewModel.name.value
                ) { dialog, name ->
                    viewModel.setDisplayName(name.toString())
                    dialog.dismiss()
                }
                negativeButton { dismiss() }
                positiveButton(R.string.change)
            }
        }
    }

    private fun initUpiAddress() {
        upiAddress.setOnClickListener {
            MaterialDialog(requireContext()).show {
                input(
                        maxLength = 25,
                        allowEmpty = false,
                        hintRes = R.string.upi_address,
                        prefill = viewModel.upi.value
                ) { dialog, address ->
                    viewModel.setUpiAddress(address.toString())
                    dialog.dismiss()
                }
                negativeButton { dismiss() }
                positiveButton(R.string.change)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUpiAddress()
        viewModel.getDisplayName()
    }

}