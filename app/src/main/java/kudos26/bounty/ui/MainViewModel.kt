package kudos26.bounty.ui

import androidx.essentials.events.Events
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kudos26.bounty.core.ViewModel
import kudos26.bounty.firebase.Extensions.admin
import kudos26.bounty.firebase.Extensions.amount
import kudos26.bounty.firebase.Extensions.archive
import kudos26.bounty.firebase.Extensions.author
import kudos26.bounty.firebase.Extensions.comment
import kudos26.bounty.firebase.Extensions.currentMember
import kudos26.bounty.firebase.Extensions.currentMembers
import kudos26.bounty.firebase.Extensions.date
import kudos26.bounty.firebase.Extensions.devices
import kudos26.bounty.firebase.Extensions.email
import kudos26.bounty.firebase.Extensions.formerMember
import kudos26.bounty.firebase.Extensions.getValue
import kudos26.bounty.firebase.Extensions.group
import kudos26.bounty.firebase.Extensions.groups
import kudos26.bounty.firebase.Extensions.impact
import kudos26.bounty.firebase.Extensions.impactOn
import kudos26.bounty.firebase.Extensions.invitation
import kudos26.bounty.firebase.Extensions.invitations
import kudos26.bounty.firebase.Extensions.inviter
import kudos26.bounty.firebase.Extensions.isAdmin
import kudos26.bounty.firebase.Extensions.joiningDate
import kudos26.bounty.firebase.Extensions.name
import kudos26.bounty.firebase.Extensions.newGroupId
import kudos26.bounty.firebase.Extensions.newTransactionId
import kudos26.bounty.firebase.Extensions.observeValue
import kudos26.bounty.firebase.Extensions.payer
import kudos26.bounty.firebase.Extensions.pendingInvite
import kudos26.bounty.firebase.Extensions.since
import kudos26.bounty.firebase.Extensions.transaction
import kudos26.bounty.firebase.Extensions.transactions
import kudos26.bounty.firebase.Extensions.upi
import kudos26.bounty.firebase.Extensions.user
import kudos26.bounty.source.model.*
import kudos26.bounty.source.repository.NotificationsRepository
import kudos26.bounty.utils.CalendarUtils.currentDate
import kudos26.bounty.utils.Extensions.Try
import kudos26.bounty.utils.Extensions.default

/**
 * Created by kunal on 06-02-2020.
 */

