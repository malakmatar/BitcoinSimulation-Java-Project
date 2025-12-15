# Bitcoin Blockchain Simulator (Java)

## Overview
This project is a **simplified Bitcoin / blockchain simulator** implemented in **Java** as part of a *Programming 2* course project.  
It demonstrates the core concepts behind a blockchain system, including transactions, blocks, mining, hashing, and balance tracking.

The entire project is intentionally implemented in **a single Java file** for simplicity and ease of submission, which is fully valid in Java when only one public class is used.

---

## Features
- SHA-256 hashing for block integrity
- Transaction creation and verification
- Proof-of-Work mining (hash must start with `0000`)
- Miner reward mechanism
- On-chain balance calculation
- Genesis block initialization
- Simple Bitcoin-style workflow simulation

---

### Classes Included
- **Transaction** – Represents a transaction between two agents
- **Block** – Represents a block containing verified transactions
- **Blockchain** – Manages the chain, pending transactions, and balances
- **Agent** – Represents a user who can create transactions
- **Miner** – Mines new blocks and receives rewards
- **BitcoinSimulation** – Main entry point (`main` method)

---

## How It Works
1. The blockchain is initialized with a **genesis block**
2. Initial balances are created using special `SYSTEM` transactions
3. Agents create transactions (added as pending)
4. Transactions are verified based on on-chain balances
5. The miner performs Proof-of-Work to mine a new block
6. A mining reward is added to the block
7. The block is appended to the blockchain
8. Final block hashes and balances are printed


---

## Example Output
- Hashes of all blocks in the blockchain
- Confirmation of mined blocks
- On-chain balances of all agents and the miner


