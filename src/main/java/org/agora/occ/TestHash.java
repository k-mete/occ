package org.agora.occ;

import org.mindrot.jbcrypt.BCrypt;

public class TestHash {
    public static void main(String[] args) {
        String dbHash = "$2a$10$wT8m9o3/XvQzj5YvD5176ea3Zk.g9XN15TInwXG3HInR5DXYKOhH2";
        System.out.println("Does password match? " + BCrypt.checkpw("admin123", dbHash));
        System.out.println("Fresh hash for admin123: " + BCrypt.hashpw("admin123", BCrypt.gensalt()));
    }
}
