package veylith.cipherCore;

import veylith.dataTransformation.KeyedPermutation;

public class Cipher {
    
    private Rotor[] rotors;
    private int[] stepping;
    private int characterCodecLength;
    private int[] deflectorPermutation;


    public Cipher(long[] permutationKeys, int[] initialPositions, int[] stepping, int characterCodecLength, long deflectorKey) {
        
        if (permutationKeys.length != initialPositions.length) {
            throw new IllegalArgumentException("Permutation keys and initial positions must have the same length.");
        }

        this.rotors = new Rotor[permutationKeys.length];
        this.stepping = stepping;
        this.characterCodecLength = characterCodecLength;
        

        KeyedPermutation keyedPermutation = new KeyedPermutation();

        for (int i = 0; i < permutationKeys.length; i++) {
            int[] perm = keyedPermutation.getPermutation(permutationKeys[i], characterCodecLength);
            rotors[i] = new Rotor(perm, initialPositions[i]);
        }

        deflectorPermutation = keyedPermutation.getPermutation(deflectorKey, characterCodecLength);
    }

    private void updateRotors() {
        for (int i = 0; i < rotors.length; i++) {

            if (rotors[i].getPosition() + stepping[i] >= characterCodecLength) {
                // If the rotor will wrap around, rotate the next rotor
                if (i + 1 < rotors.length) {
                    rotors[i + 1].rotate(1);
                } else {
                    // If it's the last rotor, wrap around to the first rotor
                    rotors[0].rotate(1);
                }
            }

            rotors[i].rotate(stepping[i]);

        }
    }

}
