package comp.finalproject.user.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