class MainViewModel(
        private val database: DatabaseReference,
        private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    val upi = MutableLiveData("")
    val title = MutableLiveData("")
    val subtitle = MutableLiveData("")
    val viewPager2Position = MutableLiveData(0)

    val group = MutableLiveData(Group())
    val transaction = MutableLiveData(Transaction())

    val dues = MutableLiveData<List<Due>>(emptyList())
    val groups = MutableLiveData<List<Group>>(emptyList())
    val archive = MutableLiveData<List<Group>>(emptyList())
    val members = MutableLiveData<List<Member>>(emptyList())
    val invitations = MutableLiveData<List<Invitation>>(emptyList())
    val transactions = MutableLiveData<List<Transaction>>(emptyList())

    fun getDisplayName() {
        database.user(uid.value!!).name.observeValue({
            Try { name.value = it.value as String }
        })
    }

    fun setDisplayName(name: String) {
        database.user(uid.value!!).name.setValue(name).addOnSuccessListener {
            this.name.value = name
        }
    }

    fun getUpiAddress() {
        database.user(uid.value!!).upi.observeValue({
            Try { upi.value = it.value as String }
        })
    }

    fun setUpiAddress(address: String) {
        database.user(uid.value!!).upi.setValue(address).addOnSuccessListener {
            upi.value = address
        }
    }

    fun createGroup(name: String) {
        database.newGroupId?.let { groupId ->
            database.user(uid.value!!).group(groupId).setValue(currentDate).addOnSuccessListener {
                database.group(groupId).let { group ->
                    group.name.setValue(name)
                    group.author.setValue(uid.value)
                    group.since.setValue(currentDate)
                    group.currentMember(uid.value!!).admin.setValue(true)
                    group.currentMember(uid.value!!).joiningDate.setValue(currentDate)
                }
            }
        }
    }

    fun getGroups() {
        database.user(uid.value!!).groups.observeValue({
            when {
                it.exists() -> default {
                    groups.postValue(mutableListOf<Group>().apply {
                        for (group in it.children) {
                            this += Group(group.key!!)
                        }
                    })
                }
                else -> groups.value = emptyList()
            }
        }, { groups.value = emptyList() })
    }

    fun joinGroup(invitation: Invitation) {
        invitation.let {
            database.user(uid.value!!).group(it.id).setValue(currentDate)
            database.group(it.id).apply {
                formerMember(uid.value!!).removeValue()
                pendingInvite(uid.value!!).removeValue()
                currentMember(uid.value!!).admin.setValue(false)
                currentMember(uid.value!!).joiningDate.setValue(currentDate)
            }
        }
        declineInvitation(invitation)
    }

    fun declineInvitation(invitation: Invitation) {
        database.user(uid.value!!).invitation(invitation.id).removeValue()
        database.group(invitation.id).pendingInvite(uid.value!!).removeValue()
    }

    fun getArchivedGroups() {
        database.user(uid.value!!).archive.observeValue({
            when {
                it.exists() -> default {
                    archive.postValue(mutableListOf<Group>().apply {
                        for (group in it.children) {
                            this += Group(group.key!!)
                        }
                    })
                }
                else -> archive.value = emptyList()
            }
        }, { archive.value = emptyList() })
    }

    fun renameGroup(group: Group, name: String) {
        database.group(group.id).name.setValue(name).addOnSuccessListener {
            group.name = name
            this.group.value = group
        }
    }

    fun deleteGroup(group: Group) {
        database.user(uid.value!!).archive(group.id).removeValue()
        database.group(group.id).formerMember(uid.value!!).removeValue()
    }

    fun addTransaction(group: Group, transaction: Transaction) {
        if (transaction.id.isBlank()) {
            transaction.payer.uid = uid.value!!
            transaction.id = database.group(group.id).newTransactionId!!
        }
        database.group(group.id).transaction(transaction.id).apply {
            date.setValue(transaction.date)
            amount.setValue(transaction.amount)
            payer.setValue(transaction.payer.uid)
            comment.setValue(transaction.comment)
            transaction.impact.forEach {
                impactOn(it.member.uid).setValue(it.percentage)
            }
        }

    }

    fun getTransactions(group: Group) {
        database.group(group.id).transactions.observeValue({
            when {
                it.exists() -> default {
                    transactions.postValue(mutableListOf<Transaction>().apply {
                        for (transaction in it.children) Try {
                            val temp = Transaction(
                                    id = transaction.key!!,
                                    date = transaction.date,
                                    amount = transaction.amount,
                                    comment = transaction.comment,
                                    payer = Member(transaction.payer)
                            )
                            this += temp
                        }
                    })
                }
                else -> transactions.value = emptyList()
            }
        }, { transactions.value = emptyList() })
    }

    fun deleteTransaction(group: Group, transaction: Transaction) {
        database.group(group.id).transaction(transaction.id).removeValue()
    }

    fun getDues(group: Group) {
        database.group(group.id).transactions.observeValue({
            when {
                it.exists() -> default {
                    dues.postValue(mutableListOf<Due>().apply {
                        for (transaction in it.children) {
                            for (impact in transaction.impact) {
                                if (transaction.payer != impact.key) Try {
                                    val exists = find { it.creditor.uid == transaction.payer && it.debtor.uid == impact.key }
                                    val existsOtherwise = find { it.debtor.uid == transaction.payer && it.creditor.uid == impact.key }
                                    when {
                                        exists != null -> exists.amount += transaction.amount * impact.value as Long / 100
                                        existsOtherwise != null -> existsOtherwise.amount -= transaction.amount * impact.value as Long / 100
                                        else -> this += Due(
                                                amount = transaction.amount * impact.value as Long / 100,
                                                debtor = Member(impact.key!!),
                                                creditor = Member(transaction.payer)
                                        )
                                    }
                                }
                            }
                        }
                    })
                }
                else -> dues.value = emptyList()
            }
        }, { dues.value = emptyList() })
    }

    fun getShares(group: Group, transaction: Transaction) {
        database.group(group.id).currentMembers.observeValue({
            when {
                it.exists() -> default {
                    transaction.impact = mutableListOf<Share>().apply {
                        for (share in it.children) Try {
                            this += Share(member = Member(share.key!!))
                        }
                        database.group(group.id).transaction(transaction.id).observeValue({
                            for (share in it.impact) Try {
                                val uid = share.key!!
                                val percentage = (share.value as Long).toInt()
                                val exists = this.find { it.member.uid == uid }
                                if (exists != null) this.removeAt(this.indexOf(exists))
                                this += Share(
                                        member = Member(uid),
                                        percentage = percentage,
                                        amount = transaction.amount * percentage / 100
                                )
                            }
                        })
                    }
                    this.transaction.postValue(transaction)
                }
                else -> this.transaction.value?.impact = emptyList()
            }
        }, { this.transaction.value?.impact = emptyList() })
    }

    fun getMembers(group: Group) {
        database.group(group.id).currentMembers.observeValue({
            when {
                it.exists() -> default {
                    members.postValue(mutableListOf<Member>().apply {
                        for (member in it.children) Try {
                            this += Member(
                                    uid = member.key as String,
                                    date = member.joiningDate,
                                    isAdmin = member.isAdmin
                            )
                        }
                    })
                }
                else -> members.value = emptyList()
            }
        }, { members.value = emptyList() })
    }

    fun makeAdmin(group: Group, member: Member) {
        database.group(group.id).currentMember(member.uid).admin.setValue(true)
    }

    fun leave(group: Group) = remove(group, Member(uid.value!!))

    fun remove(group: Group, member: Member) {
        database.user(member.uid).devices.getValue({
            if (it.exists()) default {
                for (device in it.children) {
                    notify(Payload(
                            to = device.value as String,
                            data = group
                    ))
                }
            }
        })
    }

    fun invite(group: Group, member: Member) {
        // TODO Check for existing member
        // Current structure doesn't allows us to at all
        database.email(member.email).getValue({
            if (it.exists()) {
                it.children.first().default {
                    for (device in devices) Try {
                        notify(Payload(
                                to = device.value as String,
                                data = Invitation(
                                        id = group.id,
                                        uid = uid.value!!,
                                        date = currentDate
                                )
                        ))
                    }
                }
            } else Events.publish(member)
        })
    }

    fun getInvitations() {
        database.user(uid.value!!).invitations.observeValue({
            when {
                it.exists() -> default {
                    invitations.postValue(mutableListOf<Invitation>().apply {
                        for (invitation in it.children) {
                            this += Invitation(
                                    id = invitation.key!!,
                                    date = invitation.date,
                                    uid = invitation.inviter
                            )
                        }
                    })
                }
                else -> invitations.value = emptyList()
            }
        }, { invitations.value = emptyList() })
    }

    private fun notify(payload: Payload) {
        disposeOnCleared(notificationsRepository.notify(payload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {}))
    }
}