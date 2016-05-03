package com.danil.recyclerbindableadapter.library;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.danil.recyclerbindableadapter.library.view.ClipContainer;

/**
 * Created by Danil on 08.10.2015.
 */
public abstract class ParallaxBindableAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerBindableAdapter<T, VH> {

    private static final float SCROLL_MULTIPLIER = 0.5f;
    private boolean isParallaxHeader = true;
    private boolean isParallaxFooter = true;
    private int currentHeader = -1;

    //    parallax adapter may have only one header
//    @Override
//    public void addHeader(View header) {
//        if (getHeadersCount() == 0) {
//            super.addHeader(header);
//        }
//        else {
//            removeHeader(getHeader(0));
//            super.addHeader(header);
//        }
//    }

    //parallax adapter may have only one header
    @Override
    public void addFooter(View footer) {
        if (getFootersCount() == 0) {
            super.addFooter(footer);
        } else {
            removeFooter(getFooter(0));
            super.addFooter(footer);
        }
    }

    private void translateView(float of, ClipContainer view, boolean isFooter) {
        float ofCalculated = of * SCROLL_MULTIPLIER;
        ofCalculated = isFooter ? -ofCalculated : ofCalculated;
        view.setTranslationY(ofCalculated);
        view.setClipY(Math.round(ofCalculated));
    }

    @Override
    public VH onCreateViewHolder(ViewGroup viewGroup, int type) {
        //if our position is one of our items (this comes from getItemViewType(int position) below)
        if (type != TYPE_HEADER && type != TYPE_FOOTER) {
            return (VH) onCreateItemViewHolder(viewGroup, type);
            //else if we have a header
        } else if (type == TYPE_HEADER) {
            //create a new ParallaxContainer
            ClipContainer header = new ClipContainer(viewGroup.getContext(), isParallaxHeader, false);
            //make sure it fills the space
            setHeaderFooterLayoutParams(header);
            return (VH) new HeaderFooterViewHolder(header);
            //else we have a footer
        } else {
            //create a new ParallaxContainer
            ClipContainer footer = new ClipContainer(viewGroup.getContext(), isParallaxFooter, true);
            //make sure it fills the space
            setHeaderFooterLayoutParams(footer);
            return (VH) new HeaderFooterViewHolder(footer);
        }
    }

    @Override
    final public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
        //check what type of view our position is
        if (isHeader(position)) {
            View v = getHeader(position);
            //add our view to a header view and display it
//            ClipContainer header = (ClipContainer) vh.itemView;
//            header.setClipY(0);
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);

        } else if (isFooter(position)) {
            View v = getFooter(position - getRealItemCount() - getHeadersCount());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) vh, v);
        } else {
            //it's one of our items, display as required
            onBindItemViewHolder((VH) vh, position - getHeadersCount(), getItemType(position));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (getHeadersCount() > 1) {
                    for (int i = 0; i < getHeadersCount(); i++) {
                        if (view.equals(getHeader(i).getParent())
                                && (currentHeader == -1 || currentHeader > i)) {
                            currentHeader = i;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (getHeadersCount() > 1
                        && currentHeader + 1 < getHeadersCount()
                        && view.equals(getHeader(currentHeader).getParent())) {
                    currentHeader = currentHeader + 1;
                }

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (currentHeader != -1) {
                    ClipContainer header = (ClipContainer) getHeader(currentHeader).getParent();
                    if (header != null && isParallaxHeader) {
                        int offset = recyclerView.computeVerticalScrollOffset();
                        int top = header.getTop();
                        translateView(-header.getTop(), header, false);
                    }
                }
                ClipContainer footer = (ClipContainer) getFooter(0).getParent();
                if (footer != null && isParallaxFooter) {
                    int range = recyclerView.computeVerticalScrollRange();
                    int extend = recyclerView.computeVerticalScrollExtent();
                    int offset = recyclerView.computeVerticalScrollOffset();
                    translateView(range - (extend + offset), footer, true);
                }
            }
        });
    }

    public void setParallaxHeader(boolean isParallaxHeader) {
        this.isParallaxHeader = isParallaxHeader;
    }

    public void setParallaxFooter(boolean isParallaxFooter) {
        this.isParallaxFooter = isParallaxFooter;
    }

}
