package comp.finalproject.user.controller.web;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

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
            sales = salesRepository.findByUser_IdAndItem_NameContainingOrderByIdDesc(currentUser.getId(), itemName);

        } else {
            // Tampilkan semua penjualan jika tidak ada kata kunci pencarian
            sales = salesRepository.findAll();
        }
        model.addAttribute("userSales", sales); // Menambahkan hasil pencarian ke model
        return "sale/listsales"; // Nama tampilan Thymeleaf yang akan ditampilkan
    }

    @RequestMapping(value = "/savepayment", method = RequestMethod.POST)
    public String savePayment(@RequestParam("payment") MultipartFile payment,
                              @RequestParam("id_sale") long idSale) throws IOException {
        Sale sale = salesRepository.findById(idSale);
        if (!payment.isEmpty()) {
            // Simpan gambar ke server atau lakukan operasi lain sesuai kebutuhan Anda
            String fileName = UUID.randomUUID().toString() + "-" + payment.getOriginalFilename();
            // Mengupload file ke user
            String uploadDirUser = "C:\\Users\\ASUS\\OneDrive - Microsoft 365\\Documents\\TIA - Academy\\MerdekaFinalProjectMarketplaceUser\\MerdekaFinalProjectMarketplaceUser\\src\\main\\resources\\static\\image\\payment";

            // Mengupload path ke database
            String uploadDatabase = "/image/payment/";

            String filePathUser = Paths.get(uploadDirUser, fileName).toString(); // user
            String filePathDatabase = Paths.get(uploadDatabase, fileName).toString(); // database

            payment.transferTo(new File(filePathUser));

            // Setel path gambar ke model
            sale.setProofOfPayment(filePathDatabase);
            sale.setStatus("Success");
        }

        salesRepository.save(sale);

        return "redirect:/pages/sales";
    }

    @GetMapping("/nota/{id}")
    public void generateNotaPDF(@PathVariable Long id, HttpServletResponse response) {
        Sale sale = salesRepository.findById(id).orElse(null);

        if (sale != null) {
            try {
                // Inisialisasi dokumen PDF
                PdfDocument pdfDocument = new PdfDocument(new PdfWriter(response.getOutputStream()));
                Document document = new Document(pdfDocument);

                // Setup respons HTTP untuk jenis konten PDF
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=nota.pdf");

                // Tambahkan konten ke PDF, sesuai dengan data penjualan
                Paragraph divider = new Paragraph("=====================");
                divider.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(divider);

                Paragraph title = new Paragraph("Nota Penjualan");
                title.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(title);

                Paragraph divider2 = new Paragraph("=====================");
                divider2.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(divider2);

                Paragraph cashierName = new Paragraph("Cashier : " + sale.getUser().getName());
                cashierName.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(cashierName);

                Paragraph saleId = new Paragraph("ID Penjualan: " + sale.getId());
                saleId.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(saleId);

                Paragraph date = new Paragraph("Tanggal: " + sale.getDate());
                date.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(date);

                document.add(divider);

                // ... Tambahkan rincian item, total, dan lainnya ke PDF
                DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                String formattedTotal = decimalFormat.format(sale.getSubtotal());
                String formattedAmount = decimalFormat.format(sale.getItem().getAmount());

                Paragraph itemDetails = new Paragraph(sale.getQuantity() + " " + sale.getItem().getName() + " " + formattedAmount);
                itemDetails.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(itemDetails);


                Paragraph total = new Paragraph("Total : " + formattedTotal);
                total.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(total);

                document.add(divider);

                // Ucapan Terimakasih
                Paragraph thankYou = new Paragraph("Thank You!");
                thankYou.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(thankYou);

                Paragraph pleaseComeAgain = new Paragraph("Please Come Again");
                pleaseComeAgain.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(pleaseComeAgain);

                // Tutup dokumen
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle jika penjualan tidak ditemukan
            System.out.println("Gagal menemukan penjualan");
        }
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