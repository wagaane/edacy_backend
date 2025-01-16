package sn.ods.starterkit_spring.infrastructure.config.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Abdou Karim CISSOKHO
 * @created 22/05/2024-18:23
 * @project backend_mfpai
 */
public class UtilityClass {
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final String WORD_SEPARATOR = " ";
    private static final Random RANDOM = new SecureRandom(); // Use SecureRandom to generate random numbers for password characters
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; // Define the character set for the password
    private static final String NUMERIC = "0123456789"; // Define the character set for the otp
    private static final int PASSWORD_LENGTH = 10;
    // DATE UTILITIES
    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(Date date) {
        return simpleDateFormat.format(date);
    }

    public static String formatAmount(double amount) {
        return new DecimalFormat().format(amount);
    }

    // Method to generate a secure password of specified length
    public static String generatePassword(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        // Iterate through password length and append random characters from character set
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        // Convert StringBuilder to String and return
        return new String(returnValue);
    }

    public static String generatePassword() {

        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            password.append(ALPHABET.charAt(index));
        }
        //return password.toString();
        return "P@sser123";
    }



    public static PageRequest formatPageRequest(Map<String, String> filters) {
        int page = Integer.parseInt(filters.getOrDefault("page", "0"));
        int size = Integer.parseInt(filters.getOrDefault("size", "10"));
        String sortBy = filters.getOrDefault("sortBy", "id");
        boolean sortByDescending = Boolean.parseBoolean(filters.getOrDefault("sortByDescending", "true"));

        Sort sort = sortByDescending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    public static class SaltedPasswordHasher {
        private static final int SALT_LENGTH = 16;


        // Hashes the given password with a random salt
        public static String hashPassword(String password) throws NoSuchAlgorithmException {
            SecureRandom random = new SecureRandom();
            // Generate a random salt
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            // Hash the password with the salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());
            // Concatenate the salt and hashed password, and encode as a Base64 string
            return Base64.getEncoder().encodeToString(concatenateArrays(salt, hash));
        }

        // Verifies a password against a salted hash
        public static boolean verifyPassword(String password, String saltedHash) throws NoSuchAlgorithmException {
            // Decode the salted hash back into its constituent salt and hash values
            byte[] saltAndHash = Base64.getDecoder().decode(saltedHash);
            byte[] salt = new byte[SALT_LENGTH];
            byte[] hash = new byte[saltAndHash.length - SALT_LENGTH];
            splitArray(saltAndHash, salt, hash);
            // Hash the password with the retrieved salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] testHash = md.digest(password.getBytes());
            // Compare the computed hash with the stored hash
            return MessageDigest.isEqual(hash, testHash);
        }

        // Concatenates two byte arrays into a single array
        private static byte[] concatenateArrays(byte[] a, byte[] b) {
            byte[] result = new byte[a.length + b.length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        }

        // Splits a byte array into two separate arrays
        private static void splitArray(byte[] source, byte[] dest1, byte[] dest2) {
            System.arraycopy(source, 0, dest1, 0, dest1.length);
            System.arraycopy(source, dest1.length, dest2, 0, dest2.length);
        }
    }

    public static class DateUtility {

        public static Date atStartOfDay(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }

        public static Date atEndOfDay(Date date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return calendar.getTime();
        }

        public static Date addTimeToDate(Date date, int hours, int minutes, int seconds, int millis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, seconds);
            calendar.set(Calendar.MILLISECOND, millis);
            return calendar.getTime();
        }
    }

    public static class StringUtility {
        public static String titleCaseConversion(String text) {
            if (text == null || text.isEmpty()) {
                return text;
            }

            return Arrays
                    .stream(text.split(WORD_SEPARATOR))
                    .map(word -> word.isEmpty()
                            ? word
                            : Character.toTitleCase(word.charAt(0)) + word
                            .substring(1)
                            .toLowerCase())
                    .collect(Collectors.joining(WORD_SEPARATOR));
        }
    }

    public static class ArrayUtility {
        public static <T> boolean checkIfAllValuesAreInList(List<T> a, List<T> b) {
            return new HashSet<>(b).containsAll(a);
        }

//        public static boolean hasDuplicates(Set<FuelOrderItemDTO> mySet) {
//            return mySet.stream()
//                    .map(FuelOrderItemDTO::getFuelType)
//                    .distinct()
//                    .count() < mySet.size();
//        }
    }

    public static class PasswordUtility {

        private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9*$-+?_&=!%@#\\[\\]\\\"{}/]).{8,20})";
        private final Pattern pattern;
        private Matcher matcher;

        public PasswordUtility() {
            this.pattern = Pattern.compile(PASSWORD_PATTERN);

        }

        public boolean validate(String password) {

            matcher = pattern.matcher(password);
            return matcher.matches();
        }
    }
}
