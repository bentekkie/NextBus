package com.bentekkie.nextbus;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

public class OffsetItemDecoration extends WearableRecyclerView.ItemDecoration {

    private Context ctx;

    public OffsetItemDecoration(Context ctx) {

        this.ctx = ctx;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int offset = (int) (getScreenHeight() / (float) (2)) - view.getLayoutParams().height / 2;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (parent.getChildAdapterPosition(view) == 0) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = 0;
            setupOutRect(outRect, offset, true);
        } else if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin = 0;
            setupOutRect(outRect, offset, false);
        }
    }

    private void setupOutRect(Rect rect, int offset, boolean start) {

        if (start) {
            rect.top = offset;
        } else {
            rect.bottom = offset;
        }
    }

    private int getScreenHeight() {

        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}