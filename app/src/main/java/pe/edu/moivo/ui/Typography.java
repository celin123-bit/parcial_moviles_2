package pe.edu.moivo.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.widget.TextViewCompat;
import pe.edu.moivo.R;

/** Altura de línea de 30sp también en Android 8, anterior a android:lineHeight. */
public final class Typography {
    private Typography() { }
    public static void apply(View view) {
        if (view instanceof TextView) {
            TextViewCompat.setLineHeight((TextView) view,
                view.getResources().getDimensionPixelSize(R.dimen.text_line_height));
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) apply(group.getChildAt(i));
        }
    }
}
