package kudos26.bounty.koin

import kudos26.bounty.source.repository.NotificationsRepository
import org.koin.dsl.module

/**
 * Created by kunal on 10-02-2020.
 */

val repositories = module {
    single { NotificationsRepository(get()) }
}