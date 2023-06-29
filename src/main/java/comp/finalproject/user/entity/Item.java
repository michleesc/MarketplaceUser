package comp.finalproject.user.entity;
import javax.persistence.*;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String type;
    private int quantity;
    private int amount;

    @Column(name = "total_sold")
    private int totalSold;

    @Column(name = "image_path")
    private String imagePath;

    private String description;

    public Item() {
    }

    public Item(String name, String type, int quantity, int amount, int totalSold, String imagePath, String description) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.amount = amount;
        this.totalSold = totalSold;
        this.imagePath = imagePath;
        this.description = description;
    }

    // Getter dan setter untuk semua properti


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
