package rafael.ninoles.parra.dailyit.model;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public final class Colors {
    private static final Map<String, Integer> colors= new HashMap<>();
    static {
        colors.put("blue", Color.BLUE);
        colors.put("purple", Color.MAGENTA);
    }
}
