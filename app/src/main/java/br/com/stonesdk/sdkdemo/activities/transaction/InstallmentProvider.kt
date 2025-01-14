package br.com.stonesdk.sdkdemo.activities.transaction

import co.stone.posmobile.sdk.payment.domain.model.InstallmentSetup
import stone.application.enums.InstalmentTransactionEnum
import stone.application.enums.TypeOfTransactionEnum

class InstallmentProvider {
    fun getInstallment(transaction: TypeOfTransactionEnum): List<InstallmentSetup> {
        return if (transaction == TypeOfTransactionEnum.CREDIT){
           listOf(
               InstalmentTransactionEnum.ONE_INSTALMENT,
               InstalmentTransactionEnum.TWO_INSTALMENT_NO_INTEREST,
               InstalmentTransactionEnum.THREE_INSTALMENT_NO_INTEREST,
               InstalmentTransactionEnum.FOUR_INSTALMENT_NO_INTEREST,
               InstalmentTransactionEnum.FIVE_INSTALMENT_NO_INTEREST,
               InstalmentTransactionEnum.SIX_INSTALMENT_NO_INTEREST,
               InstalmentTransactionEnum.SEVEN_INSTALMENT_NO_INTEREST,
               InstalmentTransactionEnum.EIGHT_INSTALMENT_NO_INTEREST,
               InstalmentTransactionEnum.NINE_INSTALMENT_NO_INTEREST,
           )
        } else {
            emptyList()
        }
    }
}