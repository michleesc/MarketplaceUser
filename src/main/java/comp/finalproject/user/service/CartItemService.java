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


@Service
public class CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    public CartItem findById(long id) {
        return cartItemRepository.findById(id);
    }

    public List<CartItem> findByUserId(Long id) {
        return cartItemRepository.findByCart_User_Id(id);
    }


//    public List<CartItem> findByUserId(long id) {
//        return cartItemRepository.findByUserId(id);
//    }

    public List<CartItem> findAll() {
        return cartItemRepository.findAll();
    }

    public void deleteById(long id) {
        if (cartItemRepository.existsById(id)) {
            cartItemRepository.deleteById(id);
        } else {
            throw new RuntimeException("CartItem not found with id: " + id);
        }
    }

    @Transactional
    public void updateCartItemQuantity(Long cartItemId, int newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found with id: " + cartItemId));

        Item item = cartItem.getItem();

        if (newQuantity <= 0 || newQuantity > item.getQuantity() + cartItem.getQuantity()) {
            throw new IllegalArgumentException("Invalid quantity: " + newQuantity);
        }

        int oldQuantity = cartItem.getQuantity();
        cartItem.setQuantity(newQuantity);
        cartItem.setSubtotal(newQuantity * item.getAmount());

        item.setQuantity(item.getQuantity() + oldQuantity - newQuantity);

        cartItemRepository.save(cartItem);
        itemRepository.save(item);
    }

    public CartItem addItemToCart(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem existingCartItem = cartItemRepository.findByCart_IdAndItem_Id(cart.getId(), itemId);
        CartItem cartItem;

        if (existingCartItem != null) {
            // Jika CartItem sudah ada, tambahkan quantity-nya
            cartItem = existingCartItem;
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItem.setSubtotal(cartItem.getQuantity() * item.getAmount());
            cartItemRepository.save(cartItem);
            return existingCartItem;
        } else {
            // Jika CartItem belum ada, buat yang baru
            cartItem = new CartItem();
            cartItem.setCart(cart); // Set Cart yang sudah ada
            cartItem.setItem(item);
            cartItem.setQuantity(1);
            cartItem.setSubtotal(item.getAmount());
            cartItemRepository.save(cartItem);
            return cartItem;
        }
    }
}
