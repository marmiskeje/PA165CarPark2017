package cz.muni.fi.pa165.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Matej Kralik, updated by Martin Mi�keje
 */
@Entity
public class RegionalBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable=false)
    private String name;

    @ManyToOne
    private RegionalBranch parent;

    @OneToMany(mappedBy = "parent")
    private List<RegionalBranch> children = new ArrayList<>();

    @OneToMany(mappedBy = "regionalBranch", cascade = CascadeType.ALL)
    private List<User> employees = new ArrayList<>();

    @OneToMany(mappedBy = "regionalBranch", cascade = CascadeType.ALL)
    private List<Car> cars = new ArrayList<>();

    @NotNull
    @Column(nullable = false)
    private LocalDateTime creationDate;

    @NotNull
    @Column(nullable=false)
    private LocalDateTime modificationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RegionalBranch getParent() {
        return parent;
    }

    public void setParent(RegionalBranch parent) {
        if (parent == this)
            throw new IllegalArgumentException("You cannot set this it as it's own Parent!");
        this.parent = parent;
    }

    public List<RegionalBranch> getChildren() {
        return children;
    }

    public void addChild(RegionalBranch children) {
        if (children == null)
            throw new IllegalArgumentException("You cannot add null branch!");
        if (this.children.contains(children))
            throw new IllegalArgumentException("Already contains this branch!");
        this.children.add(children);
    }
    
    public List<User> getEmployees() {
        return Collections.unmodifiableList(this.employees);
    }

    public void addEmployee(User employee) {
        if (employee == null)
            throw new IllegalArgumentException("You cannot add null employee");
        if (this.employees.contains(employee))
            throw new IllegalArgumentException("This branch already contains this employee");
        this.employees.add(employee);
        employee.setRegionalBranch(this);
    }
    
    public void removeEmployee(User employee) {
        if (employee == null)
            throw new IllegalArgumentException("You cannot remove null employee");
        if (!this.employees.contains(employee))
            throw new IllegalArgumentException("This branch does not contain this employee");
        this.employees.remove(employee);
        employee.setRegionalBranch(null);
    }

    public void removeAllEmployees(){
        this.employees.clear();
    }

    public List<Car> getCars() {
        return Collections.unmodifiableList(this.cars);
    }

    public void addCar(Car car) {
        if (car == null)
            throw new IllegalArgumentException("You cannot add null car");
        if (this.cars.contains(car))
            throw new IllegalArgumentException("This branch already contains this car!");
        this.cars.add(car);
        car.setRegionalBranch(this);
    }
    
    public void removeCar(Car car) {
        if (car == null)
            throw new IllegalArgumentException("You cannot remove null car");
        if (!this.cars.contains(car))
            throw new IllegalArgumentException("This branch does not contain this car!");
        this.cars.remove(car);
        car.setRegionalBranch(null);
    }

    public void removeAllCars(){
        this.cars.clear();
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof RegionalBranch)) return false;

        RegionalBranch that = (RegionalBranch) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return getCreationDate() != null ? getCreationDate().equals(that.getCreationDate()) : that.getCreationDate() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getCreationDate() != null ? getCreationDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RegionalBranch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + ((parent == null) ? null :parent.getId()) + //due to StackOverflowError because reginalBranch has user class
                ", children=" + children +
                ", employees=" + employees +
                ", cars=" + cars +
                ", creationDate=" + creationDate +
                ", modificationDate=" + modificationDate +
                '}';
    }
}