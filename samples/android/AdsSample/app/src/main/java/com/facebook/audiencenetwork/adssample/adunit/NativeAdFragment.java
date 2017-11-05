/*
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.facebook.audiencenetwork.adssample.adunit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.facebook.audiencenetwork.adssample.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NativeAdFragment extends Fragment {
    NativeAdsManager manager;
    Point point;
    private LinearLayout nativeAdContainer;
    private LinearLayout adView;

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_native_ad, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showNativeAd();
    }

    private void showNativeAd() {
        manager = new NativeAdsManager(getContext(), "758528754283778_1121314754671841", 5);
        manager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {
                NativeAd nativeAd = manager.nextNativeAd();

                nativeAdContainer = (LinearLayout) getView().findViewById(R.id.native_ad_container);
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View adView = layoutInflater.inflate(R.layout.native_ad_layout, nativeAdContainer, false);

                nativeAdContainer.addView(adView);
                LinearLayout adChoicesContainer = (LinearLayout) adView.findViewById(R.id.ad_choice_container);
                AdChoicesView adChoicesView = new AdChoicesView(getActivity(), nativeAd, true);
                adChoicesContainer.addView(adChoicesView);
                ImageView imageView = (ImageView) adView.findViewById(R.id.iv_fb_ad_image);
                ImageView ivAdLogo = (ImageView) adView.findViewById(R.id.iv_ad_logo);
                TextView tvAdTitle = (TextView) adView.findViewById(R.id.tv_ad_title);
                TextView tvAdDesc = (TextView) adView.findViewById(R.id.tv_ad_desc);
                TextView tvAdCta = (TextView) adView.findViewById(R.id.tv_ad_cta);
                Context context = getActivity();
                NativeAd.Image image = nativeAd.getAdCoverImage();
                if (imageView != null && image != null && image.getUrl() != null) {
                    int width = 0, height = 0;
                    float ratio = ((float) image.getWidth()) / ((float) point.x);
                    width = point.x;
                    height = (int) (image.getHeight() / ratio);
                    Picasso.with(context)
                            .load(image.getUrl())
                            .resize(width, height)
                            .into(imageView);
                    NativeAd.downloadAndDisplayImage(nativeAd.getAdIcon(), ivAdLogo);

                    tvAdTitle.setText(nativeAd.getAdTitle());
                    tvAdDesc.setText(nativeAd.getAdBody());
                    tvAdCta.setText(nativeAd.getAdCallToAction());
                    if (adView.getHeight() != height) {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) adView.getLayoutParams();
                        layoutParams.height = height;
                        adView.setLayoutParams(layoutParams);
                    }
                    ArrayList<View> clickables = new ArrayList<>();
                    clickables.add(ivAdLogo);
                    clickables.add(imageView);
                    clickables.add(tvAdTitle);
                    clickables.add(tvAdCta);
                    nativeAd.registerViewForInteraction(adView, clickables);
                }
            }

            @Override
            public void onAdError(AdError adError) {

            }
        });

        // Request an ad
        manager.loadAds();
    }

}
