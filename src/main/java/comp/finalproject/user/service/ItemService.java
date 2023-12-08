package comp.finalproject.user.service;

import comp.finalproject.user.entity.Item;
import comp.finalproject.user.model.ItemRequest;
import comp.finalproject.user.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public Item findById(long id) {
        return itemRepository.findById(id);
    }

    public List<Item> findAll() {
        return itemRepository.findByDeletedFalseOrderByCreatedAtDesc();
    }

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    public ResponseEntity<?> createRequest(ItemRequest itemDto) {
        Item item = new Item();

        // Membuat atribute Makanan sesuai data yang diterima
        item.setName(itemDto.getName());
        item.setType(itemDto.getType());
        item.setQuantity(itemDto.getQuantity());
        item.setAmount(itemDto.getAmount());
        item.setTotalSold(itemDto.getTotalSold());
        item.setImagePath(itemDto.getImagePath());
        item.setDescription(itemDto.getDescription());

        // simpan perubahan kedalam database
        Item itemSaved = itemRepository.save(item);

        return new ResponseEntity<>(itemSaved, HttpStatus.CREATED);
    }

    public ResponseEntity<?> editRequest(long id, ItemRequest itemDto) {
        // Temukan objek Makanan berdasarkan ID
        Item item = itemRepository.findById(id);

        // Jika makanan tidak ditemukan, kembalikan respons 404
        if (item == null) {
            return new ResponseEntity<>("Item tidak ada", HttpStatus.NOT_FOUND);
        }

//        Javers javers = JaversBuilder.javers()
//                .build();

        // Salin objek Item sebelum perubahan sebagai "left"
//        Item itemleft = javers.getJsonConverter().fromJson(javers.getJsonConverter().toJson(item), Item.class);

        // Update atribut Makanan sesuai dengan data yang diterima
        item.setName(itemDto.getName());
        item.setType(itemDto.getType());
        item.setQuantity(itemDto.getQuantity());
        item.setAmount(itemDto.getAmount());
        item.setTotalSold(itemDto.getTotalSold());
        item.setImagePath(itemDto.getImagePath());
        item.setDescription(itemDto.getDescription());

        // Simpan perubahan ke dalam database
        Item editItem = itemRepository.save(item);

        // Bandingkan Makanan sebelum dan sesudah perubahan
//        Diff diff = javers.compare(itemLeft, editItem);

//        System.out.println(diff.prettyPrint());

        return new ResponseEntity<>(editItem, HttpStatus.OK);
    }

    public ResponseEntity<?> softDeleteById(long id) {
        Item item = itemRepository.findById(id);
        // jika makanan ada akan diproses
        if (item != null) {
            // setting delete menjadi true
            item.setDeleted(true);
            // simpan perubahan kedalam database
            Item deletedItem = itemRepository.save(item);
            return new ResponseEntity<>(deletedItem, HttpStatus.OK);
        }
        return new ResponseEntity<>("Item tidak ada ", HttpStatus.BAD_REQUEST);
    }
}
