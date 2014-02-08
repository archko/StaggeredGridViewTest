package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import com.bulletnoid.android.widget.StaggeredGridView.HeaderFooterListAdapter;
import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView3;
import com.bulletnoid.android.widget.StaggeredGridViewDemo.R;

/**
 * @author archko
 */
public class PullToRefreshStaggeredGridView3 extends PullToRefreshBase<StaggeredGridView3> {

    public PullToRefreshStaggeredGridView3(Context context) {
        super(context);
    }

    public PullToRefreshStaggeredGridView3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshStaggeredGridView3(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshStaggeredGridView3(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected StaggeredGridView3 createRefreshableView(Context context, AttributeSet attrs) {
        StaggeredGridView3 stgv;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
            stgv=new InternalStaggeredGridViewSDK9(context, attrs);
        } else {
            stgv=new StaggeredGridView3(context, attrs);
        }

        int margin=getResources().getDimensionPixelSize(R.dimen.stgv_margin);
        stgv.setColumnCount(2);
        stgv.setItemMargin(margin);
        stgv.setPadding(margin, 0, margin, 0);

        stgv.setId(R.id.stgv);
        return stgv;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return mRefreshableView.mGetToTop;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        //return false;
        return isLastItemVisible();
    }

    public void setAdapter(BaseAdapter adapter) {
        mRefreshableView.setAdapter(adapter);
    }

    private boolean isLastItemVisible() {
        final HeaderFooterListAdapter adapter =(HeaderFooterListAdapter) mRefreshableView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
            }
            return true;
        } else {
            final int lastItemPosition = mRefreshableView.getAdapter().getCount() - 1;
            final int lastVisiblePosition = mRefreshableView.getLastVisiblePosition();

            if (DEBUG) {
                Log.v(LOG_TAG, "isLastItemVisible. Last Item Position: " + lastItemPosition + " Last Visible Pos: "
                    + lastVisiblePosition);
            }

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1) {
                final int childIndex = lastVisiblePosition - mRefreshableView.getFirstVisiblePosition();
                final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
                }
            }
        }

        return false;
    }

    @TargetApi(9)
    final class InternalStaggeredGridViewSDK9 extends StaggeredGridView3 {

        public InternalStaggeredGridViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
            int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue=super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshStaggeredGridView3.this, deltaX, scrollX, deltaY, scrollY,
                getScrollRange(), isTouchEvent);

            return returnValue;
        }

        /**
         * Taken from the AOSP ScrollView source
         */
        private int getScrollRange() {
            int scrollRange=0;
            if (getChildCount()>0) {
                View child=getChildAt(0);
                scrollRange=Math.max(0, child.getHeight()-(getHeight()-getPaddingBottom()-getPaddingTop()));
            }
            return scrollRange;
        }
    }

    /*public final void setOnLoadmoreListener(StaggeredGridView.OnLoadmoreListener listener) {
        mRefreshableView.setOnLoadmoreListener(listener);
    }*/

}
