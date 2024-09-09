package comp.finalproject.user.controller.web;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import comp.finalproject.user.entity.*;
import comp.finalproject.user.repository.ItemRepository;
import comp.finalproject.user.repository.SaleItemRepository;
import comp.finalproject.user.repository.SalesRepository;
import comp.finalproject.user.repository.UserRepository;
import comp.finalproject.user.service.CartItemService;
import comp.finalproject.user.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private CartItemService cartItemService;

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

        // menampilkan size dari cart
        List<CartItem> userCartItems = cartItemService.findByUserId(currentUser.getId());

        // Mendapatkan semua Sales untuk pengguna
        List<Sale> userSales = salesRepository.findByUserIdOrderByIdDesc(currentUser.getId());

        // Mendapatkan semua SaleItems untuk setiap Sale
        List<SaleItem> userSaleItems = saleItemRepository.findBySaleUserId(currentUser.getId());

        // Mengelompokkan SaleItem berdasarkan sale_id
        Map<Long, List<SaleItem>> saleItemsBySaleId = userSaleItems.stream()
                .collect(Collectors.groupingBy(saleItem -> saleItem.getSale().getId()));

        // Mengambil produk pertama dari setiap transaksi
        Map<Long, SaleItem> firstSaleItemBySaleId = saleItemsBySaleId.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0)  // Ambil produk pertama dari setiap transaksi
                ));


        model.addAttribute("userSales", userSales);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("saleItemsBySaleId", saleItemsBySaleId);
        model.addAttribute("userSaleItems", userSaleItems);
        model.addAttribute("firstSaleItemBySaleId", firstSaleItemBySaleId);
        model.addAttribute("userCarts", userCartItems);

        return "sale/sales";
    }

    @RequestMapping("/pages/deletesales/{id}")
    @Transactional
    public String delete(@PathVariable(name = "id") long id) {
        // Temukan semua SaleItem yang terkait dengan saleId
        List<SaleItem> saleItems = saleItemRepository.findBySaleId(id);

        // Perbarui stok item dan total terjual
        for (SaleItem saleItem : saleItems) {
            Item item = saleItem.getItem();
            int quantity = saleItem.getQuantity();

            // Mengembalikan total_sold dan quantity item ke nilai semula
            item.setTotalSold(item.getTotalSold() - quantity);
            item.setQuantity(item.getQuantity() + quantity);
            itemRepository.save(item);
        }

        // Hapus semua SaleItem terkait
        saleItemRepository.deleteBySaleId(id);

        // Hapus penjualan dari repository
        salesRepository.deleteById(id);

        return "redirect:/pages/sales";
    }

    @GetMapping("/pages/searchsales")
    public String searchSalesByItemName(Model model,
                                        @RequestParam(name = "keyword", required = false) String itemName,
                                        Principal principal) {
        String email = principal.getName();
        User currentUser = userRepository.findByEmail(email);

        // Mendapatkan semua SaleItems untuk setiap Sale
        List<SaleItem> userSaleItems = saleItemRepository.findBySaleUserId(currentUser.getId());

        // Mengelompokkan SaleItem berdasarkan sale_id
        Map<Long, List<SaleItem>> saleItemsBySaleId = userSaleItems.stream()
                .collect(Collectors.groupingBy(saleItem -> saleItem.getSale().getId()));

        // Mengambil produk pertama dari setiap transaksi
        Map<Long, SaleItem> firstSaleItemBySaleId = saleItemsBySaleId.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0)  // Ambil produk pertama dari setiap transaksi
                ));

        List<Sale> sales;
        if (itemName != null && !itemName.isEmpty()) {
            // Lakukan pencarian berdasarkan nama item pertama dari setiap transaksi
            List<Long> saleIdsWithMatchingItems = firstSaleItemBySaleId.values().stream()
                    .filter(saleItem -> saleItem.getItem().getName().toLowerCase().contains(itemName.toLowerCase()))
                    .map(saleItem -> saleItem.getSale().getId())
                    .collect(Collectors.toList());

            sales = salesRepository.findByIdInOrderByIdDesc(saleIdsWithMatchingItems);
        } else {
            // Tampilkan semua penjualan jika tidak ada kata kunci pencarian
            sales = salesRepository.findByUserIdOrderByIdDesc(currentUser.getId());
        }
        model.addAttribute("firstSaleItemBySaleId", firstSaleItemBySaleId);
        model.addAttribute("saleItemsBySaleId", saleItemsBySaleId);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userSales", sales); // Menambahkan hasil pencarian ke model
        model.addAttribute("userCarts", cartItemService.findByUserId(currentUser.getId()));
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
        // Mendapatkan penjualan berdasarkan ID
        Sale sale = salesRepository.findById(id).orElse(null);
        if (sale == null) {
            // Handle jika penjualan tidak ditemukan
            System.out.println("Gagal menemukan penjualan");
            return;
        }

        // Mendapatkan semua SaleItems untuk Sale ini
        List<SaleItem> saleItems = saleItemRepository.findBySaleId(id);

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

            // Tambahkan informasi penjualan
            addCenteredText(document, "===========================================");
            addCenteredText(document, "Nota Penjualan");
            addCenteredText(document, "===========================================");
            addCenteredText(document, "Cashier : " + sale.getUser().getName());
            addCenteredText(document, "ID Penjualan: " + sale.getId() + " Metode Pembayaran: " + sale.getMetodePembayaran());
            addCenteredText(document, "Tanggal: " + formattedDate + " Status: " + sale.getStatus());
            addCenteredText(document, "===========================================");

            // Format decimal
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");

            // Buat tabel borderless untuk item
            Table table = new Table(new float[]{1, 4, 2, 2}); // Lebar kolom: No, Item Name, Quantity, Subtotal
            table.setBorder(Border.NO_BORDER);

            // Tambahkan header tabel tanpa border
            table.addHeaderCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("No")));
            table.addHeaderCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Item Name")));
            table.addHeaderCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Quantity")));
            table.addHeaderCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Subtotal")));

            // Tambahkan baris untuk setiap SaleItem
            int index = 1;
            for (SaleItem saleItem : saleItems) {
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(String.valueOf(index++))));
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(saleItem.getItem().getName())));
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(String.valueOf(saleItem.getQuantity())).setTextAlignment(TextAlignment.CENTER)));
                table.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(decimalFormat.format(saleItem.getPrice() * saleItem.getQuantity())).setTextAlignment(TextAlignment.RIGHT)));
            }

            // Atur alignment tabel ke tengah
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // Tambahkan tabel item ke dokumen
            document.add(table);

            // Cetak total harga
            Table tableTotalHarga = new Table(new float[]{1, 2}); // Lebar kolom: Label, Value
            tableTotalHarga.setBorder(Border.NO_BORDER);

            // Tambahkan sel untuk Total
            Cell totalLabelCell = new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Total                                                  "));
            Cell totalValueCell = new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(decimalFormat.format(sale.getTotal())));

            // Set alignment untuk label dan nilai
            totalLabelCell.setTextAlignment(TextAlignment.LEFT);
            totalValueCell.setTextAlignment(TextAlignment.RIGHT);

            // Tambahkan sel ke tabel
            tableTotalHarga.addCell(totalLabelCell);
            tableTotalHarga.addCell(totalValueCell);

            // Atur alignment tabel ke tengah
            tableTotalHarga.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // Tambahkan tabel total harga ke dokumen
            addCenteredText(document, "===========================================");
            document.add(tableTotalHarga);

            // Jika metode pembayaran adalah Cash, cetak input cash dan kembalian
            if (sale.getMetodePembayaran().equals("Cash")) {
                if (sale.getCashInput() != null && sale.getChangePayment() != null) {
                    addCenteredText(document, "===========================================");
                    Table tableCashInfo = new Table(new float[]{1, 2}); // Lebar kolom: Label, Value
                    tableCashInfo.setBorder(Border.NO_BORDER);

                    // Tambahkan sel untuk Cash Paid dan Change
                    Cell cashPaidLabelCell = new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Cash Paid                                          "));
                    Cell cashPaidValueCell = new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(decimalFormat.format(sale.getCashInput())));

                    Cell changeLabelCell = new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Change                                          "));
                    Cell changeValueCell = new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(decimalFormat.format(sale.getChangePayment())));

                    // Set alignment untuk label dan nilai
                    cashPaidLabelCell.setTextAlignment(TextAlignment.LEFT);
                    cashPaidValueCell.setTextAlignment(TextAlignment.RIGHT);

                    changeLabelCell.setTextAlignment(TextAlignment.LEFT);
                    changeValueCell.setTextAlignment(TextAlignment.RIGHT);

                    // Tambahkan sel ke tabel
                    tableCashInfo.addCell(cashPaidLabelCell);
                    tableCashInfo.addCell(cashPaidValueCell);

                    tableCashInfo.addCell(changeLabelCell);
                    tableCashInfo.addCell(changeValueCell);

                    // Atur alignment tabel ke tengah
                    tableCashInfo.setHorizontalAlignment(HorizontalAlignment.CENTER);

                    // Tambahkan tabel cash info ke dokumen
                    document.add(tableCashInfo);
                }
            }

            addCenteredText(document, "===========================================");
            addCenteredText(document, "Thank You!");
            addCenteredText(document, "Please Come Again");

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCenteredText(Document document, String text) {
        Paragraph paragraph = new Paragraph(text)
                .setTextAlignment(TextAlignment.CENTER);
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