package br.com.stonesdk.sdkdemo.activities.transaction

import co.stone.posmobile.sdk.payment.domain.model.InstallmentTransaction

class InstallmentProvider {

    fun getInstallment(
        transactionType : TypeOfTransactionEnum,
        merchantInterest : Boolean = MERCHANT_INTEREST,
        maxInstallments : Int = MAX_INSTALLMENTS
    ): List<InstallmentTransaction> {

        val installments = mutableListOf<InstallmentTransaction>()

        when (transactionType) {
            TypeOfTransactionEnum.CREDIT -> {
                repeat(maxInstallments){ index ->
                    val installment = if(merchantInterest)
                        InstallmentTransaction.Merchant(index + 1)
                    else
                        InstallmentTransaction.Issuer(index + 1)
                    installments.add(installment)
                }
            }
            TypeOfTransactionEnum.DEBIT,
            TypeOfTransactionEnum.VOUCHER,
            TypeOfTransactionEnum.PIX -> {
                val installment = InstallmentTransaction.None()
                installments.add(installment)
            }
        }

        return installments
    }

    companion object{
        private const val MAX_INSTALLMENTS = 12
        private const val MERCHANT_INTEREST = true
    }

}