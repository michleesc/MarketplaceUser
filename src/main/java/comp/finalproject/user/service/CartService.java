package comp.finalproject.user.service;

import comp.finalproject.user.entity.Cart;
import comp.finalproject.user.entity.CartItem;
import comp.finalproject.user.entity.Item;
import comp.finalproject.user.entity.User;
import comp.finalproject.user.repository.CartItemRepository;
import comp.finalproject.user.repository.CartRepository;
import comp.finalproject.user.repository.ItemRepository;
import comp.finalproject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public Cart findById(long id) {
        return cartRepository.findById(id);
    }

    public List<Cart> findByUserId(long id) {
        return cartRepository.findByUserId(id);
    }

    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    public void deleteById(long id) {cartRepository.deleteById(id);}

//    @Transactional
//    public void updateCartQuantity(Long cartId, int quantity) {
//        // Mengambil entitas Cart berdasarkan ID
//        Cart cart = cartRepository.findById(cartId)
//                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
//
//        // Mengambil entitas Item yang terkait dengan Cart
//        Item item = cart.getItem();
//
//        // Validasi kuantitas
//        if (quantity <= 0 || quantity > item.getQuantity()) {
//            throw new IllegalArgumentException("Invalid quantity: " + quantity);
//        }
//
//        // Mengupdate kuantitas dan subtotal pada Cart
//        cart.setQuantity(quantity);
//        cart.setSubtotal(quantity * item.getAmount());
//
//        // Mengupdate stok Item jika perlu
//        item.setQuantity(item.getQuantity() - (quantity - cart.getQuantity())); // Adjust quantity based on previous and new quantity
//
//        // Menyimpan perubahan
//        cartRepository.save(cart);
//        itemRepository.save(item);
//    }

//    public Cart addItemToCart(Long itemId, Long userId) {
//        // Mengambil Item dengan memeriksa apakah item ada
//        Optional<Item> itemOptional = itemRepository.findById(itemId);
//        if (!itemOptional.isPresent()) {
//            throw new RuntimeException("Item not found");
//        }
//        Item item = itemOptional.get();
//
//        // Mengambil User dengan memeriksa apakah user ada
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (!userOptional.isPresent()) {
//            throw new RuntimeException("User not found");
//        }
//        User user = userOptional.get();
//
//        // Mencari cart yang sudah ada berdasarkan userId dan itemId
//        CartItem existingCartItem = cartItemRepository.findByUserIdAndItemId(userId, itemId);
//        CartItem cartItem;
//
//        if (existingCartItem != null) {
//            // Jika cart sudah ada, tambahkan quantity dan update subtotal
//            cartItem = existingCartItem;
//            cartItem.setQuantity(cartItem.getQuantity() + 1);
//            cartItem.setSubtotal(cartItem.getQuantity() * item.getAmount());
//        } else {
//            // Jika cart belum ada, buat cart baru
//            cart = new Cart();
//            cart.setUser(user);
//            cart.setItem(item);
//            cart.setQuantity(1);
//            cart.setSubtotal(item.getAmount());
//        }
//
//        // Menyimpan cart ke dalam repository
//        cartRepository.save(cart);
//        return cart;
//    }
}
