package com.wiki.searcharticle;

import com.wiki.searcharticle.model.ArticleDetails;
import com.wiki.searcharticle.repositories.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SearchArticlesApplication {

	private static final Logger log = LoggerFactory.getLogger(SearchArticlesApplication.class);

	@Autowired
	private ArticleRepository repository;
	@Autowired
	private ArticleRepository articleRepository ;

	public static void main(String[] args) {
		SpringApplication.run(SearchArticlesApplication.class, args);
	}

}
