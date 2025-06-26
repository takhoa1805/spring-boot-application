package com.lreas.generator.repositories.jpa;

import com.lreas.generator.models.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
    Setting findByName(String name);
}
