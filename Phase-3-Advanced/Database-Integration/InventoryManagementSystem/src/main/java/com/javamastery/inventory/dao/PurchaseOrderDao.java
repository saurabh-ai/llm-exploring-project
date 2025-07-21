package com.javamastery.inventory.dao;

import com.javamastery.inventory.exception.DatabaseException;
import com.javamastery.inventory.model.PurchaseOrder;
import com.javamastery.inventory.model.PurchaseOrderStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PurchaseOrder entities
 */
public class PurchaseOrderDao extends BaseDao<PurchaseOrder, Long> {
    
    public PurchaseOrderDao() {
        super();
    }
    
    public PurchaseOrderDao(DataSource dataSource) {
        super(dataSource);
    }
    
    @Override
    protected String getTableName() {
        return "purchase_orders";
    }
    
    @Override
    protected String getIdColumnName() {
        return "po_id";
    }
    
    @Override
    protected String getInsertSql() {
        return "INSERT INTO purchase_orders (supplier_id, po_number, status, order_date, total_amount, notes) VALUES (?, ?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSql() {
        return "UPDATE purchase_orders SET supplier_id = ?, po_number = ?, status = ?, order_date = ?, total_amount = ?, notes = ? WHERE po_id = ?";
    }
    
    @Override
    protected PurchaseOrder mapResultSetToEntity(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoId(rs.getLong("po_id"));
        po.setSupplierId(rs.getLong("supplier_id"));
        po.setPoNumber(rs.getString("po_number"));
        po.setStatus(PurchaseOrderStatus.valueOf(rs.getString("status")));
        
        Date orderDate = rs.getDate("order_date");
        if (orderDate != null) {
            po.setOrderDate(orderDate.toLocalDate());
        }
        
        Date expectedDate = rs.getDate("expected_date");
        if (expectedDate != null) {
            po.setExpectedDate(expectedDate.toLocalDate());
        }
        
        Date receivedDate = rs.getDate("received_date");
        if (receivedDate != null) {
            po.setReceivedDate(receivedDate.toLocalDate());
        }
        
        po.setTotalAmount(rs.getBigDecimal("total_amount"));
        po.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            po.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return po;
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, PurchaseOrder po) throws SQLException {
        stmt.setLong(1, po.getSupplierId());
        
        // Generate PO number if not set
        String poNumber = po.getPoNumber();
        if (poNumber == null || poNumber.isEmpty()) {
            poNumber = "PO-" + System.currentTimeMillis();
        }
        stmt.setString(2, poNumber);
        
        stmt.setString(3, po.getStatus().name());
        
        if (po.getOrderDate() != null) {
            stmt.setDate(4, java.sql.Date.valueOf(po.getOrderDate()));
        } else {
            stmt.setDate(4, null);
        }
        
        stmt.setBigDecimal(5, po.getTotalAmount());
        stmt.setString(6, po.getNotes());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, PurchaseOrder po) throws SQLException {
        setInsertParameters(stmt, po);
        stmt.setLong(7, po.getPoId());
    }
    
    @Override
    protected void setGeneratedId(PurchaseOrder entity, Long id) {
        entity.setPoId(id);
    }
    
    @Override
    protected Long getId(PurchaseOrder entity) {
        return entity.getPoId();
    }
    
    @Override
    protected boolean hasId(PurchaseOrder entity) {
        return entity.getPoId() != null && entity.getPoId() > 0;
    }
    
    /**
     * Find purchase orders by status
     */
    public List<PurchaseOrder> findByStatus(PurchaseOrderStatus status) {
        String sql = "SELECT * FROM purchase_orders WHERE status = ? ORDER BY order_date DESC";
        return executeQuery(sql, status.name());
    }
    
    /**
     * Find purchase orders by supplier
     */
    public List<PurchaseOrder> findBySupplier(Long supplierId) {
        String sql = "SELECT * FROM purchase_orders WHERE supplier_id = ? ORDER BY order_date DESC";
        return executeQuery(sql, supplierId);
    }
    
    /**
     * Find purchase orders with supplier information
     */
    public List<PurchaseOrder> findAllWithSupplier() {
        String sql = """
            SELECT po.*, s.name as supplier_name
            FROM purchase_orders po
            JOIN suppliers s ON po.supplier_id = s.supplier_id
            ORDER BY po.order_date DESC
        """;
        
        List<PurchaseOrder> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                results.add(mapResultSetToEntityWithSupplier(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding purchase orders with supplier", e);
        }
        
        return results;
    }
    
    private PurchaseOrder mapResultSetToEntityWithSupplier(ResultSet rs) throws SQLException {
        PurchaseOrder po = mapResultSetToEntity(rs);
        po.setSupplierName(rs.getString("supplier_name"));
        return po;
    }
}