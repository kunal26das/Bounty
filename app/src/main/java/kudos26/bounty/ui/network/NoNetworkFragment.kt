package kudos26.bounty.ui.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by kunal on 04-02-2020.
 */

class NoNetworkFragment : Fragment() {

    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return inflater.inflate(R.layout.fragment_no_network, container, false)
    }

    override fun initObservers() {}

}