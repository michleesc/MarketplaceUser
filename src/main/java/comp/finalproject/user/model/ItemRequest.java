package comp.finalproject.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private String name;
    private String type;
    private int quantity;
    private int amount;
    private int totalSold;
    private String imagePath;
    private String description;
}
