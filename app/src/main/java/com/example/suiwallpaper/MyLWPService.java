package com.example.suiwallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

public class MyLWPService extends WallpaperService {
    static int mWallpaperWidth;
    static int mWallpaperHeight;
    static int mViewWidth;
    static int mViewHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        WallpaperManager wpm = WallpaperManager.getInstance
                (getApplicationContext());
        mWallpaperWidth = wpm.getDesiredMinimumWidth();
        mWallpaperHeight = wpm.getDesiredMinimumHeight();
    }

    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyEngine extends Engine  {
        private final Handler mHandler = new Handler();
        private final Paint mPaint = new Paint();
        private final Runnable mDrawThread = new Runnable() {
            public void run() {
                drawFrame();
                try {
                    Thread.sleep(10);
                } catch (Exception ignored) {
                }
            }
        };

        MyEngine() {
            LoadTextures();
            LoadLines();
            t0 = System.currentTimeMillis();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(mDrawThread);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(mDrawThread);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder,
                                     int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            mViewWidth = width;
            mViewHeight = height;

            drawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mDrawThread);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xStep, float yStep, int xPixels, int yPixels) {
            super.onOffsetsChanged(xOffset, yOffset,xStep, yStep,
                    xPixels, yPixels);

            drawFrame();
        }

        private long t0 = 0;
        private float t_rf = 0;
        private boolean mVisible;
        private Bitmap tex_turn;
        private Bitmap tex_static;
        private float[] lines;

        void LoadTextures() {
            tex_turn = BitmapFactory.decodeResource(getResources(),
                    R.drawable.turn);
            tex_static = BitmapFactory.decodeResource(getResources(),
                    R.drawable.stat);
        }

        void LoadLines() {
            lines = new float[18 * 4];
        }

        void UpdateLines(float off, float cx, float cy, float len) {
            for (int a = 0; a < 18; a++) {
                final float t = (off / 180 * 3.14159f) + (float)a / 18 * 2 * 3.14159f;
                lines[a * 4] = cx;
                lines[a * 4 + 1] = cy;
                lines[a * 4 + 2] = (float)Math.cos(t) * len + cx;
                lines[a * 4 + 3] = (float)Math.sin(t) * len + cy;
            }
        }

        void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();
            final Rect frame = holder.getSurfaceFrame();
            final int width = frame.width();
            final int height = frame.height();
            Canvas c = null;

            t_rf = ((System.currentTimeMillis() - t0) / 1000.f) * 3.14159f;

            try {
                c = holder.lockHardwareCanvas();
                if (c != null) {
                    final float sz = 1.7f;
                    final float px = 0.1f * width;
                    final float py = 0.25f * height;

                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(0xFF202020);
                    c.drawRect(new Rect(0, 0, width, height), mPaint);

                    mPaint.setColor(0xFFC29A6A);
                    mPaint.setStrokeWidth(1.f);
                    mPaint.setStyle(Paint.Style.STROKE);

                    c.drawCircle(sz * width / 2 + px, sz * width / 2 + py, width * 0.65f, mPaint);
                    c.drawCircle(sz * width / 2 + px, sz * width / 2 + py, width * 0.95f, mPaint);
                    c.drawCircle(sz * width / 2 + px, sz * width / 2 + py, width * 0.96f, mPaint);
                    c.drawCircle(sz * width / 2 + px, sz * width / 2 + py, width * 1.2f, mPaint);

                    UpdateLines(t_rf * 0.3f, sz * width / 2 + px, sz * width / 2 + py, width * 1.2f);
                    c.drawLines(lines, mPaint);

                    mPaint.setStyle(Paint.Style.FILL);
                    final float scl = sz * (float)width / (float)tex_turn.getHeight();
                    Matrix mat_static = new Matrix();
                    mat_static.setScale(scl, scl);
                    mat_static.postTranslate(px, py);
                    Matrix mat_rot = new Matrix();
                    mat_rot.setScale(scl, scl);
                    mat_rot.postRotate(t_rf, sz * width / 2, sz * width / 2);
                    mat_rot.postTranslate(px, py);
                    c.drawBitmap(tex_static, mat_static, mPaint);
                    c.drawBitmap(tex_turn, mat_rot, mPaint);

                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            mHandler.removeCallbacks(mDrawThread);
            if (mVisible) {
                mHandler.postDelayed(mDrawThread, 10);
            }
        }
    }
}