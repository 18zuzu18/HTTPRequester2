package db.data

enum class Status(val id: Int) {
    Open(0),
    InProgress(1),
    Complete(2),
    CompleteWithError(3),
}

fun getStatus(id: Int): Status? {
    for (value in Status.values()) {
        if (value.id == id) {
            return value
        }
    }
    return null
}
