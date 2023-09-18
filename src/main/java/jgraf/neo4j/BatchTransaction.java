package jgraf.neo4j;

import jgraf.utils.BatchUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchTransaction implements AutoCloseable {
    private GraphDatabaseService graphDb;
    private final int batchSize;
    private int currentFill;
    private Transaction tx;
    private final static Logger logger = LoggerFactory.getLogger(BatchTransaction.class);

    public BatchTransaction(GraphDatabaseService graphDb) {
        this(graphDb, BatchUtils.BATCH_SIZE);
    }

    public BatchTransaction(GraphDatabaseService graphDb, int batchSize) {
        this.graphDb = graphDb;
        this.batchSize = batchSize;
        init();
    }

    private void init() {
        currentFill = 0;
        tx = graphDb.beginTx();
    }

    public void forward() {
        forward(1);
    }

    public void forward(int steps) {
        currentFill += steps;
        if (currentFill >= batchSize) commit();
    }

    public void commit() {
        close();
        init();
    }

    @Override
    public void close() {
        tx.commit();
        if (currentFill != 0) logger.info("Committed a batch transaction of size {}", currentFill);
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getCurrentFill() {
        return currentFill;
    }

    public Transaction getTx() {
        return tx;
    }
}
