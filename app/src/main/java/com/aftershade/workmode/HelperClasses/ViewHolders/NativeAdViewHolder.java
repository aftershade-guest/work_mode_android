package com.aftershade.workmode.HelperClasses.ViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.R;
import com.google.android.gms.ads.nativead.NativeAdView;

public class NativeAdViewHolder extends RecyclerView.ViewHolder {

    private NativeAdView nativeAdView;

    public NativeAdView getNativeAdView() {
        return nativeAdView;
    }

    public NativeAdViewHolder(@NonNull View itemView) {
        super(itemView);

        nativeAdView = (NativeAdView) itemView.findViewById(R.id.native_ad_view);

        nativeAdView.setMediaView(nativeAdView.findViewById(R.id.ad_media));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
        nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
        nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));

    }


}
