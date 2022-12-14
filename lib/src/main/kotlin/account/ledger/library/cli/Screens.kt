package accountLedgerCli.cli

import account.ledger.library.api.response.AccountResponse
import account.ledger.library.api.response.TransactionsResponse
import account.ledger.library.cli.App
import account.ledger.library.cli.checkAffectedAccountsAfterSpecifiedDate
import account.ledger.library.cli.viewChildAccounts
import account.ledger.library.cli.App.Companion.commandLinePrintMenuWithEnterPrompt
import accountLedgerCli.constants.Constants
import accountLedgerCli.enums.BalanceSheetRefineLevelEnum
import accountLedgerCli.enums.EnvironmentFileEntryEnum
import accountLedgerCli.enums.FunctionCallSourceEnum
import accountLedgerCli.enums.TransactionTypeEnum
import accountLedgerCli.models.AccountFrequencyModel
import accountLedgerCli.models.FrequencyOfAccountsModel
import accountLedgerCli.models.InsertTransactionResult
import accountLedgerCli.models.UserModel
import accountLedgerCli.retrofit.data.TransactionsDataSource
import accountLedgerCli.to_models.IsOkModel
import accountLedgerCli.to_utils.*
import accountLedgerCli.utils.AccountUtils
import accountLedgerCli.utils.ApiUtils
import kotlinx.coroutines.runBlocking
import accountLedgerCli.to_utils.ApiUtils as CommonApiUtils
import accountLedgerCli.to_utils.HandleResponses as CommonHandleResponses

object Screens {

