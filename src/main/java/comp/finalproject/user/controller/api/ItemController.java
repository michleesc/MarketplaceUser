package comp.finalproject.user.controller.api;

import comp.finalproject.user.entity.Item;
import comp.finalproject.user.model.ItemRequest;
import comp.finalproject.user.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("/items")
    public List<Item> findAll() {
        return itemService.findAll();
    }

    @GetMapping("/item/{id}")
    public Item getFindById(@PathVariable("id") long id) {
        return itemService.findById(id);
    }

    @PostMapping("/item")
    public ResponseEntity<?> create(@RequestBody ItemRequest item) {
        return itemService.createRequest(item);
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<?> edit(@RequestBody ItemRequest item, @PathVariable long id) {
        return itemService.editRequest(id, item);
    }

    @DeleteMapping("/makanan/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        return itemService.softDeleteById(id);
    }
}
