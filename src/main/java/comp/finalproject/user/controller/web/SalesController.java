package comp.finalproject.user.controller.web;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import comp.finalproject.user.entity.Item;
import comp.finalproject.user.entity.Sale;
import comp.finalproject.user.entity.User;
import comp.finalproject.user.repository.ItemRepository;
import comp.finalproject.user.repository.SalesRepository;
import comp.finalproject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class SalesController {

    @Value("${upload.image.item}")
    private String uploadItemImage;
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
    public String searchSalesByItemName(Model model,
                                        @RequestParam(name = "keyword", required = false) String itemName,
                                        Principal principal) {
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

            // Mengupload path ke database
            String uploadDatabase = "/image/payment/";

            String filePathUser = Paths.get(uploadItemImage, fileName).toString(); // user
            String filePathDatabase = Paths.get(uploadDatabase, fileName).toString(); // database

            payment.transferTo(new File(filePathUser));

            // Mengganti backslash menjadi slash, dan memasukkan path direktori
            String imagePathForEndpoint = filePathDatabase.replace("\\", "/");

            // Setel path gambar ke model
            sale.setProofOfPayment(imagePathForEndpoint);
            sale.setStatus("Success");
        }

        salesRepository.save(sale);

        return "redirect:/pages/sales";
    }

    @GetMapping("/image/payment/{imageName:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String imageName) throws IOException {
        Path imagePath = Paths.get(uploadItemImage + imageName);

        Resource resource = new org.springframework.core.io.FileUrlResource(imagePath.toUri().toURL());

        // Periksa apakah file ada dan dapat diakses
        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE) // Ganti dengan tipe media yang sesuai
                    .body(resource);
        } else {
            // Gagal menemukan file, kamu dapat mengembalikan contoh gambar default atau respons yang sesuai.
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nota/{id}")
    public void generateNotaPDFCash(@PathVariable Long id, HttpServletResponse response) {
        Sale sale = salesRepository.findById(id).orElse(null);
        if (sale == null) {
            // Handle jika penjualan tidak ditemukan
            System.out.println("Gagal menemukan penjualan");
            return;
        }

        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(response.getOutputStream()));
            Document document = new Document(pdfDocument);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=nota" + sale.getId() + ".pdf");

            // Format tanggal
            Date date = sale.getDate();
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = localDateTime.format(formatter);

            addCenteredText(document, "===========================================");
            addCenteredText(document, "Nota Penjualan");
            addCenteredText(document, "===========================================");
            addCenteredText(document, "Cashier : " + sale.getUser().getName());
            addCenteredText(document, "ID Penjualan: " + sale.getId() + " Metode Pembayaran: " + sale.getMetodePembayaran());
            addCenteredText(document, "Tanggal: " + formattedDate + " Status: " + sale.getStatus());
            addCenteredText(document, "===========================================");

            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            String formattedTotal = decimalFormat.format(sale.getSubtotal());
            String formattedAmount = decimalFormat.format(sale.getItem().getAmount());

            addCenteredText(document, sale.getQuantity() + "   " + sale.getItem().getName() + "   " + formattedAmount);
            addCenteredText(document, "Total                                                                " + formattedTotal);
            addCenteredText(document, "===========================================");

            if (sale.getMetodePembayaran().equals("Cash")) {
                if (sale.getCashInput() != null && sale.getChangePayment() != null) {
                    addCenteredText(document, "Cash Paid                                                                " + decimalFormat.format(sale.getCashInput()));
                    addCenteredText(document, "Change                                                                " + decimalFormat.format(sale.getChangePayment()));
                    addCenteredText(document, "===========================================");
                }
            }
            addCenteredText(document, "Thank You!");
            addCenteredText(document, "Please Come Again");

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCenteredText(Document document, String text) {
        Paragraph paragraph = new Paragraph(text)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12); // Sesuaikan dengan ukuran font yang diinginkan
        document.add(paragraph);
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