package br.com.stonesdk.sdkdemo.model;

import org.jetbrains.annotations.NotNull;

import stone.application.enums.StoneTextAlignment;
import stone.application.enums.StoneTextSize;
import stone.application.interfaces.StoneCustomizedText;

public class CentralizedBigText implements StoneCustomizedText {
    private final String text;

    public CentralizedBigText(String text) {
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
        return StoneTextSize.BIG_16_COLUMNS;
    }

    @NotNull
    @Override
    public StoneTextAlignment getAlignment() {
        return StoneTextAlignment.CENTER;
    }

}
