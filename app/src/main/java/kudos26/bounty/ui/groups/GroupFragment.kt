package kudos26.bounty.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_group.*
import kudos26.bounty.R
import kudos26.bounty.core.Fragment
import kudos26.bounty.source.model.Group
import kudos26.bounty.ui.MainViewModel
import kudos26.bounty.ui.transactions.DuesFragment
import kudos26.bounty.ui.transactions.TransactionsFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel

class GroupFragment : Fragment() {

    private var archive = false
    private lateinit var group: Group
    override val viewModel by sharedViewModel<MainViewModel>()
    private var fragments = listOf(TransactionsFragment(), DuesFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getParcelable(getString(R.string.group))!!
        archive = arguments?.getBoolean(getString(R.string.archive))!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager2.setPageTransformer(MarginPageTransformer(resources.getDimension(R.dimen.margin_global).toInt()))
        viewPager2.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int): Fragment {
                fragments[position].arguments = Bundle().apply {
                    putBoolean(getString(R.string.archive), archive)
                    putParcelable(getString(R.string.group), group)
                }
                return fragments[position]
            }
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.viewPager2Position.value = position
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewPager2.currentItem = viewModel.viewPager2Position.value ?: 0
    }
}