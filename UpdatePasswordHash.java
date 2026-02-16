import cn.hutool.crypto.digest.BCrypt;

public class UpdatePasswordHash {
    public static void main(String[] args) {
        String password = "admin123";

        // Generate new hash
        String hash = BCrypt.hashpw(password);
        System.out.println("Password: " + password);
        System.out.println("New Hash: " + hash);

        // Verify the old hash from database
        String oldHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTNDp8G3vQMWR.XHGJ.Z5QX.ZQq";
        boolean oldMatch = BCrypt.checkpw(password, oldHash);
        System.out.println("Old hash matches: " + oldMatch);

        // Verify new hash
        boolean newMatch = BCrypt.checkpw(password, hash);
        System.out.println("New hash matches: " + newMatch);

        // Print SQL to update
        System.out.println("\nSQL to update password:");
        System.out.println("UPDATE sys_user SET password='" + hash + "' WHERE username='admin';");
    }
}
