package kudos26.bounty.core

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class ViewModel : ViewModel() {

    fun disposeOnCleared(disposable: Disposable) {
        CompositeDisposable().add(disposable)
    }

    override fun onCleared() {
        CompositeDisposable().clear()
        super.onCleared()
    }

}