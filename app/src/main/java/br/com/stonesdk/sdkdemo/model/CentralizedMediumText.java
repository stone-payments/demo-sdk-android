package br.com.stonesdk.sdkdemo.model;

import org.jetbrains.annotations.NotNull;

import br.com.stone.sdk.hardware.enums.StoneTextAlignment;
import br.com.stone.sdk.hardware.enums.StoneTextSize;
import br.com.stone.sdk.hardware.providers.interfaces.StoneCustomizedText;

public class CentralizedMediumText implements StoneCustomizedText {
    private final String text;

    public CentralizedMediumText(String text) {
        this.text = text;
    }

    @NotNull
    @Override
    public String getText() {
        return text;
    }

    @NotNull
    @Override
    public StoneTextSize getTextSize() {
        return StoneTextSize.MEDIUM_32_COLUMNS;
    }

    @NotNull
    @Override
    public StoneTextAlignment getAlignment() {
        return StoneTextAlignment.CENTER;
    }

}
