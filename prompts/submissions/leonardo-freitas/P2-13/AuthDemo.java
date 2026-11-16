import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Demonstração local de armazenamento e verificação de senhas; não é um serviço web. */
public final class AuthDemo {
    private static final int ITERATIONS = 600_000;
    private static final int KEY_BITS = 256;
    private static final int SALT_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<String, StoredPassword> USERS = new HashMap<>();
    private static final StoredPassword DUMMY = store("senha-ficticia".toCharArray());

    private record StoredPassword(byte[] salt, byte[] hash) {}

    private AuthDemo() {}

    private static byte[] derive(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao derivar hash", e);
        } finally {
            spec.clearPassword();
        }
    }

    private static StoredPassword store(char[] password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        return new StoredPassword(salt, derive(password, salt));
    }

    private static void register(String username, char[] password) {
        USERS.put(username, store(password));
        Arrays.fill(password, '\0');
    }

    private static boolean authenticate(String username, char[] password) {
        StoredPassword saved = USERS.get(username);
        StoredPassword checked = saved == null ? DUMMY : saved;
        byte[] candidate = derive(password, checked.salt());
        Arrays.fill(password, '\0');
        boolean matches = MessageDigest.isEqual(candidate, checked.hash());
        Arrays.fill(candidate, (byte) 0);
        return saved != null && matches;
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
        System.out.println("OK: " + description);
    }

    public static void main(String[] args) {
        register("aluno", "SenhaDeTeste#2026".toCharArray());
        check(authenticate("aluno", "SenhaDeTeste#2026".toCharArray()), "senha correta aceita");
        check(!authenticate("aluno", "senha-incorreta".toCharArray()), "senha incorreta rejeitada");
        check(!authenticate("desconhecido", "SenhaDeTeste#2026".toCharArray()), "usuario inexistente rejeitado");
        System.out.println("Resultado: 3/3 verificacoes locais passaram.");
    }
}

