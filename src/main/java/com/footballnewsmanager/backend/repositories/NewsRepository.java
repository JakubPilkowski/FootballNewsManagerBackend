package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface NewsRepository extends JpaRepository<News,Integer> {


    boolean existsByNewsSiteIdAndNewsId(int newsSiteId, int newsId);

    void deleteByDateLessThan(LocalDate localDate);

//    boolean existsByNewsSiteId(int newsSiteId);
//    boolean existsByNewsId(int newsId);

}
