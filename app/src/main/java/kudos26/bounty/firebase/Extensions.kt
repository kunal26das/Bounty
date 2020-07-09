package kudos26.bounty.firebase

import com.google.firebase.database.*

object Extensions {

    val DataSnapshot.amount: Long
        get() = try {
            child("Amount").value as Long
        } catch (e: TypeCastException) {
            0
        }

    val DataSnapshot.inviter: String
        get() = try {
            child("Inviter").value as String
        } catch (e: TypeCastException) {
            ""
        }

    val DataSnapshot.joiningDate: String
        get() = child("DOJ").value as String

    val DataSnapshot.date: String
        get() = child("Date").value as String

    val DataSnapshot.name: String
        get() = child("Name").value as String

    val DataSnapshot.payer: String
        get() = child("Payer").value as String

    val DataSnapshot.isAdmin: Boolean
        get() = child("Admin").value as Boolean

    val DataSnapshot.comment: String
        get() = child("Comment").value as String

    val DataSnapshot.impact: Iterable<DataSnapshot>
        get() = child("Impact").children

    val DataSnapshot.devices: Iterable<DataSnapshot>
        get() = child("Devices").children

    val DataSnapshot.transactions: Iterable<DataSnapshot>
        get() = child("Transactions").children

    /** Simple Paths **/
    val DatabaseReference.upi get() = child("UPI")
    val DatabaseReference.date get() = child("Date")
    val DatabaseReference.name get() = child("Name")
    val DatabaseReference.admin get() = child("Admin")
    val DatabaseReference.email get() = child("Email")
    val DatabaseReference.payer get() = child("Payer")
    val DatabaseReference.since get() = child("Since")
    val DatabaseReference.amount get() = child("Amount")
    val DatabaseReference.author get() = child("Author")
    val DatabaseReference.groups get() = child("Groups")
    val DatabaseReference.devices get() = child("Devices")
    val DatabaseReference.archive get() = child("Archive")
    val DatabaseReference.comment get() = child("Comment")
    val DatabaseReference.inviter get() = child("Inviter")
    val DatabaseReference.joiningDate get() = child("DOJ")
    val DatabaseReference.leavingDate get() = child("DOL")
    val DatabaseReference.currentMembers get() = child("Members")
    val DatabaseReference.invitations get() = child("Invitations")
    val DatabaseReference.transactions get() = child("Transactions")

    /** Complex Paths **/
    fun DatabaseReference.group(id: String) = child("Groups").child(id)
    fun DatabaseReference.user(uid: String) = child("Users").child(uid)
    fun DatabaseReference.archive(id: String) = child("Archive").child(id)
    fun DatabaseReference.impactOn(uid: String) = child("Impact").child(uid)
    fun DatabaseReference.device(uuid: String) = child("Devices").child(uuid)
    fun DatabaseReference.archivedGroup(id: String) = child("Archive").child(id)
    fun DatabaseReference.formerMember(uid: String) = child("Formers").child(uid)
    fun DatabaseReference.invitation(id: String) = child("Invitations").child(id)
    fun DatabaseReference.currentMember(uid: String) = child("Members").child(uid)
    fun DatabaseReference.pendingInvite(uid: String) = child("Pending").child(uid)
    fun DatabaseReference.transaction(id: String) = child("Transactions").child(id)
    fun DatabaseReference.email(email: String) = child("Users").orderByChild("Email").equalTo(email)

    /** Id generators **/
    val DatabaseReference.newGroupId get() = child("Groups").push().key
    val DatabaseReference.newTransactionId get() = child("Transactions").push().key

    fun DatabaseReference.getValue(
            onDataChange: ((snapshot: DataSnapshot) -> Unit),
            onCancelled: ((error: DatabaseError) -> Unit)? = null
    ) {
        addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onDataChange.invoke(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                onCancelled?.invoke(error)
            }
        })
    }

    fun Query.getValue(
            onDataChange: ((snapshot: DataSnapshot) -> Unit),
            onCancelled: ((error: DatabaseError) -> Unit)? = null
    ) {
        addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onDataChange.invoke(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                onCancelled?.invoke(error)
            }
        })
    }

    fun DatabaseReference.observeValue(
            onDataChange: ((snapshot: DataSnapshot) -> Unit),
            onCancelled: ((error: DatabaseError) -> Unit)? = null
    ) {
        addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onDataChange.invoke(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                onCancelled?.invoke(error)
            }
        })
    }

    fun Query.observeValue(
            onDataChange: ((snapshot: DataSnapshot) -> Unit),
            onCancelled: ((error: DatabaseError) -> Unit)? = null
    ) {
        addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onDataChange.invoke(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                onCancelled?.invoke(error)
            }
        })
    }
}