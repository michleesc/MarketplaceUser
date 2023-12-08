package comp.finalproject.user.repository;

import comp.finalproject.user.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAll();
    List<Item> findByNameContaining(String keyword);
    Item findById(long id);
    List<Item> findTop8ByDeletedFalseOrderByIdDesc();
    List<Item> findTop4ByDeletedFalseOrderByIdDesc();
    List<Item> findTop8ByDeletedFalseAndTotalSoldGreaterThanOrderByTotalSoldDesc(int totalSold);
    List<Item> findTop4ByDeletedFalseAndTotalSoldGreaterThanOrderByTotalSoldDesc(int totalSold);
    List<Item> findByDeletedFalseOrderByCreatedAtDesc();
}
