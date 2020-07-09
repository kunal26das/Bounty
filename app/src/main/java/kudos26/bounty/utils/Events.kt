/*
 * Copyright (c) 2020.
 */

package kudos26.bounty.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * Created by kunal on 22-11-2019.
 */

object Events {

    private val events = PublishSubject.create<Any>()

    fun <T> subscribe(eventClass: Class<T>, action: (data: T) -> Unit): Disposable =
            events.ofType(eventClass)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ action(it) }, {})

    fun publish(event: Any) = events.onNext(event)
}