package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.StationSchedulePlanEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class StationSchedulePlanRepository implements PanacheRepositoryBase<StationSchedulePlanEntity, UUID> {

    public Optional<StationSchedulePlanEntity> findByPlanId(UUID planId) {
        return find("planId", planId).firstResultOptional();
    }

    public List<StationSchedulePlanEntity> findByTrainId(UUID trainId, int page, int size) {
        return find("train.id", trainId).page(Page.of(page, size)).list();
    }

    public List<StationSchedulePlanEntity> findByTrainId(UUID trainId) {
        return find("train.id", trainId).list();
    }

    public long countByTrainId(UUID trainId) {
        return count("train.id", trainId);
    }

    public List<StationSchedulePlanEntity> findByDate(LocalDate date, int page, int size) {
        return find("cast(arrivalPlan as date) = :date OR cast(departurePlan as date) = :date",
                Parameters.with("date", date)).page(Page.of(page, size)).list();
    }

    public List<StationSchedulePlanEntity> findByDate(LocalDate date) {
        return find("cast(arrivalPlan as date) = :date OR cast(departurePlan as date) = :date",
                Parameters.with("date", date)).list();
    }

    public long countByDate(LocalDate date) {
        return count("cast(arrivalPlan as date) = :date OR cast(departurePlan as date) = :date",
                Parameters.with("date", date));
    }

    public List<StationSchedulePlanEntity> findByTrainIdAndDate(UUID trainId, LocalDate date, int page, int size) {
        return find(
                "train.id = :trainId AND (cast(arrivalPlan as date) = :date OR cast(departurePlan as date) = :date)",
                Parameters.with("trainId", trainId).and("date", date)).page(Page.of(page, size)).list();
    }

    public List<StationSchedulePlanEntity> findByTrainIdAndDate(UUID trainId, LocalDate date) {
        return find(
                "train.id = :trainId AND (cast(arrivalPlan as date) = :date OR cast(departurePlan as date) = :date)",
                Parameters.with("trainId", trainId).and("date", date)).list();
    }

    public long countByTrainIdAndDate(UUID trainId, LocalDate date) {
        return count(
                "train.id = :trainId AND (cast(arrivalPlan as date) = :date OR cast(departurePlan as date) = :date)",
                Parameters.with("trainId", trainId).and("date", date));
    }

    public List<StationSchedulePlanEntity> findAll(int page, int size) {
        return findAll().page(Page.of(page, size)).list();
    }
}
