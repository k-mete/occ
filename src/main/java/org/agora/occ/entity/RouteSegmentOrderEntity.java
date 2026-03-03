package org.agora.occ.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "route_segment_order")
@IdClass(RouteSegmentOrderEntity.RouteSegmentOrderId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSegmentOrderEntity extends PanacheEntityBase {

    @Id
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private RouteEntity route;

    @Id
    @ManyToOne
    @JoinColumn(name = "route_segment_id", nullable = false)
    private RouteSegmentEntity routeSegment;

    @Column(name = "segment_index", nullable = false)
    private Integer segmentIndex;

    public static class RouteSegmentOrderId implements Serializable {
        public UUID route;
        public UUID routeSegment;

        public RouteSegmentOrderId() {
        }

        public RouteSegmentOrderId(UUID route, UUID routeSegment) {
            this.route = route;
            this.routeSegment = routeSegment;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            RouteSegmentOrderId that = (RouteSegmentOrderId) o;
            return Objects.equals(route, that.route) &&
                    Objects.equals(routeSegment, that.routeSegment);
        }

        @Override
        public int hashCode() {
            return Objects.hash(route, routeSegment);
        }
    }
}
