package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;
import com.google.android.gms.ads.nativead.NativeAdView;

public class TrainerNativeAdHolder extends RecyclerView.ViewHolder {

    private final NativeAdView nativeAd;

    public NativeAdView getNativeAd() {
        return nativeAd;
    }

    public TrainerNativeAdHolder(@NonNull View itemView) {
        super(itemView);

        nativeAd = itemView.findViewById(R.id.trainer_native_ad);

        nativeAd.setHeadlineView(nativeAd.findViewById(R.id.trainer_ad_headline));
        nativeAd.setIconView(nativeAd.findViewById(R.id.trainer_ad_app_icon));
        nativeAd.setCallToActionView(nativeAd.findViewById(R.id.trainers_ad_call_to_action));
        nativeAd.setStarRatingView(nativeAd.findViewById(R.id.trainer_ad_stars));
        nativeAd.setAdvertiserView(nativeAd.findViewById(R.id.trainer_ad_advertiser));

    }
}
