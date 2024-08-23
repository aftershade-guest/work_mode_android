package com.aftershade.workmode.HelperClasses.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aftershade.workmode.Databases.ErrorCatching;
import com.aftershade.workmode.HelperClasses.Listeners.ProgramClickListener;
import com.aftershade.workmode.HelperClasses.Models.ProgramsHelperClass;
import com.aftershade.workmode.HelperClasses.ViewHolders.AllProgramsHolder;
import com.aftershade.workmode.HelperClasses.ViewHolders.NativeAdViewHolder;
import com.aftershade.workmode.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
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

public class AllProgramsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PROGRAM_VIEW_TYPE = 0;
    private static final int NATIVE_AD_VIEW_TYPE = 1;

    Context context;
    ArrayList<ProgramsHelperClass> programsHelperArray;
    ArrayList<Object> allItemsList;
    DatabaseReference allProgramsRef;
    ProgramClickListener itemClick_listerner;

    AdLoader adLoader;
    ArrayList<NativeAd> nativeAdList = new ArrayList<>();

    public AllProgramsAdapter(Context context, ArrayList<ProgramsHelperClass> programsHelperClasses,
                              ProgramClickListener itemClick_listerner) {

        this.context = context;
        this.programsHelperArray = programsHelperClasses;
        allItemsList = new ArrayList<>();

        //add all items in program helper array to all items arrayu
        allItemsList.addAll(programsHelperArray);

        //set click listener
        this.itemClick_listerner = itemClick_listerner;

        //set database reference
        allProgramsRef = FirebaseDatabase.getInstance().getReference().child("Workout_Programs");

        //loads ads
        loadAds();
    }

    @Override
    public int getItemViewType(int position) {

        Object recyclerItem = allItemsList.get(position);

        if (recyclerItem instanceof NativeAd) {
            return NATIVE_AD_VIEW_TYPE;
        }

        return PROGRAM_VIEW_TYPE;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {

            case NATIVE_AD_VIEW_TYPE:
                View nativeAdView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.native_ad_layout, parent, false);

                return new NativeAdViewHolder(nativeAdView);

            case PROGRAM_VIEW_TYPE:
            default:
                View programView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_program_card,
                        parent, false);
                return new AllProgramsHolder(programView);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {
            case NATIVE_AD_VIEW_TYPE:
                NativeAd ad = (NativeAd) allItemsList.get(position);
                populateNativeAd(ad, ((NativeAdViewHolder) viewHolder).getNativeAdView());
                break;
            case PROGRAM_VIEW_TYPE:
                //fall through
            default:
                AllProgramsHolder allProgramsHolder = (AllProgramsHolder) viewHolder;
                ProgramsHelperClass helperClass = (ProgramsHelperClass) allItemsList.get(position);

                allProgramsHolder.favoriteBtn.setVisibility(View.INVISIBLE);
                allProgramsHolder.courseNameText.setText(helperClass.getCourseName());
                allProgramsHolder.ratingBar.setRating((float) helperClass.getRating());
                allProgramsHolder.descText.setText(helperClass.getDescription());
                allProgramsHolder.skillLevel.setText(helperClass.getSkillLevel());
                allProgramsHolder.duration.setText(helperClass.getDuration());
                allProgramsHolder.durationSpan.setText(helperClass.getDurationSpan());

                String key = helperClass.getCourseName() + helperClass.getTrainerId();

                allProgramsRef.child(key).child("Images").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                Picasso.get()
                                        .load(snap.getValue().toString())
                                        .placeholder(R.drawable.image_placeholder_icon)
                                        .resize(1024, 720)
                                        .error(R.drawable.image_error_icon)
                                        .into(allProgramsHolder.imageView);
                                break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ErrorCatching errorCatching = new ErrorCatching(context);
                        errorCatching.onError(error);
                    }
                });

                allProgramsHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClick_listerner.onClick((ProgramsHelperClass) allItemsList.get(position));
                    }
                });
        }

    }

    private void populateNativeAd(NativeAd ad, NativeAdView nativeAdView) {

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) nativeAdView.getHeadlineView()).setText(ad.getHeadline());
        nativeAdView.getMediaView().setMediaContent(ad.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = ad.getIcon();

        if (ad.getBody() == null) {
            nativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getBodyView()).setText(ad.getBody());
        }

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

        if (ad.getPrice() == null) {
            nativeAdView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getPriceView()).setText(ad.getPrice());
        }

        if (ad.getStore() == null) {
            nativeAdView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            nativeAdView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) nativeAdView.getStoreView()).setText(ad.getStore());
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

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        nativeAdView.setNativeAd(ad);

    }

    @Override
    public int getItemCount() {
        return allItemsList.size();
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

        adLoader.loadAds(new AdRequest.Builder().build(), allItemsList.size() / 2);

    }

    private void insertAdsIntoList() {
        int count = 1;
        int index = 2;

        int items = allItemsList.size() / 2;

        if (nativeAdList.size() != 0) {
            for (int i = 0; i < nativeAdList.size(); i++) {
                count++;
                NativeAd ad = nativeAdList.get(i);

                if (index > allItemsList.size()) {
                    allItemsList.add(ad);
                }

                if (allItemsList.size() < 2) {
                    allItemsList.add(ad);
                } else {
                    allItemsList.add(index, ad);
                    index += 3;

                }


                if (count > items) {
                    break;
                }

            }
            notifyDataSetChanged();
        }

    }

    public void filter(String query) {

        FilterAllPrograms filterAllPrograms = new FilterAllPrograms();
        filterAllPrograms.execute(query);

    }

    public class FilterAllPrograms extends AsyncTask<String, Integer, ArrayList<ProgramsHelperClass>> {

        @Override
        protected ArrayList<ProgramsHelperClass> doInBackground(String... strings) {

            String query = strings[0].toLowerCase();

            ArrayList<ProgramsHelperClass> filteredList = new ArrayList<>();

            if (query.equals("all")) {
                filteredList = programsHelperArray;
            } else {

                for (ProgramsHelperClass helperClass : programsHelperArray) {
                    String type = helperClass.getType().toLowerCase();

                    if (type.contains(query)) {
                        filteredList.add(helperClass);
                    }

                }

            }

            return filteredList;
        }

        @Override
        protected void onPostExecute(ArrayList<ProgramsHelperClass> programsHelperClasses) {
            allItemsList.clear();
            nativeAdList.clear();
            allItemsList.addAll(programsHelperClasses);
            loadAds();
            insertAdsIntoList();
        }

    }
}
