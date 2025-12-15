import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;

class Transaction {
    private final String sender;
    private final String recipient;
    private final double amount;
    private boolean verified;

    public Transaction(String sender, String recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.verified = false;
    }

    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public double getAmount() { return amount; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}

class Block {
    private final List<Transaction> transactions;
    private final String previousHash;
    private final int nonce;

    public Block(List<Transaction> transactions, String previousHash, int nonce) {
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.nonce = nonce;
    }

    public String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder data = new StringBuilder();
            for (Transaction transaction : transactions) {
                data.append(transaction.getSender());
                data.append(transaction.getRecipient());
                data.append(transaction.getAmount());
                data.append(transaction.isVerified());
            }
            String input = previousHash + data + nonce;
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = String.format("%02x", hashByte);
                hash.append(hex);
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public List<Transaction> getTransactions() { return transactions; }
}

class Blockchain {
    private final List<Block> blocks;
    final Map<String, Transaction> pendingTransactions;

    public Blockchain() {
        this.blocks = new ArrayList<>();
        Block genesisBlock = new Block(new ArrayList<>(), "0", 0);
        blocks.add(genesisBlock);
        this.pendingTransactions = new HashMap<>();
    }

    public void addBlock(Block block) { blocks.add(block); }
    public Block getLastBlock() { return blocks.get(blocks.size() - 1); }

    public void addPendingTransaction(Transaction transaction) {
        pendingTransactions.put(transaction.getSender(), transaction);
    }


    public boolean processPendingTransactions() {
        boolean anyVerified = false;

        for (Transaction transaction : pendingTransactions.values()) {
            if (verifyTransaction(transaction)) {
                anyVerified = true;
            }
        }

        return anyVerified;
    }

    private boolean verifyTransaction(Transaction transaction) {
        if (transaction.getSender().equals("SYSTEM")) {
            transaction.setVerified(true);
            return true;
        }

        double senderBalance = getBalance(transaction.getSender());
        if (senderBalance < transaction.getAmount()) {
            return false;
        }

        transaction.setVerified(true);
        return true;
    }


    public double getBalance(String agentName) {
        double balance = 0;
        for (Block block : blocks) {
            for (Transaction transaction : block.getTransactions()) {
                if (transaction.getSender().equals(agentName)) {
                    balance -= transaction.getAmount();
                }
                if (transaction.getRecipient().equals(agentName)) {
                    balance += transaction.getAmount();
                }
            }
        }
        return balance;
    }

    public List<Block> getBlocks() { return blocks; }
}

class Agent {
    private final String name;
    private double balance;

    public Agent(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() { return name; }
    public double getBalance() { return balance; }

    public void sendCoins(Agent recipient, double amount, Blockchain blockchain) {
        Transaction transaction = new Transaction(name, recipient.getName(), amount);
        blockchain.addPendingTransaction(transaction);
        System.out.println(name + " created a transaction of " + amount + " coins to " + recipient.getName());
    }
}

class Miner {
    private final String name;
    private final double reward;

    public Miner(String name, double reward) {
        this.name = name;
        this.reward = reward;
    }

    public String getName() { return name; }
    public double getReward() { return reward; }

    public void mine(Blockchain blockchain) {
        // take current pending transactions
        List<Transaction> transactions = new ArrayList<>(blockchain.pendingTransactions.values());

        Transaction rewardTransaction = new Transaction("SYSTEM", name, reward);
        rewardTransaction.setVerified(true);
        transactions.add(rewardTransaction);

        Block lastBlock = blockchain.getLastBlock();
        String previousHash = lastBlock.calculateHash();

        int nonce = 0;
        Block newBlock = new Block(transactions, previousHash, nonce);
        String newBlockHash = newBlock.calculateHash();

        while (!newBlockHash.startsWith("0000")) {
            nonce++;
            newBlock = new Block(transactions, previousHash, nonce);
            newBlockHash = newBlock.calculateHash();
        }

        blockchain.addBlock(newBlock);
        blockchain.pendingTransactions.clear();

        System.out.println("New block mined by " + name + ": " + newBlockHash);
    }
}

public class BitcoinSimulation {
    public static void main(String[] args) {
        Agent ali = new Agent("Ali", 10.0);
        Agent buse = new Agent("Buse", 15.0);
        Agent betul = new Agent("Betul", 12.0);

        Miner miner = new Miner("Miner1", 2.0);
        Blockchain blockchain = new Blockchain();

        blockchain.addPendingTransaction(new Transaction("SYSTEM", ali.getName(), ali.getBalance()));
        blockchain.addPendingTransaction(new Transaction("SYSTEM", buse.getName(), buse.getBalance()));
        blockchain.addPendingTransaction(new Transaction("SYSTEM", betul.getName(), betul.getBalance()));

        if (blockchain.processPendingTransactions()) {
            miner.mine(blockchain);
        }

        // normal transactions
        ali.sendCoins(buse, 3.0, blockchain);
        buse.sendCoins(betul, 4.0, blockchain);

        if (blockchain.processPendingTransactions()) {
            miner.mine(blockchain);
        } else {
            System.out.println("No verified transactions to mine.");
        }


        List<Block> blocks = blockchain.getBlocks();
        for (Block block : blocks) {
            System.out.println("Block Hash: " + block.calculateHash());
        }

        System.out.println("On-chain balance Ali: " + blockchain.getBalance("Ali"));
        System.out.println("On-chain balance Buse: " + blockchain.getBalance("Buse"));
        System.out.println("On-chain balance Betul: " + blockchain.getBalance("Betul"));
        System.out.println("On-chain balance Miner1: " + blockchain.getBalance("Miner1"));
    }
}
