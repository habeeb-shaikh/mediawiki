package com.wiki.searcharticle.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiki.searcharticle.constants.ArticlesEnum;
import com.wiki.searcharticle.constants.ArticleConstant;
import com.wiki.searcharticle.model.ArticlesStatics;
import com.wiki.searcharticle.model.ArticleDetails;
import com.wiki.searcharticle.repositories.ArticleRepository;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/wiki")
public class ArticleController {


    Logger logger = Logger.getLogger(ArticleController.class.getName());

    private RestTemplate restTemplate;

    @Autowired
    private ArticleRepository articleRepository ;

    /**This method stores article details fetched from web api as per specified country
     *
     * @param country specified country name
     * @return JsonNode with all details stored in database.
     * @throws JsonProcessingException
     */

    @RequestMapping("/storearticles")
    public JsonNode saveArticleDetails(@RequestParam("country")final String country) throws JsonProcessingException {
        String endpoint= "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="+country+"&format=json";
        final JsonNode  responseBody=this.getRecords( endpoint);
        this.storeRecords(responseBody);
        logger.info("Recode Stored successfully");
        return responseBody;
    }

    /**
     * This method search stored articles in database either by title or snipped as per speicifed parameters.
     * @param title to search by title
     * @param snippet to search by snippet
     * @return JsonNode contains search result.
     * @throws JsonProcessingException
     */
    @RequestMapping("/searcharticle")
    public JsonNode searchArticle(@RequestParam(value = "", required = false)String title, @RequestParam(value = "snippet",required = false )String snippet) throws JsonProcessingException {
        List<ArticleDetails> detailList;
         if(null == title)
         {
             detailList= articleRepository.findBySnippetContaining(snippet);
         }
         else
         {
             detailList= articleRepository.findByTitleContaining(title);
         }
         if(null == detailList){
             logger.warning("RECORD not found.");
         }
         else
         {
             logger.info("RECORD Retrieved Successfully");
         }

        return this.getJsonNode(detailList);
    }

    /**
     * This method find smallest article as per wordcounts as per specified country.
     * @param country specified country name.
     * @return JsonNode contains smallest article details as per specified country.
     * @throws JsonProcessingException
     */
    @RequestMapping("/smallestarticle")
    public JsonNode findSmallArticle(@RequestParam("country")final String country)  throws JsonProcessingException {
        String type= ArticlesEnum.MIN.toString();
        String endpoint= "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="+country+"&format=json";
        final JsonNode  response=this.getRecords( endpoint);
        logger.info("Found Smallest article.");
        return this.findArticle(response,type);
    }

    /**
     * This method returns largest article details as per specified country.
     * @param country specified country name.
     * @return JsonNode contains smallest article details as per specified country.
     * @throws JsonProcessingException
     */
    @RequestMapping("/largestarticle")
    public JsonNode getLargeArticle(@RequestParam("country")final String country)  throws JsonProcessingException {
        String type= ArticlesEnum.MAX.toString();
        String endpoint= "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="+country+"&format=json";
        final JsonNode  responseBody=this.getRecords( endpoint);
//        System.out.println("RESPONSE>>" + responseBody);
        return this.findArticle(responseBody,type);
    }

    /**
     * This method provided required statics on the basis of size and wordcount as per country.
     * @param country specified country name.
     * @return JsonNode contains statics.
     * @throws JsonProcessingException
     */
    @RequestMapping("/articlestatics")
    public JsonNode getArticleStatics(@RequestParam("country")final String country)  throws JsonProcessingException {

        String endpoint= "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="+country+"&format=json";
        final JsonNode  response=this.getRecords( endpoint);
//        System.out.println("RESPONSE>>" + response);
        return  this.calculateStatics(response);

    }

    /**
     * This method generate statics and store into JsonNode.
     * @param response
     * @return
     */
    private JsonNode calculateStatics( JsonNode response)
    {
        Object[] statics= this.getStatics(response);
        ArticlesStatics articleStatics = this.setStatics(statics);
        return getJsonNode(articleStatics);
    }

    /**
     * This method add statics data into model.
     * @param statics calculated statics.
     * @return Article Statics object.
     */
    private ArticlesStatics setStatics(Object[] statics)
    {
        ArticlesStatics articleStatics = new ArticlesStatics();

        articleStatics.setSizeMax((Long)statics[0]);
        articleStatics.setSizeMin((Long)statics[1]);
        articleStatics.setSizeMedian((Double) statics[2]);
        articleStatics.setWordCountMax((Long)statics[3]);
        articleStatics.setWordCountMin((Long) statics[4]);
        articleStatics.setWordCountMedian((Double) statics[5]);
        return articleStatics;
    }

