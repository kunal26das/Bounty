package kudos26.bounty.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_add_group.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class AddGroupFragment : Fragment() {

    override val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupNameInputLayout.setEndIconOnClickListener {
            viewModel.createGroup(groupNameInput.text.toString())
            findNavController().navigateUp()
        }
    }


    override fun initObservers() {}

}
