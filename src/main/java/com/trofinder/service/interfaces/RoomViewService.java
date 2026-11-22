package com.trofinder.service.interfaces;

public interface RoomViewService {

    void recordView(Long roomId, String ipAddress, Long userId);

    long getTotalViews(Long roomId);
}
