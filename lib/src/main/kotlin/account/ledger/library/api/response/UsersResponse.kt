package account.ledger.library.api.response

internal data class UsersResponse(

    internal val status: UInt,
    internal val users: List<UserResponse>
)