package rafael.ninoles.parra.dailyit.utilities;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public final class Colors {
    private static final Map<String, Integer> colors= new HashMap<>();
    static {
        colors.put("blue", 0xFF5D83BD);
        colors.put("purple", 0xFF905DBD);
        colors.put("sky",0xFF5DBABD);
        colors.put("red",0xFFBD5D5D);
        colors.put("yellow",0xFFBABD5D);
        colors.put("green",0xFF5DBDB0);
        colors.put("orange",0xFFBD835D);
        colors.put("grey",0xFF777777);

    }

    public static Integer getColor(String name){
        //TODO Manejo excep
        return colors.get(name);
    }
}
