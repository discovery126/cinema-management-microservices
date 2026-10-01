package com.github.discovery126.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "roles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false)
    private UUID id;

    @Size(max = 20)
    @NotNull
    @Column(nullable = false, length = 20)
    private String name;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}