package com.ahmed.homeservices.reusable;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.config.AppConfig;
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;

//Model for views to fill all Questions with it's data (Model num 2) Header - layout - footer.
public class ExpansionHeaderWithLayout {
    //    private ExpansionHeader expansionHeader;
    private View expansionHeader;
    private ImageView ivIcon;
    private TextView tvTitle;
    //    private ExpansionLayout expansionLayout;
    private View expansionLayout;
    private ViewGroup myViewContainer = null;
    private View root;
    //a separator line
    private View viewSeparator;
    private AppCompatImageView headerIndicator;

    public ExpansionHeaderWithLayout() {
        @SuppressLint("InflateParams") View root = LayoutInflater.from(AppConfig.getInstance().getApplicationContext())
                .inflate(R.layout.expantion2, null);
        this.root = root;


        //header
        View expansionHeader = root.findViewById(R.id.sampleHeader);
//        expansionHeader.setId(i);
        setExpansionHeader(expansionHeader);

        AppCompatImageView headerIndicator = root.findViewById(R.id.headerIndicator);
        setHeaderIndicator(headerIndicator);

        ImageView ivIcon = root.findViewById(R.id.ivIcon);
        setIvIcon(ivIcon);

        TextView tvTitle = root.findViewById(R.id.tvTitle);
        setTvTitle(tvTitle);

        View expansionLayout = root.findViewById(R.id.expansionLayout);
//        expansionLayout.setId(i + 1);
        setExpansionLayout(expansionLayout);

        LinearLayout linearLayout = root.findViewById(R.id.subviewContainer);
        setMyViewContainer(linearLayout);

        View view = root.findViewById(R.id.viewSeparator);
        setViewSeparator(view);

//        expansionHeader.setExpansionLayoutId(expansionLayout.getId());
//        expansionHeader.setHeaderIndicatorId(headerIndicator.getId());
//        expansionHeader.setExpansionLayout(expansionLayout);
    }

    public View getExpansionHeader() {
        return expansionHeader;
    }

    private void setExpansionHeader(ExpansionHeader expansionHeader) {
        this.expansionHeader = expansionHeader;
    }


//    public ExpansionHeaderWithLayout() {
//        @SuppressLint("InflateParams") View root = LayoutInflater.from(AppConfig.getInstance().getApplicationContext())
//                //Binary XML file line #2: <merge /> can be used only with a valid ViewGroup root and attachToRoot=true
////                .inflate(R.layout.expantion, container, true);
////                .inflate(R.layout.expantion, null);
//                .inflate(R.layout.expantion2, null);
//        this.root = root;
//
//
//        ExpansionHeader expansionHeader = root.findViewById(R.id.sampleHeader);
////        expansionHeader.setId(i);
//        setExpansionHeader(expansionHeader);
//
//        AppCompatImageView headerIndicator = root.findViewById(R.id.headerIndicator);
//        setHeaderIndicator(headerIndicator);
//
//        ImageView ivIcon = root.findViewById(R.id.ivIcon);
//        setIvIcon(ivIcon);
//
//        TextView tvTitle = root.findViewById(R.id.tvTitle);
//        setTvTitle(tvTitle);
//
//        ExpansionLayout expansionLayout = root.findViewById(R.id.expansionLayout);
////        expansionLayout.setId(i + 1);
//        setExpansionLayout(expansionLayout);
//
//        LinearLayout linearLayout = root.findViewById(R.id.subviewContainer);
//        setMyViewContainer(linearLayout);
//
//        View view = root.findViewById(R.id.viewSeparator);
//        setViewSeparator(view);
//
////        expansionHeader.setExpansionLayoutId(expansionLayout.getId());
////        expansionHeader.setHeaderIndicatorId(headerIndicator.getId());
////        expansionHeader.setExpansionLayout(expansionLayout);
//    }

    private void setExpansionHeader(View expansionHeader) {
        this.expansionHeader = expansionHeader;
    }

    public View getExpansionLayout() {
        return expansionLayout;
    }

    private void setExpansionLayout(View expansionLayout) {
        this.expansionLayout = expansionLayout;
    }

    private void setExpansionLayout(ExpansionLayout expansionLayout) {
        this.expansionLayout = expansionLayout;
    }

    public AppCompatImageView getHeaderIndicator() {
        return headerIndicator;
    }

    private void setHeaderIndicator(AppCompatImageView headerIndicator) {
        this.headerIndicator = headerIndicator;
    }

    public View getViewSeparator() {
        return viewSeparator;
    }

    private void setViewSeparator(View viewSeparator) {
        this.viewSeparator = viewSeparator;
    }

//    public ExpansionHeader getExpansionHeader() {
//        return expansionHeader;
//    }

    public ViewGroup getMyViewContainer() {
        return myViewContainer;
    }

    private void setMyViewContainer(ViewGroup myViewContainer) {
        this.myViewContainer = myViewContainer;
    }

    public View getRoot() {
        return root;
    }

    public void setRoot(View root) {
        this.root = root;
    }

    public ImageView getIvIcon() {
        return ivIcon;
    }

    private void setIvIcon(ImageView ivIcon) {
        this.ivIcon = ivIcon;
    }

//    public ExpansionLayout getExpansionLayout() {
//        return expansionLayout;
//    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    private void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }
}
