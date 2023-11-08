package comp.finalproject.user.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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
    @Column(name = "created_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm z", timezone = "Asia/Jakarta")
    private Date createdAt = new Date();
    private boolean deleted = false;
}
