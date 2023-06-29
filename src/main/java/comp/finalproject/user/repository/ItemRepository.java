package comp.finalproject.user.repository;

import comp.finalproject.user.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAll();

    Item findById(long id);

    List<Item> findTop8ByOrderByIdDesc();

    List<Item> findTop4ByOrderByIdDesc();

    // Menampilkan semua product
    List<Item> findByTotalSoldGreaterThanOrderByTotalSoldDesc(int totalSold);
    // Menampilkan 8 product saja
    List<Item> findTop8ByTotalSoldGreaterThanOrderByTotalSoldDesc(int totalSold);

    List<Item> findTop4ByTotalSoldGreaterThanOrderByTotalSoldDesc(int totalSold);


    /*void deleteById(long id);


    void update(Item item);

    int deleteById(int id);

    void save(Item item);

    */

}
