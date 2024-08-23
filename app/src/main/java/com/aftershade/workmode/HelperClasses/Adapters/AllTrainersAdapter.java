package com.aftershade.workmode.HelperClasses.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Listeners.TrainersClickListener;
import com.aftershade.workmode.HelperClasses.Models.Instructor;
import com.aftershade.workmode.HelperClasses.ViewHolders.TrainerNativeAdHolder;
import com.aftershade.workmode.HelperClasses.ViewHolders.TrainersViewHolder;
import com.aftershade.workmode.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.AdChoicesView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT;

public class AllTrainersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Instructor> trainerHelperArray;
    ArrayList<Object> allTrainerItems;
    DatabaseReference allTrainersRef;
    TrainersClickListener trainersClickListener;

    AdLoader adLoader;
    ArrayList<NativeAd> nativeAdList = new ArrayList<>();

    private static final int TRAINER_VIEW_TYPE = 0;
    private static final int NATIVE_AD_VIEW_TYPE = 1;

    public AllTrainersAdapter(Context context, ArrayList<Instructor> trainerHelperArray, TrainersClickListener trainersClickListener) {
        this.context = context;
        allTrainerItems = new ArrayList<>();
        this.trainerHelperArray = trainerHelperArray;
        //set click listener
        this.trainersClickListener = trainersClickListener;
        //add all items from trainer array
        allTrainerItems.addAll(trainerHelperArray);
        //set databse reference
        allTrainersRef = FirebaseDatabase.getInstance().getReference().child("Trainers");

        //loads ads
        loadAds();
    }

    @Override
    public int getItemViewType(int position) {
        Object recyclerItem = allTrainerItems.get(position);

        if (recyclerItem instanceof NativeAd) {
            return NATIVE_AD_VIEW_TYPE;
        }

        return TRAINER_VIEW_TYPE;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case NATIVE_AD_VIEW_TYPE:
                View nativeAdView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.native_ad_trainers_card, parent, false);

                return new TrainerNativeAdHolder(nativeAdView);

            case TRAINER_VIEW_TYPE:
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainers_card,
                        parent, false);

                return new TrainersViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {
            case NATIVE_AD_VIEW_TYPE:
                NativeAd ad = (NativeAd) allTrainerItems.get(position);
                populateNativeAd(ad, ((TrainerNativeAdHolder) holder).getNativeAd());
                break;
            case TRAINER_VIEW_TYPE:
                //fall through
            default:

                TrainersViewHolder trainersHolder = (TrainersViewHolder) holder;

                Instructor instrutorSuper = (Instructor) allTrainerItems.get(position);

                trainersHolder.fullName.setText(instrutorSuper.getFullName());
                trainersHolder.rating.setRating(0);
                trainersHolder.type.setText(instrutorSuper.getTrainerType());

                allTrainersRef.child(instrutorSuper.getUid()).child("Images").child("profilePhoto")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    Picasso.get()
                                            .load(snapshot.getValue(String.class))
                                            .placeholder(R.drawable.image_placeholder_icon)
                                            .error(R.drawable.image_error_icon)
                                            .into(trainersHolder.profilePic);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                ErrorCatching errorCatching = new ErrorCatching(context);
                                errorCatching.onError(error);
                            }
                        });

                trainersHolder.itemView.setOnClickListener(v -> trainersClickListener.onClick(instrutorSuper));
        }

    }

    private void populateNativeAd(NativeAd ad, NativeAdView nativeAdView) {

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) nativeAdView.getHeadlineView()).setText(ad.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = ad.getIcon();

        if (ad.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) nativeAdView.getCallToActionView()).setText(ad.getCallToAction());
        }

        if (icon == null) {
            nativeAdView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(icon.getDrawable());
            nativeAdView.getIconView().setVisibility(View.VISIBLE);
        }

        if (ad.getStarRating() == null) {
            nativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
            ((RatingBar) nativeAdView.getStarRatingView()).setRating(ad.getStarRating().floatValue());
        }

        if (ad.getAdvertiser() == null) {
            nativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getAdvertiserView()).setText(ad.getAdvertiser());
        }

        if (ad.getAdChoicesInfo() != null) {
            AdChoicesView adChoicesView = new AdChoicesView(context);
            nativeAdView.setAdChoicesView(adChoicesView);
        }

        nativeAdView.setNativeAd(ad);

    }

    @Override
    public int getItemCount() {
        return allTrainerItems.size();
    }

    private void loadAds() {

        AdLoader.Builder builder = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110");
        adLoader = builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                nativeAdList.add(nativeAd);

                if (!adLoader.isLoading()) {
                    insertAdsIntoList();
                }

            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (!adLoader.isLoading()) {
                    insertAdsIntoList();
                }
            }
        }).withNativeAdOptions(new NativeAdOptions.Builder()
                .setRequestCustomMuteThisAd(true)
                .setAdChoicesPlacement(ADCHOICES_TOP_RIGHT)
                .build())
                .build();

        adLoader.loadAds(new AdRequest.Builder().build(), allTrainerItems.size() / 2);

    }

    private void insertAdsIntoList() {
        int count = 1;
        int index = 2;

        int items = allTrainerItems.size() / 2;

        if (nativeAdList.size() != 0) {
            for (int i = 0; i < nativeAdList.size(); i++) {
                count++;
                NativeAd ad = nativeAdList.get(i);

                if (allTrainerItems.size() < 2) {
                    allTrainerItems.add(ad);
                } else {
                    allTrainerItems.add(index, ad);
                    index += 3;

                }

                if (count >= items) {
                    break;
                }

            }
            notifyDataSetChanged();
        }

    }

}
