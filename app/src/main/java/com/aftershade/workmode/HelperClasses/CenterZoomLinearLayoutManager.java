package com.aftershade.workmode.HelperClasses;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CenterZoomLinearLayoutManager extends LinearLayoutManager {

    private final float mShrinkAmount = 0.15f;
    private final float mShrinkDistance = 0.9f;

    public CenterZoomLinearLayoutManager(Context context) {
        super(context);
    }

    public CenterZoomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();

        if (orientation == RecyclerView.HORIZONTAL) {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);

            float midpoint = getWidth() / 2.f;
            float d0 = 0.f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1.f;
            float s1 = 1.f - mShrinkAmount;

            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                float childMidPoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2;
                float d = Math.min(d1, Math.abs(midpoint - childMidPoint));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }

            return scrolled;

        } else {
            return 0;
        }
    }
}
