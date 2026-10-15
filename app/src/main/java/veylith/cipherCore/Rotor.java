package veylith.cipherCore;

public class Rotor {
    
    private final int[] permutation;
    private int position;

    /**
     * Constructor for the Rotor class.
     * @param permutation The permutation array representing the rotor's wiring.
     * @param position The initial position of the rotor.
     */
    public Rotor(int[] permutation, int position) {
        this.permutation = permutation;
        this.position = position;
    }

    /**
     * Forward: apply the rotor's permutation to the input, taking into account the rotor's current position.
     * The input is first shifted by the rotor's position, then the permutation is applied, and finally the output is shifted back by the rotor's position.
     * @param input
     * @return
     */
    public int forward(int input) {
        int shiftedInput = (input + position) % permutation.length;
        int permutedOutput = permutation[shiftedInput];
        return (permutedOutput + position) % permutation.length;
    }

    /**
     * Inverse: apply the inverse of the rotor's permutation to the input, taking into account the rotor's current position.
     * The input is first shifted by the rotor's position, then the inverse permutation is applied, and finally the output is shifted back by the rotor's position.
     * @param input
     * @return
     */
    public int backward(int input) {
        int shiftedInput = (input - position + permutation.length) % permutation.length;
        int permutedOutput = permutation[shiftedInput];
        return (permutedOutput - position + permutation.length) % permutation.length;
    }

    /**
     * Rotate the rotor by a specified number of steps. The position is updated modulo the length of the permutation to ensure it wraps around correctly.
     * @param steps
     */
    public void rotate(int steps) {
        position = (position + steps) % permutation.length;
    }

    public int getPosition() {
        return position;
    }

}
