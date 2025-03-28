package com.example.Bibliotech_backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Dịch vụ sinh ID tự động cho các bảng trong cơ sở dữ liệu.
 * <p>
 * Lớp này sử dụng {@link ReentrantLock} để đảm bảo tính đồng bộ khi tạo ID mới.
 * Phương thức của lớp sẽ lấy ID lớn nhất hiện có trong bảng và tăng lên 1 để tạo ID tiếp theo.
 * </p>
 */
@Service
public class IdGeneratorService {
    /**
     * Khóa đồng bộ để đảm bảo chỉ một luồng có thể tạo ID tại một thời điểm.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * EntityManager dùng để thực hiện truy vấn cơ sở dữ liệu.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Tạo một ID mới cho người dùng.
     * <p>
     * Phương thức này lấy ID lớn nhất hiện có trong bảng "Users" và tăng lên 1.
     * Quá trình này được bảo vệ bằng {@link ReentrantLock} để tránh điều kiện tranh chấp (race condition).
     * </p>
     *
     * @return ID mới của người dùng.
     */
    @Transactional
    public int generateUserId() {
        lock.lock(); // Khóa để đảm bảo an toàn luồng
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(user_id), 0) FROM Users"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Mở khóa để giải phóng tài nguyên
        }
    }

    /**
     * Tạo một ID mới cho sách.
     * <p>
     * Phương thức này tìm ID lớn nhất trong bảng "Books" và tăng lên 1.
     * Được bảo vệ bằng {@link ReentrantLock} để đảm bảo tính đồng bộ.
     * </p>
     *
     * @return ID mới của sách.
     */
    @Transactional
    public int generateBookId() {
        lock.lock(); // Khóa trước khi thực hiện truy vấn
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(book_id), 0) FROM Books"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Mở khóa để đảm bảo các luồng khác có thể tiếp tục
        }
    }

    /**
     * Tạo một ID mới cho danh mục.
     * <p>
     * Phương thức này tìm ID lớn nhất trong bảng "Categories" và tăng lên 1.
     * Được bảo vệ bằng {@link ReentrantLock} để đảm bảo tính đồng bộ.
     * </p>
     *
     * @return ID mới của danh mục.
     */
    @Transactional
    public int generateCategoryId() {
        lock.lock(); // Đảm bảo không có hai luồng nào truy cập cùng lúc
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(category_id), 0) FROM Categories"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Mở khóa ngay khi xong để tránh deadlock
        }
    }

    /**
     * Tạo một ID mới cho cultivation level.
     * <p>
     * Phương thức này tìm ID lớn nhất trong bảng "CultivationLevels" và tăng lên 1.
     * Được bảo vệ bằng {@link ReentrantLock} để đảm bảo tính đồng bộ.
     * </p>
     *
     * @return ID mới của cultivation level.
     */
    @Transactional
    public int generateCultivationLevelId() {
        lock.lock(); // Đảm bảo không có hai luồng nào truy cập cùng lúc
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(level_id), 0) FROM CultivationLevels"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Mở khóa ngay khi xong để tránh deadlock
        }
    }

    /**
     * Generate a new ID for deals.
     * <p>
     * This method finds the highest ID in the "Deals" table and increments it by 1.
     * Protected by {@link ReentrantLock} to ensure thread safety.
     * </p>
     *
     * @return New ID for deals.
     */
    @Transactional
    public int generateDealId() {
        lock.lock(); // Ensure no two threads access this at the same time
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(deal_id), 0) FROM Deals"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Unlock to allow other threads to proceed
        }
    }

    /**
     * Tạo một ID mới cho giao dịch.
     * <p>
     * Phương thức này tìm ID lớn nhất trong bảng "Transactions" và tăng lên 1.
     * Được bảo vệ bằng {@link ReentrantLock} để đảm bảo tính đồng bộ.
     * </p>
     *
     * @return ID mới của giao dịch.
     */
    @Transactional
    public int generateTransactionId() {
        lock.lock(); // Khóa để đảm bảo an toàn luồng
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(transaction_id), 0) FROM Transactions"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Mở khóa để giải phóng tài nguyên
        }
    }

    /**
     * Tạo một ID mới cho phương thức thanh toán.
     * <p>
     * Phương thức này tìm ID lớn nhất trong bảng "PaymentMethods" và tăng lên 1.
     * Được bảo vệ bằng {@link ReentrantLock} để đảm bảo tính đồng bộ.
     * </p>
     *
     * @return ID mới của phương thức thanh toán.
     */
    @Transactional
    public int generatePaymentMethodId() {
        lock.lock(); // Đảm bảo không có hai luồng nào truy cập cùng lúc
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(payment_method_id), 0) FROM PaymentMethods"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Mở khóa ngay khi xong để tránh deadlock
        }
    }

    /**
     * Tạo một ID mới cho gói Premium.
     * <p>
     * Phương thức này tìm ID lớn nhất trong bảng "PremiumPackages" và tăng lên 1.
     * Được bảo vệ bằng {@link ReentrantLock} để đảm bảo tính đồng bộ.
     * </p>
     *
     * @return ID mới của gói Premium.
     */
    @Transactional
    public int generatePremiumPackageId() {
        lock.lock(); // Đảm bảo không có hai luồng nào truy cập cùng lúc
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(package_id), 0) FROM PremiumPackages"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Mở khóa ngay khi xong để tránh deadlock
        }
    }

    //
    @Transactional
    public int generateWishlistId() {
        lock.lock(); // Đảm bảo không có hai luồng nào truy cập cùng lúc
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT COALESCE(MAX(wishlist_id), 0) FROM Wishlist"
            );
            Number result = (Number) query.getSingleResult();
            return result.intValue() + 1;
        } finally {
            lock.unlock(); // Mở khóa ngay khi xong để tránh deadlock
        }
    }
}

