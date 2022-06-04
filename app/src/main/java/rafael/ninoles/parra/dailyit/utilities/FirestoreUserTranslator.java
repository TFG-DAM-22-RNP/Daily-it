package rafael.ninoles.parra.dailyit.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to translate the language between the user and the DB
 */
public class FirestoreUserTranslator {
    private static final Map<String, String> translations = new HashMap<>();

    public static String getFirestoreWord(String userWord) {
        String translated = translations.get(userWord);
        if (translated != null) {
            return translated;
        }
        return userWord;
    }

    static {
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
