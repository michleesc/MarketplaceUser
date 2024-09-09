package comp.finalproject.user.repository;

import comp.finalproject.user.entity.Cart;
import comp.finalproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findAll();
    Cart findById(long id);
    List<Cart> findByUserId(Long userId);
    Optional<Cart> findByUser(User user);
//    Cart findByUserIdAndItemId(Long userId, Long itemId);
}
