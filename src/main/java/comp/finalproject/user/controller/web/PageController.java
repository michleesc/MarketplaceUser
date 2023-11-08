package comp.finalproject.user.controller.web;

import comp.finalproject.user.entity.Item;
import comp.finalproject.user.entity.Sale;
import comp.finalproject.user.entity.User;
import comp.finalproject.user.repository.ItemRepository;
import comp.finalproject.user.repository.SalesRepository;
import comp.finalproject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Controller
public class PageController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private SalesRepository salesRepository;

    public PageController(UserRepository userRepository, ItemRepository itemRepository, SalesRepository salesRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.salesRepository = salesRepository;
    }


    // handler method to handle pages after login
    @GetMapping("/pages/newproduct")
    public String pagesNewProduct(Model model, Principal principal){
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);
        // Menyimpan pengguna yang sedang login dalam model
        model.addAttribute("currentUser", currentUser);

        // Mengambil 8 item terbaru berdasarkan ID
        List<Item> listItem = itemRepository.findTop8ByOrderByIdDesc();

        model.addAttribute("listItem", listItem);
        return "page/pagenewproduct";
    }

    @GetMapping("/pages/allproduct")
    public String pagesAllProduct(Model model, Principal principal){
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);
        // Menyimpan pengguna yang sedang login dalam model
        model.addAttribute("currentUser", currentUser);

        // Mengambil 8 item terbaru berdasarkan ID
        List<Item> listItem = itemRepository.findAll();

        model.addAttribute("listItem", listItem);
        return "page/pageallproduct";
    }

    @GetMapping("/pages/popularitem")
    public String pagesPopularItem(Model model, Principal principal){
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);
        // Menyimpan pengguna yang sedang login dalam model
        model.addAttribute("currentUser", currentUser);

        // Mengambil 8 item terbaru berdasarkan ID
        List<Item> listItem = itemRepository.findTop8ByTotalSoldGreaterThanOrderByTotalSoldDesc(0);

        model.addAttribute("listItem", listItem);
        return "page/pagepopularitem";
    }
    @RequestMapping("/pages/buyItem/{itemId}")
    public String buyItem(@PathVariable("itemId") long itemId, Model model, Principal principal) {
        // Mendapatkan informasi pengguna yang sedang login
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);

        // Mendapatkan informasi barang yang akan dibeli
        Item item = itemRepository.findById(itemId);

        // untuk menampilkan produk terbaru dibawah nya
        List<Item> listItem = itemRepository.findTop4ByOrderByIdDesc();
        model.addAttribute("listItem", listItem);

        // Menampilkan informasi pembelian kepada pengguna
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("item", item);

        return "page/pagebuyitem";
    }

    @PostMapping("/purchase")
    public String purchaseItem(@RequestParam("itemId") long itemId, @RequestParam("quantity") int quantity, Principal principal, Model model) {
        // Mendapatkan informasi pengguna yang sedang login
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);

        // Mendapatkan informasi barang yang akan dibeli
        Item item = itemRepository.findById(itemId);

        if (item.getQuantity() >= quantity) {
            // Jumlah quantity cukup, lakukan proses pembelian
            // Kurangi jumlah quantity yang tersedia
            item.setQuantity(item.getQuantity() - quantity);
            item.setTotalSold(item.getTotalSold() + quantity);
            itemRepository.save(item);

            // Menghitung subtotal berdasarkan harga barang dan jumlah yang dipilih
            float subtotal = item.getAmount() * quantity;

            // Membuat objek Sale baru berdasarkan barang yang dibeli dan jumlah yang dipilih
            Sale sale = new Sale(item, quantity, subtotal, currentUser.getId());
            sale.setDate(new Date());
            sale.setStatus("Menunggu Konfirmasi");

            // Mengaitkan sale dengan pengguna yang sedang login
            currentUser.getSales().add(sale);

            // Menyimpan perubahan pada pengguna
            salesRepository.save(sale);

            // Menampilkan informasi pembelian kepada pengguna
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("sales", currentUser.getSales());
            model.addAttribute("subtotal", subtotal);

            return "redirect:/pages/sales";
        } else {
            // Jumlah quantity tidak mencukupi, berikan peringatan
            String warningMessage = "Jumlah quantity tidak mencukupi.";
            model.addAttribute("warningMessage", warningMessage);
            return "redirect:/pages/sales";
        }
    }

    @GetMapping("/pages")
    public String listProducts(Model model, @RequestParam(name = "keyword", required = false) String keyword, Principal principal) {
        List<Item> items;
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);
        // Menyimpan pengguna yang sedang login dalam model
        model.addAttribute("currentUser", currentUser);

        if (keyword != null) {
            items = itemRepository.findByNameContaining(keyword);
        } else {
            items = itemRepository.findAll();
        }
        model.addAttribute("listItem", items);
        return "page/list";
    }
}