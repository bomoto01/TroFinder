package com.trofinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trofinder.entity.ReportHistory;

public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {

    List<ReportHistory> findByReportIdOrderByProcessedAtDesc(Long reportId);
}
