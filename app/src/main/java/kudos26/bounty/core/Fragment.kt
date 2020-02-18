package kudos26.bounty.core

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by kunal on 06-02-2020.
 */

abstract class Fragment : Fragment() {
    abstract val viewModel: ViewModel
    protected val firebaseAuth = FirebaseAuth.getInstance()
    protected val firebaseDatabase = FirebaseDatabase.getInstance().reference

    open fun initObservers() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }
}