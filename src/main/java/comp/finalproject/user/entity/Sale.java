package comp.finalproject.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sales")
public class Sale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "iditem", unique = false, referencedColumnName = "id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "idUser")
    @JsonIgnoreProperties("sales")
    private User user;

    private int quantity;
    private float subtotal;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String status;

    public Sale() {
        this.date = new Date();
    }

    public Sale(int quantity, float subtotal) {
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.date = new Date();
    }

    public Sale(long id, int quantity, float subtotal) {
        this.id = id;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.date = new Date();
    }

    public Sale(Item item, int quantity, float subtotal, User user) {
        this.item = item;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.user = user;
        this.date = new Date();
    }

    public Sale(Item item, int quantity, float subtotal, Long userId) {
        this.item = item;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.user = new User();
        this.user.setId(userId);
        this.date = new Date();
    }

    // Getter and setter methods go here...

    // id getter and setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // item getter and setter
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    // user getter and setter
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // quantity getter and setter
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // subtotal getter and setter
    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    // date getter and setter
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // status getter and setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
