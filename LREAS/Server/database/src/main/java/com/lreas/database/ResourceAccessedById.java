package com.lreas.database;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
@Embeddable
public class ResourceAccessedById implements Serializable {
    @ManyToOne
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceAccessedById that = (ResourceAccessedById) o;
        return Objects.equals(this.resource.getId(), that.getResource().getId()) &&
                Objects.equals(this.user.getId(), that.user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.resource.getId(), this.user.getId());
    }
}