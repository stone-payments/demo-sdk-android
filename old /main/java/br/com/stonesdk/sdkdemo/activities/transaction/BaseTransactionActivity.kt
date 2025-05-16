package br.com.stonesdk.sdkdemo.activities.transaction

//import stone.application.enums.Action
//import stone.application.enums.ErrorsEnum
//import stone.application.enums.InstalmentTransactionEnum
//import stone.application.enums.TypeOfTransactionEnum
//import stone.application.interfaces.StoneActionCallback
//import stone.database.transaction.TransactionObject
//import stone.providers.BaseTransactionProvider
//import stone.user.UserModel
//import stone.utils.Stone


//abstract class BaseTransactionActivity<T : BaseTransactionProvider?> : AppCompatActivity(),
//    StoneActionCallback {
//    private lateinit var binding: ActivityTransactionBinding
//
//    protected var transactionProvider: BaseTransactionProvider? = null
//        private set
//
//    @JvmField
//    protected val transactionObject: TransactionObject = TransactionObject()
//
//    private var builder: Dialog? = null
//
//    public override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        if (FeatureFlag.composeRefactorEnabled){
//            setContent {
//                MaterialTheme {
//                    TransactionScreen()
//                }
//            }
//        } else {
//            onCreateConfig()
//        }
//    }
//
//    fun onCreateConfig(){
//        binding = ActivityTransactionBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        spinnerAction()
//        radioGroupClick()
//        binding.apply {
//            sendTransactionButton.setOnClickListener { initTransaction() }
//            cancelTransactionButton.setOnClickListener { transactionProvider?.abortPayment() }
//        }
//
//        builder = Dialog(this).apply {
//            requestWindowFeature(Window.FEATURE_NO_TITLE)
//            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        }
//    }
//
//    private fun radioGroupClick() = with(binding) {
//        transactionTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.radioPix, R.id.radioDebit, R.id.radioVoucher -> {
//                    installmentsTextView.visibility = View.GONE
//                    installmentsSpinner.visibility = View.GONE
//                }
//
//                R.id.radioCredit -> {
//                    installmentsTextView.visibility = View.VISIBLE
//                    installmentsSpinner.visibility = View.VISIBLE
//                }
//            }
//        }
//    }
//
//    private fun spinnerAction() = with(binding) {
//        val adapter = ArrayAdapter.createFromResource(
//            this@BaseTransactionActivity,
//            R.array.installments_array,
//            android.R.layout.simple_spinner_item
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        installmentsSpinner.adapter = adapter
//
//        val stoneCodeAdapter = ArrayAdapter<String>(
//            this@BaseTransactionActivity,
//            android.R.layout.simple_list_item_1, android.R.id.text1
//        )
//        stoneCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        for (userModel in Stone.sessionApplication.userModelList) {
//            stoneCodeAdapter.add(userModel.stoneCode)
//        }
//        stoneCodeSpinner.adapter = stoneCodeAdapter
//    }
//
//    private fun initTransaction() = with(binding) {
//        val installmentsEnum = InstalmentTransactionEnum.getAt(
//            installmentsSpinner.selectedItemPosition
//        )
//
//        // Informa a quantidade de parcelas.
//        transactionObject.instalmentTransaction = InstalmentTransactionEnum.getAt(
//            installmentsSpinner.selectedItemPosition
//        )
//
//        // Verifica a forma de pagamento selecionada.
//        val transactionType = when (transactionTypeRadioGroup.checkedRadioButtonId) {
//            R.id.radioCredit -> TypeOfTransactionEnum.CREDIT
//            R.id.radioDebit -> TypeOfTransactionEnum.DEBIT
//            R.id.radioVoucher -> TypeOfTransactionEnum.VOUCHER
//            R.id.radioPix -> TypeOfTransactionEnum.PIX
//            else -> TypeOfTransactionEnum.CREDIT
//        }
//        transactionObject.initiatorTransactionKey = null
//        transactionObject.typeOfTransaction = transactionType
//        transactionObject.isCapture = captureTransactionCheckBox.isChecked
//        transactionObject.amount = amountEditText.text.toString()
//
//        transactionProvider = buildTransactionProvider()
//        transactionProvider?.setConnectionCallback(this@BaseTransactionActivity)
//        transactionProvider?.execute()
//    }
//
//    protected val authorizationMessage: String?
//        get() = transactionProvider?.messageFromAuthorize
//
//    protected abstract fun buildTransactionProvider(): T
//
//    protected fun providerHasErrorEnum(errorsEnum: ErrorsEnum?): Boolean {
//        return transactionProvider?.theListHasError(errorsEnum) ?: false
//    }
//
//    override fun onError() {
//        runOnUiThread {
//            Toast.makeText(
//                this@BaseTransactionActivity,
//                "Erro: " + transactionProvider?.listOfErrors,
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//
//    override fun onStatusChanged(action: Action) {
//        runOnUiThread { binding.logTextView.append(action.name + "\n") }
//
//        if (action == Action.TRANSACTION_WAITING_QRCODE_SCAN) {
//            val imageView = ImageView(this)
//            imageView.setImageBitmap(transactionObject.qrCode)
//
//            runOnUiThread {
//                builder?.addContentView(
//                    imageView, RelativeLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                    )
//                )
//                builder?.show()
//            }
//        } else {
//            runOnUiThread { builder?.dismiss() }
//        }
//    }
//
//    protected val selectedUserModel: UserModel
//        get() = Stone.getUserModel(binding.stoneCodeSpinner.selectedItemPosition)
//}
