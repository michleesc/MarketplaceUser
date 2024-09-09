package comp.finalproject.user.repository;

import comp.finalproject.user.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findById(long id);
    List<CartItem> findByCart_User_Id(Long userId);
//    List<CartItem> findByUserId(Long userId);
    CartItem findByCart_IdAndItem_Id(Long cartId, Long itemId);
}
