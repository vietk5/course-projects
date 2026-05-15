package com.demo.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility để track các transaction đã xử lý, tránh duplicate order Trong
 * production nên dùng Redis hoặc database
 */
public class TransactionTracker {

    // Thread-safe set để lưu các txnRef đã xử lý
    private static final Set<String> processedTransactions
            = Collections.synchronizedSet(new HashSet<>());

    /**
     * Kiểm tra xem transaction đã được xử lý chưa
     */
    public static boolean isProcessed(String txnRef) {
        return processedTransactions.contains(txnRef);
    }

    /**
     * Đánh dấu transaction đã xử lý
     */
    public static boolean markAsProcessed(String txnRef) {
        return processedTransactions.add(txnRef);
    }

    /**
     * Xóa transaction khỏi danh sách (dùng khi rollback)
     */
    public static void remove(String txnRef) {
        processedTransactions.remove(txnRef);
    }

    /**
     * Clear tất cả (dùng cho testing)
     */
    public static void clear() {
        processedTransactions.clear();
    }

    /**
     * Lấy số lượng transaction đã xử lý
     */
    public static int getProcessedCount() {
        return processedTransactions.size();
    }
}
