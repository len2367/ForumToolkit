package com.signatures;

import androidx.annotation.Nullable;

import com.signatures.models.responses.ServerResponse;
import com.signatures.models.responses.errors.ServerErrorResponse;

public interface UICallback {
    void onFailure(@Nullable ServerErrorResponse serverErrorResponse);

    void onSuccess(@Nullable ServerResponse serverResponse);
}
