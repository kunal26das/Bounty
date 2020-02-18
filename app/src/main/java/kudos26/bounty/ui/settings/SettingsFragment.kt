package kudos26.bounty.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.fragment_settings.*
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
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchAccount.setOnClickListener {

        }
    }

}