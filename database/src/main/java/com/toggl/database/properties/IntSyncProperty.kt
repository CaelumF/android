package com.toggl.database.properties

data class IntSyncProperty(
    val current: Int,
    val backup: Int,
    val status: PropertySyncStatus
) {
    companion object {
        fun from(value: Int) = IntSyncProperty(value, value, PropertySyncStatus.InSync)
    }
}

infix fun IntSyncProperty.updateWith(value: Int): IntSyncProperty {
    if (current == value) return this
    return when (status) {
        PropertySyncStatus.Syncing,
        PropertySyncStatus.InSync -> copy(
            backup = current,
            current = value,
            status = PropertySyncStatus.SyncNeeded
        )
        PropertySyncStatus.SyncNeeded -> copy(
            current = value,
        )
    }
}
