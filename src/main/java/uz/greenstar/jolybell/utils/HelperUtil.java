package uz.greenstar.jolybell.utils;

public class HelperUtil {
    public static boolean validatePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.trim().replace("+", "");
        if (phoneNumber.length() != 12) {
            return false;
        }
        try {
            Long.valueOf(phoneNumber);
        } catch (Exception ex) {
            return false;
        }

        return true;
    }
}
