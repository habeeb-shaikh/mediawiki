package com.wiki.searcharticle.repositories;

import com.wiki.searcharticle.model.ArticleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleDetails,Long> {

    List<ArticleDetails> findByPageId(String Id);

    List<ArticleDetails> findByTitleContaining(String title);
    List<ArticleDetails> findBySnippetContaining(String Snippet);

}
