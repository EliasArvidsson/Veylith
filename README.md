# Veylith

**Veylith** is an experimental Java cipher engine inspired by the mechanical principles of the Enigma machine, reimagined through configurable rotor systems, layered enciphering, and deterministic state reconstruction.

The project explores the design of a custom cipher architecture in which character mappings, rotor configurations, permutation keys, and conditional transformations work together to produce ciphertext.

### Core Features

- **Configurable Character Codec** — Defines the character set used by the cipher and maps characters to numerical indices.
- **Rotor-Based Transformations** — Uses configurable rotor counts, stepping speeds, starting positions, and permutation mappings.
- **Layered Enciphering** — Applies multiple cipher configurations sequentially to transform plaintext through successive stages.
- **Conditional Cipher Operations** — Introduces key-driven conditions that determine when specific transformations are applied.
- **Custom CipherKey Format** — Encodes the parameters required to reconstruct the cipher's operational state.
- **Key Obfuscation** — Adds a separate layer of complexity to the representation and interpretation of cipher keys.

*note: All features may not be implemented at any moment*

### Project Goals

Veylith aims to explore the relationship between deterministic state, permutation systems, layered transformations, and reproducible enciphering. Its architecture is intended to support further experimentation with custom cipher mechanisms and extensible character sets.

### Security Notice

Veylith is an experimental cryptographic project. Its custom algorithms, layered transformations, and key-obfuscation mechanisms have not been established as secure against modern cryptanalysis. It is intended for learning, experimentation, and algorithm development, not for protecting sensitive information.
