package com.lreas.file_management.repositories.jpa;

import com.lreas.file_management.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
    Setting findByName(String name);
}