    /**
     * This method consolidate statical data into Object.
     * @param response web api response.
     * @return statical data object.
     */
    private Object[] getStatics(JsonNode response)
    {
        Object[] statics = new Object[6];
        String size= ArticleConstant.SIZE;
        String wordcount=ArticleConstant.WORDCOUNT;

        statics[0] = calculateMinMax(response,size, ArticlesEnum.MAX.toString());
        statics[1] = calculateMinMax(response,size, ArticlesEnum.MIN.toString());
        statics[2] = calculateMedian(response,size);
        statics[3] = calculateMinMax(response,wordcount, ArticlesEnum.MAX.toString());
        statics[4] = calculateMinMax(response,wordcount, ArticlesEnum.MIN.toString());
        statics[5] = calculateMedian(response,wordcount);
        return statics;
    }

    /**
     * This method calculate min or max value as per input
     * @param response provided response from web api.
     * @param _type either Size or Wordcount.
     * @param _operator either MIN or MAX.
     * @return
     */
    private long calculateMinMax(JsonNode response, String _type, String _operator)
    {
            long count=0;
            long sizeCount=0;
            int position=0;
            String type=_type;
            String operator=_operator;
            int limit=  response.get("query").get("search").size();

            for (int i=0;i < limit;i++) {
                long tempCount = response.get("query").get("search").get(i).get(type).asLong();

                if(count ==0 )
                {
                    count=tempCount;
                }
                if(ArticlesEnum.MIN.toString().contains(operator) && tempCount < count)
                {
//                    if (tempCount < count) {
                        count = tempCount;
//                    }
                }
                else if(ArticlesEnum.MAX.toString().contains(operator) && tempCount > count)
                {
//                    if (tempCount >  count) {
                        count = tempCount;
//                    }
                }
            }
            logger.info("COUNT:"+count);
            return count;
    }

    /**
     * This method calculate Median of specified type.
     * @param response provided response from web api.
     * @param _type Either Size or Wordcount.
     * @return return calculated median.
     */
    private double calculateMedian(JsonNode response,String _type)
    {
        String type=_type;
        int size=  this.getJsonNodeSize(response);
        double[] values= new double[size];
        double value;

        for (int i=0;i < size;i++) {

            value = response.get("query").get("search").get(i).get(type).asDouble();
            values[i]=value;
        }
        return this.getMedian(values);
    }

    /**
     * This method gets Median value from given values array,
     * @param values values array to calculate median.
     * @return calculated median.
     */
    private double getMedian(double[] values)
    {
        double medianValue=0;
        Median median = new Median();
        medianValue= median.evaluate(values);
        if(0== medianValue)
        {
         logger.warning("Median is not calculated");
        }
        return medianValue;
    }

    /**
     * This method finds Min or Max article as per wordcount.
     * @param response provided response from web api.
     * @param type Either Min or Max.
     * @return JsonNode contains Mib or Max data as per input.
     */
    private JsonNode findArticle(JsonNode response,final String type)
    {
        long wordCount=0;
        int position=0;
        boolean updateRecord=false;
        String operator=type;
        ArticleDetails details=null;
        int size= this.getJsonNodeSize(response);

        for (int i=0;i < size;i++) {

            long tempWordCount = response.get("query").get("search").get(i).get("wordcount").asLong();
            if(wordCount ==0 )
            {
                wordCount=tempWordCount;
                updateRecord=true;
            }
            if(ArticlesEnum.MIN.toString().contains(operator) && tempWordCount < wordCount)
            {
//                if (tempWordCount < wordCount) {
                    wordCount = tempWordCount;
                    position=i;
                    updateRecord=true;
//                }
            }
            else if(ArticlesEnum.MAX.toString().contains(operator) && tempWordCount >  wordCount)
            {
//                if (tempWordCount >  wordCount) {
                    wordCount = tempWordCount;
                    position=i;
                    updateRecord=true;
//                }
            }
            if (updateRecord) {
                wordCount = tempWordCount;
                position=i;
                details= this.setArticleDetails(response,position);
                updateRecord=false;
                logger.info("RECORD UPDATED.");
            }
        }
        return this.getJsonNode(details);
    }

