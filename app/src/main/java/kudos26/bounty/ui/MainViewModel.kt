package kudos26.bounty.ui

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kudos26.bounty.core.ViewModel
import kudos26.bounty.source.model.*
import kudos26.bounty.source.repository.FirebaseCloudMessagingRepository
import kudos26.bounty.utils.getCurrentDate
import kudos26.bounty.utils.toDisplayDate

/**
 * Created by kunal on 06-02-2020.
 */

class MainViewModel(
        firebaseAuth: FirebaseAuth,
        firebaseDatabase: FirebaseDatabase,
        val firebaseCloudMessagingRepository: FirebaseCloudMessagingRepository
) : ViewModel() {

    private val databaseReference = firebaseDatabase.reference
    val uid = MutableLiveData<String>(firebaseAuth.currentUser?.uid)
    val email = MutableLiveData<String>(firebaseAuth.currentUser?.email)
    val displayName = MutableLiveData<String>(firebaseAuth.currentUser?.displayName)

    private val groupsCount = MutableLiveData<Int?>(null)
    val groups = MutableLiveData<List<Group>>(emptyList())
    val transactions = MutableLiveData<List<Transaction>>(emptyList())
    val shares = MutableLiveData<Map<Share, Ratio>>(emptyMap())
    val transaction = MutableLiveData<Transaction>(Transaction())
    val members = MutableLiveData<List<Member>>(emptyList())

    fun getNumberOfGroups() {
        databaseReference.apply {
            child("groups").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    groupsCount.value = if (dataSnapshot.exists()) {
                        (dataSnapshot.value as List<*>).size
                    } else {
                        0
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    groupsCount.value = null
                }
            })
        }
    }

    fun createGroup(name: String) {
        groupsCount.value?.toString()?.let { index ->
            databaseReference.apply {
                child("groups").child(index).child("created").apply {
                    child("on").setValue(getCurrentDate())
                    child("by").setValue(uid.value)
                }
                child("groups").child(index).child("name").setValue(name)
                child("groups").child(index).child("members").child(uid.value!!).apply {
                    child("name").setValue(displayName.value)
                    child("doj").setValue(getCurrentDate())
                    child("admin").setValue(true)
                }
                child("users").child(uid.value!!).child("groups").child(index).setValue(name)
            }
        }
    }

    fun getGroups() {
        databaseReference.apply {
            child("users").child(uid.value!!).child("groups").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    groups.value = if (dataSnapshot.exists()) {
                        val groups = mutableListOf<Group>()
                        for (group in dataSnapshot.children) {
                            groups += Group(group.key!!, group.value as String)
                        }
                        groups
                    } else {
                        emptyList()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    groups.value = emptyList()
                }
            })
        }
    }

    fun getTransactions(group: Group) {
        databaseReference.apply {
            child("groups").child(group.id).child("transactions").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    transactions.value = if (dataSnapshot.exists()) {
                        val transactions = mutableListOf<Transaction>()
                        for (transaction in dataSnapshot.children) {
                            try {
                                Transaction().let {
                                    it.id = transaction.key!!
                                    it.date = transaction.child("date").value as String
                                    it.amount = (transaction.child("amount").value as Long).toInt()
                                    it.comment = transaction.child("comment").value as String
                                    it.from = transaction.child("from").value as String
                                    for (ratio in transaction.child("to").children) {
                                        Ratio().apply {
                                            uid = ratio.key as String
                                            percentage = try {
                                                (ratio.value as Double)
                                            } catch (classCastException: ClassCastException) {
                                                (ratio.value as Long).toDouble()
                                            }
                                            it.to += this
                                        }
                                    }
                                    transactions += it
                                }
                            } catch (typeCastException: TypeCastException) {
                            }
                        }
                        transactions
                    } else {
                        emptyList()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    transactions.value = emptyList()
                }
            })
        }
    }

    fun getShares(group: Group) {
        databaseReference.apply {
            child("groups").child(group.id).child("members").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    shares.value = if (dataSnapshot.exists()) {
                        val shares = mutableMapOf<Share, Ratio>()
                        dataSnapshot.children.forEach {
                            val share = Share()
                            val ratio = Ratio()
                            share.amount = 0
                            ratio.percentage = 0.0
                            ratio.uid = it.key as String
                            share.name = it.child("name").value as String
                            shares[share] = ratio
                        }
                        shares
                    } else {
                        emptyMap()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    shares.value = emptyMap()
                }
            })
        }
    }

    fun addTransaction(group: Group) {
        databaseReference.apply {
            transaction.value?.let { transaction ->
                if (transaction.id.isEmpty()) {
                    transaction.id = transactions.value?.size.toString()
                }
                child("groups").child(group.id).child("transactions").child(transaction.id).apply {
                    child("amount").setValue(transaction.amount)
                    child("date").setValue(transaction.date)
                    child("comment").setValue(transaction.comment)
                    child("from").setValue(transaction.from)
                    transaction.to.forEach {
                        if (it.percentage != 0.0) {
                            child("to").child(it.uid).setValue(it.percentage)
                        } else {
                            child("to").child(it.uid).setValue(null)
                        }
                    }
                }
            }
        }
        Payload().apply {
            notification.body = "New Transaction Added"
            notification.title = group.name
            sendNotification(this)
        }
    }

    fun getMembers(group: Group) {
        databaseReference.apply {
            child("groups").child(group.id).child("members").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    members.value = if (dataSnapshot.exists()) {
                        val members = mutableListOf<Member>()
                        dataSnapshot.children.forEach {
                            Member().apply {
                                try {
                                    uid = it.key as String
                                    name = it.child("name").value as String
                                    admin = it.child("admin").value as Boolean
                                    doj = (it.child("doj").value as String).toDisplayDate()
                                    members += this
                                } catch (typeCastException: TypeCastException) {
                                }
                            }
                        }
                        members
                    } else {
                        emptyList()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    members.value = emptyList()
                }
            })
        }
    }

    fun addMember(group: Group, email: String) {
        databaseReference.apply {
            child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.children.count() == 1) {
                        val uid = dataSnapshot.children.elementAt(0).key!!
                        val name = dataSnapshot.children.elementAt(0).child("name").value
                        child("groups").child(group.id).child("members").child(uid).apply {
                            child("name").setValue(name)
                            child("admin").setValue(false)
                            child("doj").setValue(getCurrentDate())
                        }
                        child("users").child(uid).child("groups").child(group.id).setValue(group.name)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    private fun sendNotification(payload: Payload) {
        members.value?.forEach {
            if (it.uid != uid.value) {
                databaseReference.child("users").child(it.uid).child("token").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Payload().apply {
                            to = dataSnapshot.value as String
                            notification = payload.notification
                            disposeOnCleared(firebaseCloudMessagingRepository.sendNotification(this)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({}, {}))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
    }
}