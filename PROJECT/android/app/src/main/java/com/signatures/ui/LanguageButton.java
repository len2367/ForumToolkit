package com.signatures.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class LanguageButton extends androidx.appcompat.widget.AppCompatButton {
    private List<String> languages;
    private int iLang = 0;

    public void setLanguages(List<String> languages) {
        this.languages = languages;
        setText(languages.get(0));
    }

    public void changeLanguage() {
        iLang = (iLang + 1) % languages.size();
        setText(languages.get(iLang));
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(v -> {
            changeLanguage();
            l.onClick(v);
        });
    }

    public String getCurrentLanguage() {
        return languages.get(iLang);
    }

    public LanguageButton(@NonNull Context context) {
        super(context);
    }

    public LanguageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LanguageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
