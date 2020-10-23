package com.wiki.searcharticle.db;

import com.wiki.searcharticle.model.ArticleDetails;
import com.wiki.searcharticle.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@Component
//public class JPACrud{
//    @Autowired
//    private ArticleRepository articleRepository ;
//
//    public void run(String... args) throws Exception {
//
//        ArticleDetails jordan = new ArticleDetails();
//
//        jordan.setNs("0");
//        jordan.setTitle("Jordan");
//        jordan.setPageId("1");
//        jordan.setSize("50");
//        jordan.setWordcount("5000");
//        jordan.setSnippet("This is jordan wiki pages");
//
//        articleRepository.save(jordan);
//
//        ArticleDetails Amman = new ArticleDetails();
//
//        Amman.setNs("0");
//        Amman.setTitle("Amman");
//        Amman.setPageId("2");
//        Amman.setSize("100");
//        Amman.setWordcount("10000");
//        Amman.setSnippet("This is Amman wiki pages");
//
//        articleRepository.save(Amman);
//
//        System.out.println("\nfindAll()");
//        articleRepository.findAll().forEach(x -> System.out.println(x));
//
//        System.out.println("\nfindById(1L)");
//        articleRepository.findById(1l).ifPresent(x -> System.out.println(x));
//
//        System.out.println("\nfindByName('Node')");
//        articleRepository.findByPageId("1").forEach(x -> System.out.println(x));
//    }
//}