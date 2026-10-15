package veylith.dataTransformation;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Keyed character permuter with high bit-entropy extraction and
 * secure information handling.
 *
 * Improvements over the previous implementation:
 *
 *  - 256-bit internal state derived via SHA-256 from the 64-bit key,
 *    the plaintext length and a domain separator.  Every key bit
 *    influences the entire permutation (strong avalanche).
 *  - Deterministic CSPRNG-style stream built by repeated hashing of
 *    the state + counter to produce unbiased random integers for Fisher-Yates.
 *  - Rejection sampling to remove modulo bias from the random integers.
 */
public class KeyedChunkPermuter {

    private static final long DOMAIN = 0x4B45595045524D4CL; // "KEYPERML"

    /**
     * Expand the caller-supplied 64-bit key into a 256-bit state.
     * Mixing in length and a domain constant prevents cross-protocol
     * reuse of the same key material.
     */
    private byte[] deriveState(long key, int length) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            ByteBuffer buf = ByteBuffer.allocate(20);
            buf.putLong(key);
            buf.putLong(DOMAIN);
            buf.putInt(length);

            md.update(buf.array());

            // Second round for additional diffusion
            byte[] first = md.digest();
            md.update(first);
            md.update((byte) 0xA5);

            return md.digest();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 required", e);
        }
    }

    /**
     * Deterministic 64-bit value from the state and a counter.
     * Acts as a keyed stream generator with high entropy.
     */
    private long nextLong(byte[] state, int counter) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(state);
            ByteBuffer ctr = ByteBuffer.allocate(4);

            ctr.putInt(counter);
            md.update(ctr.array());

            byte[] out = md.digest();

            return ByteBuffer.wrap(out).getLong();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unbiased integer in [0, bound).  Rejection sampling removes
     * the classic modulo bias that reduces effective entropy.
     */
    private int nextInt(byte[] state, int[] counter, int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }
        long mask = 0xFFFF_FFFFL;
        long limit = mask - (mask % bound);
        long r;

        do {
            r = nextLong(state, counter[0]++) & mask;
        } while (r >= limit);

        return (int) (r % bound);
    }

    /**
     * Produce a permutation array of length n driven solely by the key.
     * Uses Fisher-Yates for uniform distribution over all n! permutations.
     */
    private int[] generatePermutation(byte[] state, int n) {
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }

        int[] counter = {0};

        for (int i = n - 1; i > 0; i--) {
            int j = nextInt(state, counter, i + 1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }
        return perm;
    }

    /**
     * Invert a permutation (O(n) construction of the inverse mapping).
     */
    private int[] invert(int[] perm) {

        int[] inv = new int[perm.length];

        for (int i = 0; i < perm.length; i++) {
            inv[perm[i]] = i;
        }

        return inv;
    }

    /**
     * Forward: apply a keyed, high-entropy permutation to the characters.
     */
    public String getPermutedText(String plaintext, long key) {

        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }

        int n = plaintext.length();

        byte[] state = deriveState(key, n);

        int[] perm = generatePermutation(state, n);

        char[] src = plaintext.toCharArray();
        char[] dst = new char[n];

        for (int i = 0; i < n; i++) {
            dst[i] = src[perm[i]];
        }

        return new String(dst);
    }

    /**
     * Inverse: regenerate the identical permutation from the key and
     * apply its inverse.  Perfect round-trip for any key / length.
     */
    public String backwardPermute(String permutedText, long key) {

        if (permutedText == null || permutedText.isEmpty()) {
            return permutedText;
        }

        int n = permutedText.length();
        byte[] state = deriveState(key, n);

        int[] perm = generatePermutation(state, n);
        int[] inv = invert(perm);

        char[] src = permutedText.toCharArray();
        char[] dst = new char[n];
        
        for (int i = 0; i < n; i++) {
            dst[i] = src[inv[i]];
        }
        
        return new String(dst);
    }

    /**
     * Generate a fresh 64-bit key with full OS-level entropy.
     * Prefer this over hard-coded or low-entropy sources.
     */
    public static long generateSecureKey() {
        SecureRandom rng = new SecureRandom(); // strongest available provider
        return rng.nextLong();
    }
}