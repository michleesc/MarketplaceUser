package comp.finalproject.user.controller.api;

import comp.finalproject.user.dto.ItemDto;
import comp.finalproject.user.entity.Item;
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
    public ResponseEntity<?> create(@RequestBody ItemDto itemDto) {
        return itemService.createRequest(itemDto);
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<?> edit(@RequestBody ItemDto itemDto, @PathVariable long id) {
        return itemService.editRequest(id, itemDto);
    }

    @DeleteMapping("/makanan/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        return itemService.softDeleteById(id);
    }
}
