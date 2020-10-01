package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News,Integer> {


    boolean existsByNewsSiteId(int newsSiteId);
    boolean existsByNewsId(int newsId);

}
