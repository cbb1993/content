package com.zyn.lib.util;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;


public class FrescoUtils
{
    public enum ImageType
    {
        Normal, DEFAULT
    }


    public static void setOption(SimpleDraweeView view, ImageType imageType, String s)
    {
        switch (imageType)
        {
            case Normal:
            {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(s)
                        .setOldController(view.getController())
                        .build();

                view.setController(controller);
            }
            break;
            case DEFAULT:
            {
                GenericDraweeHierarchy hierarchy = view.getHierarchy();
                hierarchy.setPlaceholderImage(Drawable.createFromPath(s));
            }
            break;
        }
    }

    public static void setResizeImage(SimpleDraweeView simpleDraweeView,String url,int width,int height)
    {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(simpleDraweeView.getController())
                .setImageRequest(request)
                .build();
        simpleDraweeView.setController(controller);
    }
}
