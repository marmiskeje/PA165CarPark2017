package cz.muni.fi.pa165.entity;

import cz.muni.fi.pa165.enums.UserType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @author Matej Kralik, updated by Martin Mi�keje
 */

@Entity
@Table(name="USERS") //user is reserved keyword
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(nullable=false, unique = true)
    private String userName;

    @NotNull
    @Size(min = 8)
    @Column(nullable=false)
    private String password;

    @NotNull
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private UserType type;

    @NotNull
    @Column(nullable=false)
    private LocalDateTime creationDate;

    private LocalDateTime activationDate;

    @NotNull
    @Column(nullable=false)
    private LocalDateTime modificationDate;
    
    @ManyToOne
    @JoinColumn(name = "regionalBranchId", nullable = true)
    private RegionalBranch regionalBranch = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDateTime activationDate) {
        this.activationDate = activationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
    
    public RegionalBranch getRegionalBranch() {
        return regionalBranch;
    }
    // internal only
    void setRegionalBranch(RegionalBranch regionalBranch) {
        this.regionalBranch = regionalBranch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getId() != user.getId()) return false;
        if (getUserName() != null ? !getUserName().equals(user.getUserName()) : user.getUserName() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(user.getPassword()) : user.getPassword() != null)
            return false;
        return getType() == user.getType();
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getUserName() != null ? getUserName().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", creationDate=" + creationDate +
                ", activationDate=" + activationDate +
                ", modificationDate=" + modificationDate +
                ", regionalBranchId=" + ((regionalBranch == null) ? null :regionalBranch.getId()) + //due to StackOverflowError because reginalBranch has user class
                ", regionalBranchName=" + ((regionalBranch == null) ? null :regionalBranch.getName()) + //due to StackOverflowError because reginalBranch has user class
                '}';
    }
}
