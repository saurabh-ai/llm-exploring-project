package com.javamastery.inventory.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Supplier entity representing product suppliers
 */
public class Supplier {
    private Long supplierId;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
    
    // Constructors
    public Supplier() {}
    
    public Supplier(String name, String contactPerson, String email, String phone, String address) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    
    public Supplier(Long supplierId, String name, String contactPerson, 
                   String email, String phone, String address, LocalDateTime createdAt) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Validation methods
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }
    
    public boolean hasValidEmail() {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    // Object methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(supplierId, supplier.supplierId) &&
               Objects.equals(name, supplier.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(supplierId, name);
    }
    
    @Override
    public String toString() {
        return "Supplier{" +
               "supplierId=" + supplierId +
               ", name='" + name + '\'' +
               ", contactPerson='" + contactPerson + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", address='" + address + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}