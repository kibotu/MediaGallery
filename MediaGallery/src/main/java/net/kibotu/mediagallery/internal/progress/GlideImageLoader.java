package net.kibotu.mediagallery.internal.progress;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.daimajia.numberprogressbar.NumberProgressBar;

//public class GlideImageLoader {
//
//    private BlurryImageView background;
//    private ImageView image;
//    private NumberProgressBar mProgressBar;
//
//    public GlideImageLoader(BlurryImageView background, ImageView image, NumberProgressBar progressBar) {
//        this.background = background;
//        this.image = image;
//        mProgressBar = progressBar;
//    }
//
//    public void load(final String url, RequestOptions options) {
//        if (url == null || options == null) return;
//
//        onConnecting();
//
//        //set Listener & start
//        ProgressAppGlideModule.expect(url, new ProgressAppGlideModule.UIonProgressListener() {
//            @Override
//            public void onProgress(long bytesRead, long expectedLength) {
//                if (mProgressBar != null) {
//                    mProgressBar.setProgress((int) (100 * bytesRead / expectedLength));
//                }
//            }
//
//            @Override
//            public float getGranualityPercentage() {
//                return 1.0f;
//            }
//        });
//
//        Glide.with(image.getContext())
//                .asBitmap()
//                .load(url)
//                .apply(options.skipMemoryCache(true))
//                .listener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        ProgressAppGlideModule.forget(url);
//                        onFinished();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        ProgressAppGlideModule.forget(url);
//                        background.blur(resource);
//                        onFinished();
//                        return false;
//                    }
//                })
//                .into(image);
//    }
//
//
//    private void onConnecting() {
//        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
//    }
//
//    private void onFinished() {
//        if (mProgressBar != null && image != null) {
//            mProgressBar.setVisibility(View.GONE);
//            image.setVisibility(View.VISIBLE);
//        }
//    }
//}