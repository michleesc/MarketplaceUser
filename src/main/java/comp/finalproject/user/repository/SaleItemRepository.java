package comp.finalproject.user.repository;

import comp.finalproject.user.entity.Item;
import comp.finalproject.user.entity.Sale;
import comp.finalproject.user.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySale_UserIdOrderBySaleIdDesc (Long saleId);
    List<SaleItem> findBySaleIdOrderByIdDesc(Long saleId);
    List<SaleItem> findBySaleUserId(Long userId);
    List<SaleItem> findBySaleId(Long saleId);
    @Transactional
    void deleteBySaleId(Long saleId);
    SaleItem findFirstBySaleUserIdOrderBySaleIdDesc(Long userId);
}
