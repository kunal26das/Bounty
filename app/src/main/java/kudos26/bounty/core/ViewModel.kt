package kudos26.bounty.core

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class ViewModel : ViewModel(), KoinComponent {

    protected val firebaseAuth: FirebaseAuth by inject()
    private val compositeDisposable = CompositeDisposable()
    val uid = MutableLiveData(firebaseAuth.currentUser?.uid)
    val currentUser = MutableLiveData(firebaseAuth.currentUser)
    val name = MutableLiveData(firebaseAuth.currentUser?.displayName)

    fun disposeOnCleared(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}