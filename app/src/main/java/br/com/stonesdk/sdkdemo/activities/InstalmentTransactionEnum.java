package br.com.stonesdk.sdkdemo.activities;

/**
 * Created by JGabrielFreitas on 05/08/2015 - 16:22.
 */
public enum InstalmentTransactionEnum {

    ONE_INSTALMENT(1, false),

    // without interest first (merchant)
    TWO_INSTALMENT_NO_INTEREST(2, false),
    THREE_INSTALMENT_NO_INTEREST(3, false),
    FOUR_INSTALMENT_NO_INTEREST(4, false),
    FIVE_INSTALMENT_NO_INTEREST(5, false),
    SIX_INSTALMENT_NO_INTEREST(6, false),
    SEVEN_INSTALMENT_NO_INTEREST(7, false),
    EIGHT_INSTALMENT_NO_INTEREST(8, false),
    NINE_INSTALMENT_NO_INTEREST(9, false),
    TEN_INSTALMENT_NO_INTEREST(10, false),
    ELEVEN_INSTALMENT_NO_INTEREST(11, false),
    TWELVE_INSTALMENT_NO_INTEREST(12, false),

    // with interest after (issuer)
    TWO_INSTALMENT_WITH_INTEREST(2, true),
    THREE_INSTALMENT_WITH_INTEREST(3, true),
    FOUR_INSTALMENT_WITH_INTEREST(4, true),
    FIVE_INSTALMENT_WITH_INTEREST(5, true),
    SIX_INSTALMENT_WITH_INTEREST(6, true),
    SEVEN_INSTALMENT_WITH_INTEREST(7, true),
    EIGHT_INSTALMENT_WITH_INTEREST(8, true),
    NINE_INSTALMENT_WITH_INTEREST(9, true),
    TEN_INSTALMENT_WITH_INTEREST(10, true),
    ELEVEN_INSTALMENT_WITH_INTEREST(11, true),
    TWELVE_INSTALMENT_WITH_INTEREST(12, true);

    public int count = 1;

    /**
     *
     * @deprecated Typo in this attribute name (insterest), use interest instead
     */
    @Deprecated
    public boolean insterest = this.interest;

    public boolean interest = false;

    InstalmentTransactionEnum(int count, boolean interest) {
        this.count = count;
        this.interest = interest;
    }

    InstalmentTransactionEnum() {
    }

    public static InstalmentTransactionEnum getAt(int index) {
        return values()[index];
    }

}