package comp.finalproject.user.controller.web;

import comp.finalproject.user.entity.*;
import comp.finalproject.user.repository.*;
import comp.finalproject.user.service.CartItemService;
import comp.finalproject.user.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

@Controller
public class CartController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/pages/cart")
    public String cartView(Model model, Principal principal) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);
        List<String> statusList = Arrays.asList("Cash", "Transfer");
        model.addAttribute("statusList", statusList);

        List<CartItem> userCartItems = cartItemService.findByUserId(currentUser.getId());
        // Menghitung total harga
//        float totalPrice = 0;
//        for (CartItem cartItem : userCartItems) {
//            totalPrice += cartItem.getSubtotal();
//        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userCarts", userCartItems);
//        model.addAttribute("totalPrice", totalPrice);
        return "page/cart";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam(value = "itemId", required = false) Long itemId,
                            @RequestParam(value = "userId", required = false) Long userId,
                            Model model) {
        if (itemId != null && userId != null) {
            cartItemService.addItemToCart(itemId, userId);
        } else {
            // Handle invalid input or display an error message
        }
        return "redirect:/pages/cart";
    }

    @RequestMapping("/pages/deletecarts/{id}")
    public String deleteCart(@PathVariable(name = "id") long id) {
        CartItem cart = cartItemService.findById(id);
        if (cart != null) {
            cartItemService.deleteById(id);
        }
        return "redirect:/pages/cart";
    }

    @PostMapping("/purchaseCart")
    public String purchaseCart(@RequestParam("cartId") List<Long> cartIds,
                               @RequestParam("quantity") List<Integer> quantities,
                               @RequestParam("metodePembayaran") String metodePembayaran,
                               @RequestParam(value = "cashInput", required = false) Float cashInput,
                               Principal principal) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);

        // Validasi input
        if (cartIds.size() != quantities.size()) {
            return "redirect:/pages/cart?error";
        }
        List<Cart> carts = cartRepository.findByUserId(currentUser.getId());

        // Variabel untuk menyimpan total harga
        float totalPrice = 0;

        // Menginisialisasi list untuk menyimpan item yang akan dibeli
        List<SaleItem> saleItems = new ArrayList<>();

        // Looping melalui setiap item di cart
        for (int i = 0; i < cartIds.size(); i++) {
            Long cartId = cartIds.get(i);
            int quantity = quantities.get(i);

            // Mendapatkan CartItem berdasarkan cartId
            CartItem cartItem = cartItemRepository.findById(cartId).orElse(null);

            if (cartItem != null) {
                // Menghitung subtotal untuk setiap item
                float subtotal = cartItem.getItem().getAmount() * quantity;
                totalPrice += subtotal;

                // Membuat SaleItem untuk setiap item dalam cart
                SaleItem saleItem = new SaleItem();
                saleItem.setItem(cartItem.getItem());
                saleItem.setQuantity(quantity);
                saleItem.setPrice(cartItem.getItem().getAmount());
                saleItems.add(saleItem);

                // Update stok dan totalSold item
                Item item = cartItem.getItem();
                item.setQuantity(item.getQuantity() - quantity); // Kurangi stok
                item.setTotalSold(item.getTotalSold() + quantity); // Tambah total terjual
                itemRepository.save(item); // Simpan perubahan item
            }
        }

        // Menghitung kembalian
        Float change = (cashInput != null) ? cashInput - totalPrice : 0.0f;

        // Membuat objek Sale
        Sale sale = new Sale();
        sale.setCashInput(cashInput);
        sale.setChangePayment(change);
        sale.setUser(currentUser);
        sale.setTotal(totalPrice);
        sale.setDate(new Date());
        sale.setMetodePembayaran(metodePembayaran);
        if (metodePembayaran.equals("Cash")) {
            sale.setStatus("Success");
        } else if (metodePembayaran.equals("Transfer")) {
            sale.setStatus("Menunggu");
            sale.setCashInput(0.0f);

        }
        // Menyimpan Sale ke database
        salesRepository.save(sale);

        // Menghubungkan Sale dengan SaleItem dan menyimpan SaleItem
        for (SaleItem saleItem : saleItems) {
            saleItem.setSale(sale);
            saleItemRepository.save(saleItem);
        }

        // Menghapus item dari cart
        for (Long cartId : cartIds) {
            cartItemRepository.deleteById(cartId);
        }

        return "redirect:/pages/sales";
    }

//    @PostMapping("/updatecart")
//    public Map<String, Object> updateCart(@RequestParam("cartId") long cartId, @RequestParam("quantity") int quantity) {
//        Cart cart = cartService.findById(cartId);
//        if (cart != null && quantity > 0) {
//            cart.setQuantity(quantity);
//            cartRepository.save(cart); // Simpan pembaruan quantity di database
//
//            int subtotal = cart.getQuantity() * cart.getItem().getAmount();
//            int total = cartService.calculateTotalPriceForUser(); // Method untuk menghitung total semua cart
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("subtotal", subtotal);
//            response.put("total", total);
//            return response;
//        }
//        return null;
//    }
}
