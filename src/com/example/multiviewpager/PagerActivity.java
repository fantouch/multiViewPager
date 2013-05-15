
package com.example.multiviewpager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.fantouch.libs.log.Logg;
import me.fantouch.libs.multiviewpager.PagerContainer;
import me.fantouch.libs.multiviewpager.RefImgDownloader;

import net.tsz.afinal.FinalBitmap;

public class PagerActivity extends Activity {
    private static final String TAG = PagerActivity.class.getSimpleName();
    private PagerContainer mContainer;
    private FinalBitmap fb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Logg.setEnableLogcat(true);

        initFinalBitmap();
        initMultiViewPager();
    }

    private void initFinalBitmap() {
        fb = FinalBitmap.create(this);
        fb.configDownlader(new RefImgDownloader(this));
        fb.configCompressFormat(Bitmap.CompressFormat.PNG);
        fb.configLoadingImage(android.R.drawable.ic_menu_sort_by_size);
        fb.configLoadfailImage(android.R.drawable.ic_menu_close_clear_cancel);
        fb.configBitmapMaxWidth(getResources().getDimensionPixelSize(R.dimen.bitmapMaxWidth));
        fb.configBitmapMaxHeight(getResources()
                .getDimensionPixelSize(R.dimen.bitmapMaxHeight));
    }

    @Override
    protected void onPause() {
        fb.onPause();
        Logg.d("");
        super.onPause();
    }

    @Override
    protected void onResume() {
        fb.onResume();
        Logg.d("");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        fb.onDestroy();
        Logg.d("");
        super.onDestroy();
    }

    private void initMultiViewPager() {
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        ViewPager pager = mContainer.getViewPager();
        pager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.pageMargin));
        // If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        pager.setClipChildren(false);
        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(calcOffscreenPageLimit());
        pager.setAdapter(new MyPagerAdapter());
    }

    private int calcOffscreenPageLimit() {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int eachPageWidth = getResources().getDimensionPixelSize(R.dimen.bitmapMaxWidth)
                + getResources().getDimensionPixelSize(R.dimen.pageMargin);
        int pagesCanDisplay = (int) Math.ceil(screenWidth * 1.0f / eachPageWidth);
        return pagesCanDisplay;
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View item = View.inflate(PagerActivity.this, R.layout.item, null);

            TextView tv = (TextView) item.findViewById(R.id.txtView);
            tv.setText("第" + (position + 1) + "张");

            ImageView imageView = (ImageView) item.findViewById(R.id.imgView);
            fb.display(imageView, "http://192.168.1.100:88/drawable/img" + (position + 1) +
                    ".jpg");

            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 22;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
}
