package rafael.ninoles.parra.dailyit.exceptions;

/**
 * Exception to use when a user input is not filled and
 * its required
 */
public class InputNeededException extends Exception {
    public InputNeededException(String message) {
        super(message);
    }
}
