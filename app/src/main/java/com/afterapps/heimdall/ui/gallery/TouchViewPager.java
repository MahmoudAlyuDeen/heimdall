package com.afterapps.heimdall.ui.gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.viewpager.widget.ViewPager;
import com.ortiz.touchview.TouchImageView;

/**
 * Extending canScroll() to prevent scrolling when image is zoomed
 * See https://github.com/MikeOrtiz/TouchImageView
 */
public class TouchViewPager extends ViewPager {

    public TouchViewPager(Context context) {
        super(context);
    }

    public TouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof TouchImageView) {
            return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);

        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }
}
