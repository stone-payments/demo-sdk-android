package br.com.stonesdk.sdkdemo.model;

import org.jetbrains.annotations.NotNull;

import stone.application.enums.StoneTextAlignment;
import stone.application.enums.StoneTextSize;
import stone.application.interfaces.StoneCustomizedText;

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
