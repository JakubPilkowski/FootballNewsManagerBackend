package com.footballnewsmanager.backend.repositories;

import com.footballnewsmanager.backend.models.NewsTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsTagRepository extends JpaRepository<NewsTag, Long> {

}
