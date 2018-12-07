package org.superbiz.moviefun;

import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    TransactionTemplate movieTemplate;
    TransactionTemplate albumTemplate;
    PlatformTransactionManager platformTransactionManagerAlbum;
    PlatformTransactionManager platformTransactionManagerMovie;


    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures,
                          PlatformTransactionManager platformTransactionManagerAlbum, PlatformTransactionManager platformTransactionManagerMovie) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        //this.albumTemplate = albumTemplate;
        //this.movieTemplate = movieTemplate;
        this.platformTransactionManagerAlbum = platformTransactionManagerAlbum;
        this.platformTransactionManagerMovie = platformTransactionManagerMovie;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        /*movieTemplate.execute(TransactionCallbackWithoutResult() { protected void doInTransactionWithoutResult(TransactionStatus status) { moviesBean.addMovie(movie); } }; */
       new TransactionTemplate(platformTransactionManagerAlbum).execute(new TransactionCallbackWithoutResult() {
           @Override
           protected void doInTransactionWithoutResult(org.springframework.transaction.TransactionStatus transactionStatus) {
               for (Album album : albumFixtures.load()) {
                   albumsBean.addAlbum(album);
               }
           }
       });

        new TransactionTemplate(platformTransactionManagerMovie).execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(org.springframework.transaction.TransactionStatus transactionStatus) {
                for (Movie movie : movieFixtures.load()) {
                    moviesBean.addMovie(movie);
                }
            }
        });




        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
