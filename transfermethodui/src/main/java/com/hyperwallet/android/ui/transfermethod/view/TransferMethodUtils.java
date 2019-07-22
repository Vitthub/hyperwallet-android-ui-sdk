/*
 * The MIT License (MIT)
 * Copyright (c) 2019 Hyperwallet Systems Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hyperwallet.android.ui.transfermethod.view;

import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodFields.BANK_ACCOUNT_ID;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodFields.CARD_NUMBER;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodFields.EMAIL;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodFields.TYPE;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodTypes.BANK_ACCOUNT;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodTypes.BANK_CARD;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodTypes.PAPER_CHECK;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodTypes.PAYPAL_ACCOUNT;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodTypes.PREPAID_CARD;
import static com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodTypes.WIRE_ACCOUNT;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod;
import com.hyperwallet.android.model.transfermethod.HyperwalletTransferMethod.TransferMethodType;
import com.hyperwallet.android.ui.R;

import java.util.Locale;

public class TransferMethodUtils {

    private static final int LAST_FOUR_DIGIT = 4;

    /**
     * Get string resource by TransferMethodType
     *
     * @param context Context
     * @param type    TransferMethodType
     * @return String by the defined resource
     * @throws Resources.NotFoundException if the given ID does not exist.
     */
    public static String getStringResourceByName(@NonNull final Context context,
            @NonNull @TransferMethodType final String type) {
        int resId = context.getResources().getIdentifier(type.toLowerCase(Locale.ROOT), "string",
                context.getPackageName());
        if (resId == 0) {
            resId = context.getResources().getIdentifier(BANK_ACCOUNT.toLowerCase(Locale.ROOT), "string",
                    context.getPackageName());
        }
        return context.getString(resId);
    }

    public static String getStringFontIcon(@NonNull final Context context,
            @NonNull @TransferMethodType final String type) {
        int resId = context.getResources().getIdentifier(type.toLowerCase(Locale.ROOT) + "_font_icon", "string",
                context.getPackageName());
        if (resId == 0) {
            resId = context.getResources().getIdentifier(BANK_ACCOUNT.toLowerCase(Locale.ROOT) + "_font_icon",
                    "string",
                    context.getPackageName());
        }

        return context.getString(resId);
    }

    /**
     * Get title by the {@link HyperwalletTransferMethod.TransferMethodFields#TYPE}
     *
     * @param context        {@link Context}
     * @param transferMethod {@link HyperwalletTransferMethod}
     * @return title or null if a TYPE doesn't match any defined string resources
     */
    @NonNull
    public static String getTransferMethodName(@NonNull final Context context,
            final HyperwalletTransferMethod transferMethod) {
        String transferMethodType = transferMethod.getField(TYPE);
        if (transferMethodType == null) {
            transferMethodType = "";
        }
        return getTransferMethodName(context, transferMethodType);
    }

    /**
     * Get title by the type
     *
     * @param context            {@link Context}
     * @param transferMethodType {@link String}
     * @return title if a TYPE matches to defined {@link TransferMethodType}
     */
    @NonNull
    public static String getTransferMethodName(@NonNull final Context context,
            @NonNull @TransferMethodType final String transferMethodType) {

        String title;

        switch (transferMethodType) {
            case BANK_ACCOUNT:
                title = context.getString(R.string.bank_account);
                break;
            case BANK_CARD:
                title = context.getString(R.string.bank_card);
                break;
            case PAPER_CHECK:
                title = context.getString(R.string.paper_check);
                break;
            case PREPAID_CARD:
                title = context.getString(R.string.prepaid_card);
                break;
            case WIRE_ACCOUNT:
                title = context.getString(R.string.wire_account);
                break;
            case PAYPAL_ACCOUNT:
                title = context.getString(R.string.paypal_account);
                break;
            default:
                title = transferMethodType.toLowerCase(Locale.ROOT) + context.getString(
                        R.string.not_translated_in_braces);
        }

        return title;
    }

    /**
     * Gets Transfer method identifier from the {@link HyperwalletTransferMethod} field
     * by a {@link TransferMethodType}.
     *
     * @param context        Context
     * @param transferMethod HyperwalletTransferMethod
     * @param type           TransferMethodType
     */
    public static String getTransferMethodDetail(@NonNull Context context,
            @NonNull final HyperwalletTransferMethod transferMethod,
            @Nullable @TransferMethodType final String type) {
        if (type == null) {
            return "";
        }

        switch (type) {
            case BANK_CARD:
            case PREPAID_CARD:
                return getFourDigitsIdentification(context,
                        transferMethod,
                        CARD_NUMBER,
                        R.string.transfer_method_list_item_description);
            case BANK_ACCOUNT:
            case WIRE_ACCOUNT:
                return getFourDigitsIdentification(context, transferMethod, BANK_ACCOUNT_ID,
                        R.string.transfer_method_list_item_description);
            case PAYPAL_ACCOUNT:
                final String transferIdentification = transferMethod.getField(EMAIL);
                return transferIdentification != null ? transferIdentification : "";
            default:
                return "";
        }
    }

    private static String getFourDigitsIdentification(@NonNull final Context context,
            @NonNull final HyperwalletTransferMethod transferMethod,
            @NonNull @HyperwalletTransferMethod.TransferMethodFieldKey final String fieldKey,
            @StringRes final int stringResId) {
        final String transferIdentification = transferMethod.getField(fieldKey);

        final String identificationText =
                transferIdentification != null && transferIdentification.length() > LAST_FOUR_DIGIT
                        ? transferIdentification.substring(transferIdentification.length() - LAST_FOUR_DIGIT)
                        : "";

        return context.getString(stringResId, identificationText);
    }
}