    /**
     * This method gets JsonNode from Object.
     * @param details given object to be converted into JsonNode.
     * @return JsonNode from given object.
     */
    private JsonNode getJsonNode(Object details)
    {
        JsonNode node=null;
        ObjectMapper mapper = new ObjectMapper();
        node= mapper.valueToTree(details);
        if(null == node)
        {
            logger.warning("JSON Node is null");
        }
        else
        {
            logger.info("Json Node return Successfully.");
        }
        return node;
    }

    /**
     * This method sets article details into Object.
     * @param response  response given by web api.
     * @param index store data in object.
     * @return data stored on object.
     */
    private ArticleDetails setArticleDetails(JsonNode response,int  index)
    {
        ArticleDetails details= new ArticleDetails();
        details.setNs(response.get("query").get("search").get(index).get("ns").toString());
        details.setTitle(response.get("query").get("search").get(index).get("title").toString());
        details.setPageId(response.get("query").get("search").get(index).get("pageid").toString());
        details.setSize(response.get("query").get("search").get(index).get("size").toString());
        details.setWordcount(response.get("query").get("search").get(index).get("wordcount").toString());
        details.setSnippet(response.get("query").get("search").get(index).get("snippet").toString());
        details.setTimestamp(response.get("query").get("search").get(index).get("timestamp").toString());
        logger.info("Article Records set into Object.");
        return details;
    }

    /**
     * This method return response provided by web api endpoint.
     * @param endpoint provided endpoint.
     * @return JsonNode for given web api endpoint.
     */
    private JsonNode getRecords(String endpoint)
    {
        JsonNode node= null;
        restTemplate = new RestTemplate();
        node  =restTemplate.getForObject(endpoint, JsonNode.class);
        if(null == node)
        {
            logger.warning("No Record Found in Web Request");
        }
        else
        {
            logger.info("Data return successfully.");
        }
        return node;
    }

    /**
     * This method store record into database.
     * @param response
     */
    private void storeRecords(JsonNode response)
    {
        ArticleDetails details;
        int size=this.getJsonNodeSize(response);
        logger.info("SIZE"+size);
        for (int i=0;i < size;i++) {
            details = new ArticleDetails();
            details.setNs(response.get("query").get("search").get(i).get("ns").toString());
            details.setTitle(response.get("query").get("search").get(i).get("title").toString());
            details.setPageId(response.get("query").get("search").get(i).get("pageid").toString());
            details.setSize(response.get("query").get("search").get(i).get("size").toString());
            details.setWordcount(response.get("query").get("search").get(i).get("wordcount").toString());
            details.setSnippet(response.get("query").get("search").get(i).get("snippet").toString());
            details.setTimestamp(response.get("query").get("search").get(i).get("timestamp").toString());
            articleRepository.save(details);
        }
        logger.info("Record save successfully in database.");
    }

    /**
     * This method return JsonNode size.
     * @param response contains response got from web api.
     * @return size of JsonNode.
     */
    private int getJsonNodeSize(JsonNode response)
    {   int size=0;
        size  =  response.get("query").get("search").size();
        if(0 != size)
        {
            logger.info("Size:"+ size);
        }
        else {
            logger.warning("Size NOT calculated.");
        }
        return size;
    }




//    private String displayRecord(List<ArticleDetails> detailList)
//    {
//        String result="Record Not Found";
//        ArticleDetails details;
//        for (int i=0;i<detailList.size();i++) {
//            details= detailList.get(i);
//            System.out.println(details.getNs());
//            System.out.println(details.getTitle());
//            System.out.println(details.getPageId());
//            System.out.println(details.getSize());
//            System.out.println(details.getWordcount());
//            System.out.println(details.getSnippet());
//            System.out.println(details.getTimestamp());
//            result="Record Found.";
//        }
//        return result;
//    }

//    private void displayAllRecords()
//    {
//        System.out.println("\nfindAll()");
//        articleRepository.findAll().forEach(x -> System.out.println(x.getNs()));
//        articleRepository.findAll().forEach(x -> System.out.println(x.getPageId()));
//        articleRepository.findAll().forEach(x -> System.out.println(x.getSize()));
//        articleRepository.findAll().forEach(x -> System.out.println(x.getTitle()));
//        articleRepository.findAll().forEach(x -> System.out.println(x.getWordcount()));
//        articleRepository.findAll().forEach(x -> System.out.println(x.getSnippet()));
//        articleRepository.findAll().forEach(x -> System.out.println(x.getTimestamp()));
//
//    }

}


