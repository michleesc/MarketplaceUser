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
import java.util.List;

@Controller
public class SalesController {

    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    public SalesController(SalesRepository salesRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.salesRepository = salesRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    // handler methods go here...
    @GetMapping("/pages/sales")
    public String showSales(Model model, Principal principal) {
        // Mendapatkan informasi pengguna yang sedang login
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);

        List<Sale> userSales = salesRepository.findByUserIdOrderByIdDesc(currentUser.getId());
        model.addAttribute("userSales", userSales);
        model.addAttribute("currentUser", currentUser);
        return "sale/sales";
    }


    @RequestMapping("/pages/deletesales/{id}")
    public String delete(@PathVariable(name = "id") long id) {
        Sale sale = salesRepository.findById(id);
        if (sale != null) {
            // Mengembalikan total_sold dan quantity item ke nilai semula
            Item item = sale.getItem();
            int quantity = sale.getQuantity();

            item.setTotalSold(item.getTotalSold() - quantity);
            item.setQuantity(item.getQuantity() + quantity);
            itemRepository.save(item);

            // Menghapus penjualan dari repository
            salesRepository.deleteById(id);
        }
        return "redirect:/pages/sales";
    }

    @GetMapping("/pages/searchsales")
    public String searchSalesByItemName(Model model, @RequestParam(name = "keyword", required = false) String itemName, Principal principal) {
        List<Sale> sales;
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);
        model.addAttribute("currentUser", currentUser);
        if (itemName != null && !itemName.isEmpty()) {
            // Lakukan pencarian berdasarkan nama barang
            sales = salesRepository.findByUser_IdAndItem_NameContaining(currentUser.getId(), itemName);

        } else {
            // Tampilkan semua penjualan jika tidak ada kata kunci pencarian
            sales = salesRepository.findAll();
        }
        model.addAttribute("userSales", sales); // Menambahkan hasil pencarian ke model
        return "sale/listsales"; // Nama tampilan Thymeleaf yang akan ditampilkan
    }


    /*@RequestMapping("/newsales")
    public String showNewForm(Model model) {
        Sale sale = new Sale();
        model.addAttribute("sale", sale);

        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);

        return "salesnew";
    }*/

    /*@RequestMapping(value = "/savesales", method = RequestMethod.POST)
    public String save(@ModelAttribute("sale") Sale sale) {
        salesRepository.save(sale);

        return "redirect:/sales";
    }*/

    /*@RequestMapping("/editsales/{id}")
    public ModelAndView showEditForm(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("salesedit");
        Sale sale = salesRepository.findById(id);
        mav.addObject("sale", sale);

        return mav;
    }*/

    /*@RequestMapping(value = "/updatesales", method = RequestMethod.POST)
    public String update(@ModelAttribute("sale") Sale sale) {
        salesRepository.save(sale);

        return "redirect:/sales";
    }*/


}