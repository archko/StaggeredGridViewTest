package com.me.archko.staggered.huewupla;

import android.view.View;
import android.widget.LinearLayout;
import com.huewu.pla.lib.internal.PLA_AbsListView;

/**
 * @author archko
 */
public class RecycleHolder implements PLA_AbsListView.RecyclerListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMovedToScrapHeap(final View view) {
        if (view instanceof LinearLayout) {
            LinearLayout itemView=(LinearLayout) view;

            if (null!=itemView) {
                itemView.removeAllViews();
            }
        }

        /*MusicHolder holder=(MusicHolder) view.getTag();
        if (holder==null) {
            holder=new MusicHolder(view);
            view.setTag(holder);
        }

        // Release mBackground's reference
        if (holder.mBackground.get()!=null) {
            holder.mBackground.get().setImageDrawable(null);
            holder.mBackground.get().setImageBitmap(null);
        }

        // Release mImage's reference
        if (holder.mImage.get()!=null) {
            holder.mImage.get().setImageDrawable(null);
            holder.mImage.get().setImageBitmap(null);
        }

        // Release mLineOne's reference
        if (holder.mLineOne.get()!=null) {
            holder.mLineOne.get().setText(null);
        }

        // Release mLineTwo's reference
        if (holder.mLineTwo.get()!=null) {
            holder.mLineTwo.get().setText(null);
        }

        // Release mLineThree's reference
        if (holder.mLineThree.get()!=null) {
            holder.mLineThree.get().setText(null);
        }*/
    }

}
