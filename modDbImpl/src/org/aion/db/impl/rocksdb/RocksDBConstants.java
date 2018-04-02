package org.aion.db.impl.rocksdb;

public class RocksDBConstants {
    public static int MAX_OPEN_FILES = 1024;
    public static int BLOCK_SIZE = 4096;
    public static int WRITE_BUFFER_SIZE = 16 * 1024 * 1024;

    private RocksDBConstants() {}
}
