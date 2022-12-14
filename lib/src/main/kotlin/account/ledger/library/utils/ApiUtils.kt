package accountLedgerCli.utils

import account.ledger.library.api.response.AccountsResponse
import accountLedgerCli.constants.Constants
import accountLedgerCli.retrofit.data.AccountsDataSource
import kotlinx.coroutines.runBlocking
import accountLedgerCli.to_utils.ApiUtils as CommonApiUtils

internal object ApiUtils {

    @JvmStatic
    internal fun getAccountsFull(
        userId: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean
    ): Result<AccountsResponse> {

        //TODO : Change return to AccountsResponse instead of Result<AccountsResponse>
        return CommonApiUtils.getResultFromApiRequestWithOptionalRetries(

            apiCallFunction = fun(): Result<AccountsResponse> {

                return runBlocking {

                    AccountsDataSource().selectUserAccountsFull(userId = userId)
                }
            },
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    @JvmStatic
    internal fun isNoTransactionsResponseWithMessage(

        responseStatus: UInt,
        noDataBeforeMessageActions: () -> Unit = fun() {}

    ): Boolean {

        return CommonApiUtils.isNoDataResponseWithMessageIncludingBeforeMessageActionsAnd1AsIndicator(

            responseStatus = responseStatus,
            noDataMessageBeforeActions = noDataBeforeMessageActions,
            itemSpecification = Constants.transactionText
        )
    }

    internal fun isNotNoTransactionsResponseWithMessage(

        responseStatus: UInt,
        noDataBeforeMessageActions: () -> Unit = fun() {}

    ): Boolean = !isNoTransactionsResponseWithMessage(

        responseStatus = responseStatus,
        noDataBeforeMessageActions = noDataBeforeMessageActions
    )
}
