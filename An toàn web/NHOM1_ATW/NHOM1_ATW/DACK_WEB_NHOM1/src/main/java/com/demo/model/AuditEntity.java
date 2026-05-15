
package com.demo.model;

import jakarta.persistence.*;
import java.time.*;

@MappedSuperclass
public abstract class AuditEntity {

    @Column(name = "ngay_tao", updatable = false)
    protected LocalDate ngayTao = LocalDate.now();
    @Column(name = "ngay_cap_nhat")
    protected LocalDate ngayCapNhat;

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDate.now();
    }
}
