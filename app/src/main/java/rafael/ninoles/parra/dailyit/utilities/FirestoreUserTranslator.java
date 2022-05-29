package rafael.ninoles.parra.dailyit.utilities;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

public class FirestoreUserTranslator {
    private static final Map<String, String> translations = new HashMap<>();

    public static String getFirestoreWord(String userWord){
        String translated = translations.get(userWord);
        System.out.println("LLEGA PARA TRADUCIR "+userWord);
        System.out.println(translated);
        if(translated != null){
            return translated;
        }
        return userWord;
    }

    static {
        //TODO PROBLEMA CAMBIO IDIOMAS
        translations.put("Treball", "Work");
        translations.put("Trabajo", "Work");
        translations.put("Per fer", "ToDo");
        translations.put("To do", "ToDo");
        translations.put("Por hacer", "ToDo");
        translations.put("Fent", "Doing");
        translations.put("Haciendo", "Doing");
        translations.put("Fet", "Done");
        translations.put("Hecho", "Done");
    }
}
