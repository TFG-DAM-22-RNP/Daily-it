package rafael.ninoles.parra.dailyit.utilities;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class to get colors and traduce it to the DB language
 */
public final class Colors {
    private static final String ENGLISH_CODE = "en";
    private static final String SPANISH_CODE = "es";
    private static final String CATALONIAN_CODE = "ca";
    private static final Map<String, Integer> colors= new HashMap<>();
    private static final Map<String, String> colorsInDB= new HashMap<>();
    private static final Map<String, String> dbToSpanish= new HashMap<>();
    private static final Map<String, String> dbToCatalonian= new HashMap<>();
    static {
        //HEXADECIMAL COLORS TO BACKGROUNDS
        colors.put("blue", 0xFF5D83BD);
        colors.put("blau", 0xFF5D83BD);
        colors.put("azul", 0xFF5D83BD);
        colors.put("purple", 0xFF905DBD);
        colors.put("porpra", 0xFF905DBD);
        colors.put("morado", 0xFF905DBD);
        colors.put("sky",0xFF5DBABD);
        colors.put("cel",0xFF5DBABD);
        colors.put("cielo",0xFF5DBABD);
        colors.put("red",0xFFBD5D5D);
        colors.put("vermell",0xFFBD5D5D);
        colors.put("rojo",0xFFBD5D5D);
        colors.put("yellow",0xFFBABD5D);
        colors.put("groc",0xFFBABD5D);
        colors.put("amarillo",0xFFBABD5D);
        colors.put("green",0xFF5DBDB0);
        colors.put("verd",0xFF5DBDB0);
        colors.put("verde",0xFF5DBDB0);
        colors.put("orange",0xFFBD835D);
        colors.put("taronja",0xFFBD835D);
        colors.put("naranja",0xFFBD835D);
        colors.put("grey",0xFF777777);
        colors.put("gris",0xFF777777);

        //NAMES TO SAVE IN FIRESTORE
        colorsInDB.put("blue", "blue");
        colorsInDB.put("blau", "blue");
        colorsInDB.put("azul", "blue");
        colorsInDB.put("purple", "purple");
        colorsInDB.put("porpra", "purple");
        colorsInDB.put("morado", "purple");
        colorsInDB.put("sky","sky");
        colorsInDB.put("cel","sky");
        colorsInDB.put("cielo","sky");
        colorsInDB.put("red","red");
        colorsInDB.put("vermell","red");
        colorsInDB.put("rojo","red");
        colorsInDB.put("yellow","yellow");
        colorsInDB.put("groc","yellow");
        colorsInDB.put("amarillo","yellow");
        colorsInDB.put("green","green");
        colorsInDB.put("verd","green");
        colorsInDB.put("verde","green");
        colorsInDB.put("orange","orange");
        colorsInDB.put("taronja","orange");
        colorsInDB.put("naranja","orange");
        colorsInDB.put("grey","grey");
        colorsInDB.put("gris","grey");

        //TO SPANISH
        dbToSpanish.put("grey","Gris");
        dbToSpanish.put("orange","Naranja");
        dbToSpanish.put("green","Verde");
        dbToSpanish.put("yellow","Amarillo");
        dbToSpanish.put("red","Rojo");
        dbToSpanish.put("sky","Cielo");
        dbToSpanish.put("purple","Morado");
        dbToSpanish.put("blue","Azul");

        //TO CATALONIAN
        dbToCatalonian.put("grey","Gris");
        dbToCatalonian.put("orange","Taronja");
        dbToCatalonian.put("green","Verd");
        dbToCatalonian.put("yellow","Groc");
        dbToCatalonian.put("red","Vermell");
        dbToCatalonian.put("sky","Cel");
        dbToCatalonian.put("purple","Porpra");
        dbToCatalonian.put("blue","Blau");
    }

    public static Integer getColor(String name){
        return colors.get(name.toLowerCase());
    }

    /**
     * Return color translated from db to locale language
     * @param name
     * @return color translated from db to locale language
     */
    public static String getFromDBToLocale(String name){
        String search = name.toLowerCase();
        String actualLenguage = Locale.getDefault().getLanguage();
        if(actualLenguage.equals(SPANISH_CODE)){
            return dbToSpanish.get(search);
        }else if(actualLenguage.equals(CATALONIAN_CODE)){
            return dbToCatalonian.get(search);
        }
        return name;
    }

    /**
     * Get the color name in the DB
     * @param name
     * @return the color name in the DB
     */
    public static String getColorInDB(String name){
        return colorsInDB.get(name.toLowerCase());
    }
}
