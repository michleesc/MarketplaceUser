package comp.finalproject.user.repository;

import comp.finalproject.user.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Repository
public interface SalesRepository extends JpaRepository<Sale, Long> {

    List<Sale> findAll();
    List<Sale> findByUserId(long userId);
    Sale findById(long id);

    List<Sale> findByUserIdOrderByIdDesc(long userId);

    List<Sale> findByUser_IdAndItem_NameContainingOrderByIdDesc(Long userId, String itemName);


    /*int deleteById(int id);

    void save(Sale sale);

    void update(Sale sale);*/

}
