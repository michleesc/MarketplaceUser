package comp.finalproject.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sale_items")
public class SaleItem implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne@JoinColumn(name = "sale_id", nullable = false)private Sale sale;

    @ManyToOne@JoinColumn(name = "item_id", nullable = false)private Item item;

    private int quantity;
    private float price;
}
