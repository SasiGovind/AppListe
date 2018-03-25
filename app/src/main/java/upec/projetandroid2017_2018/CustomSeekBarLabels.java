package upec.projetandroid2017_2018;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Sasig on 25/03/2018.
 */

public class CustomSeekBarLabels {

    int maxCount, textColor;
    Context mContext;
    LinearLayout mSeekLin;

    public CustomSeekBarLabels(Context context, int maxCount, int textColor) {
        this.mContext = context;
        this.maxCount = maxCount;
        this.textColor = textColor;
    }

    public void addSeekBar(LinearLayout parent) {

        if (parent instanceof LinearLayout) {

            parent.setOrientation(LinearLayout.VERTICAL);

            // Add LinearLayout for labels below SeekBar
            mSeekLin = new LinearLayout(mContext);
            mSeekLin.setOrientation(LinearLayout.HORIZONTAL);
            mSeekLin.setPadding(10, 0, 10, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(35, 10, 35, 0);
            mSeekLin.setLayoutParams(params);

            addLabelsBelowSeekBar();
            parent.addView(mSeekLin);

        } else {

            Log.e("CustomSeekBarLabels", " Parent is not a LinearLayout");

        }

    }

    private void addLabelsBelowSeekBar() {
        for (int count = 0; count < maxCount; count++) {
            TextView textView = new TextView(mContext);
            textView.setText(String.valueOf(count + 1));
            textView.setTextColor(textColor);
            textView.setGravity(Gravity.LEFT);
            mSeekLin.addView(textView);
            textView.setLayoutParams((count == maxCount - 1) ? getLayoutParams(0.0f) : getLayoutParams(1.0f));
        }
    }

    LinearLayout.LayoutParams getLayoutParams(float weight) {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, weight);
    }
}