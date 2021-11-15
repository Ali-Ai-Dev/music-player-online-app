package com.app.ali_bozorgzad.music_player;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurImage {
    private static final float BITMAP_SCALE = 0.5f;
    private static final float BLUR_RADIUS = 9.5f;

    public static Bitmap blur(Context context, Bitmap image){
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript renderScript = RenderScript.create(context);
        Allocation tempInput = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation tempOutput = Allocation.createFromBitmap(renderScript, outputBitmap);

        ScriptIntrinsicBlur theIntrinsic;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tempInput);
            theIntrinsic.forEach(tempOutput);
        }
        tempOutput.copyTo(outputBitmap);

        return outputBitmap;
    }
}