package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.exception.BadRequestException;
import com.example.Bibliotech_backend.model.PremiumPackage;
import com.example.Bibliotech_backend.repository.PremiumPackageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PremiumPackageService {

    private static final Logger logger = LoggerFactory.getLogger(PremiumPackageService.class);

    @Autowired
    private PremiumPackageRepository premiumPackageRepository;

    @Autowired
    private IdGeneratorService idGeneratorService;

    /**
     * Lấy tất cả các gói Premium đang hoạt động.
     *
     * @return Danh sách các gói Premium đang hoạt động.
     */
    public List<PremiumPackage> getAllActivePackages() {
        return premiumPackageRepository.findByIsActiveTrue();
    }

    /**
     * Lấy tất cả các gói Premium.
     *
     * @return Danh sách tất cả các gói Premium.
     */
    public List<PremiumPackage> getAllPackages() {
        return premiumPackageRepository.findAll();
    }

    /**
     * Lấy thông tin gói Premium theo ID.
     *
     * @param packageId ID của gói Premium.
     * @return Thông tin gói Premium.
     * @throws BadRequestException Nếu không tìm thấy gói Premium.
     */
    public PremiumPackage getPackageById(Integer packageId) {
        return premiumPackageRepository.findById(packageId)
                .orElseThrow(() -> new BadRequestException("Premium package not found with id: " + packageId));
    }

    /**
     * Thêm gói Premium mới.
     *
     * @param premiumPackage Đối tượng {@link PremiumPackage} chứa thông tin gói Premium.
     * @return {@link PremiumPackage} đã lưu.
     * @throws BadRequestException Nếu tên gói đã tồn tại.
     */
    @Transactional
    public PremiumPackage addPremiumPackage(PremiumPackage premiumPackage) {
        logger.debug("Adding new premium package: {}", premiumPackage.getPackageName());

        // Kiểm tra tên gói đã tồn tại chưa
        if (premiumPackageRepository.existsByPackageName(premiumPackage.getPackageName())) {
            throw new BadRequestException("Package name already exists");
        }

        // Tạo ID mới nếu chưa có
        if (premiumPackage.getPackageId() == null) {
            int packageId = idGeneratorService.generatePremiumPackageId();
            premiumPackage.setPackageId(packageId);
        }

        // Mặc định là hoạt động nếu chưa được thiết lập
        if (premiumPackage.getIsActive() == null) {
            premiumPackage.setIsActive(true);
        }

        // Lưu gói Premium
        return premiumPackageRepository.save(premiumPackage);
    }

    /**
     * Cập nhật thông tin gói Premium.
     *
     * @param packageId ID của gói Premium cần cập nhật.
     * @param premiumPackage Đối tượng {@link PremiumPackage} chứa thông tin cập nhật.
     * @return {@link PremiumPackage} đã cập nhật.
     * @throws BadRequestException Nếu không tìm thấy gói Premium hoặc tên gói đã tồn tại.
     */
    @Transactional
    public PremiumPackage updatePackage(Integer packageId, PremiumPackage premiumPackage) {
        PremiumPackage existingPackage = getPackageById(packageId);

        // Kiểm tra tên gói đã tồn tại chưa (nếu tên thay đổi)
        if (!existingPackage.getPackageName().equals(premiumPackage.getPackageName()) &&
                premiumPackageRepository.existsByPackageName(premiumPackage.getPackageName())) {
            throw new BadRequestException("Package name already exists");
        }

        // Cập nhật thông tin
        premiumPackage.setPackageId(packageId);
        return premiumPackageRepository.save(premiumPackage);
    }

    /**
     * Thay đổi trạng thái hoạt động của gói Premium.
     *
     * @param packageId ID của gói Premium.
     * @param isActive Trạng thái hoạt động mới.
     * @return {@link PremiumPackage} đã cập nhật.
     * @throws BadRequestException Nếu không tìm thấy gói Premium.
     */
    @Transactional
    public PremiumPackage toggleActiveStatus(Integer packageId, boolean isActive) {
        PremiumPackage existingPackage = getPackageById(packageId);
        existingPackage.setIsActive(isActive);
        return premiumPackageRepository.save(existingPackage);
    }

    /**
     * Xóa gói Premium.
     *
     * @param packageId ID của gói Premium cần xóa.
     * @throws BadRequestException Nếu không tìm thấy gói Premium.
     */
    @Transactional
    public void deletePackage(Integer packageId) {
        if (!premiumPackageRepository.existsById(packageId)) {
            throw new BadRequestException("Premium package not found with id: " + packageId);
        }
        premiumPackageRepository.deleteById(packageId);
    }
}