    internal fun userScreen(

        username: String,
        userId: UInt,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult {

        if (isDevelopmentMode) {

            // TODO : dotenv into parameters
            println("Env. Variables : ${App.dotenv.entries()}")
        }
        var insertTransactionResult = InsertTransactionResult(

            isSuccess = false,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount
        )
        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(
                listOf(
                    "\nUser : $username",
                    AccountUtils.getFrequentlyUsedTop10Accounts(userId = userId),
                    "1 - List Accounts : Top Levels",
                    "2 - ${getQuickTransactionOnWalletText()}",
                    "3 - ${getQuickTransactionOnWalletToFrequent1Text()}",
                    "4 - ${getQuickTransactionOnWalletToFrequent2Text()}",
                    "5 - ${getQuickTransactionOnWalletToFrequent3Text()}",
                    "6 - ${getQuickTransactionOnBankText()}",
                    "7 - ${getQuickTransactionOnBankToFrequent1Text()}",
                    "8 - ${getQuickTransactionOnBankToFrequent2Text()}",
                    "9 - ${getQuickTransactionOnBankToFrequent3Text()}",
                    "10 - ${getQuickTransactionOnFrequent1Text()}",
                    "11 - ${getQuickTransactionOnFrequent2Text()}",
                    "12 - ${getQuickTransactionOnFrequent3Text()}",
                    "13 - List Accounts : Full Names",
                    "14 - Import Transactions To : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } From CSV",
                    "15 - Import Transactions To : Bank : ${
                        getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
                    } From XLX",
                    "16 - Check Affected A/Cs : After A Specified Date",
                    "17 - View Transactions of a Specific A/C",
                    "18 - View Balance Sheet Ledger (All)",
                    "19 - View Balance Sheet Ledger (Excluding Open Balances)",
                    "20 - View Balance Sheet Ledger (Excluding Open Balances & Misc. Incomes)",
                    "21 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes & Investment Returns)",
                    "22 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes, Investment Returns & Family Accounts)",
                    "23 - View Balance Sheet Ledger (Excluding Open Balances, Misc. Incomes, Investment Returns, Family & Expense Accounts)",
                    "24 - View Transactions of Wallet A/C",
                    "25 - View Transactions of ${getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)} A/C",
                    "26 - View Transactions of ${getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)} A/C",
                    "27 - View Transactions of ${getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)} A/C",
                    "28 - View Transactions of ${getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)} A/C",
                    "29 - Check Affected A/Cs : From First Entry",
                    "30 - Check Affected A/Cs : From Start Date",
                    "31 - View Last 10 Transactions",
                    "32 - Check Affected A/Cs : From Start Date to A Specified Date",
                    "33 - Check Affected A/Cs : From Start Date to A Specified Time Stamp",
                    "34 - Check Affected A/Cs : After A Specified Date to A Specified Date",
                    "35 - Check Affected A/Cs : After A Specified Date to A Specified Time Stamp",
                    "36 - Check Affected A/Cs : After A Specified Time Stamp to A Specified Date",
                    "37 - Check Affected A/Cs : After A Specified Time Stamp to A Specified Time Stamp",
                    "0 - Logout",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readLine()!!) {

                "1" -> {

                    insertTransactionResult = HandleResponses.handleAccountsResponseAndPrintMenu(

                        apiResponse = getAccounts(

                            userId = userId,
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        username = username,
                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "2" -> {

                    insertTransactionResult = quickTransactionOnWallet(

                        insertTransactionResult = insertTransactionResult,
                        userId = userId,
                        username = username,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "3" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "4" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "5" -> {

                    insertTransactionResult = quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "6" -> {

                    insertTransactionResult = quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "7" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "8" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "9" -> {

                    insertTransactionResult = quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "10" -> {

                    insertTransactionResult = quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "11" -> {

                    insertTransactionResult = quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "12" -> {

                    insertTransactionResult = quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "13" -> {

                    insertTransactionResult = HandleResponses.handleAccountsResponseAndPrintMenu(

                        apiResponse = ApiUtils.getAccountsFull(

                            userId = userId,
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode
                        ),
                        username = username,
                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "14", "15", "29", "32", "34", "35", "36", "37" -> {

                    ToDoUtils.showTodo()
                }

                "16" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = InputUtils.getValidDateInNormalPattern(),
                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "17" -> {

                    TransactionViews.viewTransactionsOfInputAccount(

                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "18" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.ALL,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "19" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_OPEN_BALANCES,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "20" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_MISC_INCOMES,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "21" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_INVESTMENT_RETURNS,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "22" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_FAMILY_ACCOUNTS,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "23" -> {

                    printBalanceSheetOfUser(

                        currentUserName = username,
                        currentUserId = userId,
                        refineLevel = BalanceSheetRefineLevelEnum.WITHOUT_EXPENSE_ACCOUNTS,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "24" -> {

                    // TODO : Check for env. variable availability
                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.walletAccount.value!!,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "25" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.bankAccount.value!!,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "26" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.frequent1Account.value!!,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "27" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.frequent2Account.value!!,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "28" -> {

                    insertTransactionResult = viewTransactionsOfAnAccountIndex(

                        userId = userId,
                        insertTransactionResult = insertTransactionResult,
                        username = username,
                        desiredAccountIndex = InsertOperations.frequent3Account.value!!,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "30" -> {

                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = getUserInitialTransactionDateFromUsername(username = username).minusDays(1)
                            .format(DateTimeUtils.normalDatePattern),
                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "31" -> {

                    val getTransactionsResult: IsOkModel<TransactionsResponse> =
                        CommonApiUtils.makeApiRequestWithOptionalRetries(
                            apiCallFunction = fun(): Result<TransactionsResponse> {

                                return runBlocking {

                                    TransactionsDataSource().selectTransactions(userId = userId)
                                }
                            },
                            isConsoleMode = isConsoleMode,
                            isDevelopmentMode = isDevelopmentMode
                        )

                    CommonHandleResponses.isOkModelHandler(

                        isOkModel = getTransactionsResult,
                        data = Unit,
                        successActions = fun() {

                            insertTransactionResult = TransactionViews.viewTransactions(

                                userTransactionsResponse = getTransactionsResult.data!!,
                                accountFullName = "Last 10",
                                dateTimeInText = insertTransactionResult.dateTimeInText,
                                transactionParticulars = insertTransactionResult.transactionParticulars,
                                transactionAmount = insertTransactionResult.transactionAmount,
                                fromAccount = insertTransactionResult.fromAccount,
                                viaAccount = insertTransactionResult.viaAccount,
                                toAccount = insertTransactionResult.toAccount,
                                username = username,
                                accountId = 0u,
                                functionCallSource = FunctionCallSourceEnum.FROM_VIEW_TRANSACTIONS_OF_AN_ACCOUNT,
                                userId = userId,
                                isConsoleMode = isConsoleMode,
                                isDevelopmentMode = isDevelopmentMode
                            ).addTransactionResult
                        })
                }

                "33" -> {

                    val toTimeStamp: String = InputUtils.getValidDateTimeInNormalPattern(promptPrefix = "Up to ")
                    insertTransactionResult = checkAffectedAccountsAfterSpecifiedDate(

                        desiredDate = getUserInitialTransactionDateFromUsername(username = username).minusDays(1)
                            .format(DateTimeUtils.normalDatePattern),
                        userId = userId,
                        username = username,
                        insertTransactionResult = insertTransactionResult,
                        isUpToTimeStamp = true,
                        upToTimeStamp = toTimeStamp,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "0" -> {

                    return insertTransactionResult
                }

                else -> {

                    invalidOptionMessage()
                }
            }
        } while (true)
    }

    internal fun getQuickTransactionOnBankToFrequent1Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnBankToFrequent2Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnBankToFrequent3Text() =
        getQuickTransactionOnBankToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnWalletToFrequent1Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnWalletToFrequent2Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)

    internal fun getQuickTransactionOnWalletToFrequent3Text() =
        getQuickTransactionOnWalletToFrequentXText(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)

    private fun getQuickTransactionOnWalletToFrequentXText(environmentVariableName: String) =
        "${getQuickTransactionOnWalletText()} To : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = environmentVariableName)
        }"

    private fun getQuickTransactionOnBankToFrequentXText(environmentVariableName: String) =
        "${getQuickTransactionOnBankText()} To : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = environmentVariableName)
        }"

    internal fun getQuickTransactionOnBankText() = getQuickTransactionOnXText(
        itemSpecification = "Bank : ${
            getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.BANK_ACCOUNT_NAME.name)
        }"
    )

    internal fun getQuickTransactionOnFrequent1Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_1_ACCOUNT_NAME.name)
    )

    internal fun getQuickTransactionOnFrequent2Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_2_ACCOUNT_NAME.name)
    )

    internal fun getQuickTransactionOnFrequent3Text() = getQuickTransactionOnXText(
        itemSpecification = getEnvironmentVariableValueForUserScreen(environmentVariableName = EnvironmentFileEntryEnum.FREQUENT_3_ACCOUNT_NAME.name)
    )

    internal fun getQuickTransactionOnWalletText() = getQuickTransactionOnXText(itemSpecification = "Wallet")

    private fun getQuickTransactionOnXText(itemSpecification: String) =
        "Insert Quick Transaction On : $itemSpecification"

    internal fun quickTransactionOnFrequent3(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnX(

        account = InsertOperations.frequent3Account,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )


    internal fun quickTransactionOnFrequent2(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnX(

        account = InsertOperations.frequent2Account,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )


    internal fun quickTransactionOnFrequent1(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnX(

        account = InsertOperations.frequent1Account,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    internal fun quickTransactionOnBankToFrequent3(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnBankToX(

        account2 = InsertOperations.frequent3Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    internal fun quickTransactionOnBankToFrequent2(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnBankToX(

        account2 = InsertOperations.frequent2Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    internal fun quickTransactionOnBankToFrequent1(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnBankToX(

        account2 = InsertOperations.frequent1Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    internal fun quickTransactionOnBank(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnX(

        account = InsertOperations.bankAccount,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    internal fun quickTransactionOnWalletToFrequent3(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnWalletToX(

        account2 = InsertOperations.frequent3Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    internal fun quickTransactionOnWalletToFrequent2(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnWalletToX(

        account2 = InsertOperations.frequent2Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    internal fun quickTransactionOnWalletToFrequent1(

        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnWalletToX(

        account2 = InsertOperations.frequent1Account,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    private fun quickTransactionOnWalletToX(

        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnXToY(

        account1 = InsertOperations.walletAccount,
        account2 = account2,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    private fun quickTransactionOnBankToX(

        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnXToY(

        account1 = InsertOperations.bankAccount,
        account2 = account2,
        userId = userId,
        username = username,
        insertTransactionResult = insertTransactionResult,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    private fun quickTransactionOnXToY(

        account1: EnvironmentVariableForWholeNumber,
        account2: EnvironmentVariableForWholeNumber,
        userId: UInt,
        username: String,
        insertTransactionResult: InsertTransactionResult,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = InsertOperations.insertQuickTransactionFromAccount1toAccount2(

        account1 = account1,
        account2 = account2,
        userId = userId,
        username = username,
        fromAccount = insertTransactionResult.fromAccount,
        viaAccount = insertTransactionResult.viaAccount,
        toAccount = insertTransactionResult.toAccount,
        dateTimeInText = insertTransactionResult.dateTimeInText,
        transactionParticulars = insertTransactionResult.transactionParticulars,
        transactionAmount = insertTransactionResult.transactionAmount,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    internal fun quickTransactionOnWallet(

        insertTransactionResult: InsertTransactionResult,
        userId: UInt,
        username: String,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ) = quickTransactionOnX(

        account = InsertOperations.walletAccount,
        insertTransactionResult = insertTransactionResult,
        userId = userId,
        username = username,
        isConsoleMode = isConsoleMode,
        isDevelopmentMode = isDevelopmentMode
    )

    private fun quickTransactionOnX(

        account: EnvironmentVariableForWholeNumber,
        insertTransactionResult: InsertTransactionResult,
        userId: UInt,
        username: String,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult {

        return InsertOperations.openSpecifiedAccountHome(

            account = account,
            userId = userId,
            username = username,
            fromAccount = insertTransactionResult.fromAccount,
            viaAccount = insertTransactionResult.viaAccount,
            toAccount = insertTransactionResult.toAccount,
            dateTimeInText = insertTransactionResult.dateTimeInText,
            transactionParticulars = insertTransactionResult.transactionParticulars,
            transactionAmount = insertTransactionResult.transactionAmount,
            isConsoleMode = isConsoleMode,
            isDevelopmentMode = isDevelopmentMode
        )
    }

    private fun viewTransactionsOfAnAccountIndex(

        userId: UInt,
        insertTransactionResult: InsertTransactionResult,
        username: String,
        desiredAccountIndex: UInt,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult {

        var localInsertTransactionResult: InsertTransactionResult = insertTransactionResult

        val getUserAccountsMapResult: IsOkModel<LinkedHashMap<UInt, AccountResponse>> =
            HandleResponses.getUserAccountsMap(
                apiResponse = ApiUtils.getAccountsFull(

                    userId = userId,
                    isConsoleMode = isConsoleMode,
                    isDevelopmentMode = isDevelopmentMode
                )
            )

        if (getUserAccountsMapResult.isOK && getUserAccountsMapResult.data!!.containsKey(desiredAccountIndex)) {

            val selectedAccount: AccountResponse = getUserAccountsMapResult.data[desiredAccountIndex]!!
            localInsertTransactionResult = TransactionViews.viewTransactionsForAnAccount(

                userId = userId,
                username = username,
                accountId = desiredAccountIndex,
                accountFullName = selectedAccount.fullName,
                insertTransactionResult = insertTransactionResult,
                fromAccount = selectedAccount,
                isConsoleMode = isConsoleMode,
                isDevelopmentMode = isDevelopmentMode

            ).addTransactionResult
        }
        return localInsertTransactionResult
    }


    internal fun getAccountFrequenciesForUser(

        frequencyOfAccounts: FrequencyOfAccountsModel,
        userId: UInt

    ): List<AccountFrequencyModel>? {

        return frequencyOfAccounts.users.find { user: UserModel -> user.id == userId }?.accountFrequencies
    }

    private fun getEnvironmentVariableValueForUserScreen(environmentVariableName: String) =
        EnvironmentFileOperations.getEnvironmentVariableValueForTextWithDefaultValue(

            dotenv = App.dotenv,
            environmentVariableName = environmentVariableName,
            defaultValue = Constants.defaultValueForStringEnvironmentVariables
        )

    internal fun accountHome(

        userId: UInt,
        username: String,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        dateTimeInText: String,
        transactionParticulars: String,
        transactionAmount: Float,
        isConsoleMode: Boolean,
        isDevelopmentMode: Boolean

    ): InsertTransactionResult {

        var localInsertTransactionResult = InsertTransactionResult(

            isSuccess = false,
            dateTimeInText = dateTimeInText,
            transactionParticulars = transactionParticulars,
            transactionAmount = transactionAmount,
            fromAccount = fromAccount,
            viaAccount = viaAccount,
            toAccount = toAccount
        )
        do {
            commandLinePrintMenuWithEnterPrompt.printMenuWithEnterPromptFromListOfCommands(

                listOfCommands = listOf(
                    "\nUser : $username",
                    "Account - ${fromAccount.fullName}",
                    "1 - View Transactions",
                    "2 - Add Transaction",
                    "3 - View Child Accounts",
                    "4 - Add Via. Transaction",
                    "5 - Add Two Way Transaction",
                    "17 - ${getQuickTransactionOnWalletText()}",
                    "18 - ${getQuickTransactionOnWalletToFrequent1Text()}",
                    "19 - ${getQuickTransactionOnWalletToFrequent2Text()}",
                    "20 - ${getQuickTransactionOnWalletToFrequent3Text()}",
                    "21 - ${getQuickTransactionOnBankText()}",
                    "22 - ${getQuickTransactionOnBankToFrequent1Text()}",
                    "23 - ${getQuickTransactionOnBankToFrequent2Text()}",
                    "24 - ${getQuickTransactionOnBankToFrequent3Text()}",
                    "25 - ${getQuickTransactionOnFrequent1Text()}",
                    "26 - ${getQuickTransactionOnFrequent2Text()}",
                    "27 - ${getQuickTransactionOnFrequent3Text()}",
                    "0 - Back",
                    "",
                    "Enter Your Choice : "
                )
            )
            when (readLine()!!) {

                "1" -> {
                    localInsertTransactionResult = TransactionViews.viewTransactionsForAnAccount(

                        userId = userId,
                        username = username,
                        accountId = fromAccount.id,
                        accountFullName = fromAccount.fullName,
                        insertTransactionResult = localInsertTransactionResult,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode

                    ).addTransactionResult
                }

                "2" -> {
                    localInsertTransactionResult = InsertOperations.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.NORMAL,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "3" -> {
                    localInsertTransactionResult = viewChildAccounts(
                        username = username,
                        userId = userId,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "4" -> {
                    localInsertTransactionResult = InsertOperations.addTransaction(
                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.VIA,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "5" -> {
                    localInsertTransactionResult = InsertOperations.addTransaction(

                        userId = userId,
                        username = username,
                        transactionType = TransactionTypeEnum.TWO_WAY,
                        fromAccount = localInsertTransactionResult.fromAccount,
                        viaAccount = localInsertTransactionResult.viaAccount,
                        toAccount = localInsertTransactionResult.toAccount,
                        dateTimeInText = localInsertTransactionResult.dateTimeInText,
                        transactionParticulars = localInsertTransactionResult.transactionParticulars,
                        transactionAmount = localInsertTransactionResult.transactionAmount,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "17" -> {

                    localInsertTransactionResult = quickTransactionOnWallet(

                        insertTransactionResult = localInsertTransactionResult,
                        userId = userId,
                        username = username,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "18" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "19" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "20" -> {

                    localInsertTransactionResult = quickTransactionOnWalletToFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "21" -> {

                    localInsertTransactionResult = quickTransactionOnBank(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "22" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "23" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "24" -> {

                    localInsertTransactionResult = quickTransactionOnBankToFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "25" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent1(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "26" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent2(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "27" -> {

                    localInsertTransactionResult = quickTransactionOnFrequent3(

                        userId = userId,
                        username = username,
                        insertTransactionResult = localInsertTransactionResult,
                        isConsoleMode = isConsoleMode,
                        isDevelopmentMode = isDevelopmentMode
                    )
                }

                "0" -> {

                    return localInsertTransactionResult
                }

                else -> {
                    invalidOptionMessage()
                }
            }
        } while (true)
    }

    @JvmStatic
    internal fun getUserWithCurrentAccountSelectionsAsText(

        username: String,
        fromAccount: AccountResponse,
        viaAccount: AccountResponse,
        toAccount: AccountResponse,
        transactionType: TransactionTypeEnum,
        userId: UInt

    ): List<String> {

        var menuItems: List<String> = listOf(
            "\nUser : $username",
            "Transaction Type : ${
                EnumUtils.getEnumNameForPrint(localEnum = transactionType)
            }",
            "From Account - ${fromAccount.id} : ${fromAccount.fullName}"
        )
        if (transactionType == TransactionTypeEnum.VIA) {
            menuItems = menuItems + listOf("Via. Account - ${viaAccount.id} : ${viaAccount.fullName}")
        }
        menuItems = menuItems + listOf(
            "To Account - ${toAccount.id} : ${toAccount.fullName}",
            AccountUtils.getFrequentlyUsedTop40Accounts(userId = userId)
        )
        return menuItems
    }
}
