package com.pryabykh.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "intershop.images")
public class Image {

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("bytes")
    private byte[] bytes;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
